package org.trsfrm.model;

import lombok.Data;

@Data
public class Repository {
	private String path;
	private String fileType;
	private String parserType;
    private KafkaSettings kafkaSetting;
    public static Repository Build(){
    	return new Repository();
    }
    public Repository withPath(String path){
    	this.path = path;
    	return this;
    }
    public Repository withFileType(String fileType){
    	this.fileType = fileType;
    	return this;
    }
    public Repository withParserType(String parserType){
    	this.parserType = parserType;
    	return this;
    }
    
    public Repository withKaKaSetting(KafkaSettings kafkaSetting){
    	this.kafkaSetting = kafkaSetting;
    	return this;
    }
    
}
