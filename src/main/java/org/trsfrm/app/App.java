package org.trsfrm.app;


import java.io.IOException;
import java.io.PrintStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.trsfrm.file.FileConfig;
import org.trsfrm.repository.FileWatcherLauncher;
import org.trsfrm.repository.RepositoryConf;
import org.trsfrm.utils.LoggingOutputStream;

@SpringBootApplication
@ComponentScan(basePackageClasses = {FileConfig.class, RepositoryConf.class})

public class App implements CommandLineRunner {

	@Autowired
	FileWatcherLauncher fileWatcherLauncher;

	@Value("${repository.count}")
	private int repositoryCount;

	public static void main(String[] args) throws IOException {
		 SpringApplication.run(App.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		Logger out = Logger.getLogger("");
		System.setOut(new PrintStream(new LoggingOutputStream(out, Level.INFO), true));

		System.out.println("Value is " + repositoryCount);
		fileWatcherLauncher.launchInspectRepositories();
	}

}
