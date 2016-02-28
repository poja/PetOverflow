package petoverflow.dao.utility;

import java.sql.Timestamp;

/**
 * All elements with timestamp need to implement getTimestamp
 *
 */
public interface Timestampable {

	public Timestamp getTimestamp() throws Exception;

}
