package org.trsfrm.file.attribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FileAttributeService implements IFileAttributeService{

	private Map<String, List<String>> attributesName = new HashMap<>();
	private Map<String, String[]> blocDelimitors = new HashMap<>();
	@Value("${repository.count:0}")
	private int repositoryCount;
	private String fileSeparator = "\\";

	@Autowired
	Environment env;

	@PostConstruct
	void init() {
		loadDelimitorRepository();
		loadAttributesRepositories();
	}

	private void loadDelimitorRepository() {
		String delimitor;
		String path;
		for (int i = 1; i <= repositoryCount; i++) {
			path = env.getProperty("repository." + i + ".directory.path");
			delimitor = env.getProperty("repository." + i + ".directory.delimitor");
			blocDelimitors.put(path, delimitor.split(";"));
		}
	}

	private void loadAttributesRepositories() {
		String path;
		List<String> attributes = new ArrayList<>();
		for (int i = 1; i <= repositoryCount; i++) {
			path = env.getProperty("repository." + i + ".directory.path");
			Path delimitorFile = Paths.get(path + fileSeparator + "conf"+fileSeparator+"attributes.txt");
			try(Stream<String> stream = Files.lines(delimitorFile)) {
				
				stream.forEach(e -> attributes.add(e));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			attributesName.put(path, attributes);
		}
	}

	public List<String> getAttributes(String path) {

		return attributesName.get(path);
	}

	public String[] getDelimitorRepositpry(String path) {
		return blocDelimitors.get(path);
	}

}
