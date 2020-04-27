package org.trsfrm.file.parser;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit4.SpringRunner;
import org.trsfrm.file.FileConfig;
import org.trsfrm.file.FileParser;
import org.trsfrm.file.attribute.FileAttributeService;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.FileValidator;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
// @ContextConfiguration(classes={FileConfig.class})
@TestPropertySource(locations = { "classpath:application.properties" })
public class JsonFileParserTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Mock
	IFileValidator jsonFileValidatorService;
	@Mock
	IFileAttributeService fileAttributeService;
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();

	//@Test
	@EnabledIf("'2' == systemEnvironment.get('activateT')")
	
	public void loadFileCheckFormatFalse()  {

		exception.expect(Exception.class);
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		
		assertNotNull(jsonFileValidatorService);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(false);
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		assertNull(jsonFileParser.loadFile(null));
		verify(jsonFileParser, times(0)).readBlock(anyObject());
		/*
		 * verify(jsonFileParser, times(1)).movFile(anyString(), anyObject());
		 * assertTrue(jsonFileParser.movFile(anyString(), anyObject()));
		 * assertTrue(jsonFileValidatorService.checkFileFormat(anyObject()));
		 */

		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		assertNotNull(jsonFileParser.loadFile(null));
		verify(jsonFileParser, times(1)).readBlock(anyObject());
		
	}
	
	//@Test
	@EnabledIf("1 == systemEnvironment.get('activateT')")
	public void loadFileCheckFormatTrue()  {
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(anyString())).thenReturn(Arrays.asList("c2"));
		when(fileAttributeService.getDelimitorRepositpry(anyString())).thenReturn(new String[]{"a","b"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		
		exception.expect(Exception.class);
		assertNull(jsonFileParser.loadFile(new FileSettingsToSend(null, null, null)));
		verify(jsonFileParser, times(1)).readBlock(anyObject());
		verify(fileAttributeService.getAttributes(anyString()), times(1));
		verify(fileAttributeService.getDelimitorRepositpry(anyString()), times(1));
		
	}
	
	//@Test
	@EnabledIf("1 == systemEnvironment.get('activateT')")
	public void checkFileWithGoodContentAttribute(){
		try{
		File file = folder.newFile("data.json");
		FileWriter out = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(out);
		buffer.append("{\"a\":{\"name\":\"myName\",\"b\":\"a\"}}");
		buffer.flush();
		
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("b"));
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(null);
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "b=a");
		
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	
	//@Test	
	public void checkFileWithGoodContentObject(){
		try{
		File file = folder.newFile("data.json");
		FileWriter out = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(out);
		buffer.append("{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a\"}}}");
		buffer.flush();
		
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("c2"));
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"b"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "c2=a");
		
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	//@Test	
	public void checkFileWithGoodContentAttributeArray(){
		try{
		File file = folder.newFile("data.json");
		FileWriter out = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(out);
		buffer.append("{\"a\":{\"name\":\"myName\",\"b\":[\"c2\",\"a\"]}}");
		buffer.flush();
		
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("b"));
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(null);
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "b=c2,b=a");
		
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	//@Test
	@EnabledIf("1 == systemEnvironment.get('activateT')")
	public void checkFileWithGoodContentArray(){
		try{
		File file = folder.newFile("data.json");
		FileWriter out = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(out);
		buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a\"}}},{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a2\"}}}]}");
		buffer.flush();
		
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("c2"));
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"a","b"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "c2=a");
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "c2=a2");
		
		assertFalse(iterator.hasNext());
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	//@Test
	public void checkforContententArrayOfArray(){
		try{
			File file = folder.newFile("data.json");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\"},{\"c2\":\"a2\"}]}},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\"},{\"c2\":\"a3\"}]}}]}");
			buffer.flush();
			
			JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
			JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
			when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("c2"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"a","b"});
			doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
			Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
			assertNotNull(iterator);
			
			assertTrue(iterator.hasNext());
			assertEquals(iterator.next(), "c2=a,c2=a2");
			
			assertTrue(iterator.hasNext());
			assertEquals(iterator.next(), "c2=a,c2=a3");
			
			assertFalse(iterator.hasNext());
			}catch(Exception e ){
				e.printStackTrace();
			}
		
	}
	
	@Test
	public void checkGetDeep(){
		try{
			File file = folder.newFile("data.json");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\"},{\"c2\":\"a2\"}]}},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\"},{\"c2\":\"a3\"}]}}]}");
			//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}},{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}},{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}}]}}]}");
			buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}] },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\": [{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  }]}}]}");
			buffer.flush();
			final ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(file);
		    JsonFileParser parser = new JsonFileParser(null, null);
		    List listNode = new ArrayList<JsonNode>();
		    parser.getDeepTreeWay(root, new String[]{"data","a","b","d","e" }, listNode);
		    assertEquals(listNode.size(), 8);
			}catch(Exception e ){
				e.printStackTrace();
			}
		
	}

	@BeforeClass
	public static void settingUp() {

	}

	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
	}
}
