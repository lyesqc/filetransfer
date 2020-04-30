package org.trsfrm.file;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.trsfrm.file.attribute.FileAttributeService;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.FileValidator;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSend;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class FileParser {

	/**
	 * move file to staged area, and check if format is valid if not, move it to
	 * errors area start read file and return the iterator of block file
	 * 
	 * @return
	 */
	protected FileParser(IFileValidator jsonFileValidatorService, IFileAttributeService fileAttributeService2) {
		this.fileValidator = jsonFileValidatorService;
		this.fileAttributeService = fileAttributeService2;
	}

	protected IFileValidator fileValidator;
	protected IFileAttributeService fileAttributeService;

	public Iterator<String> loadFile(FileSettingsToSend fileSetting) {

		System.out.println("file before staged");
		// try

		if (!movFile("staged", fileSetting))
			return null;
		// System.out.println("file after move
		// "+fileSetting.getFile().getAbsolutePath());
		boolean isValidFormatFile = fileValidator.checkFileFormat(fileSetting);
		if (!isValidFormatFile) {
			System.out.println("file format is not valid");
			return null;
		}
		try {

			return readBlock(fileSetting);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (e instanceof NullPointerException)
				throw new NullPointerException();
		} finally {

		}

		return null;
	}

	public abstract Iterator<String> readBlock(FileSettingsToSend fileSettingsToSend);

	public boolean movFile(String destPath, FileSettingsToSend fileSetting) {
		boolean moveResult;
		try
		{
			String direstoryPath = fileSetting.getDiretoryPath();
			File file = fileSetting.getFile();
			File destFile = new File(direstoryPath + "\\" + destPath + "\\" + file.getName());

			moveResult = file.renameTo(destFile.getAbsoluteFile());
			if (!moveResult) {
				System.out.println(
						"move is " + moveResult + " " + file.getAbsolutePath() + "=>" + destFile.getAbsoluteFile());
				return false;
			}
			fileSetting.setFile(destFile);
		}
		catch(Exception e){ return false; }
		
		return moveResult;

	}
}
