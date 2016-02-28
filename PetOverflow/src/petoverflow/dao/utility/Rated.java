package petoverflow.dao.utility;

/**
 * All rated elements have to implement getRating
 *
 */
public interface Rated {

	public double getRating() throws Exception;
	
}
