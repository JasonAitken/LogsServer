package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.LogsServlet;

public class TestGetLogs {

	@Test
	public void testGetVaild() throws IOException {

		String logEventString = "{\r\n" + "  \"id\": \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\r\n"
				+ "  \"message\": \"application started\",\r\n" + "  \"timestamp\": {},\r\n"
				+ "  \"thread\": \"main\",\r\n" + "  \"logger\": \"com.example.Foo\",\r\n"
				+ "  \"level\": \"DEBUG\",\r\n" + "  \"errorDetails\": \"string\"\r\n" + "}";

		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();
		requestGet.setParameter("limit", "1");
		requestGet.setParameter("level", "ALL");
		MockHttpServletRequest requestPost = new MockHttpServletRequest();
		MockHttpServletResponse responsePost = new MockHttpServletResponse();
		requestPost.setParameter("LogEvent", logEventString);

		LogsServlet servlet = new LogsServlet();
		servlet.doPost(requestPost, responsePost);
		servlet.doGet(requestGet, responseGet);

		assertEquals(200, responseGet.getStatus());
	}
	
	@Test
	public void testGetNegitive() throws IOException {


		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();
		requestGet.setParameter("limit", "-1");
		requestGet.setParameter("level", "ALL");

		LogsServlet servlet = new LogsServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(400, responseGet.getStatus());
	}
	
	@Test
	public void testGetNonLevel() throws IOException {


		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();
		requestGet.setParameter("limit", "1");
		requestGet.setParameter("level", "level 1");

		LogsServlet servlet = new LogsServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(400, responseGet.getStatus());
	}
	
	@Test
	public void testGetBadParam() throws IOException {


		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();
		requestGet.setParameter("max", "1");
		requestGet.setParameter("level", "ALL");

		LogsServlet servlet = new LogsServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(400, responseGet.getStatus());
	}
	
	
}
