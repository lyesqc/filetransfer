package org.trsfrm.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.trsfrm.file.FileParser;
import org.trsfrm.kafka.KafkaFileProducer;
import org.trsfrm.model.FileSettingsToSend;
import org.trsfrm.model.KafkaSettings;
import org.trsfrm.model.Repository;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import javafx.geometry.Orientation;

@Component
@Scope("prototype")
public class RepositoryThreadInpector implements Runnable {

	// private FileParser parser;
	private Repository repository;
	private Path directory;
	public KafkaProducer<Long, String> producer;
	List<Future> fileHandler = new ArrayList<>();
	private String topic;

	@Autowired
	Map<String, FileParser> parserList;
	private static Vector<FileSettingsToSend> inspectedFiles = new Vector<>();

	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

	FileParser eligeableFileParser;
	// Callable<Integer> runner;
	private KafkaFileProducer kafkaFileProducer;

	public Repository getRepository() {
		return repository;

	}

	public RepositoryThreadInpector(Repository repository, KafkaFileProducer kafkaFileProducer) {
		this.repository = repository;
		this.directory = Paths.get(repository.getPath());
		this.topic = repository.getKafkaSetting().getTopic();
		this.kafkaFileProducer = kafkaFileProducer;
	}

	/**
	 * survey the directory and for each new file launch new producer kafka
	 * Handler
	 */
	public void run() {

		while (true) {
			try {
				System.out.println("Start inspect " + directory);
				Thread.sleep(1000);
				while (Files.list(directory).count() == 4)
					Thread.sleep(1000);
				System.out.println("Files numbers is great than 4");

				Files.list(directory).filter(e -> !Files.isDirectory(e)).forEach(e -> {
					try {
						FileSettingsToSend fileSetting = new FileSettingsToSend(repository.getPath().toString(),
								e.toFile(), eligeableFileParser);
						if (fileAlreadyInInspect(fileSetting))
							return;

						System.out.println("new File add to " + directory + " : " + e);
						//Callable<Integer> runner = new init(fileSetting, producer, topic);
						Future<Integer> result = (Future<Integer>) executor.submit(this.kafkaFileProducer.init(fileSetting, producer, topic));
						fileHandler.add(result);
						System.out.println("adding file to list " + directory.toString() + File.separator
								+ e.getFileName().toString());
						trackInsoectedFile(fileSetting);
					} catch (Exception e1) {
						System.out.println(e1.getMessage());
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}

	}

	/**
	 * create a kafka producer for each repository
	 */
	@PostConstruct
	private void createProducer() {
		KafkaSettings kafkaSetting = repository.getKafkaSetting();
		this.eligeableFileParser = selectFileParserOfRepository(repository);
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSetting.getBrokers());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "1");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		this.producer = new KafkaProducer<>(props);
	}

	private FileParser selectFileParserOfRepository(Repository repository) {
		return parserList.get(repository.getParserType());

	}

	public static void untrackInpectedFile(FileSettingsToSend fileSetting, int patternDateLength) {
		if (fileSetting == null || fileSetting.getFile() == null || fileSetting.getDiretoryPath() == null)
			return;
		String originFileName = fileSetting.getFile().getName();
		FileSettingsToSend newFileSetting = new FileSettingsToSend(fileSetting.getDiretoryPath(),
				new File(originFileName), null);
		System.out.println("remove file is " + RepositoryThreadInpector.inspectedFiles.remove(newFileSetting));
	}

	public void trackInsoectedFile(FileSettingsToSend fileSetting) {
		inspectedFiles.add(fileSetting);
	}

	private boolean fileAlreadyInInspect(FileSettingsToSend fileSetting) {
		System.out.println("check if file existe " + directory.toString() + File.separator
				+ fileSetting.getFile().getName().toString() + "  -->" + inspectedFiles
						.contains(directory.toString() + File.separator + fileSetting.getFile().getName().toString()));
		System.out.println("Existing files are " + inspectedFiles.toString());
		return inspectedFiles.contains(fileSetting);
	}

}