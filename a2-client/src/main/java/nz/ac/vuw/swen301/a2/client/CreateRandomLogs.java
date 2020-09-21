package nz.ac.vuw.swen301.a2.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class CreateRandomLogs {

	public static void main(String[] args) throws InterruptedException {
		//BasicConfigurator.configure(); // was getting errors, but by adding this it was able to work
		Logger logger = Logger.getLogger("test2");
		Appender appender = new Resthome4LogsAppender();
		logger.addAppender(appender);
		//logger.app
		logger.setLevel(Level.ALL);
		
		List<Level> lvlList = new ArrayList<Level>();
		// Available values : ALL, DEBUG, INFO, WARN, ERROR, FATAL, TRACE, OFF
		// lvlList.add(Level.ALL);
		lvlList.add(Level.DEBUG);
		lvlList.add(Level.INFO);
		lvlList.add(Level.WARN);
		lvlList.add(Level.ERROR);
		lvlList.add(Level.FATAL);
		lvlList.add(Level.TRACE);
		//lvlList.add(Level.OFF);
		List<String> msgList = new ArrayList<String>();
		msgList.add("these");
		msgList.add("are");
		msgList.add("my");
		msgList.add("random");
		msgList.add("words");
		msgList.add("for");
		msgList.add("the");
		msgList.add("message");
		int lvlRange = lvlList.size() - 1 + 0 + 1; // 0 isnt needed but is to show the min, left in for style reasons
		int msgRange = msgList.size() - 1 + 0 + 1;
		while (true) {

			
			int randLvl = (int) (Math.random() * lvlRange);
			switch (randLvl) {
			case 0:
				logger.debug(msgList.get((int) (Math.random() * msgRange)) + " " + msgList.get((int) (Math.random() * msgRange)));
				System.out.println("case 0");
				break;
			case 1:
				logger.info(msgList.get((int) (Math.random() * msgRange)) + " " + msgList.get((int) (Math.random() * msgRange)));
				System.out.println("case 1");
				break;
			case 2:
				logger.warn(msgList.get((int) (Math.random() * msgRange)) + " " + msgList.get((int) (Math.random() * msgRange)));
				System.out.println("case 2");
				break;
			case 3:
				logger.error(msgList.get((int) (Math.random() * msgRange)) + " " + msgList.get((int) (Math.random() * msgRange)));
				System.out.println("case 3");
				break;
			case 4:
				logger.fatal(msgList.get((int) (Math.random() * msgRange)) + " " + msgList.get((int) (Math.random() * msgRange)));
				System.out.println("case 4");
				break;
			case 5:
				logger.trace(msgList.get((int) (Math.random() * msgRange)) + " " + msgList.get((int) (Math.random() * msgRange)));
				System.out.println("case 5");
				break;
			//case 6:
				//logger.off(msgList.get((int) (Math.random() * msgRange)) + " " + msgList.get((int) (Math.random() * msgRange)));
			}
			TimeUnit.SECONDS.sleep(1);
			
		}

	}

}
