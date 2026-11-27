package com.proyectofinal.veterinarialosarcos.services;

import com.proyectofinal.veterinarialosarcos.models.Paciente;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelService {

    public void generarReportePacientes(
            List<Paciente> pacientes
    ) throws Exception {

        Workbook workbook =
                new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet(
                        "Pacientes Registrados"
                );

        Row encabezado =
                sheet.createRow(0);

        encabezado.createCell(0)
                .setCellValue("Expediente");

        encabezado.createCell(1)
                .setCellValue("Paciente");

        encabezado.createCell(2)
                .setCellValue("Especie");

        encabezado.createCell(3)
                .setCellValue("Raza");

        encabezado.createCell(4)
                .setCellValue("Sexo");

        encabezado.createCell(5)
                .setCellValue("Propietario");

        encabezado.createCell(6)
                .setCellValue("Edad");

        int fila = 1;

        for (Paciente paciente : pacientes) {

            Row row =
                    sheet.createRow(fila);

            row.createCell(0)
                    .setCellValue(
                            paciente.getNumExpediente()
                    );

            row.createCell(1)
                    .setCellValue(
                            paciente.getNombre()
                    );

            row.createCell(2)
                    .setCellValue(
                            paciente.getEspecieTexto()
                    );

            row.createCell(3)
                    .setCellValue(
                            paciente.getRaza()
                    );

            row.createCell(4)
                    .setCellValue(
                            paciente.getSexoTexto()
                    );

            row.createCell(5)
                    .setCellValue(
                            paciente.getOwner()
                                    .getNombreOwner()
                    );

            row.createCell(6)
                    .setCellValue(
                            paciente.getEdadTexto()
                    );

            fila++;
        }

        Row total =
                sheet.createRow(fila + 2);

        total.createCell(0)
                .setCellValue(
                        "Total pacientes: "
                                + pacientes.size()
                );

        for (int i = 0; i < 7; i++) {

            sheet.autoSizeColumn(i);

        }

        FileOutputStream fos =
                new FileOutputStream(
                        "PacientesRegistrados.xlsx"
                );

        workbook.write(fos);

        fos.close();

        workbook.close();
    }
}