package org.trsfrm.model;

import java.io.File;

import org.trsfrm.file.FileParser;

import lombok.Data;

@Data
public class FileSettingsToSend {

	private String diretoryPath;
	private File file;
	private FileParser fileParser;
	
	public FileSettingsToSend(String  diretoryPath, File file, FileParser fileParser){
		this.diretoryPath = diretoryPath;
		this.file = file;
		this.fileParser = fileParser;
	}
}
