package me.zfork.fkitspro.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.zfork.fkitspro.PlayersKits;

public class MySQL {

	private Connection connection;
	private PreparedStatement stmt;

	private String mysqlHost, mysqlUser, mysqlPass, mysqlDB;

	public MySQL(String mysqlHost, String mysqlUser, String mysqlPass, String mysqlDB) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + mysqlHost + "/" + mysqlDB + "", mysqlUser, mysqlPass);
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS fkitsdelay (player VARCHAR(255), kit VARCHAR(255), tempo VARCHAR(255))");
		stmt.executeUpdate();
		closeConnection();
		this.mysqlHost = mysqlHost;
		this.mysqlUser = mysqlUser;
		this.mysqlPass = mysqlPass;
		this.mysqlDB = mysqlDB;
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		if(connection == null) connection = DriverManager.getConnection("jdbc:mysql://" + mysqlHost + "/" + mysqlDB + "", mysqlUser, mysqlPass);
	}

	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("com.mysql.jdbc.Driver");
			if(stmt != null) stmt.close();
			connection.close();
			stmt = null;
			connection = null;
		}
	}

	public void addNew(final String player, final String kit, final String tempo) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		stmt = connection.prepareStatement("INSERT INTO fkitsdelay (player, kit, tempo) VALUES (?, ?, ?);");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, kit);
		stmt.setString(3, tempo);
		stmt.executeUpdate();
	}
	
	public void restart() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		stmt = connection.prepareStatement("DROP TABLE IF EXISTS fkitsdelay");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS fkitsdelay (player VARCHAR(255), kit VARCHAR(255), tempo VARCHAR(255))");
		stmt.executeUpdate();
	}
	
	public synchronized void downloadPlayersKits() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		stmt = connection.prepareStatement("SELECT * FROM fkitsdelay");
		final ResultSet rs = stmt.executeQuery();
		String player = "";
		PlayersKits playersKits = null;
		while(rs.next()){
			if(!player.equals(rs.getString("player"))){
				playersKits = new PlayersKits(rs.getString("player"));
				player = rs.getString("player");
			}
			playersKits.addDelay(rs.getString("kit"), Long.parseLong(rs.getString("tempo")));
		}
	}

}
