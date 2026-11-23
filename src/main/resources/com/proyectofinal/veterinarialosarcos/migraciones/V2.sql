-- V2: el perfil usa las columnas existentes "nombres" y "apellidos".
-- Se eliminan las columnas duplicadas "nombre" y "apellido" que Hibernate llegó a
-- crear, solo si existen y están vacías (para no perder datos).
DO $$
DECLARE
    col text;
    con_datos boolean;
BEGIN
    FOREACH col IN ARRAY ARRAY['nombre', 'apellido'] LOOP
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'usuarios' AND column_name = col)
           AND EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'usuarios' AND column_name = col || 's') THEN
            EXECUTE format('SELECT EXISTS (SELECT 1 FROM usuarios WHERE %I IS NOT NULL)', col)
                INTO con_datos;
            IF NOT con_datos THEN
                EXECUTE format('ALTER TABLE usuarios DROP COLUMN %I', col);
            END IF;
        END IF;
    END LOOP;
END $$;
