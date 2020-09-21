package me.zfork.spartanoscraft.spartanosbitcoins.tasks;

import java.sql.SQLException;

import me.zfork.spartanoscraft.spartanosbitcoins.SpartanosBitCoins;
import me.zfork.spartanoscraft.spartanosbitcoins.dbmanager.MySQL;

import org.bukkit.scheduler.BukkitRunnable;

public class VerifyTask extends BukkitRunnable{

	private MySQL mysql;
	
	public VerifyTask(SpartanosBitCoins instance) {
		this.mysql = instance.getMySQL();
	}

	@Override
	public void run() {
		try {
			mysql.openConnection();
			mysql.verifyPlayerPayments();
			mysql.closeConnection();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

}
