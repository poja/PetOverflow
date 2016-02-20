package petoverflow.dao.items;

/**
 * The Vote class represent a single vote of a user to a question or an answer.
 * The vote can be positive(Up) or negative(Down)
 */
public class Vote {

	/**
	 * Possible types of votes
	 */
	public enum VoteType {
		Up, Down
	}

	/**
	 * Id of this vote's voter
	 */
	private int m_voterId;

	/**
	 * Type of this vote
	 */
	private VoteType m_type;

	/**
	 * Constructor
	 * 
	 * This constructor is not meant to be used directly. It should be called
	 * through an votes DAO
	 * 
	 * @param voterId
	 *            id of this vote's voter
	 * @param type
	 *            type of the vote
	 */
	public Vote(int voterId, VoteType type) {
		m_voterId = voterId;
		m_type = type;
	}

	/**
	 * Get the id voter of this vote
	 * 
	 * @return this vote's voter id
	 */
	public int getVoterId() {
		return m_voterId;
	}

	/**
	 * Get the type of this vote
	 * 
	 * @return this vote's type
	 */
	public VoteType getType() {
		return m_type;
	}

	public int hashCode() {
		return m_voterId ^ ((m_type == VoteType.Up ? 1 : -1) << 16);
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof Vote)) {
			return false;
		}

		Vote other = (Vote) o;
		if (m_voterId != other.m_voterId) {
			return false;
		} else if (!m_type.equals(other.m_type)) {
			return false;
		}
		return true;
	}

}
