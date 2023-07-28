package majhrs16.ct.storage.data;

import java.sql.DriverManager;
import java.sql.SQLException;

import majhrs16.ct.storage.SQL;

public class MySQL extends SQL {
	public void connect() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
//			Class.forName("org.mariadb.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		conn = DriverManager.getConnection(url, user, password);
	}
}
