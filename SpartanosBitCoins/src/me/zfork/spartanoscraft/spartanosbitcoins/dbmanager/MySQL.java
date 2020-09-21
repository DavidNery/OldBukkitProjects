package me.zfork.spartanoscraft.spartanosbitcoins.dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.zfork.spartanoscraft.spartanosbitcoins.SpartanosBitCoins;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MySQL {

	private SpartanosBitCoins instance;
	private String user, password, database, host, tabela;
	private Connection connection;

	public MySQL(SpartanosBitCoins instance, final String user, final String password, final String database, final String host, final String tabela) {
		this.instance = instance;
		this.user = user;
		this.password = password;
		this.database = database;
		this.host = host;
		this.tabela = tabela;
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, user, password);
	}

	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("com.mysql.jdbc.Driver");
			connection.close();
			connection = null;
		}
	}

	@SuppressWarnings("deprecation")
	public void verifyPlayerPayments() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + tabela + ";");
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			String player = rs.getString("nick");
			int qnt = rs.getInt("bitcoins");
			deletePayment(player);
			if(instance.getConfig().getBoolean("Config.Avisar_Ativacao_Pagamento_A_Player")){
				Player p = instance.getServer().getPlayer(player);
				if(p != null){
					for(String msg : instance.getConfig().getStringList("Mensagem.Pagamento_Aprovado_Player"))
						p.sendMessage(msg.replace("{qnt}", qnt+"").replace("&", "§"));
					if(instance.getConfig().getBoolean("Config.Ativar_Som_Ativacao_Pagamento"))
						p.playSound(p.getLocation(), Sound.valueOf(instance.getConfig().getString("Config.SOUND")), 5.0F, 1.0F);
				}
			}
			if(instance.getConfig().getBoolean("Config.Avisar_Ativacao_Pagamento_A_Todos")
					&& instance.getServer().getOnlinePlayers().size() > 0){
				String msg = "";
				for(String s : instance.getConfig().getStringList("Mensagem.Pagamento_Aprovado_Todos"))
					msg += s.replace("{qnt}", qnt+"").replace("{player}", player).replace("&", "§") + "\n";
				for(Player on : instance.getServer().getOnlinePlayers())
					on.sendMessage(msg.substring(0, msg.length()-2));
			}
			instance.getPlayerPointsAPI().give(instance.getServer().getOfflinePlayer(player).getUniqueId(), qnt);
		}
	}

	public void deletePayment(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		final PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + tabela + " WHERE nick=?;");
		stmt.setString(1, player);
		stmt.executeUpdate();
	}

}
