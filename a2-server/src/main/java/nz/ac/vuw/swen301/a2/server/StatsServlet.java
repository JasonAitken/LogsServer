package nz.ac.vuw.swen301.a2.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class StatsServlet extends HttpServlet {
	URI logServiceURL = URI.create("http://localhost:8080/resthome4logs/logs");

	public StatsServlet() {
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		
		String[][] data = new String[100][5];//temp numbers
		try {
			data = fetch();
		} catch (Exception e) {
		}

		int lim = data.length-1;
		ArrayList<String> days = new ArrayList<String>();

		for (int i = 0; i < lim; i++) {
			if (!days.contains(data[i][0])) {
				days.add(data[i][0]);
			}
		}

		int[] loggersPerDay = new int[days.size()];

		for (int j = 0; j < days.size(); j++) {
			ArrayList<String> loggers = new ArrayList<String>();
			for (int i = 0; i < lim; i++) {
				if ((data[i][0].equals(days.get(j))) && !loggers.contains(data[i][2])) {
					loggers.add(data[i][2]);
					loggersPerDay[j]++;
				}
			}
		}

		int[] logLvlsPerDay = new int[days.size()];

		for (int j = 0; j < days.size(); j++) {//TODO has issues
			ArrayList<String> logLvls = new ArrayList<String>();
			for (int i = 0; i < lim; i++) {
				if ((data[i][0].equals(days.get(j))) && !logLvls.contains(data[i][1])) {
					logLvls.add(data[i][1]);
					logLvlsPerDay[j]++;
				}
			}
		}

		int[] threadsPerDay = new int[days.size()];

		for (int j = 0; j < days.size(); j++) {
			ArrayList<String> threads = new ArrayList<String>();
			for (int i = 0; i < lim; i++) {
				if ((data[i][0].equals(days.get(j))) && !threads.contains(data[i][3])) {
					threads.add(data[i][3]);
					threadsPerDay[j]++;
				}
			}
		}

		
		out.println("<table>");
		out.println("<tr>");
		out.println("<th> </th>");
		for(String d : days) {
			out.println("<th>"+ d + "</th>");
		}
		out.println("</tr>");
		for(int i =0; i < 3; i++) {
			out.println("<tr>");
			if(i == 0) {
				out.println("<td>Loggers</td>");
			}else if (i == 1) {
				out.println("<td>Log Levels</td>");
			}
			else if (i == 2) {
				out.println("<td>Threads</td>");
			}
			for(int j =0; j < days.size(); j++) {
				if(i == 0) {
					out.println("<td>" + loggersPerDay[j] + "</td>");
				}else if(i == 1) {
					out.println("<td>" + logLvlsPerDay[j] + "</td>");
				}else if(i == 2) {
					out.println("<td>" + threadsPerDay[j] + "</td>");
				}
			}			
			out.println("</tr>");
		}
		out.println("</table>");
		out.close();

	}

	private String[][] fetch() throws Exception {
		// int lim = 1;
		// String lvl = "ALL";
		// String[][] data = new String[100][5]; // lim is 100 for now, can be changed.

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("localhost").setPort(8080).setPath("resthome4logs/logs")
				.setParameter("limit", "ALL").setParameter("level", "ALL");

		logServiceURL = builder.build();

		// create and execute the request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(logServiceURL);
		HttpResponse response = httpClient.execute(request);

		String content = EntityUtils.toString(response.getEntity());

		String[] msgList = content.split("\n");

		String[] logsList = msgList[3].split("}");

		String[][] data = new String[logsList.length][5];
		for (int i = 0; i < logsList.length - 1; i++) {
			// "TIME", "LEVEL", "LOGGER", "THREAD", "MESSAGE"
			data[i][0] = (new Date(Long.parseLong(
					logsList[i].substring(logsList[i].indexOf("timestamp") + 12, logsList[i].indexOf("thread") - 3))))
							.toString();
			data[i][1] = logsList[i].substring(logsList[i].indexOf("level") + 10, logsList[i].indexOf("timestamp") - 5);
			data[i][2] = logsList[i].substring(logsList[i].indexOf("logger") + 11, logsList[i].indexOf("level") - 5);
			data[i][3] = logsList[i].substring(logsList[i].indexOf("thread") + 11, logsList[i].indexOf("message") - 5);
			data[i][4] = logsList[i].substring(logsList[i].indexOf("message") + 12, logsList[i].length() - 2);
			/*
			 * Date date = new Date(Long.parseLong(data[0][0]));
			 * System.out.println(date.toString());
			 */
		}
		return data;
	}
}
