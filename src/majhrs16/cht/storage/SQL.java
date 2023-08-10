package majhrs16.cht.storage;

import majhrs16.lib.storages.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.UUID;

public class SQL extends Database {
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
			return result.getString("Lang");
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
