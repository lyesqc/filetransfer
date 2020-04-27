package org.trsfrm.file.parser;

import java.io.File;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.trsfrm.file.FileParser;
import org.trsfrm.file.attribute.FileAttributeService;
import org.trsfrm.file.validator.FileValidator;
import org.trsfrm.model.FileSettingsToSend;



@Service
@Qualifier("xmlFile")
public final class XmlFileParser extends FileParser{
	
	@Autowired
	public XmlFileParser(FileValidator xmlFileValidatorService, FileAttributeService fileAttributeService){
		super(xmlFileValidatorService,fileAttributeService);
	}
	
	@Override 
	public Iterator<String> readBlock(FileSettingsToSend fileSetting){
		 return null;
	 }
	
	

}
