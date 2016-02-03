package petoverflow.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import petoverflow.dao.UserDao;
import petoverflow.dao.derby.UserDaoDerby;

/**
 * An authenticated version of the HttpServlet that checks if the request is
 * allowed, if not send an error massage as a response
 */
public class AuthenticatedHttpServlet extends HttpServlet {

	private UserDao m_userDao;

	private static final long serialVersionUID = 1L;

	public AuthenticatedHttpServlet() {
		super();
		m_userDao = UserDaoDerby.getInstance();
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			doAuthenticatedPut(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			doAuthenticatedPost(request, response);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			doAuthenticatedGet(request, response);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			doAuthenticatedDelete(request, response);
		}
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			doAuthenticatedHead(request, response);
		}
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			doAuthenticatedOptions(request, response);
		}
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			doAuthenticatedTrace(request, response);
		}
	}

	/**
	 * Do Authenticated Put
	 * 
	 * Authenticated version of {@link HttpServlet#doPut}
	 */
	protected void doAuthenticatedPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated post
	 * 
	 * Authenticated version of {@link HttpServlet#doPost}
	 */
	protected void doAuthenticatedPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated get
	 * 
	 * Authenticated version of {@link HttpServlet#doGet}
	 */
	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated delete
	 * 
	 * Authenticated version of {@link HttpServlet#doDelete}
	 */
	protected void doAuthenticatedDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated head
	 * 
	 * Authenticated version of {@link HttpServlet#doHead}
	 */
	protected void doAuthenticatedHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated options
	 * 
	 * Authenticated version of {@link HttpServlet#doOptions}
	 */
	protected void doAuthenticatedOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated trace
	 * 
	 * Authenticated version of {@link HttpServlet#doTrace}
	 */
	protected void doAuthenticatedTrace(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Checks if a request is authenticated
	 * 
	 * @param request
	 *            the request that being checked
	 * @return {@link HttpServletResponse#SC_ACCEPTED} if authenticated, else -
	 *         an error code from {@link HttpServletResponse}
	 */
	private int authenticate(HttpServletRequest request) {
		// Get username and password from the cookies
		Cookie[] cookies = request.getCookies();
		String username = null, password = null;

		if (cookies == null) {
			return HttpServletResponse.SC_UNAUTHORIZED;
		}

		for (Cookie c : cookies) {
			if (c.getName().equals("username")) {
				username = c.getValue();
			} else if (c.getName().equals("password")) {
				password = c.getValue();
			}
		}

		// Check if the username and password are valid, and if so, if they
		// match
		if (username == null || password == null) {
			return HttpServletResponse.SC_UNAUTHORIZED;
		} else if (!DataValidator.isValidUsername(username)) {
			return HttpServletResponse.SC_UNAUTHORIZED;
		} else if (!DataValidator.isValidPassword(username)) {
			return HttpServletResponse.SC_UNAUTHORIZED;
		} else {
			boolean isAuthentication;
			try {
				isAuthentication = m_userDao.isAuthenticationPair(username, password);
				if (!isAuthentication) {
					return HttpServletResponse.SC_UNAUTHORIZED;
				} else {
					return HttpServletResponse.SC_CONTINUE;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return HttpServletResponse.SC_UNAUTHORIZED;
			}

		}

	}

}
