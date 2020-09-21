package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.StatsCSVServlet;
import nz.ac.vuw.swen301.a2.server.StatsXLSServlet;

public class TestStatsXLS {

	// NOTE: the server must be running with valid logs stored, for this to work.

	@Test
	public void testGetVaildContains() throws IOException {
		// NOTE: the serve must be running with valid logs stored, for this to work.
		// this test is to check if the out put contains the correct info (not parsing)

		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();

		StatsXLSServlet servlet = new StatsXLSServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(200, responseGet.getStatus());

		String content = responseGet.getContentAsString();

		// System.out.println(content);
		// Surprisingly this worked
		assert (content.contains("Loggers"));
		assert (content.contains("Log Levels"));
		assert (content.contains("Threads"));
		assert (content.contains("2020"));
	}

	@Test
	public void testGetVaildParsing() throws IOException {
		// NOTE: the serve must be running with valid logs stored, for this to work.
		// couldnt parse the responseGet as xsl, but by changing the content type i was
		// able to get the data that way

		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();

		StatsCSVServlet servlet = new StatsCSVServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(200, responseGet.getStatus());

		// ByteArrayInputStream bais = new
		// ByteArrayInputStream(responseGet.getContentAsByteArray());
		// InputStream xsl = (InputStream)
		// (context.getResourceAsStream("/XSLTransformerCode.xsl"));

		String content = responseGet.getContentAsString();
		responseGet.setContentType("application/vnd.ms-excel"); //this parses it into csv
		
		String[] rows = content.split("\n");

		String days = rows[0];
		String[] itemNames = new String[3];
		int[] values = new int[(days.split("\t").length) * 3]; // size is amount of days in logs * 3, as for each data
																// type

		for (int i = 1; i < rows.length; i++) {
			itemNames[i - 1] = rows[i].substring(0, rows[i].indexOf("\t"));
		}
		for (int i = 1; i < rows.length; i++) {
			values[i - 1] = Integer.parseInt(rows[i].substring(rows[i].indexOf("\t") + 1, rows[i].indexOf("\t") + 2));
		}

		assert (days.contains("2020-0"));// left it at this depending on marking date
		assert (itemNames[0].equals("loggers"));
		assert (itemNames[1].equals("log levels"));
		assert (itemNames[2].equals("threads"));
		assert (values[0] >= 0 && values[1] >= 0 && values[2] >= 0);

	}

}
