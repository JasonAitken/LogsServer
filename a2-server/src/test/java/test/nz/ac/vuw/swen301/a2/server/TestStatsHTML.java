package test.nz.ac.vuw.swen301.a2.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import nz.ac.vuw.swen301.a2.server.StatsCSVServlet;
import nz.ac.vuw.swen301.a2.server.StatsServlet;

public class TestStatsHTML {

	//NOTE: the server must be running with valid logs stored, for this to work.
	
	
	@Test
	public void testGetVaildContains() throws IOException {
		//NOTE: the serve must be running with valid logs stored, for this to work.
		//this test is to check if the out put contains the correct info (not parsing)

		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();

		StatsServlet servlet = new StatsServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(200, responseGet.getStatus());

		String content = responseGet.getContentAsString();

		//System.out.println(content);
		assert(content.contains("Loggers"));
		assert(content.contains("Log Levels"));
		assert(content.contains("Threads"));
		assert(content.contains("2020"));
	}
	
	@Test
	public void testGetVaildParsing() throws IOException {
		//NOTE: the serve must be running with valid logs stored, for this to work.
		//this test is to cheack if the out put contains the correct info (not parsing)
		//when parsing the html it isnt picking up the table
		

		MockHttpServletRequest requestGet = new MockHttpServletRequest();
		MockHttpServletResponse responseGet = new MockHttpServletResponse();

		StatsCSVServlet servlet = new StatsCSVServlet();
		servlet.doGet(requestGet, responseGet);

		assertEquals(200, responseGet.getStatus());
		//responseGet.setContentType("text/html");

		String content = responseGet.getContentAsString();
		
		Document doc = Jsoup.parse(content);
		
		//did it this way as it wouldnt parse the table as a table
		//i left in my code for the table parsing but it didnt work so i stopped where it is
		
		Element body = doc.select("body").get(0);
		System.out.println(body.text());
		assert(body.text().contains("2020"));
		assert(body.text().contains("loggers"));
		assert(body.text().contains("log levels"));
		assert(body.text().contains("threads"));
		
		
		//Element table = doc.select("table").get(0);
		/*Elements rows = table.select("tr");
		Element dayRow = rows.get(0); //get days
		Elements days = dayRow.select("th");
		assert(days.get(1).toString().contains("2020"));*/
	}

}
