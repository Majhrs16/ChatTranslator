package me.majhrs16.cht.storage;

import me.majhrs16.lib.storages.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Statement;
import java.util.UUID;

public abstract class SQL extends Database {
	private static final String table = "Storage";

	public SQL(String driver, String type) {
		super(driver, type);
	}

	public abstract void connect() throws SQLException;

	public void createTable() throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS " + table + " (uuid TEXT, discordID TEXT, lang TEXT)";
		try (Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		}
	}

	public String[] get(UUID uuid) throws SQLException {
		return getData("SELECT * FROM " + table + " WHERE uuid = ?", uuid.toString());
	}

	public String[] get(String discordID) throws SQLException {
		return getData("SELECT * FROM " + table + " WHERE discordID = ?", discordID);
	}

	private String[] getData(String sql, String param) throws SQLException {
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setString(1, param);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				return new String[] {
						result.getString(1),
						result.getString(2),
						result.getString(3)
				};
			} else {
				return null;
			}
		}
	}

	public void insert(UUID uuid, String discordID, String lang) throws SQLException {
		String sql = "INSERT INTO " + table + " (uuid, discordID, lang) VALUES (?, ?, ?)";

		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setString(1, uuid.toString());
			statement.setString(2, discordID);
			statement.setString(3, lang);
			statement.executeUpdate();
		}
	}

	public void update(UUID uuid, String discordID, String lang) throws SQLException {
		String sql = "UPDATE " + table + " SET lang = ?%s WHERE uuid = ?";

		if (discordID == null)
			sql = String.format(sql, "");

		else
			sql = String.format(sql, ", discordID = ?");

		try (PreparedStatement statement = conn.prepareStatement(sql)) {

			statement.setString(1, lang);

			if (discordID == null) {
				statement.setString(2, uuid.toString());

			} else {
				statement.setString(2, discordID);
				statement.setString(3, uuid.toString());
			}

			statement.executeUpdate();
		}
	}
}