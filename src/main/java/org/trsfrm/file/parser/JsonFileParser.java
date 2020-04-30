package org.trsfrm.file.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.trsfrm.file.FileParser;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSend;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("jsonFile")
@Qualifier("jsonFile")
public class JsonFileParser extends FileParser {

	public final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public JsonFileParser(IFileValidator jsonFileValidatorService, IFileAttributeService fileAttributeService) {
		super(jsonFileValidatorService, fileAttributeService);
	}

	/**
	 * load a json file and load all requested  object node
	 * @param fileSetting : file descriptor 
	 * @return : iterator of object node wich contain attributes list
	 */
	@Override
	public Iterator<String> readBlock(FileSettingsToSend fileSetting) {
		ObjectMapper mapper = new ObjectMapper();
		List<JsonNode> listNodeToParse = new ArrayList<>();
		if (fileSetting == null)
			throw new NullPointerException();
		List<String> listAttribute = fileAttributeService.getAttributes(fileSetting.getDiretoryPath());
		String[] blocDelimitor = fileAttributeService.getDelimitorRepositpry(fileSetting.getDiretoryPath());
		JsonNode root = null;
		try {
			root = mapper.readTree(fileSetting.getFile());

		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		if (root == null)
			return null;
		getListNodeObject(root, blocDelimitor, listNodeToParse);
		if (listNodeToParse == null || listNodeToParse.size() == 0)
			return null;
		Iterator<JsonNode> nodeIterator = listNodeToParse.iterator();

		return new Iterator<String>() {

			@Override
			public boolean hasNext() {

				return nodeIterator.hasNext();
			}

			@Override
			public String next() {
				String res = null;
				JsonNode node = nodeIterator.next();
				/** get all attributes value in same result string, separated by comma **/
				if (node.isObject())
					res = listAttribute.stream().map(e -> extractAttributeValues(node, e)).filter(e -> e != null)
							.collect(Collectors.joining(","));
				return res;
			}

		};

	}

	/**
	 * parse node and extract the attribute value, 
	 * @param node : node from which we extract attribute value
	 * @param attribute : the attribute to look for
	 * @return : is key  value form of : attributeName=attributeValue
	 */
	private String extractAttributeValues(JsonNode node, String attribute) {
		{
			String result = null;
			JsonNode valueNode;

			/** we have two case :  
			 *  1.node contain an array of value, of for [val1,val2,val3]
			 *  2. node contain one occurence attribute value on form key:value
			 * **/
			if (node.get(attribute) != null && node.get(attribute).isArray()) {
				/*
				if (node.get(attribute) == null)
					return null;
				*/
				System.out.println(node.toString());
				Iterator<JsonNode> valueArray = node.get(attribute).elements();
				while (valueArray.hasNext()) {
					valueNode = valueArray.next();
					System.out.println(valueNode.toString());
					if (valueNode.isValueNode()) {
						if (result != null)
							result = result + "," + attribute + "=" + valueNode.asText();
						else
							result = attribute + "=" + valueNode.asText();
					} 
				}
			}
			if (node.get(attribute) != null && node.get(attribute).isValueNode()) {
				result = attribute + "=" + node.get(attribute).asText();
			}
			return result;
		}
	}

	/**
	 * it is recursive method,get all object of tree, stop on last object node
	 * each returned object will contain a set of requested attributes
	 * @param node : node to check if it is researched node or not, if yes add it to list, else call method to its child
	 * @param delimitor : a list of node tree to parse
	 * @param listNode : list which contain our result node
	 */
	public void getListNodeObject(JsonNode node, String[] delimitor, List<JsonNode> listNode) {
		if (node == null)
			return;
		if (delimitor.length == 0) {
			if (node.isArray())
				for (JsonNode child : node)
					listNode.add(child);
			else
				listNode.add(node);
			return;
		}
		if (node.isObject()) {
			getListNodeObject(node.path(delimitor[0]), Arrays.copyOfRange(delimitor, 1, delimitor.length), listNode);
		}
		if (node.isArray()) {
			for (JsonNode nodeChild : node)
				getListNodeObject(nodeChild.path(delimitor[0]), Arrays.copyOfRange(delimitor, 1, delimitor.length),
						listNode);
		}

	}
}