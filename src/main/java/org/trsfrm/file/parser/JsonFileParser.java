package org.trsfrm.file.parser;

import java.io.IOException;
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

	@Override
	public Iterator<String> readBlock(FileSettingsToSend fileSetting) {
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("inside readBlock json implementation");
		if (fileSetting == null)
			throw new NullPointerException();
		List<String> listAttribute = fileAttributeService.getAttributes(fileSetting.getDiretoryPath());
		String[] blocDelimitor = fileAttributeService.getDelimitorRepositpry(fileSetting.getDiretoryPath());
		System.out.println("after validator "+blocDelimitor);

		JsonNode root = null;
		try {
			root = mapper.readTree(fileSetting.getFile());

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		if (root == null)
			return null;
		Iterator<JsonNode> nodeIterator;
		if (root.elements().next().isArray())
			nodeIterator = root.elements().next().elements();
		else
			nodeIterator = root.elements();

		return new Iterator<String>() {
			JsonNode node;

			@Override
			public boolean hasNext() {

				return nodeIterator.hasNext();
			}

			@Override
			public String next() {
				int i = 0;
				node = nodeIterator.next();
				System.out.println("after nex " + node.toString());
				if(blocDelimitor != null  && blocDelimitor.length >0)
				Arrays.stream(blocDelimitor).forEach(e -> {
					node = node.path(e);
					System.out.println("inside next " + node.toString());
				});
				String result = null;
				System.out.println("find : "+node.toString()+" list Attr :"+listAttribute.toString());
				if (node.isArray()) {
					System.out.println("It is Array "+node.toString());
					for (JsonNode node1 : node) {

						if (i != 0)
						{ if(node1.isValueNode()) result = result+","+listAttribute.get(0)+"="+node1.toString();
						else
							result = result + "," + listAttribute.stream().map(e -> e + "=" + ((JsonNode)node1.get(e)).textValue())
									.collect(Collectors.joining(","));
						}
							else{
							    if(node1.isValueNode()) result = listAttribute.get(0)+"="+node1.toString();
							    else
								result = listAttribute.stream().map(e -> e + "=" +  ((JsonNode)node1.get(e)).textValue())
									.collect(Collectors.joining(","));
							}
							i++;
					}
				} else
					if(node.isObject()){
					result = listAttribute.stream().map(e -> e + "=" + ((JsonNode)node.get(e)).textValue() ).collect(Collectors.joining(","));
					}else if(node.isValueNode()){
						result  = listAttribute.get(0)+"="+node.toString();
					}
				System.out.println("return "+result);
				return result;
			}

		};

	}

	public void getDeepTreeWay(JsonNode node, String[] delimitor, List<JsonNode> listNode){
		if(node == null) return;
		System.out.println(node.toString()+" :Delimitor "+delimitor.length);
		if(delimitor.length==0) {listNode.add(node); System.out.println("adding node "+node.toString());return;}
		if(node.isObject()) {System.out.println("is object "+node.toString());getDeepTreeWay(node.path(delimitor[0]),Arrays.copyOfRange(delimitor,1,delimitor.length), listNode);}
		if(node.isArray()){
			//if(delimitor.length>=2)
			for(JsonNode nodeChild : node) getDeepTreeWay(nodeChild.path(delimitor[0]), Arrays.copyOfRange(delimitor,1,delimitor.length), listNode);
		}
		
	}
}