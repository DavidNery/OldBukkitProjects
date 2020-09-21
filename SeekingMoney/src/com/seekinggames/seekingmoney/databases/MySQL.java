package com.seekinggames.seekingmoney.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.seekinggames.seekingmoney.Conta;
import com.seekinggames.seekingmoney.SeekingMoney;

public class MySQL {
	
	private Connection conn;
	private Statement stmt;
	
	public MySQL(String user, String password, String host, String database){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "", user, password);
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS `contas` (`player` VARCHAR(255), `money` VARCHAR(255))");
		} catch (SQLException | ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void closeConnection(){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addPlayer(String player, String money){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "INSERT INTO `contas` (`player`, `money`) VALUES ('" + player + "', '" + money + "');";
			stmt.executeUpdate(sql);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatePlayer(String player, String money){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "UPDATE `contas` SET `money`='" + money + "' WHERE `player`='" + player + "';";
			stmt.executeUpdate(sql);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasPlayer(String player) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT * FROM `contas` WHERE `player`='" + player + "';";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				 return rs.getString("player").equalsIgnoreCase(player);
			}
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void updateContas(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
				if(!hasPlayer(contas.getNome())){
					addPlayer(contas.getNome(), contas.getMoney() + "");
				}else{
					updatePlayer(contas.getNome(), contas.getMoney() + "");
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void putAllUsers(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT * FROM `contas`";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				SeekingMoney.getSeekingMoney().getPlayers().add(new Conta(rs.getString("player"), Double.valueOf(rs.getString("money"))));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void putTops(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT * FROM `contas` ORDER BY `money` DESC LIMIT " + SeekingMoney.getSeekingMoney().getConfig().getInt("Top_Quantidade");
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				SeekingMoney.getSeekingMoney().getTops().put(rs.getString("player"), Double.valueOf(rs.getString("money")));
				SeekingMoney.getSeekingMoney().getTopsArray().add(rs.getString("player"));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateAllUsers(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			for(Conta players : SeekingMoney.getSeekingMoney().getPlayers()){
				String sql = "UPDATE `contas` SET `money`='" + players.getMoney() + "' WHERE `player`='" + players.getNome() + "';";
				stmt.executeUpdate(sql);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeContas(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT * FROM `contas`";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				if(!SeekingMoney.getSeekingMoney().getPlayers().contains(rs.getString("player"))){
					String sql1 = "DELETE FROM `contas` WHERE `player`='" + rs.getString("player") + "';";
					stmt.executeUpdate(sql1);
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

}
