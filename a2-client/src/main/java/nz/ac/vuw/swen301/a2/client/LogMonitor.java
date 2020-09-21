package nz.ac.vuw.swen301.a2.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LogMonitor extends JFrame implements WindowListener {
	private JPanel mainPanel;
	private JFrame main;
	private String[][] data = new String[100][5];
	private final Gson gson = new GsonBuilder().create();
	URI logServiceURL = URI.create("http://localhost:8080/resthome4logs/logs");

	public LogMonitor() {
		begin();
	}

	public static void main(String[] args) {
		new LogMonitor();
	}

	public void begin() {

		main = new JFrame("Log Monitor");
		JPanel tablePanel = new JPanel();
		JPanel headerPanel = new JPanel();
		mainPanel = new JPanel();// new BoxLayout(mainPanel, BoxLayout.Y_AXIS)
		main.setLayout(null);
		main.setContentPane(mainPanel);
		main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JLabel min = new JLabel("Min Level");
		min.setHorizontalAlignment(JLabel.LEFT);
		JLabel lim = new JLabel("limit");
		min.setHorizontalAlignment(JLabel.RIGHT);

		// Available values : ALL, DEBUG, INFO, WARN, ERROR, FATAL, TRACE, OFF
		String minLevel[] = { "ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "TRACE", "OFF" };
		JComboBox cb = new JComboBox(minLevel);
		cb.setBounds(50, 50, 90, 20);

		String[] limit = new String[100];
		for (int i = 0; i < 100; i++) {
			limit[i] = "" + i;
		}
		JComboBox limitcb = new JComboBox(limit);
		cb.setBounds(50, 50, 90, 20);

		JButton fetchB = new JButton("FETCH");
		

		String column[] = { "TIME", "LEVEL", "LOGGER", "THREAD", "MESSAGE" };
		
		JTable jt = new JTable(this.data, column);
		JScrollPane sp = new JScrollPane(jt);
		
		fetchB.addActionListener((l) -> {
			try {
				this.data = fetch(Integer.parseInt((String) limitcb.getItemAt(limitcb.getSelectedIndex())),
						(String) cb.getItemAt(cb.getSelectedIndex()));
			} catch (Exception e) {
			}
			for(int i = 0; i<this.data.length;i++) {
				for(int j =0; j< this.data[0].length;j++) {
					if(this.data[i][j]== null) {
						jt.getModel().setValueAt("Empty", i, j);
					}
					else jt.getModel().setValueAt(this.data[i][j], i, j);
				}
			}
			repaint();
		});
		

		headerPanel.add(min);
		headerPanel.add(cb);
		headerPanel.add(lim);
		headerPanel.add(limitcb);
		headerPanel.add(fetchB);
		tablePanel.add(sp);
		main.getContentPane().add(headerPanel);
		main.getContentPane().add(tablePanel);
		main.setSize(500, 600);
		main.setLocation(85, 100);
		main.setVisible(true);
		// main.repaint();

	}

	private String[][] fetch(int lim, String lvl) throws Exception{
		String[][] data = new String[lim][5]; // TODO

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http")
		.setHost("localhost")
		.setPort(8080)
		.setPath("resthome4logs/logs")
		.setParameter("limit",  Integer.toString(lim))
		.setParameter("level",  lvl);
		
		logServiceURL = builder.build();

		// create and execute the request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(logServiceURL);
		HttpResponse response = httpClient.execute(request);

		String content = EntityUtils.toString(response.getEntity());
		
		String[] msgList = content.split("\n");
		
		String[] logsList = msgList[3].split("}");
		for(int i = 0; i<logsList.length-1; i++) {
			//"TIME", "LEVEL", "LOGGER", "THREAD", "MESSAGE"
			data[i][0] = logsList[i].substring(logsList[i].indexOf("timestamp") + 12, logsList[i].indexOf("thread")-3);
			data[i][1] = logsList[i].substring(logsList[i].indexOf("level") + 10, logsList[i].indexOf("timestamp")-5); 
			data[i][2] = logsList[i].substring(logsList[i].indexOf("logger") +11, logsList[i].indexOf("level")-5);
			data[i][3] = logsList[i].substring(logsList[i].indexOf("thread") + 11, logsList[i].indexOf("message")-5);
			data[i][4] = logsList[i].substring(logsList[i].indexOf("message") + 12, logsList[i].length()-2);
			/*Date date = new Date(Long.parseLong(data[0][0]));
			System.out.println(date.toString());*/
		} 
		this.data = data;
		return data;
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
