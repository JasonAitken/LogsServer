package nz.ac.vuw.swen301.a2.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class StatsXLSServlet extends HttpServlet {
	URI logServiceURL = URI.create("http://localhost:8080/resthome4logs/logs");

	public StatsXLSServlet() {
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.ms-excel");
		// PrintWriter out = response.getWriter();
		ServletOutputStream out = response.getOutputStream();

		String[][] data = new String[100][5];// temp numbers
		try {
			data = fetch();
		} catch (Exception e) {
		}

		int lim = data.length - 1;
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

		for (int j = 0; j < days.size(); j++) {// TODO has issues
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

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Log Stats");
		HSSFRow daysRow = sheet.createRow(0);
		HSSFCell emptyCell = daysRow.createCell(0);
		for (int i = 0; i < days.size(); i++) {
			daysRow.createCell(i + 1).setCellValue(days.get(i));
		}

		HSSFRow loggersRow = sheet.createRow(1);
		HSSFCell loggerCell = loggersRow.createCell(0);
		loggerCell.setCellValue("Loggers");
		HSSFRow logLvlsRow = sheet.createRow(2);
		HSSFCell logLvlCell = logLvlsRow.createCell(0);
		logLvlCell.setCellValue("Log Levels");
		HSSFRow threadsRow = sheet.createRow(3);
		HSSFCell threadsCell = threadsRow.createCell(0);
		threadsCell.setCellValue("Threads");

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < days.size(); j++) {
				if (i == 0) {
					loggersRow.createCell(j+1).setCellValue(loggersPerDay[j]);
				} else if (i == 1) {
					logLvlsRow.createCell(j+1).setCellValue(logLvlsPerDay[j]);
				} else if (i == 2) {
					threadsRow.createCell(j+1).setCellValue(threadsPerDay[j]);
				}
			}
		}

		// return / out the sheet or workbook
		workbook.write(out);
		workbook.close();
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
