package org.trsfrm.file.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.trsfrm.file.FileParser;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSend;

@Service
@Qualifier("txtFile")
public class TxtFileParser extends FileParser {

	@Autowired
	TxtFileParser(IFileValidator txtFileValidatorService, IFileAttributeService fileAttributeService) {
		super(txtFileValidatorService, fileAttributeService);
	}

	@Override
	public Iterator<String> readBlock(FileSettingsToSend fileSetting) {

		String[] delimitor = fileAttributeService.getDelimitorRepositpry(fileSetting.getDiretoryPath());
		String lineAttributeDelimitor = delimitor != null && delimitor.length > 0 ? delimitor[0] : null;
		List listAttribute = fileAttributeService.getAttributes(fileSetting.getDiretoryPath());
		try {
			return new Iterator<String>() {
				List<String> lines = Files.lines(Paths.get(fileSetting.getFile().getPath()))
						.collect(Collectors.toList());

				Iterator<String> iterator = lines.iterator();
				String currentLine = null;
				Map<Integer, String> attributeIndex = extractAttributeIndexes(
						iterator.hasNext() ? iterator.next() : null, listAttribute, lineAttributeDelimitor);

				@Override
				public boolean hasNext() {
					// TODO Auto-generated method stub

					return iterator.hasNext();
				}

				@Override
				public String next() {
					if (attributeIndex == null)
						return null;
					if (iterator.hasNext())
						System.out.println("there is data " + attributeIndex.size());
					currentLine = iterator.next();
					return extractAttributesValFromLine(currentLine, attributeIndex, lineAttributeDelimitor);
				}
			};
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * file has form of : first line : attribute_name_1;attribute_name_2;
	 * attribute_name_2 next lines : sholud have same attribute order separated
	 * by comma : val1;val2;val3
	 */

	/**
	 * 
	 * @param firstLineFile
	 * @param listAttribute
	 * @return listIndexAttribute to extract
	 */
	Map extractAttributeIndexes(String firstLineFile, List<String> listAttribute, String delimitor) {

		if (firstLineFile == null)
			return null;
		Map<Integer, String> listIndex = new HashMap();
		String[] lineAttributesValue = firstLineFile.split(delimitor);
		int i = 0;
		for (String value : lineAttributesValue) {
			if (listAttribute.contains(value))
				listIndex.put(Integer.valueOf(i), value);
			i++;
		}
		;
		return listIndex;
	}

	/**
	 * 
	 * @param line
	 * @param indexToExtract
	 * @param delimitor
	 * @return extract all value of requested attribute on that line, with a
	 *         form of key1=value1, key2=value2
	 * 
	 */
	String extractAttributesValFromLine(String line, Map<Integer, String> indexToExtract, String delimitor) {
		if (line == null || line.length() == 0)
			return null;
		StringBuilder result = new StringBuilder();
		try{
		String[] values = line.split(delimitor);
		for (Entry<Integer, String> entry : indexToExtract.entrySet()) {
			if (result.length() == 0)
				result = result.append(entry.getValue() + "=" + values[entry.getKey()]);
			else
				result = result.append("," + entry.getValue() + "=" + values[entry.getKey()]);
		}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return result.toString();
	}
}
