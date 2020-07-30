package org.trsfrm.model;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.File;

import org.mockito.internal.matchers.Equals;
import org.trsfrm.file.FileParser;

import lombok.Data;

@Data
public class FileSettingsToSend {

	private String diretoryPath;
	private File file;
	private FileParser fileParser;

	public FileSettingsToSend(String diretoryPath, File file, FileParser fileParser) {
		this.diretoryPath = diretoryPath;
		this.file = file;
		this.fileParser = fileParser;
	}

	@Override
	public boolean equals(Object setting) {
		if (!(setting instanceof FileSettingsToSend))
			return false;
		FileSettingsToSend fileSetting = (FileSettingsToSend) setting;
		System.out.println("Compare File "+file.getName()+" with : "+fileSetting.getFile().getName() +"--> "+file.getName().equals(fileSetting.getFile().getName()));
		System.out.println("Compare Directory "+fileSetting.getDiretoryPath()+" with : "+diretoryPath+" -->"+fileSetting.getDiretoryPath().equals(diretoryPath));
		return (fileSetting.getDiretoryPath().equals(diretoryPath) && file.getName().equals(fileSetting.getFile().getName()));
	}
}
