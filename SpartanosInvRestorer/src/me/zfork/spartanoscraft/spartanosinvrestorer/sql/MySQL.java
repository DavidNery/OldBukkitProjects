package me.zfork.spartanoscraft.spartanosinvrestorer.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventory;
import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventoryUtils;

public class MySQL {
	
	private PlayerInventoryUtils playerInventoryUtils;
	private String user, password, database, host;
	private Connection connection;

	public MySQL(final String user, final String password, final String database, final String host) 
			throws ClassNotFoundException, SQLException {
		this.user = user;
		this.password = password;
		this.database = database;
		this.host = host;
		
		openConnection();
		createTable();
		closeConnection();
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		if(connection != null) return;
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
	
	private void createTable() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS inventories ("
				+ "player VARCHAR(20) NOT NULL, "
				+ "inventario LONGTEXT NOT NULL, "
				+ "preco DOUBLE NOT NULL, "
				+ "expiry BIGINT NOT NULL, "
				+ "podecomprar BOOLEAN NOT NULL, "
				+ "temitemraro BOOLEAN NOT NULL, "
				+ "id INTEGER PRIMARY KEY NOT NULL);");
		stmt.executeUpdate();
	}
	
	private void restartTable() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		PreparedStatement stmt = connection.prepareStatement("DROP TABLE inventories");
		stmt.executeUpdate();
		createTable();
	}
	
	private void addNew(PlayerInventory playerInventory) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO inventories (player, inventario, preco, expiry, podecomprar, temitemraro, id) VALUES (?, ?, ?, ?, ?, ?, ?)");
		stmt.setString(1, playerInventory.getOwnerName());
		stmt.setString(2, playerInventoryUtils.toBase64(playerInventory.getPreviewInventory()));
		stmt.setDouble(3, playerInventory.getPreco());
		stmt.setLong(4, playerInventory.getExpiryDate());
		stmt.setBoolean(5, playerInventory.getPodeComprar());
		stmt.setBoolean(6, playerInventory.getItemRaro());
		stmt.setInt(7, playerInventory.getId());
		stmt.executeUpdate();
	}
	
	public void saveToDataBase(ArrayList<PlayerInventory> inventories) throws ClassNotFoundException, SQLException {
		openConnection();
		restartTable();
		for(PlayerInventory inventory : inventories)
			addNew(inventory);
		closeConnection();
	}
	
	public ArrayList<PlayerInventory> getInventories() throws SQLException, ClassNotFoundException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		openConnection();
		ArrayList<PlayerInventory> inventories = new ArrayList<>();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM inventories ORDER BY player");
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			String ownerName = rs.getString("player");
			String playerInventoryInBase64 = rs.getString("inventario").trim();
			double preco = rs.getDouble("preco");
			long expiryDate = rs.getLong("expiry");
			PlayerInventory playerInventory = new PlayerInventory(ownerName, expiryDate, preco, rs.getBoolean("podecomprar"), rs.getBoolean("temitemraro"));
			playerInventory.setPreviewInventory(playerInventoryUtils.fromBase64(playerInventoryInBase64, playerInventory.getId()));
			inventories.add(playerInventory);
		}
		closeConnection();
		return inventories;
	}
	
	public void setPlayerInventoryUtils(PlayerInventoryUtils playerInventoryUtils) {
		this.playerInventoryUtils = playerInventoryUtils;
	}

}
