package org.trsfrm.file.parser;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSend;

public class XmlFileParserTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Mock
	IFileValidator xmlFileValidatorService;
	@Mock
	IFileAttributeService fileAttributeService;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void ArrayDataAttributeTest() {
		try {
			File file = folder.newFile("data.xml");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("<data><a><b1>b</b1><b1>c</b1></a></data>");
			buffer.flush();

			XmlFileParser xmlFileParserMock = new XmlFileParser(xmlFileValidatorService, fileAttributeService);
			XmlFileParser xmlFileParser = Mockito.spy(xmlFileParserMock);
			when(xmlFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("b1", "c1"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[] { "a" });
			doReturn(true).when(xmlFileParser).movFile(anyString(), anyObject());
			Iterator<String> iterator = xmlFileParser.loadFile(new FileSettingsToSend(null, file, null));

			assertNotNull(iterator);
			assertEquals(iterator.next(), "b1=b,b1=c");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void EmptyDataTest() {
		try {
			File file = folder.newFile("data.xml");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("<data></data>");
			buffer.flush();
			XmlFileParser xmlFileParserMock = new XmlFileParser(xmlFileValidatorService, fileAttributeService);
			XmlFileParser xmlFileParser = Mockito.spy(xmlFileParserMock);
			when(xmlFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("b1", "c1"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[] { "a" });
			doReturn(true).when(xmlFileParser).movFile(anyString(), anyObject());
			Iterator<String> iterator = xmlFileParser.loadFile(new FileSettingsToSend(null, file, null));

			assertNotNull(iterator);
			assertFalse(iterator.hasNext());
			// assertEquals(iterator.next(), "b1=b,b1=c");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void ArrayDataTagTest() {
		try {
			File file = folder.newFile("data.xml");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("<data><a><b1><b2>b</b2></b1><b1><b2>c</b2></b1></a></data>");
			buffer.flush();

			XmlFileParser xmlFileParserMock = new XmlFileParser(xmlFileValidatorService, fileAttributeService);
			XmlFileParser xmlFileParser = Mockito.spy(xmlFileParserMock);
			when(xmlFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("b2", "c1"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[] { "b1" });
			doReturn(true).when(xmlFileParser).movFile(anyString(), anyObject());
			Iterator<String> iterator = xmlFileParser.loadFile(new FileSettingsToSend(null, file, null));

			assertNotNull(iterator);
			assertEquals(iterator.next(), "b2=b");

			assertNotNull(iterator);
			assertEquals(iterator.next(), "b2=c");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void wrongDataTagTest() {
		try {
			File file = folder.newFile("data.xml");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("<data><a><b1><b2>b</b2></b1><b1><b2>c</b2></b1></a></data>");
			buffer.flush();

			XmlFileParser xmlFileParserMock = new XmlFileParser(xmlFileValidatorService, fileAttributeService);
			XmlFileParser xmlFileParser = Mockito.spy(xmlFileParserMock);
			when(xmlFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("b3", "c1"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[] { "cc" });
			doReturn(true).when(xmlFileParser).movFile(anyString(), anyObject());
			Iterator<String> iterator = xmlFileParser.loadFile(new FileSettingsToSend(null, file, null));

			assertNotNull(iterator);
			assertFalse(iterator.hasNext());
			//assertEquals(iterator.next(), "b2=b");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void initTest() {
		MockitoAnnotations.initMocks(this);
	}

}
