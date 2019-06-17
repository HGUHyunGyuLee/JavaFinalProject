package edu.handong.csee.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CSVReadNotPossible extends Exception {
	ArrayList<String> ErrorOccuredFiles = new ArrayList<String>();

	public void addToErrorList(String name) {
		ErrorOccuredFiles.add(name);
	}
	public CSVReadNotPossible() {
		//empty constructor
	}

	public void WriteErrorOccuredFiles() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet();

		int rownum = 0;
		// int i=0;
		int cellnum = 0;
		for (String files : ErrorOccuredFiles) {
			Row row = sheet.createRow(rownum++);
			Cell cell = row.createCell(cellnum);
			cell.setCellValue(files);
		}

		try {
			FileOutputStream out = new FileOutputStream(new File("error.csv"));
			workbook.write(out);
			out.close();
			workbook.close();
			System.out.println("Error Occured Files written in error.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
