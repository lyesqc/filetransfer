package org.trsfrm.file.attribute;

import java.util.List;

public interface IFileAttributeService {
	
	public List<String> getAttributes(String path) ;
	public String[] getDelimitorRepositpry(String path) ;

}
