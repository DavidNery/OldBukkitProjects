package me.zfork.fpin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {

	private static Connection connection;
	private static Statement stmt;

	public SQLite() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + FPin.getFPin().getDataFolder().getAbsolutePath() + File.separator + "pins.db");
		stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS `pins` (`player` VARCHAR(255), `pin` VARCHAR(255), `email` VARCHAR(255), `data` VARCHAR(255))");
		closeConnection();
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + FPin.getFPin().getDataFolder().getAbsolutePath() + File.separator + "pins.db");
		stmt = connection.createStatement();
	}

	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("org.sqlite.JDBC");
			connection.close();
			stmt = null;
		}
	}

	public void addNew(final String player, final String pin, final String email, final String data) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		final String sql = "INSERT INTO `pins` (`player`, `pin`, `email`, `data`) VALUES ('" + player + "', '" + pin + "', '" + email + "', '" + data + "');";
		stmt.executeUpdate(sql);
	}
	
	public void delPlayer(final String player) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		final String sql = "DELETE FROM `pins` WHERE `player`='" + player + "';";
		stmt.executeUpdate(sql);
	}
	
	public boolean hasPlayer(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		final String sql = "SELECT * FROM `pins` WHERE `player`='" + player + "';";
		final ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) return rs.getString("player").equalsIgnoreCase(player);
		return false;
	}
	
	public String getPin(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		final String sql = "SELECT `pin` FROM `pins` WHERE `player`='" + player + "'";
		final ResultSet rs = stmt.executeQuery(sql);
		return rs.getString("pin");
	}
	
	public String getEmail(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		final String sql = "SELECT `email` FROM `pins` WHERE `player`='" + player + "'";
		final ResultSet rs = stmt.executeQuery(sql);
		return rs.getString("email");
	}
	
	public String getData(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		final String sql = "SELECT `data` FROM `pins` WHERE `player`='" + player + "'";
		final ResultSet rs = stmt.executeQuery(sql);
		return rs.getString("data");
	}

}
