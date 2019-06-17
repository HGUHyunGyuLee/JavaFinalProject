package edu.handong.csee.java.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.TreeMap;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.handong.csee.java.CSVReadNotPossible;

public class MyCSVParser implements Comparable<Object>{


	private String myFileName;
	private String zipFileName;
	private static String otherFileName;
	boolean removeHeader = false;
	boolean combineString = false;
	static boolean ErrorOccuredFileFlag = false;
	ArrayList<String> lines = new ArrayList<String>();


	public String getMyFileName() {
		return myFileName;
	}

	public void setMyFileName(String myFileName) {
		this.myFileName = myFileName;
	}
	static public String getOtherFileName() {
		return otherFileName;
	}

	public void setOtherFileName(String otherName) {
		this.otherFileName = otherName;
	}

	public MyCSVParser(String fileName) {
		myFileName = fileName;
		zipFileName = fileName.split("-")[0];
	}

	@Override
	public int compareTo(Object o) {
		String compareName = ((MyCSVParser) o).getZipFileName();
		int toBeCompared = Integer.parseInt(compareName);
		return Integer.parseInt(this.zipFileName)-toBeCompared;
	}

	public String getZipFileName() {
		return zipFileName;
	}

	public void ReadCSV() throws IOException {
		String line = "";
		try {
			FileInputStream file = new FileInputStream(new File(myFileName));

			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			String str = "";
			rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// Check the cell type and format accordingly
					switch (cell.getCellType()) {

					// case NUMERIC: System.out.print(cell.getNumericCellValue() + "\n"); break;

					case BLANK:
						str = " ";
						line = line + str + "//";
						break;
					case STRING:
						str = cell.getStringCellValue();

						line = line + str + "//";
						break;
					case ERROR:
						ErrorOccuredFileFlag = true;
						throw new CSVReadNotPossible();
					default:
						break;
					}
				}
				lines.add(line);
				line = "";
			}
			workbook.close();
			file.close();
		} catch (CSVReadNotPossible e) {
			e.addToErrorList(myFileName);
			if (ErrorOccuredFileFlag)
				e.WriteErrorOccuredFiles();
		}
	}



	public ArrayList<String> getLines() {
		return lines;
	}

	public void write(MyCSVParser parser2, String outputFile, boolean firstOutput)
			throws IOException, InvalidFormatException {
		FileOutputStream fileOut = null;

		if (firstOutput) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet();

			int num = 0;
			for (String line : lines) {
				Row row1 = sheet.createRow(num++);
				int cellnum1 = 0;

				for (int i = -1; i < line.split("//").length; i++) {
					Cell cell = row1.createCell(cellnum1++);

					if (i == -1)
						cell.setCellValue(zipFileName);
					else if (line.split("//")[i].equals(" ")) {
					
						cell.setBlank();
					} else {
						cell.setCellValue(line.split("//")[i]);
					}
				}
			}
			fileOut = new FileOutputStream(outputFile);
			workbook.write(fileOut);
			workbook.close();
		} else if (!firstOutput) {
			InputStream inp = new FileInputStream(outputFile);
			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheetAt(0);
			int num1 = sheet.getLastRowNum();
			int cellnum = sheet.getLeftCol();
			int j = 1;
			int num = num1;

			ArrayList<String> lines2 = parser2.getLines();
			for (String line : lines2) {
				Row row = sheet.createRow(++num);
				cellnum = 0;
				for (int i = -1; i < line.split("//").length; i++) {
					Cell cell = row.createCell(cellnum++);
					if (i == -1)
						cell.setCellValue(parser2.getZipFileName());
					else if (line.split("//")[i].equals(" "))
						cell.setBlank();
					else
						cell.setCellValue(line.split("//")[i]);
				}
			}
			fileOut = new FileOutputStream(outputFile);
			wb.write(fileOut);
		}
		fileOut.close();
	}

}
