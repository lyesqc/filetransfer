package org.trsfrm.kafka;

import org.trsfrm.model.FileSettingsToSend;
import org.trsfrm.repository.RepositoryThreadInpector;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.trsfrm.file.FileParser;

public class KafkaFileProducer implements Callable<Integer> {

	private KafkaProducer<Long, String> producer;
	private String topic;
	private FileSettingsToSend fileSetting;
	FileParser fileParser;
	File file;
	String valueToSend = null;
	int resultOfSend = 0;
	String datePattern = "yyyy_MM_dd_HH_mm";
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
	String destPath = "success";

	public Callable<Integer> init(FileSettingsToSend fileSettingsToSend, KafkaProducer<Long, String> producer,
			String topic) {
		this.fileSetting = fileSettingsToSend;
		this.producer = producer;
		this.topic = topic;
		this.fileParser = fileSettingsToSend.getFileParser();
		return this;
	}

	/**
	 * load file, and start read, it cancel sending if at least one message
	 * cannot be sent
	 */
	@Override
	public Integer call() {

		Iterator<String> iterator = null;
		// System.out.println("Inside handler for file :" +
		// fileSetting.getFile().toPath());
		String originFileName;
		try {
			iterator = fileParser.loadFile(fileSetting);
			if (iterator == null)
				resultOfSend = -1;
			while (resultOfSend != -1 && iterator.hasNext()) {
				valueToSend = iterator.next();
				if (valueToSend != null && valueToSend.length() > 0)
					/**
					 * call onCompleteKafkaSend method to put resultOfSend =-1
					 * if send not done
					 */
					producer.send(new ProducerRecord<Long, String>(topic, valueToSend),
							(meta, excep) -> onCompleteKafkaSend(meta, excep));
			}
		} catch (Exception e) {
			resultOfSend = -1;
			System.out.println(e.getMessage());
		}

		finally {
			if (resultOfSend == -1)
				destPath = "error";
			System.out.println("in finally result send is " + resultOfSend);
			if (resultOfSend == 0)
				producer.flush();
				RepositoryThreadInpector.untrackInpectedFile(fileSetting, datePattern.length());
			fileParser.movFile(destPath, fileSetting, true);
			
		}
		return resultOfSend;
	}

	
	/**
	 * method called on finish of produccer.send, to track the data send status
	 * put resultOfSend = -1 if error is occured when sending data
	 * 
	 * @param meta
	 * @param e
	 * @param fileSetting
	 */
	public void onCompleteKafkaSend(RecordMetadata meta, Exception e) {
		if (meta == null) {
			resultOfSend = -1;
			System.out.println("Execption occured when try to send to kafka " + resultOfSend);
			destPath = "error";
		}
	}

}
