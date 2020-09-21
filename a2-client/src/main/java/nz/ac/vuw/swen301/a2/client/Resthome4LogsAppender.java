package nz.ac.vuw.swen301.a2.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.pattern.LogEvent;
import org.apache.log4j.spi.LoggingEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Resthome4LogsAppender extends AppenderSkeleton {
	private final Gson gson = new GsonBuilder().create();
	URI logServiceURL = URI.create("http://localhost:8080/resthome4logs/logs");
	int count = 0;

	public Resthome4LogsAppender() {

	}

	public void close() {

	}

	public boolean requiresLayout() {
		// TODO Shouldnt need to be changed
		return false;
	}

	@Override
	protected void append(LoggingEvent le) {
		// TODO this is were the use of POST will be

		Map<String, Object> toReturn = new LinkedHashMap();

		// toReturn.put(le.getLevel().toString(), safeToString(le.getMessage()));
		toReturn.put("id", le.hashCode());
		toReturn.put("logger", le.getLoggerName());
		toReturn.put("level", le.getLevel().toString());
		toReturn.put("timestamp", le.timeStamp);
		toReturn.put("thread", le.getThreadName());
		toReturn.put("message", safeToString(le.getMessage()));

		String leMade = gson.toJson(toReturn) + "\n";// temp string that needs to be posted to servlet

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("localhost").setPort(8080).setPath("resthome4logs/logs")
				.setParameter("LogEvent", leMade);
		try {
			logServiceURL = builder.build();
		} catch (URISyntaxException e) {
		}

		// create and execute the request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(logServiceURL);
		try {
			HttpResponse response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

	}

	private static String safeToString(Object obj) { // found this when looking for hints, found it useful so included
														// it
		if (obj == null)
			return null;
		try {
			return obj.toString();
		} catch (Throwable t) {
			return "Error getting message: " + t.getMessage();
		}
	}

}
