package utility;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Reads test data from the Excel file set in config.properties (testDataFile).
 * Row 1 of every sheet is the header and is skipped.
 */
public class ExcelUtility {

	/** Reads one cell, same idea as the original getExceldata(row, cell). */
	public static String getCellData(String sheetName, int rowNum, int cellNum) {
		try (FileInputStream file = new FileInputStream(ConfigReader.get("testDataFile"));
				Workbook workbook = WorkbookFactory.create(file)) {

			Sheet sheet = getSheet(workbook, sheetName);
			return new DataFormatter().formatCellValue(sheet.getRow(rowNum).getCell(cellNum));

		} catch (IOException e) {
			throw new RuntimeException("Could not read the test data file", e);
		}
	}

	/**
	 * Reads a whole sheet into a 2D array for a TestNG @DataProvider.
	 * Every value is read as text, and empty cells come back as "".
	 */
	public static Object[][] getSheetData(String sheetName) {
		try (FileInputStream file = new FileInputStream(ConfigReader.get("testDataFile"));
				Workbook workbook = WorkbookFactory.create(file)) {

			Sheet sheet = getSheet(workbook, sheetName);
			DataFormatter formatter = new DataFormatter();

			int rowCount = sheet.getLastRowNum();
			int columnCount = sheet.getRow(0).getLastCellNum();
			Object[][] data = new Object[rowCount][columnCount];

			for (int i = 1; i <= rowCount; i++) {
				Row row = sheet.getRow(i);
				for (int j = 0; j < columnCount; j++) {
					data[i - 1][j] = (row == null) ? "" : formatter.formatCellValue(row.getCell(j));
				}
			}
			return data;

		} catch (IOException e) {
			throw new RuntimeException("Could not read the test data file", e);
		}
	}

	private static Sheet getSheet(Workbook workbook, String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			throw new RuntimeException("Sheet not found in test data file: " + sheetName);
		}
		return sheet;
	}

}
