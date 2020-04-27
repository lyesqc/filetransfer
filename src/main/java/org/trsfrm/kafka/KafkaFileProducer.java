package org.trsfrm.kafka;

import org.trsfrm.model.FileSettingsToSend;
import java.io.File;
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

	public KafkaFileProducer(FileSettingsToSend fileSettingsToSend, KafkaProducer<Long, String> producer,
			String topic) {
		this.fileSetting = fileSettingsToSend;
		this.producer = producer;
		this.topic = topic;
		this.fileParser = fileSettingsToSend.getFileParser();
	}

	/**
	 * load file, and start read it cancel sendind if at least one message
	 * cannot be sent
	 */
	@Override
	public Integer call()  {

		String destPath = "success";
		Iterator<String> iterator = null;
		System.out.println("Inside handler for file :" + fileSetting.getFile().toPath());
		try {
			iterator = fileParser.loadFile(fileSetting);
			if (iterator == null)
				resultOfSend = -1;
			while (resultOfSend != -1 && iterator.hasNext()) {
				valueToSend = iterator.next();
				if (valueToSend != null && valueToSend.length() > 0)
					producer.send(new ProducerRecord<Long, String>(topic, valueToSend),
							(meta, excep) -> onCompleteKafkaSend(meta, excep));
			}
		} catch (Exception e) {
			resultOfSend = -1;
			System.out.println(e.getMessage());
			throw e;
		}

		finally {
			if (resultOfSend == -1)
				destPath = "error";
			System.out.println("result send is " + resultOfSend);
			if (resultOfSend == 0)
				producer.flush();
			fileParser.movFile(destPath, fileSetting);
		}
		return resultOfSend;
	}

	public void onCompleteKafkaSend(RecordMetadata meta, Exception e) {
		if (meta == null) {
			resultOfSend = -1;
			System.out.println("Execption occured when try to send to kafka " + resultOfSend);
		}

	}

}
