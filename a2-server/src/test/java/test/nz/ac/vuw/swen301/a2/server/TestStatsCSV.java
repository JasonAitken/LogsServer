package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsCSVServlet;

public class TestStatsCSV {

	//NOTE: the server must be running with valid logs stored, for this to work.
	
	LogsServlet logsServlet = new LogsServlet();

	public void sendSingleLog() throws IOException { //this did not work
		
		
		String logEventString = "{\"id\":\"d290f1ee-6c54-4b01-90e6-d701748f0851\","
				+ "\"logger\":\"test2\","
				+ "\"level\":\"DEBUG\","
				+ "\"timestamp\":\"1593160829254\","
				+ "\"thread\":\"main\","
				+ "\"message\":\"for the\"}";
				
		MockHttpServletRequest requestPost = new MockHttpServletRequest();
		MockHttpServletResponse responsePost = new MockHttpServletResponse();
		requestPost.setParameter("LogEvent", logEventString);

		
		this.logsServlet.doPost(requestPost, responsePost);
		assertEquals(201, responsePost.getStatus());
		
	}

	public void sendVariousLvls() throws IOException { //this did not work
		String debug = "{\"id\":\"d290f1ee-6c54-4b01-90e6-d701748f0851\","
				+ "\"logger\":\"test2\","
				+ "\"level\":\"DEBUG\","
				+ "\"timestamp\":\"1593160829254\","
				+ "\"thread\":\"main\","
				+ "\"message\":\"for the\"}";
		
				
		String info = "{\"id\":\"g290f1ee-6c54-4b01-90e6-d701748f0851\","
				+ "\"logger\":\"test2\","
				+ "\"level\":\"INFO\","
				+ "\"timestamp\":\"1593160829254\","
				+ "\"thread\":\"main\","
				+ "\"message\":\"for the\"}";
		
		String warn = "{\"id\":\"f290f1ee-6c54-4b01-90e6-d701748f0851\","
				+ "\"logger\":\"test2\","
				+ "\"level\":\"WARN\","
				+ "\"timestamp\":\"1593160829254\","
				+ "\"thread\":\"main\","
				+ "\"message\":\"for the\"}";
		
		String[] logs = {debug, info, warn};

		LogsServlet servlet = new LogsServlet();
		
		for(String s : logs) {

			
			MockHttpServletRequest requestPost = new MockHttpServletRequest();
			MockHttpServletResponse responsePost = new MockHttpServletResponse();
			requestPost.setParameter("LogEvent", s);

			servlet.doPost(requestPost, responsePost);
		}
	}

	@Test
	public void testGetVaildContains() throws IOException {
		//NOTE: the serve must be running with valid logs stored, for this to work.
		//this test is to check if the out put contains the correct info (not parsing)

		//sendSingleLog(); //this did not work

		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();

		StatsCSVServlet servlet = new StatsCSVServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(200, responseGet.getStatus());

		String content = responseGet.getContentAsString();
		
		assert(content.contains("loggers"));
		assert(content.contains("log levels"));
		assert(content.contains("threads"));
		assert(content.contains("2020"));
	}
	
	@Test
	public void testGetVaildParsing() throws IOException {
		//NOTE: the serve must be running with valid logs stored, for this to work.
		//this test is to cheack if the out put contains the correct info (not parsing)

		//sendSingleLog(); //this did not work

		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();

		StatsCSVServlet servlet = new StatsCSVServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(200, responseGet.getStatus());

		String content = responseGet.getContentAsString();
		
		String[] rows = content.split("\n");
		
		String days = rows[0];
		String[] itemNames = new String[3];
		int[] values = new int[(days.split("\t").length)*3]; //size is amount of days in logs * 3, as for each data type
		
		for(int i =1; i< rows.length; i++) {
			itemNames[i-1] = rows[i].substring(0,rows[i].indexOf("\t"));
		}
		for(int i =1; i< rows.length; i++) {
			values[i-1] = Integer.parseInt(rows[i].substring(rows[i].indexOf("\t")+1,rows[i].indexOf("\t")+2));
		}
		
		assert(days.contains("2020-0"));//left it at this depending on marking date
		assert(itemNames[0].equals("loggers"));
		assert(itemNames[1].equals("log levels"));
		assert(itemNames[2].equals("threads"));
		assert(values[0]>=0 && values[1]>=0 && values[2]>=0);
		System.out.println(content);
	}

}
