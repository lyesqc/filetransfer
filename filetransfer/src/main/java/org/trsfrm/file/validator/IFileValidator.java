package org.trsfrm.file.validator;

import org.trsfrm.model.FileSettingsToSend;

public interface IFileValidator {
	 boolean checkFileFormat(FileSettingsToSend fileSetting);

}
