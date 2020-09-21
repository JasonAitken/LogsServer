package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;

public class TestPostLogs {
	
	String logEventString = "{\"id\":\"d290f1ee-6c54-4b01-90e6-d701748f0851\","
			+ "\"logger\":\"test2\","
			+ "\"level\":\"DEBUG\","
			+ "\"timestamp\":\"1593160829254\","
			+ "\"thread\":\"main\","
			+ "\"message\":\"for the\"}";

	@Test
	public void testPostVaild() throws IOException {

		MockHttpServletRequest requestPost = new MockHttpServletRequest();
		requestPost.setParameter("LogEvent", logEventString);
		MockHttpServletResponse responsePost = new MockHttpServletResponse();

		LogsServlet servlet = new LogsServlet();
		servlet.doPost(requestPost, responsePost);

		assertEquals(201, responsePost.getStatus());
	}

	/*@Test
	public void testPostDuplicate() throws IOException {

		MockHttpServletRequest requestPost = new MockHttpServletRequest();
		MockHttpServletResponse responsePost = new MockHttpServletResponse();
		requestPost.setParameter("LogEvent", logEventString);
		MockHttpServletRequest requestPost2 = new MockHttpServletRequest();
		MockHttpServletResponse responsePost2 = new MockHttpServletResponse();
		requestPost2.setParameter("LogEvent", logEventString);

		LogsServlet servlet = new LogsServlet();
		servlet.doPost(requestPost, responsePost);
		servlet.doPost(requestPost2, responsePost2);

		//assert(responsePost2.getStatus() == 409 || responsePost.getStatus() == 409);
		assertEquals(409, responsePost2.getStatus());
	} */

	@Test
	public void testPostBadInput() throws IOException {
		// removed a needed : from the string

		MockHttpServletRequest requestPost = new MockHttpServletRequest();
		MockHttpServletResponse responsePost = new MockHttpServletResponse();
		requestPost.setParameter("LogEvent", "JSON string");

		LogsServlet servlet = new LogsServlet();
		servlet.doPost(requestPost, responsePost);

		assertEquals(400, responsePost.getStatus());
	}

	@Test
	public void testPostBadParam() throws IOException {
		// lowercase l in logEvent param
		
		MockHttpServletRequest requestPost = new MockHttpServletRequest();
		MockHttpServletResponse responsePost = new MockHttpServletResponse();
		requestPost.setParameter("logEvent", logEventString);

		LogsServlet servlet = new LogsServlet();
		servlet.doPost(requestPost, responsePost);

		assertEquals(400, responsePost.getStatus());
	}

}
