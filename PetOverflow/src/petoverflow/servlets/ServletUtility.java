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

	public static HashMap<String, String> getRequestParameters(HttpServletRequest request) throws IOException {
		String data = getRequestData(request);
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		HashMap<String, String> params = gson.fromJson(data, HashMap.class);
		return params;
	}

	public static List<String> convertListFromJson(String arrayStr) {
		Gson gson = new Gson();
		String[] array = gson.fromJson(arrayStr, String[].class);
		return Arrays.asList(array);
	}

	@Deprecated
	public static String getRequestData(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
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
