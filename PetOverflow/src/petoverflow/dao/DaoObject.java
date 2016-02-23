package petoverflow.dao;

/**
 * The DaoObject class is used as super class of all DAO classes. This class
 * provide a member field of DAO manager and allow by it all DAO operations.
 */
public abstract class DaoObject {

	private final DaoManager m_daoManager;

	protected DaoObject(DaoManager daoManager) {
		m_daoManager = daoManager;
	}

	public DaoManager getDaoManager() {
		return m_daoManager;
	}

}
