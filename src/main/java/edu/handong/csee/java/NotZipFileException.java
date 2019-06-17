package edu.handong.csee.java;

public class NotZipFileException extends Exception {
	
	public NotZipFileException(){
		super("File is not in zip File format!");
	} 
	public NotZipFileException(String message){
		super(message);
	} 

}
