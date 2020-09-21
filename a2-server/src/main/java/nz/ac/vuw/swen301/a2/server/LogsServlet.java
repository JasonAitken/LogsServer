package nz.ac.vuw.swen301.a2.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import org.apache.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LogsServlet extends HttpServlet {
	private List<String> stringLeList = new ArrayList<String>();
	private int limit;
	private Level minLevel;
	static Gson gson = new GsonBuilder().create();
	String fail = null;

	public LogsServlet() {

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String jsonString = "[ \n";

		if (request.getParameter("limit") == null || request.getParameter("level") == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (request.getParameter("limit").equals("ALL")) {
			limit = stringLeList.size();
		} else {
			limit = Integer.parseInt(request.getParameter("limit")); // get the max amount of logs and level to return
			if (limit <= 0) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}

		String lvlStr = request.getParameter("level");
		if (!lvlStr.equals("ALL") && !lvlStr.equals("DEBUG") && !lvlStr.equals("INFO") && !lvlStr.equals("WARN")
				&& !lvlStr.equals("ERROR") && !lvlStr.equals("FATAL") && !lvlStr.equals("TRACE")
				&& !lvlStr.equals("OFF")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} else {
			minLevel = Level.toLevel(request.getParameter("level"));
		}

		boolean goodLevel = true;
		// Available values : ALL, DEBUG, INFO, WARN, ERROR, FATAL, TRACE, OFF
		// This is messy, but works
		for (int i = 0; i < this.limit; i++) {
			if (stringLeList.get(i).contains("ALL")) {
				if (Level.toLevel("ALL").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			} else if (stringLeList.get(i).contains("DEBUG")) {
				if (Level.toLevel("DEBUG").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			} else if (stringLeList.get(i).contains("INFO")) {
				if (Level.toLevel("INFO").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			} else if (stringLeList.get(i).contains("WARN")) {
				if (Level.toLevel("WARN").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			} else if (stringLeList.get(i).contains("ERROR")) {
				if (Level.toLevel("ERROR").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			} else if (stringLeList.get(i).contains("FATAL")) {
				if (Level.toLevel("FATAL").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			} else if (stringLeList.get(i).contains("TRACE")) {
				if (Level.toLevel("TRACE").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			} else if (stringLeList.get(i).contains("OFF")) {
				if (Level.toLevel("OFF").toInt() < minLevel.toInt()) {
					goodLevel = false;
				}
			}
			if (goodLevel) {
				jsonString += stringLeList.get(i);
				jsonString += ",";
			} else {
				this.limit++;
			} // by doing this it means it has skipped the addition of the log, and will ajust
		} // limit to ensure that the correct amount of logs are still returned

		jsonString += "] \n";

		out.println("<html>");
		out.println("<body>");
		out.println(jsonString);
		out.println("</body>");
		out.println("</html>");
		// response.sendError(HttpServletResponse.SC_OK);
		out.close();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (request.getParameter("LogEvent") == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String param = request.getParameter("LogEvent");
		//System.out.println(param);

		if (!isJSONValid(param)) {
			//System.out.println("not valid");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String[] lines = param.split(",");
		for (String line : lines) {
			
			if (line.contains("id")) {
				String ID = line.substring(9, line.length() - 1);
				System.out.println(ID);
				for (String search : stringLeList) {
					String searchID = search.substring(9, search.indexOf("logger") - 3);
					//System.out.println(searchID);
					if (searchID.equals(ID)) { //could parse if id is all numbers, but decided against this
						response.sendError(HttpServletResponse.SC_CONFLICT);
						return;
					}
				}
			}
		}
		String jsonString = gson.toJson(param);

		stringLeList.add(jsonString);

		response.sendError(HttpServletResponse.SC_CREATED);

	}

	public static boolean isJSONValid(String jsonInString) {
		try {
			gson.fromJson(jsonInString, Object.class);
			return true;
		} catch (com.google.gson.JsonSyntaxException ex) {
			return false;
		}
	}

}