package majhrs16.ct.storage;

import java.sql.SQLException;

public interface DataBase {
	public void set(String host, int port, String database, String user, String password);

    public void connect() throws SQLException;

    public void disconnect() throws SQLException;
}
