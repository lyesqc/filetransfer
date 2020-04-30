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

public class TxtFileParserTest {
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Mock
	IFileValidator xmlFileValidatorService;
	@Mock
	IFileAttributeService fileAttributeService;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void standardDataTagTest() {
		try {
			File file = folder.newFile("data.txt");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("atr1;atr2;atr3");
			buffer.newLine();
			buffer.append("val1;val2;val2");
			buffer.flush();

			TxtFileParser txtFileParserMock = new TxtFileParser(xmlFileValidatorService, fileAttributeService);
			TxtFileParser xmlFileParser = Mockito.spy(txtFileParserMock);
			when(xmlFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("atr1", "atr2"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[] { ";" });
			doReturn(true).when(xmlFileParser).movFile(anyString(), anyObject());
			Iterator<String> iterator = xmlFileParser.loadFile(new FileSettingsToSend(null, file, null));

			assertNotNull(iterator);
			assertEquals(iterator.next(), "atr1=val1,atr2=val2");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void emptyDataTest() {
		try {
			File file = folder.newFile("data.txt");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			//buffer.append("atr1;atr2;atr3");
			//buffer.newLine();
			buffer.append("");
			buffer.flush();

			TxtFileParser txtFileParserMock = new TxtFileParser(xmlFileValidatorService, fileAttributeService);
			TxtFileParser xmlFileParser = Mockito.spy(txtFileParserMock);
			when(xmlFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("atr1", "atr2"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[] { ";" });
			doReturn(true).when(xmlFileParser).movFile(anyString(), anyObject());
			Iterator<String> iterator = xmlFileParser.loadFile(new FileSettingsToSend(null, file, null));

			assertNotNull(iterator);
			assertFalse(iterator.hasNext());
			//assertEquals(iterator.next(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void emptyLineDataTest() {
		try {
			File file = folder.newFile("data.txt");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("atr1;atr2;atr3");
			buffer.newLine();
			buffer.newLine();
			buffer.append("val1;val2;val3");
			buffer.flush();

			TxtFileParser txtFileParserMock = new TxtFileParser(xmlFileValidatorService, fileAttributeService);
			TxtFileParser xmlFileParser = Mockito.spy(txtFileParserMock);
			when(xmlFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("atr1", "atr2"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[] { ";" });
			doReturn(true).when(xmlFileParser).movFile(anyString(), anyObject());
			Iterator<String> iterator = xmlFileParser.loadFile(new FileSettingsToSend(null, file, null));

			assertNotNull(iterator);
			assertTrue(iterator.hasNext());
			assertEquals(iterator.next(),null);
			//iterator.next();
			assertEquals(iterator.next(),"atr1=val1,atr2=val2");
			
			
			//assertEquals(iterator.next(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void initTest() {
		MockitoAnnotations.initMocks(this);
	}
}
