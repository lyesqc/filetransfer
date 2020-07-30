package org.trsfrm.file.parser;


import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit4.SpringRunner;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSend;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes={FileConfig.class})
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

	@Test
	public void loadFileCheckFormatFalse()  {

		exception.expect(Exception.class);
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		
		assertNotNull(jsonFileValidatorService);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(false);
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		assertNull(jsonFileParser.loadFile(null));
		verify(jsonFileParser, times(0)).readBlock(anyObject());
		/*
		 * verify(jsonFileParser, times(1)).movFile(anyString(), anyObject());
		 * assertTrue(jsonFileParser.movFile(anyString(), anyObject()));
		 * assertTrue(jsonFileValidatorService.checkFileFormat(anyObject()));
		 */

		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		assertNull(jsonFileParser.loadFile(null));
		verify(jsonFileParser, times(1)).readBlock(anyObject());
		
	}
	
	@Test
	@EnabledIf("1 == systemEnvironment.get('activateT')")
	public void loadFileCheckFormatTrue()  {
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(anyString())).thenReturn(Arrays.asList("c2"));
		when(fileAttributeService.getDelimitorRepositpry(anyString())).thenReturn(new String[]{"a","b"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		
		exception.expect(Exception.class);
		assertNull(jsonFileParser.loadFile(new FileSettingsToSend(null, null, null)));
		verify(jsonFileParser, times(1)).readBlock(anyObject());
		verify(fileAttributeService.getAttributes(anyString()), times(1));
		verify(fileAttributeService.getDelimitorRepositpry(anyString()), times(1));
		
	}
	
	@Test
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
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"a"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "b=a");
		
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	
	@Test	
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
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"a","b"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "c2=a");
		
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	@Test	
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
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"a"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "b=c2,b=a");
		
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	@Test
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
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"data","a","b"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
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
	
	@Test
	public void checkFileWithGoodContentArrayComplicated(){
		try{
		File file = folder.newFile("data.json");
		FileWriter out = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(out);
		//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a\"}}},{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a2\"}}}]}");
		//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}},{\"c2\":\"a\",\"d\":{\"c\":\"toi\"}} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\":{\"c\":\"vous\"}},{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}}]}}]}");
	    buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\",\"m\":\"l\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}] },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"vous\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\": [{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  }]}}]}");

		buffer.flush();
		
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("h","m"));
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"data","a","b","d","e"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "h=n,m=l");
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "h=n");
		
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(), "h=vous");
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	@Test
	public void checkFileWithEmptyChild(){
		try{
		File file = folder.newFile("data.json");
		FileWriter out = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(out);
		//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a\"}}},{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a2\"}}}]}");
		//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}},{\"c2\":\"a\",\"d\":{\"c\":\"toi\"}} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\":{\"c\":\"vous\"}},{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}}]}}]}");
	    //buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\",\"m\":\"l\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}] },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"vous\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\": [{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  }]}}]}");
		buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a\"}}},{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a2\"}}}]}");
		buffer.flush();
		
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("h","m"));
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"data","b","d","e"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNull(iterator);
		
		
				}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	@Test
	public void checkFileWithEmptyAttributeValude(){
		try{
		File file = folder.newFile("data.json");
		FileWriter out = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(out);
		//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a\"}}},{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a2\"}}}]}");
		//buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}},{\"c2\":\"a\",\"d\":{\"c\":\"toi\"}} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\":{\"c\":\"vous\"}},{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}}]}}]}");
	    //buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\",\"m\":\"l\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}] },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"vous\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\": [{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  }]}}]}");
		buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a\"}}},{\"a\":{\"name\":\"myName\",\"b\":{\"c2\":\"a2\"}}}]}");
		buffer.flush();
		
		JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
		JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
		when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
		when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("c2"));
		when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"data","a","b"});
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
		doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
		Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(iterator.next(),"c2=a");
		
				}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	@Test
	public void checkforContententArrayOfArray(){
		try{
			File file = folder.newFile("data.json");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"c1\":\"b1\"},{\"c2\":\"a2\",\"c1\":\"b2\"}]}},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\"},{\"c2\":\"a3\"}]}}]}");
			buffer.flush();
			
			JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
			JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
			when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("c2","c1"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"data","a","b"});
			doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
			doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
			Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
			assertNotNull(iterator);
			assertTrue(iterator.hasNext());
			assertEquals(iterator.next(), "c2=a,c1=b1");
			}catch(Exception e ){
				e.printStackTrace();
			}
		
	}
	
	@Test
	public void checkforContententArrayOfArrayNull(){
		try{
			File file = folder.newFile("data.json");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":[]}},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"c1\":\"d\"},{\"c2\":\"a3\"}]}}]}");
			buffer.flush();
			
			JsonFileParser jsonFileParserMock = new JsonFileParser(jsonFileValidatorService, fileAttributeService);
			JsonFileParser jsonFileParser = Mockito.spy(jsonFileParserMock);
			when(jsonFileValidatorService.checkFileFormat(anyObject())).thenReturn(true);
			when(fileAttributeService.getAttributes(null)).thenReturn(Arrays.asList("c2","c1"));
			when(fileAttributeService.getDelimitorRepositpry(null)).thenReturn(new String[]{"data","a","b"});
			doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject());
			doReturn(true).when(jsonFileParser).movFile(anyString(), anyObject(),Mockito.anyBoolean());
			Iterator <String> iterator = jsonFileParser.loadFile(new FileSettingsToSend(null, file, null));
			assertNotNull(iterator);
			assertTrue(iterator.hasNext());
			assertEquals(iterator.next(), "c2=a,c1=d");
			
			//assertTrue(iterator.hasNext());
			//assertEquals(iterator.next(), "c2=a2,c1=b2");
			
			//assertFalse(iterator.hasNext());
			}catch(Exception e ){
				e.printStackTrace();
			}
		
	}
	
	@Test
	public void checkGetDeep(){
		try{
			/*
			File file = folder.newFile("data.json");
			FileWriter out = new FileWriter(file);
			BufferedWriter buffer = new BufferedWriter(out);
			*/
			List listNode = new ArrayList<JsonNode>();
			final ObjectMapper mapper = new ObjectMapper();
			StringBuilder buffer = new StringBuilder();
		    JsonFileParser parser = new JsonFileParser(null, null);
		    
			buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\"},{\"c1\":\"a2\"}]}},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\"},{\"c2\":\"a3\"}]}}]}");
		    //buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}},{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}},{\"c2\":\"a\",\"d\":{\"c\":\"moi\"}}]}}]}");
		    //buffer.append("{\"data\" :[{\"a\":{\"name\":\"myName\",\"b\": [{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}] },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]} ] }},{\"a\":{\"name\":\"myName\",\"b\":[{\"c2\":\"a\",\"d\": [{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  },{\"c2\":\"a\",\"d\":[{\"c\":\"moi\",\"e\":{\"h\":\"n\"}},{\"c\":\"moi\",\"e\":{\"h\":\"n\"}}]  }]}}]}");
		    //buffer.append("{\"a\":{\"name\":\"myName\",\"b\":[\"c2\",\"a\"]}}");
		    //buffer.flush();
			JsonNode root = mapper.readTree(buffer.toString());
			parser.getListNodeObject(root, new String[]{"a" }, listNode);
			
		    //parser.getDeepTreeWay(root, new String[]{"data","a","b","d","e" }, listNode);
		    //parser.getDeepTreeWay(root, new String[]{"a"}, listNode);
		    //assertEquals(listNode.size(), 1);
		    //assertEquals(listNode.get(0).toString(), "{\"name\":\"myName\",\"b\":[\"c2\",\"a\"]}");
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
