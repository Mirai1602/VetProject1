package com.proyectofinal.veterinarialosarcos.utils;

import com.proyectofinal.veterinarialosarcos.models.Consulta;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Genera reportes PDF y Excel (.xlsx) con JasperReports.
 *
 * <p>Las plantillas (.jrxml) están en {@code resources/.../reportes/} y se pueden editar
 * con Jaspersoft Studio. Los datos se pasan como mapas de texto ya formateado, así la
 * plantilla no depende de las entidades JPA.</p>
 */
public final class ReporteService {

    private static final String HISTORIAL = "/com/proyectofinal/veterinarialosarcos/reportes/historial_consultas.jrxml";
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** La plantilla se compila una sola vez (la primera exportación tarda un poco más). */
    private static JasperReport historialCompilado;

    private ReporteService() {
    }

    /** Exporta el historial de consultas del paciente a un archivo PDF. */
    public static void exportarHistorialPdf(String negocio, Paciente paciente, List<Consulta> consultas, File destino)
            throws JRException {
        JasperPrint impreso = llenarHistorial(negocio, paciente, consultas, false);
        JasperExportManager.exportReportToPdfFile(impreso, destino.getAbsolutePath());
    }

    /**
     * Exporta el historial de consultas a Excel (.xlsx) con la misma plantilla del PDF.
     * Se ignora la paginación para que todas las consultas queden en una sola hoja.
     */
    public static void exportarHistorialExcel(String negocio, Paciente paciente, List<Consulta> consultas, File destino)
            throws JRException {
        JasperPrint impreso = llenarHistorial(negocio, paciente, consultas, true);

        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setOnePagePerSheet(false);
        config.setDetectCellType(true);
        config.setRemoveEmptySpaceBetweenRows(true);
        config.setRemoveEmptySpaceBetweenColumns(true);
        config.setWhitePageBackground(false);
        config.setCollapseRowSpan(false);
        config.setSheetNames(new String[]{"Expediente"});

        JRXlsxExporter exportador = new JRXlsxExporter();
        exportador.setExporterInput(new SimpleExporterInput(impreso));
        exportador.setExporterOutput(new SimpleOutputStreamExporterOutput(destino));
        exportador.setConfiguration(config);
        exportador.exportReport();
    }

    private static JasperPrint llenarHistorial(String negocio, Paciente paciente, List<Consulta> consultas,
                                               boolean sinPaginacion) throws JRException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("NEGOCIO", texto(negocio));
        parametros.put("NUM_EXPEDIENTE", texto(paciente.getNumExpediente()));
        parametros.put("PACIENTE", texto(paciente.getNombre()));
        parametros.put("ESPECIE", paciente.getEspecieTexto());
        parametros.put("RAZA", texto(paciente.getRaza()));
        parametros.put("SEXO", paciente.getSexoTexto());
        parametros.put("EDAD", paciente.getEdadTexto());
        parametros.put("PROPIETARIO", paciente.getOwner() == null ? "—" : texto(paciente.getOwner().getNombreOwner()));
        parametros.put("TELEFONO", paciente.getOwner() == null ? "—" : texto(paciente.getOwner().getTelefono()));
        parametros.put("FECHA_EMISION", LocalDate.now().format(FECHA));

        Collection<Map<String, ?>> filas = new ArrayList<>();
        for (Consulta c : consultas) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", c.getFecha() == null ? "" : c.getFecha().format(FECHA));
            fila.put("motivo", texto(c.getMotivoConsulta()));
            fila.put("temperatura", c.getTemperatura() == null ? "—" : numero(c.getTemperatura()) + " °C");
            fila.put("peso", c.getPeso() == null ? "—" : numero(c.getPeso()) + " kg");
            fila.put("comentarios", texto(c.getComentarios()));
            filas.add(fila);
        }

        parametros.put(JRParameter.IS_IGNORE_PAGINATION, sinPaginacion);
        return JasperFillManager.fillReport(historial(), parametros, new JRMapCollectionDataSource(filas));
    }

    private static synchronized JasperReport historial() throws JRException {
        if (historialCompilado == null) {
            try (InputStream plantilla = ReporteService.class.getResourceAsStream(HISTORIAL)) {
                if (plantilla == null) {
                    throw new JRException("No se encontró la plantilla " + HISTORIAL);
                }
                historialCompilado = JasperCompileManager.compileReport(plantilla);
            } catch (IOException ex) {
                throw new JRException(ex);
            }
        }
        return historialCompilado;
    }

    /** 38.0 → "38"; 38.5 → "38.5". */
    private static String numero(Double n) {
        return n % 1 == 0 ? String.valueOf(n.longValue()) : String.valueOf(n);
    }

    private static String texto(String s) {
        return s == null || s.isBlank() ? "—" : s.strip();
    }
}
