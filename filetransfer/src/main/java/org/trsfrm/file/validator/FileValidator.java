package org.trsfrm.file.validator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.trsfrm.model.FileSettingsToSend;




public abstract class FileValidator implements IFileValidator{
	
	static Environment ctx;
	protected static Map<String,File> listSchema = new HashMap();
	
	@Autowired
	public final void setCtx(Environment ctx) {
		this.ctx = ctx;
	}
	
	public  abstract boolean checkFileFormat(FileSettingsToSend fileSetting);
	
	@PostConstruct
	protected static void loadSchemas(){
		int numerRepository = Integer.valueOf(ctx.getProperty("repository.count"));
		System.out.println("inside File Validator "+numerRepository);
		String fileType;
		for(int i= 1; i<=numerRepository; i++){
			fileType = ctx.getProperty("repository."+i+".directory.format");
			String path = ctx.getProperty("repository."+i+".directory.path");
			System.out.println("inside File Validator "+fileType+"||"+path);
			File schema = new File(path+"\\conf\\schema."+fileType);
			listSchema.put(path,schema);
		}
	}
}
