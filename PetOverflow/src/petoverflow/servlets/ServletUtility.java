package petoverflow.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

public class ServletUtility {

	public static HashMap<String, Object> getRequestParameters(HttpServletRequest request) throws IOException {
		String data = getRequestData(request);
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		HashMap<String, Object> params = gson.fromJson(data, HashMap.class);
		return params;
	}

	public static List<String> convertListFromJson(String arrayStr) {
		Gson gson = new Gson();
		String[] array = gson.fromJson(arrayStr, String[].class);
		return Arrays.asList(array);
	}

	private static String getRequestData(HttpServletRequest request) throws IOException {
		String method = request.getMethod();
		if (method.equals("GET")) {
			return request.getParameter("data");
		} else {
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		}
	}

	public static String getPath(HttpServletRequest request) throws ServletException {
		String fullPath = request.getRequestURI();
		String contexPath = request.getContextPath();
		if (!fullPath.startsWith(contexPath)) {
			throw new ServletException("Unexpected, Invalid URI: " + fullPath + " expected start with " + contexPath);
		}
		if (fullPath.length() == contexPath.length()) {
			return fullPath;
		} else {
			return fullPath.substring(contexPath.length());
		}
	}

}
