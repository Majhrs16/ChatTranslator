package majhrs16.lib.storages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database {
	protected Connection conn;

	protected String host;
	protected int port;
	protected String database;
	protected String user;
	protected String password;

	public void set(String host, int port, String database, String user, String password) {
		this.host     = host;
		this.port     = port;
		this.database = database;
		this.user     = user;
		this.password = password;
	}

	public void connect() throws SQLException {
		try {
			Class.forName("DB.driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String url = "jdbc:typeDB://" + host + ":" + port + "/" + database;
		conn = DriverManager.getConnection(url, user, password);
	}

	public void disconnect() throws SQLException {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}
}
