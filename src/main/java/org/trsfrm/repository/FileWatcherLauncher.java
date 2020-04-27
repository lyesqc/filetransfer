package org.trsfrm.repository;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.trsfrm.model.KafkaSettings;
import org.trsfrm.model.Repository;
import org.trsfrm.utils.LoggingOutputStream;

//@PropertySource("classpath:application.properties")
@Service
public class FileWatcherLauncher implements FileWatcher {

	@Autowired
	FileWatcherLauncher fileWatcherLauncher;
	@Value("${repository.count:0}")
	private int repositoryCount = 1;
	@Autowired
	ApplicationContext ctx;
	@Autowired
	Environment env;
	Logger out = Logger.getLogger("SystemOut");

	// final static Logger logger =
	// Logger.getLogger(FileWatcherLauncher.class.getName());
	public FileWatcherLauncher() {
		System.setOut(new PrintStream(new LoggingOutputStream(out, Level.INFO), true));
	}

	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(repositoryCount);

	List<Repository> listRepository = new ArrayList();
	private Map<String, Future> repositoriesThread = new HashMap<>();

	/**
	 * entry point for our application
	 */
	public void launchInspectRepositories() {
		listRepository.forEach(repository -> {
			try {
				launchInspector(repository);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		});
	}

	public void launchInspector(Repository repository) throws Exception {
		RepositoryThreadInpector threadDirectory = (RepositoryThreadInpector) ctx
				.getBean(RepositoryThreadInpector.class, repository);

		repositoriesThread.put(repository.getPath(), executor.submit(threadDirectory));
	}

	/**
	 * load repository setting from properties file and put it in list of
	 * repository
	 */
	@PostConstruct
	public void loadRepository() {
		String path;
		String parserType;
		String fileType;
		String brokers;
		String topic;
		KafkaSettings kafkaSetting;
		for (int i = 1; i <= repositoryCount; i++) {
			path = env.getProperty("repository." + i + ".directory.path");
			fileType = env.getProperty("repository." + i + ".directory.format");
			parserType = env.getProperty("repository." + i + ".directory.parser");
			topic = env.getProperty("repository." + i + ".kafka.topic");
			brokers = env.getProperty("repository." + i + ".kafka.brokers");
			System.out.println("Parser is " + parserType + ", " + brokers);
			kafkaSetting = KafkaSettings.bluid().withBrokers(brokers).withTopic(topic);
			Repository repository = Repository.Build().withFileType(fileType).withParserType(parserType).withPath(path)
					.withKaKaSetting(kafkaSetting);
			listRepository.add(repository);
		}

	}

	public Map<String, Future> getRepositoriesThread() {
		return repositoriesThread;
	}

}
