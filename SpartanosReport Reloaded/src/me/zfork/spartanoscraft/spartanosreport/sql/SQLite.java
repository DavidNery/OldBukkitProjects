package me.zfork.spartanoscraft.spartanosreport.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import me.zfork.spartanoscraft.spartanosreport.SpartanosReport;
import me.zfork.spartanoscraft.spartanosreport.utils.PlayerReports;
import me.zfork.spartanoscraft.spartanosreport.utils.PlayerReportsManager;

public class SQLite {

	private SpartanosReport instance;
	private PlayerReportsManager playerreportsmanager;
	private Connection connection;
	private PreparedStatement stmt;

	public SQLite(SpartanosReport instance) throws SQLException, ClassNotFoundException, IOException {
		this.instance = instance;
		this.playerreportsmanager = instance.getPlayerReportsManager();
		Class.forName("org.sqlite.JDBC");
		if(!new File(instance.getDataFolder(), "reports.db").exists()) new File(instance.getDataFolder().getAbsolutePath() + File.separator + "reports.db").createNewFile();
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "reports.db");
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reports (player VARCHAR(255), report VARCHAR(255), quantidade INTEGER, lastreporter VARCHAR (255)"
				+ ", data VARCHAR(255))");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS fechados (player VARCHAR(255), report VARCHAR(255), quantidade INTEGER, remover VARCHAR(255), "
				+ "lastreporter VARCHAR (255), data VARCHAR(255))");
		stmt.executeUpdate();
		closeConnection();
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "reports.db");
	}

	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("org.sqlite.JDBC");
			connection.close();
			stmt.close();
			connection = null;
			stmt = null;
		}
	}
	
	public void delPlayer(final String player) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("DELETE FROM reports WHERE player=?;");
		stmt.setString(1, player.toLowerCase());
		stmt.executeUpdate();
	}
	
	public void saveReports() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		for(PlayerReports pr : playerreportsmanager.getReports()){
			for(Map.Entry<String, Integer> reports : pr.getReports().entrySet()){
				stmt = connection.prepareStatement("INSERT INTO reports (player, report, quantidade, lastreporter, data) VALUES (?, ?, ?, ?, ?);");
				stmt.setString(1, pr.getPlayer());
				stmt.setString(2, reports.getKey());
				stmt.setInt(3, reports.getValue());
				stmt.setString(4, pr.getLastReporter());
				stmt.setString(5, pr.getDate());
				stmt.executeUpdate();
			}
		}
		for(PlayerReports pr : playerreportsmanager.getClosedReports()){
			for(Map.Entry<String, Integer> reports : pr.getReports().entrySet()){
				stmt = connection.prepareStatement("INSERT INTO fechados (player, report, quantidade, remover, lastreporter, data) VALUES (?, ?, ?, ?, ?, ?);");
				stmt.setString(1, pr.getPlayer());
				stmt.setString(2, reports.getKey());
				stmt.setInt(3, reports.getValue());
				stmt.setString(4, pr.getRemove()+"");
				stmt.setString(5, pr.getLastReporter());
				stmt.setString(6, pr.getDate());
				stmt.executeUpdate();
			}
		}
	}
	
	public void removeReports() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("DROP TABLE reports");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("DROP TABLE fechados");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reports (player VARCHAR(255), report VARCHAR(255), quantidade INTEGER, lastreporter VARCHAR (255)"
				+ ", data VARCHAR(255))");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS fechados (player VARCHAR(255), report VARCHAR(255), quantidade INTEGER, remover VARCHAR(255), "
				+ "lastreporter VARCHAR (255), data VARCHAR(255))");
		stmt.executeUpdate();
	}
	
	public void loadReports() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM reports");
		ResultSet rs = stmt.executeQuery();
		String last = "", report;
		PlayerReports pr = null;
		while(rs.next()){
			if(!last.equalsIgnoreCase(rs.getString("player"))){
				last = rs.getString("player");
				pr = new PlayerReports(rs.getString("player"), rs.getString("report"), rs.getString("lastreporter"), rs.getString("data"));
				pr.getReports().put(rs.getString("report"), rs.getInt("quantidade"));
				playerreportsmanager.addNewReport(pr);
			}else{
				report = rs.getString("report");
				playerreportsmanager.addOneReport(pr, report, rs.getInt("quantidade"));
			}
			pr.setReportsTotal(pr.getReportsTotal()+rs.getInt("quantidade"));
		}
		stmt = connection.prepareStatement("SELECT * FROM fechados");
		rs = stmt.executeQuery();
		last = "";
		while(rs.next()){
			if(!last.equalsIgnoreCase(rs.getString("player"))){
				last = rs.getString("player");
				pr = new PlayerReports(rs.getString("player"), rs.getString("report"), rs.getString("lastreporter"), rs.getString("data"));
				pr.setRemove(Long.parseLong(rs.getString("remover")));
				pr.getReports().put(rs.getString("report"), rs.getInt("quantidade"));
				playerreportsmanager.addNewClosedReport(pr);
			}else{
				report = rs.getString("report");
				playerreportsmanager.addClosedReport(pr, rs.getString("report"), rs.getInt("quantidade"));
			}
			pr.setReportsTotal(pr.getReportsTotal()+rs.getInt("quantidade"));
		}
	}

}
