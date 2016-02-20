package petoverflow;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import petoverflow.dao.utility.Rated;
import petoverflow.dao.utility.Timestampable;

public class Utility {

	public static <T> List<T> fromEnumeration(Enumeration<T> enumeration) {
		List<T> list = new ArrayList<T>();
		while (enumeration.hasMoreElements()) {
			list.add(enumeration.nextElement());
		}
		return list;
	}

	public static <T> List<T> cutList(List<T> list, int size, int offset) {
		if (list == null) {
			throw new IllegalArgumentException("list can't be null");
		} else if (size < 0) {
			throw new IllegalArgumentException("size shouldn't be negative");
		} else if (offset < 0) {
			throw new IllegalArgumentException("offset shouldn't be negative");
		}

		if (offset >= list.size()) {
			return new ArrayList<T>();
		}
		return list.subList(offset, Math.min(list.size(), offset + size));
	}

	/**
	 * Sorts a list, so that the highest rated elements are <b>first</b>
	 * 
	 * @param list
	 *            A list of Rated items to sort
	 */
	public static void sortByRating(List<? extends Rated> list) {
		final HashMap<Rated, Double> ratings = new HashMap<Rated, Double>();
		for (Rated e : list) {
			double rating = 0;
			try {
				rating = e.getRating();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			ratings.put(e, rating);
		}
		Collections.sort(list, new Comparator<Rated>() {

			@Override
			public int compare(Rated o1, Rated o2) {
				Double r1 = ratings.get(o1);
				Double r2 = ratings.get(o2);
				if (r1 == r2) {
					return 0;
				} else if (r1 < r2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	public static void sortByTimestamp(List<? extends Timestampable> list) {
		Collections.sort(list, new Comparator<Timestampable>() {

			@Override
			public int compare(Timestampable o1, Timestampable o2) {
				try {
					Timestamp t1 = o1.getTimestamp();
					Timestamp t2 = o2.getTimestamp();
					if (t1.equals(t2)) {
						return 0;
					} else if (t1.after(t2)) {
						return -1;
					} else
						return 1;

				} catch (Exception e) {
					return 0;
				}
			}
		});
	}

	public static void sendSms(String phoneNumber, String message) {
		System.out.println("SMS message:");
		System.out.println(message);
		System.out.println("To phone:");
		System.out.println(phoneNumber);
		phoneNumber = phoneNumber.replaceFirst("\\+", "+ ");

		try {
			String urlStr = "http://yishai.imrapid.io/sms?";
			urlStr += "&message=" + URLEncoder.encode(message, "UTF-8");
			urlStr += "&number=" + URLEncoder.encode(phoneNumber, "UTF-8");
			System.out.println("GET request to: ");
			System.out.println(urlStr);

			URL url = new URL(urlStr);
			ExecutorService executor = Executors.newFixedThreadPool(1);
			executor.submit(new SmsSender(url));
		} catch (Exception e) {
			// Ignore exceptions. If it didn't work - too bad.
		}
	}

}

class SmsSender implements Callable<Integer> {
	private URL m_url;

	public SmsSender(URL url) {
		m_url = url;
	}

	@Override
	public Integer call() {
		try {
			HttpURLConnection con = (HttpURLConnection) m_url.openConnection();
			con.setRequestMethod("GET");
			con.getInputStream().close();
			con.disconnect();
		} catch (Exception e) {
			// Doesn't matter, didn't work - too bad.
		}
		return 0;
	}
};
