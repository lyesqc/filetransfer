package org.trsfrm.file.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.trsfrm.file.FileParser;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSend;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Service
@Qualifier("xmlFile")
public class XmlFileParser extends FileParser {

	@Autowired
	public XmlFileParser(IFileValidator xmlFileValidatorService, IFileAttributeService fileAttributeService) {
		super(xmlFileValidatorService, fileAttributeService);
	}

	@Override
	public Iterator<String> readBlock(FileSettingsToSend fileSetting) {

		if (fileSetting == null)
			throw new NullPointerException();
		List listNodeValue = new ArrayList<String>();
		SAXParser saxParser;
		List<String> listAttribute = fileAttributeService.getAttributes(fileSetting.getDiretoryPath());
		String[] blocDelimitor = fileAttributeService.getDelimitorRepositpry(fileSetting.getDiretoryPath());
		String nodeName = blocDelimitor[0];
		SAXParserFactory factory = SAXParserFactory.newInstance();
		DefaultHandler handler = new NodeHandler(listNodeValue, nodeName, listAttribute);
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(fileSetting.getFile(), handler);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println(listNodeValue.toString());

		return new Iterator<String>() {

			Iterator<String> iterator = listNodeValue.iterator();

			@Override
			public boolean hasNext() {

				return iterator.hasNext();
			}

			@Override
			public String next() {
				// TODO Auto-generated method stub
				return iterator.next();
			}
		};
	}

	/**
	 * 
	 * @author Lyes.bensaada class which parse the xml file and extact a list of
	 *         attribute as key vlaue string.
	 */
	class NodeHandler extends DefaultHandler {
		List<String> listNode;
		List<String> listAttributes;
		String nodeName;
		StringBuilder resultNode = null;
		boolean read;

		public NodeHandler(List<String> listNodeValue, String nodeName, List<String> listAttributes) {
			this.listNode = listNodeValue;
			this.nodeName = nodeName;
			this.listAttributes = listAttributes;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {

			if (qName.equalsIgnoreCase(nodeName))
				resultNode = null;
			listAttributes.stream().forEach(e -> {
				if (e.equalsIgnoreCase(qName)) {
					read = true;
					if (resultNode != null)
						resultNode = resultNode.append("," + e + "=");
					else
						resultNode = new StringBuilder(e + "=");
				}
			});

		}

		public void endElement(String uri, String localName, String qName) throws SAXException {

			if (qName.equalsIgnoreCase(nodeName)) {
				if(resultNode != null)
				listNode.add(resultNode.toString());
				resultNode = null;
			}
			if (read)
				read = false;

		}

		public void characters(char ch[], int start, int length) throws SAXException {

			if (read) {

				resultNode = resultNode.append(new String(ch, start, length));
				read = false;
			}
		}

	}

}
