package org.trsfrm.file.validator;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.trsfrm.model.FileSettingsToSend;
import org.xml.sax.SAXException;

public class XmlFileValidatorService extends FileValidator {

	@Override
	public boolean checkFileFormat(FileSettingsToSend fileSetting) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(FileValidator.listSchema.get(fileSetting.getDiretoryPath()));
			Validator validator = schema.newValidator();
			validator.validate(
					new StreamSource(new File(fileSetting.getFile().getAbsoluteFile().toString())));
		} catch (IOException | SAXException e) {
			System.out.println("Exception: in validating XML " + e.getMessage());
			return false;
		}
		return true;
	}
}
