package majhrs16.ct.storage.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import majhrs16.ct.storage.DataBase;

public class SQLite implements DataBase {
    private Connection conn;
    private String database;

	public void set(String host, int port, String database, String user, String password) {
		this.database = database;
	}

    public void connect() throws SQLException {
    	try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	conn = DriverManager.getConnection("jdbc:sqlite:plugins/ChatTranslator/" + database + ".db");
    }

    public void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Players (UUID TEXT, Lang TEXT)";
        conn.createStatement().execute(sql);
    }

    public void insert(UUID uuid, String lang) throws SQLException {
        String sql = "INSERT INTO Players (UUID, Lang) VALUES (?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
       	statement.setString(1, uuid.toString());
       	statement.setString(2, lang);
        statement.executeUpdate();
    }
    
    public String get(UUID uuid) throws SQLException {
        String sql = "SELECT Lang FROM Players WHERE UUID = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, uuid.toString());
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            return result.getString(1);
        } else {
            return null;
        }
    }
    
    public void update(UUID uuid, String lang) throws SQLException {
        String sql = "UPDATE Players SET Lang = ? WHERE UUID = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, lang);
        statement.setString(2, uuid.toString());
        statement.executeUpdate();
    }
}