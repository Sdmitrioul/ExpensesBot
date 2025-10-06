package com.dskroba.base.formatter;

import com.dskroba.base.exception.CustomException;
import com.dskroba.base.formatter.extractor.Extractor;
import com.dskroba.base.formatter.extractor.type.*;
import com.dskroba.type.ContentType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ExcelFormatter implements Formatter {
    @Override
    public ContentType contentType() {
        return ContentType.XLSX;
    }

    @Override
    public <T> byte[] formatOutput(List<FieldSelector<T>> fieldSelectors, List<T> values) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expenses");

            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fieldSelectors.size(); i++) {
                var selector = fieldSelectors.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(selector.name());
                cell.setCellStyle(headerStyle);
            }

            int rowCounter = 1;
            for (T value : values) {
                Row row = sheet.createRow(rowCounter++);

                for (int colIdx = 0; colIdx < fieldSelectors.size(); colIdx++) {
                    Cell cell = row.createCell(colIdx);
                    FieldSelector<T> selector = fieldSelectors.get(colIdx);
                    Extractor<T, ? extends TypeValue<?>> extractor = selector.valueExtractor();

                    setCellValue(cell, extractor, value, dateStyle);
                }
            }

            for (int i = 0; i < fieldSelectors.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CustomException("Problem closing excel worksheet.", e);
        }
    }

    private <T> void setCellValue(Cell cell, Extractor<T, ? extends TypeValue<?>> extractor, T entity, CellStyle dateStyle) {
        TypeValue<?> typeValue = extractor.extract(entity);
        switch (typeValue) {
            case StringValue(String value) -> setCellValue(cell, value);
            case IntegerValue(Integer value) -> setCellValue(cell, value);
            case LongValue(Long value) -> setCellValue(cell, value);
            case DoubleValue(Double value) -> setCellValue(cell, value);
            case BooleanValue(Boolean value) -> setCellValue(cell, value);
            case DateValue(Date date) -> setCellValue(cell, date, dateStyle);
            case DateTimeValue(LocalDateTime value) -> setCellValue(cell, value, dateStyle);
        }
    }

    private void setCellValue(Cell cell, String value) {
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setBlank();
        }
    }

    private void setCellValue(Cell cell, Integer value) {
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setBlank();
        }
    }

    private void setCellValue(Cell cell, Long value) {
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setBlank();
        }
    }

    private void setCellValue(Cell cell, Double value) {
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setBlank();
        }
    }

    private void setCellValue(Cell cell, Boolean value) {
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setBlank();
        }
    }

    private void setCellValue(Cell cell, Date value, CellStyle dateStyle) {
        if (value != null) {
            cell.setCellValue(value);
            cell.setCellStyle(dateStyle);
        } else {
            cell.setBlank();
        }
    }

    private void setCellValue(Cell cell, LocalDateTime value, CellStyle dateStyle) {
        if (value != null) {
            cell.setCellValue(value);
            cell.setCellStyle(dateStyle);
        } else {
            cell.setBlank();
        }
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
