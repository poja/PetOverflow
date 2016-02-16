package petoverflow.dao;

public abstract class DaoObject {

	protected final DaoManager m_daoManager;

	protected DaoObject(DaoManager daoManager) {
		m_daoManager = daoManager;
	}

}
