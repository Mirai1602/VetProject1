package com.proyectofinal.veterinarialosarcos.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Mantiene la base de datos al día de forma automática en cada arranque.
 *
 * <p>Hibernate ({@code hbm2ddl.auto=update}) crea tablas y columnas nuevas por sí solo,
 * pero NO puede cambiar tipos de columna, renombrar, borrar ni agregar columnas
 * obligatorias a tablas con datos. Para esos casos se usan scripts SQL en
 * {@code resources/com/proyectofinal/veterinarialosarcos/migraciones/}:</p>
 *
 * <ul>
 *   <li>{@code V1.sql}, {@code V2.sql}, ... se ejecutan UNA sola vez, en orden, antes de que
 *       Hibernate actualice el esquema. Se registran en la tabla {@code schema_migraciones}.</li>
 *   <li>{@code R1.sql}, {@code R2.sql}, ... (repetibles) se ejecutan en CADA arranque,
 *       después de Hibernate.</li>
 * </ul>
 *
 * <p>Los scripts deben poder ejecutarse sobre una base nueva (usar {@code IF EXISTS} /
 * {@code IF NOT EXISTS}), porque en una base vacía las tablas todavía no existen.</p>
 */
public final class MigracionesBD {

    private static final String RUTA = "/com/proyectofinal/veterinarialosarcos/migraciones/";

    private MigracionesBD() {
    }

    /** Ejecuta los scripts V*.sql pendientes usando una conexión sin generación de esquema. */
    static void aplicarVersionadas(String unidad) {
        EntityManagerFactory temporal = Persistence.createEntityManagerFactory(
                unidad, Map.of("hibernate.hbm2ddl.auto", "none"));
        try {
            conConexion(temporal, conexion -> {
                try (Statement st = conexion.createStatement()) {
                    st.execute("CREATE TABLE IF NOT EXISTS schema_migraciones ("
                            + "version VARCHAR(50) PRIMARY KEY, "
                            + "aplicada TIMESTAMP NOT NULL DEFAULT now())");
                }
                for (int n = 1; ; n++) {
                    String version = "V" + n;
                    String sql = leer(version + ".sql");
                    if (sql == null) {
                        break;
                    }
                    if (yaAplicada(conexion, version)) {
                        continue;
                    }
                    try (Statement st = conexion.createStatement()) {
                        st.execute(sql);
                    }
                    try (PreparedStatement ps = conexion.prepareStatement(
                            "INSERT INTO schema_migraciones (version) VALUES (?)")) {
                        ps.setString(1, version);
                        ps.executeUpdate();
                    }
                    System.out.println("[MigracionesBD] Aplicada " + version + ".sql");
                }
            });
        } finally {
            temporal.close();
        }
    }

    /** Ejecuta los scripts R*.sql (repetibles) en cada arranque. */
    static void aplicarRepetibles(EntityManagerFactory emf) {
        conConexion(emf, conexion -> {
            for (int n = 1; ; n++) {
                String sql = leer("R" + n + ".sql");
                if (sql == null) {
                    break;
                }
                try (Statement st = conexion.createStatement()) {
                    st.execute(sql);
                }
            }
        });
    }

    private static boolean yaAplicada(Connection conexion, String version) throws SQLException {
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT 1 FROM schema_migraciones WHERE version = ?")) {
            ps.setString(1, version);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private interface Trabajo {
        void ejecutar(Connection conexion) throws SQLException;
    }

    /** Ejecuta el trabajo dentro de una transacción; si algo falla, deshace todo. */
    private static void conConexion(EntityManagerFactory emf, Trabajo trabajo) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.unwrap(Session.class).doWork(trabajo::ejecutar);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalStateException("Falló la migración de la base de datos", ex);
        } finally {
            em.close();
        }
    }

    private static String leer(String archivo) {
        try (InputStream in = MigracionesBD.class.getResourceAsStream(RUTA + archivo)) {
            return in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer la migración " + archivo, e);
        }
    }
}
