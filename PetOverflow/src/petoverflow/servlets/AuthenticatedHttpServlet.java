package petoverflow.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import petoverflow.authentication.DataValidator;
import petoverflow.dao.DaoManager;
import petoverflow.dao.items.User;

/**
 * An authenticated version of the HttpServlet that checks if the request is
 * allowed, if not send an error massage as a response
 */
public class AuthenticatedHttpServlet extends HttpServlet {

	protected DaoManager m_daoManager;

	private static final long serialVersionUID = 1L;

	public AuthenticatedHttpServlet() {
		super();
		m_daoManager = DaoManager.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			try {
				doAuthenticatedPut(request, response, getCurrentUser(request));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			try {
				doAuthenticatedPost(request, response, getCurrentUser(request));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			try {
				doAuthenticatedGet(request, response, getCurrentUser(request));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			try {
				doAuthenticatedDelete(request, response, getCurrentUser(request));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			try {
				doAuthenticatedHead(request, response, getCurrentUser(request));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			try {
				doAuthenticatedOptions(request, response, getCurrentUser(request));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int authenticatorResponse = authenticate(request);
		if (authenticatorResponse != HttpServletResponse.SC_CONTINUE) {
			response.sendError(authenticatorResponse);
		} else {
			try {
				doAuthenticatedTrace(request, response, getCurrentUser(request));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
	}

	/**
	 * Do Authenticated Put
	 * 
	 * Authenticated version of {@link HttpServlet#doPut}
	 */
	protected void doAuthenticatedPut(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated post
	 * 
	 * Authenticated version of {@link HttpServlet#doPost}
	 */
	protected void doAuthenticatedPost(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated get
	 * 
	 * Authenticated version of {@link HttpServlet#doGet}
	 */
	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated delete
	 * 
	 * Authenticated version of {@link HttpServlet#doDelete}
	 */
	protected void doAuthenticatedDelete(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated head
	 * 
	 * Authenticated version of {@link HttpServlet#doHead}
	 */
	protected void doAuthenticatedHead(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated options
	 * 
	 * Authenticated version of {@link HttpServlet#doOptions}
	 */
	protected void doAuthenticatedOptions(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
	}

	/**
	 * Do authenticated trace
	 * 
	 * Authenticated version of {@link HttpServlet#doTrace}
	 */
	protected void doAuthenticatedTrace(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
	}

	/**
	 * Get the current logged in user
	 * 
	 * @param request
	 *            the request from the client
	 * @return the current logged in user
	 * @throws Exception
	 *             if DAO fails
	 */
	protected User getCurrentUser(HttpServletRequest request) throws Exception {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		String username = null;
		for (Cookie c : cookies) {
			if (c.getName().equals("username")) {
				username = c.getValue();
			}
		}
		if (username == null) {
			return null;
		}
		return m_daoManager.getUserDao().getUser(username);
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
				isAuthentication = m_daoManager.getUserDao().isAuthenticationPair(username, password);
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
