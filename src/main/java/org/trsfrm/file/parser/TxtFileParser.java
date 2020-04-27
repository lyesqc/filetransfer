package org.trsfrm.file.parser;

import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.trsfrm.file.FileParser;
import org.trsfrm.file.attribute.FileAttributeService;
import org.trsfrm.file.validator.FileValidator;
import org.trsfrm.model.FileSettingsToSend;

@Service
@Qualifier("txtFile")
public final class TxtFileParser extends FileParser{
	
	@Autowired
	FileAttributeService FileAttributeFilter;
	
	@Autowired
	TxtFileParser(FileValidator txtFileValidatorService, FileAttributeService fileAttributeService){
		super(txtFileValidatorService, fileAttributeService);
	}
	
	@Override 
	public Iterator<String> readBlock(FileSettingsToSend fileSetting){
		 return null;
	 }
	
	

}
