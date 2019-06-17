package edu.handong.csee.java.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzip {


	private String zipFileName = null;
	private static int number = 0;

	ArrayList<String> unzippedFiles = new ArrayList<String>();

	public Unzip( String zipFileName) {
		this.zipFileName = zipFileName;

	}

	public Unzip() {
		// TODO Auto-generated constructor stub
	}

	synchronized public void unzip() {
		String zipFilePath = this.zipFileName;
		try {
			System.out.println("zipFilePath = " + zipFilePath);
			String[] tokens = zipFilePath.split("/");
			String zipFileToken = tokens[tokens.length-1];
			String studentName = zipFileToken.split(".zip")[0];
	
			ZipFile zipFile = null;
			// if (!zipFilePath.endsWith("1.zip")) {
			zipFile = new ZipFile(zipFilePath, Charset.forName("ms949"));// "ms949","ISO-8859-1","UTF-8"
			// }
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					System.out.print("dir  : " + entry.getName());
					String destPath = entry.getName();
					System.out.println(" => " + destPath);
					File file = new File(destPath);
					file.mkdirs();
					// continue;
				} else if (!entry.isDirectory() && !entry.getName().contains("/")) {
					String newName = studentName + "-" + Integer.toString(++number) + ".xlsx";
					String destPath = newName;

					try (InputStream inputStream = zipFile.getInputStream(entry);
							FileOutputStream outputStream = new FileOutputStream(destPath);) {
						int data = inputStream.read();
						while (data != -1) {
							outputStream.write(data);
							data = inputStream.read();
						}
					}

					if (!entry.getName().contains("/"))
						unzippedFiles.add(newName);
					System.out.println("file : " + entry.getName() + " => " + destPath);
				}
			}
			zipFile.close();
		} catch (IOException e) {
			throw new RuntimeException("Error unzipping file " + zipFilePath, e); 
		}

	}

	public ArrayList<String> getUnzippedFiles() {
		return unzippedFiles;
	}
}