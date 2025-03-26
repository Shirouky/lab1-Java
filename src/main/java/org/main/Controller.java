package org.main;

import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.data.Results;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Controller {
    Results results = new Results();
    HashMap<String, ArrayList<Double>> data = new HashMap<>();

    public HashMap<String, HashMap<String, Double>> calculate() {
        this.results.calculate(this.data);
        return this.results.export();
    }

    public void importData(String path, String sheetName) throws IOException, ParseException, IllegalArgumentException, OLE2NotOfficeXmlFileException, NotOfficeXmlFileException, NullPointerException {
        FileInputStream file = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheet(sheetName);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                for (Cell cell : row) {
                    this.data.put(cell.toString(), new ArrayList<>(sheet.getPhysicalNumberOfRows() - 1));

                }
            } else {
                int index = 0;
                for (String key : this.data.keySet()) {
                    var cell = row.getCell(index);
                    if (cell.getCellType() == CellType.FORMULA) {
                        cell.removeFormula();
                    }
                    this.data.get(sheet.getRow(0).getCell(index).toString()).add(Double.parseDouble(cell.toString()));
                    index++;
                }
            }
        }

        workbook.close();
    }

    public void export() throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Most statistics");
        Sheet covarianceSheet = workbook.createSheet("Covariance Coefficients");
        Sheet intervalSheet = workbook.createSheet("Confidence Intervals");

        var stats = this.results.export();

        Row row = sheet.createRow(0);
        var columns = this.results.getColumns();
        var index = 1;
        for (String column : columns) {
            Cell cell = row.createCell(index);
            cell.setCellValue(column);
            index++;
        }

        index = 1;
        for (String key : stats.keySet()) {
            row = sheet.createRow(index);
            Cell cell = row.createCell(0);
            cell.setCellValue(key);
            var i = 1;
            for (String column : columns) {
                cell = row.createCell(i);
                cell.setCellValue(stats.get(key).get(column));
                i++;
            }
            index++;
        }

        var row0 = covarianceSheet.createRow(0);
        columns = this.results.getColumns();
        index = 1;
        var covariance = this.results.exportCovariance();
        for (String column : columns) {
            Cell cell = row0.createCell(index);
            cell.setCellValue(column);

            row = covarianceSheet.createRow(index);
            cell = row.createCell(0);
            cell.setCellValue(column);
            int j = 1;
            for (String c : columns) {
                cell = row.createCell(j);
                cell.setCellValue(covariance.get(column + c));
                j++;
            }
            index++;
        }

        row0 = intervalSheet.createRow(0);
        Cell cell = row0.createCell(1);
        cell.setCellValue("Start");
        cell = row0.createCell(2);
        cell.setCellValue("End");
        index = 1;
        var intervals = this.results.exportInterval();
        for (String key : intervals.keySet()) {
            row = intervalSheet.createRow(index);
            cell = row.createCell(0);
            cell.setCellValue(key);
            cell = row.createCell(1);
            cell.setCellValue(intervals.get(key)[0]);
            cell = row.createCell(2);
            cell.setCellValue(intervals.get(key)[1]);
            index++;
        }

        FileOutputStream out = new FileOutputStream("D:\\МИФИ\\4 семестр\\Теория и технология программирования\\lab1\\Saved results.xlsx");
        workbook.write(out);
        out.close();


        workbook.close();
    }
}
