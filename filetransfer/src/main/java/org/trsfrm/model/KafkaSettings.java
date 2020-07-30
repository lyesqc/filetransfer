package org.trsfrm.model;

import lombok.Data;

@Data
public class KafkaSettings {
	
	private String brokers;
	private String topic;
	private String key;
	private String keySerializer;
	private String valueSerializer;
	private String[] partitions;
	private String cleintId;
	public static KafkaSettings bluid(){
		return new KafkaSettings();
	}
	public KafkaSettings withBrokers(String brokers){
		this.brokers = brokers;
		return this;
	}
	
	public KafkaSettings withTopic(String topic){
		this.topic = topic;
		return this;
	}
	
	public KafkaSettings withKeySerializer(String keySerializer){
		this.keySerializer = keySerializer;
		return this;
	}
	
	public KafkaSettings withKey(String key){
		this.key = key;
		return this;
	}
	
	public KafkaSettings withValueSerializer(String valueSerializer){
		this.valueSerializer = valueSerializer;
		return this;
	}
	
	public KafkaSettings withPartitions(String[] partitions){
		this.partitions = partitions;
		return this;
	}
	
	public KafkaSettings withCleintId(String cleintId){
		this.cleintId = cleintId;
		return this;
	}
}
