package nz.ac.vuw.swen301.a2.server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
//import java.sql.Date;
import java.util.ArrayList;
import java.util.Date;

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

public class StatsPNGServlet extends HttpServlet {
	URI logServiceURL = URI.create("http://localhost:8080/resthome4logs/logs");

	public static final int BAR_WIDTH = 600;
	public static final int V_OFFSET = 7;
	public static final int H_OFFSET = 80;
	public static final int BAR_HEIGTH = 20;

	public StatsPNGServlet() {
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("image/png");

		// since an image contains bytes and no characters, we must output to a stream
		// now!
		ServletOutputStream out = response.getOutputStream();

		BufferedImage image = new BufferedImage(BAR_WIDTH + H_OFFSET + 5, (BAR_HEIGTH * 7) + (V_OFFSET * 2) + 5,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		String[][] data = new String[100][5];// temp numbers
		try {
			data = fetch();
		} catch (Exception e) {
		}

		int lim = data.length - 1;

		String[] logLvls = { "DEBUG", "INFO","WARN", "ERROR", "FATAL", "TRACE" };
		int[] logLvlsPerLvl = { 0, 0, 0, 0, 0, 0 }; // init all values incase there is none of that log level

		for (int i = 0; i < lim; i++) {
			for (int j = 0; j < logLvls.length; j++) {
				if (data[i][1].equals(logLvls[j])) { 
					logLvlsPerLvl[j]+= 1;
				}
			}
		}

		int logsTotal = 0;
		for (int i : logLvlsPerLvl) {
			logsTotal += i;
		}
		// Available values : ALL, DEBUG, INFO, WARN, ERROR, FATAL, TRACE, OFF
		g.setColor(Color.red);
		g.draw3DRect(H_OFFSET, 0, BAR_WIDTH, BAR_HEIGTH, true);
		g.fill3DRect(H_OFFSET, 0, BAR_WIDTH * logLvlsPerLvl[0] / logsTotal, BAR_HEIGTH, true);
		g.drawString("" + logLvlsPerLvl[0] + " DEBUG", 5, BAR_HEIGTH / 2);

		g.setColor(Color.green);
		g.draw3DRect(H_OFFSET, BAR_HEIGTH + V_OFFSET, BAR_WIDTH, BAR_HEIGTH, true);
		g.fill3DRect(H_OFFSET, BAR_HEIGTH + V_OFFSET, BAR_WIDTH * logLvlsPerLvl[1] / logsTotal, BAR_HEIGTH, true);
		g.drawString("" + logLvlsPerLvl[1] + " INFO", 5, BAR_HEIGTH + V_OFFSET + BAR_HEIGTH / 2);

		g.setColor(Color.blue);
		g.draw3DRect(H_OFFSET, 2 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH, BAR_HEIGTH, true);
		g.fill3DRect(H_OFFSET, 2 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH * logLvlsPerLvl[2] / logsTotal, BAR_HEIGTH, true);
		g.drawString("" + logLvlsPerLvl[2] + " WARN", 5, 2 * (BAR_HEIGTH + V_OFFSET) + BAR_HEIGTH / 2);

		g.setColor(Color.white);
		g.draw3DRect(H_OFFSET, 3 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH, BAR_HEIGTH, true);
		g.fill3DRect(H_OFFSET, 3 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH * logLvlsPerLvl[3] / logsTotal, BAR_HEIGTH, true);
		g.drawString("" + logLvlsPerLvl[3] + " ERROR", 5, 3 * (BAR_HEIGTH + V_OFFSET) + BAR_HEIGTH / 2);

		g.setColor(Color.orange);
		g.draw3DRect(H_OFFSET, 4 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH, BAR_HEIGTH, true);
		g.fill3DRect(H_OFFSET, 4 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH * logLvlsPerLvl[4] / logsTotal, BAR_HEIGTH, true);
		g.drawString("" + logLvlsPerLvl[4] + " FATAL", 5, 4 * (BAR_HEIGTH + V_OFFSET) + BAR_HEIGTH / 2);
		
		g.setColor(Color.magenta);
		g.draw3DRect(H_OFFSET, 5 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH, BAR_HEIGTH, true);
		g.fill3DRect(H_OFFSET, 5 * (BAR_HEIGTH + V_OFFSET), BAR_WIDTH * logLvlsPerLvl[5] / logsTotal, BAR_HEIGTH, true);
		g.drawString("" + logLvlsPerLvl[5] + " TRACE", 5, 5 * (BAR_HEIGTH + V_OFFSET) + BAR_HEIGTH / 2);

		javax.imageio.ImageIO.write(image, "png", out);

		// clean up - important in order to free resources on the server
		g.dispose();
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
