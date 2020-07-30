package org.trsfrm.file.validator;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;
import org.trsfrm.model.FileSettingsToSend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonFileValidatorService extends FileValidator {

	@Override
	public boolean checkFileFormat(FileSettingsToSend fileSetting) {
		JSONObject jsonSchema = null;
		ObjectMapper mapper = new ObjectMapper();
		Logger log = Logger.getLogger(this.getClass());
		
		log.info("Validate Json File Type");

		try (InputStream in = new FileInputStream(FileValidator.listSchema.get(fileSetting.getDiretoryPath()))) {

			jsonSchema = new JSONObject(new JSONTokener(in));
			Schema schema = SchemaLoader.load(jsonSchema);
			JsonNode node = mapper.readTree(fileSetting.getFile());
			System.out.println("node in validate " + node.toString());
			schema.validate(new JSONObject(node.toString()));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {

		}
		return true;
	}

}
