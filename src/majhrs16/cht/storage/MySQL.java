package majhrs16.cht.storage;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQL {
	public MySQL() {
		super("com.mysql.jdbc.Driver", "mysql");
	}

	public void connect() throws SQLException {
		try {
			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String url = "jdbc:" + type + "://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
		conn = DriverManager.getConnection(url, user, password);
	}
}