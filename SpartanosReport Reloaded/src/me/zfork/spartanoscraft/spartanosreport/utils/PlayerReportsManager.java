package me.zfork.spartanoscraft.spartanosreport.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import me.zfork.spartanoscraft.spartanosreport.SpartanosReport;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerReportsManager {

	private SpartanosReport instance;

	private ArrayList<PlayerReports> reports;
	private ArrayList<PlayerReports> closedReports;
	private ArrayList<Inventory> reportsInventories;
	private ArrayList<Inventory> closedReportsInventories;
	private HashMap<String, ArrayList<Inventory>> playerReportsInventories;
	private HashMap<String, ArrayList<Inventory>> playerClosedReportsInventories;

	private HashMap<String, String> staffLook;

	private ItemStack anterior, proximo, voltar, confirm, cancel, del;

	private Inventory optionsInv;

	public PlayerReportsManager(SpartanosReport instance) {
		this.instance = instance;
		this.reports = new ArrayList<>();
		this.closedReports = new ArrayList<>();
		this.reportsInventories = new ArrayList<>();
		Inventory inv = instance.getServer().createInventory(null, 54, "§c§lReports - §f§l1");
		reportsInventories.add(inv);
		this.closedReportsInventories = new ArrayList<>();
		inv = instance.getServer().createInventory(null, 54, "§c§lFechados - §f§l1");
		closedReportsInventories.add(inv);
		this.playerReportsInventories = new HashMap<>();
		this.playerClosedReportsInventories = new HashMap<>();

		this.staffLook = new HashMap<>();

		anterior = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Invs.Previous_Item"));
		proximo = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Invs.Next_Item"));
		voltar = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Invs.Back_Item"));
		confirm = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Invs.Confirm_Item"));
		cancel = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Invs.Cancel_Item"));
		del = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Invs.Del_Item"));

		optionsInv = instance.getServer().createInventory(null, 9, instance.getConfig().getString("Config.Invs.Options_Inv_Name").replace("&", "§"));
		optionsInv.setItem(1, confirm);
		optionsInv.setItem(3, voltar);
		optionsInv.setItem(5, cancel);
		optionsInv.setItem(7, del);
	}

	public ArrayList<PlayerReports> getReports() {
		return reports;
	}

	public ArrayList<Inventory> getReportsInventories() {
		return reportsInventories;
	}

	public HashMap<String, ArrayList<Inventory>> getPlayerReportsInventories() {
		return playerReportsInventories;
	}

	public HashMap<String, ArrayList<Inventory>> getPlayerClosedReportsInventories() {
		return playerClosedReportsInventories;
	}

	public HashMap<String, String> getStaffLook() {
		return staffLook;
	}

	public ItemStack getAnterior() {
		return anterior;
	}

	public ItemStack getVoltar() {
		return voltar;
	}

	public ItemStack getProximo() {
		return proximo;
	}

	public ItemStack getCancel() {
		return cancel;
	}

	public ItemStack getConfirm() {
		return confirm;
	}

	public ItemStack getDel() {
		return del;
	}

	public Inventory getOptionsInv() {
		return optionsInv;
	}
	
	public ArrayList<Inventory> getClosedReportsInventories() {
		return closedReportsInventories;
	}
	
	public ArrayList<PlayerReports> getClosedReports() {
		return closedReports;
	}

	public PlayerReports getPlayerReports(String player) {
		for(PlayerReports pr : reports)
			if(pr.getPlayer().equalsIgnoreCase(player))
				return pr;
		return null;
	}
	
	public PlayerReports getClosedPlayerReports(String player) {
		for(PlayerReports pr : closedReports)
			if(pr.getPlayer().equalsIgnoreCase(player))
				return pr;
		return null;
	}

	public void addNewReport(PlayerReports playerReports) {
		reports.add(playerReports);
		Inventory inv = reportsInventories.get(reportsInventories.size()-1);
		ItemStack head = instance.getHead(playerReports.getPlayer());
		ItemMeta headMeta = head.getItemMeta();
		headMeta.setDisplayName("§7Reports de §a" + playerReports.getPlayer());
		List<String> lore = new ArrayList<>();
		lore.add("§fReports do player §a" + playerReports.getPlayer());
		lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
		lore.add("§fReports:");
		for(Entry<String, Integer> reports : playerReports.getReports().entrySet())
			lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
		headMeta.setLore(lore);
		head.setItemMeta(headMeta);
		if(inv.firstEmpty() == 53){
			inv.setItem(53, proximo);
			inv = instance.getServer().createInventory(null, 54, "§c§lReports - §f§l" + (reportsInventories.size()+1));
			inv.setItem(45, anterior);
			inv.setItem(0, head);
			reportsInventories.add(inv);
		}else{
			inv.setItem(inv.firstEmpty(), head);
		}
		if(playerReportsInventories.containsKey(playerReports.getPlayer())){
			ArrayList<Inventory> invs = playerReportsInventories.get(playerReports.getPlayer());
			inv = invs.get(invs.size()-1);
		}else{
			ArrayList<Inventory> inventories = new ArrayList<>();
			inv = instance.getServer().createInventory(null, 54, "§c§lPlayer - §f§l1");
			inv.setItem(49, voltar);
			inventories.add(inv);
			playerReportsInventories.put(playerReports.getPlayer(), inventories);
		}
		String report = playerReports.getReports().keySet().iterator().next();
		ItemStack item = (ItemStack) instance.criarItem(
				instance.getConfig().getString("Config.Reports_Validos." + report.replace(" ", "_") + ".Report_Item")
				.replace("{player}", playerReports.getPlayer()).replace("{motivo}", report.replace(" ", "_")).replace("{motivoqnt}", playerReports.getReports().get(report)+"")
				.replace("{lastreporter}", playerReports.getLastReporter()));
		if(inv.firstEmpty() == 53){
			inv.setItem(53, proximo);
			ArrayList<Inventory> invs = playerReportsInventories.get(playerReports.getPlayer());
			inv = instance.getServer().createInventory(null, 54, "§c§lPlayer - §f§l" + (invs.size()+1));
			inv.setItem(45, anterior);
			inv.setItem(49, voltar);
			inv.setItem(0, item);
			invs.add(inv);
		}else{
			inv.setItem(inv.firstEmpty(), item);
		}
	}
	
	public void addNewClosedReport(PlayerReports playerReports) {
		closedReports.add(playerReports);
		Inventory inv = closedReportsInventories.get(closedReportsInventories.size()-1);
		ItemStack head = instance.getHead(playerReports.getPlayer());
		ItemMeta headMeta = head.getItemMeta();
		headMeta.setDisplayName("§7Reports de §a" + playerReports.getPlayer());
		List<String> lore = new ArrayList<>();
		lore.add("§fReports do player §a" + playerReports.getPlayer());
		lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
		lore.add("§fReports:");
		for(Entry<String, Integer> reports : playerReports.getReports().entrySet())
			lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
		headMeta.setLore(lore);
		head.setItemMeta(headMeta);
		if(inv.firstEmpty() == 53){
			inv.setItem(53, proximo);
			inv = instance.getServer().createInventory(null, 54, "§c§lFechados - §f§l" + (closedReportsInventories.size()+1));
			inv.setItem(45, anterior);
			inv.setItem(0, head);
			closedReportsInventories.add(inv);
		}else{
			inv.setItem(inv.firstEmpty(), head);
		}
		if(playerClosedReportsInventories.containsKey(playerReports.getPlayer())){
			ArrayList<Inventory> invs = playerClosedReportsInventories.get(playerReports.getPlayer());
			inv = invs.get(invs.size()-1);
		}else{
			ArrayList<Inventory> inventories = new ArrayList<>();
			inv = instance.getServer().createInventory(null, 54, "§c§lPlayer - §f§l1");
			inv.setItem(49, voltar);
			inventories.add(inv);
			playerClosedReportsInventories.put(playerReports.getPlayer(), inventories);
		}
		String report = playerReports.getReports().keySet().iterator().next();
		ItemStack item = (ItemStack) instance.criarItem(
				instance.getConfig().getString("Config.Reports_Validos." + report.replace(" ", "_") + ".Report_Item")
				.replace("{player}", playerReports.getPlayer()).replace("{motivo}", report.replace(" ", "_")).replace("{motivoqnt}", playerReports.getReports().get(report)+"")
				.replace("{lastreporter}", playerReports.getLastReporter()));
		if(inv.firstEmpty() == 53){
			inv.setItem(53, proximo);
			ArrayList<Inventory> invs = playerClosedReportsInventories.get(playerReports.getPlayer());
			inv = instance.getServer().createInventory(null, 54, "§c§lPlayer - §f§l" + (invs.size()+1));
			inv.setItem(45, anterior);
			inv.setItem(49, voltar);
			inv.setItem(0, item);
			invs.add(inv);
		}else{
			inv.setItem(inv.firstEmpty(), item);
		}
	}

	public void addReport(PlayerReports playerReports, ItemStack item, String report) {
		ItemStack head;
		loop :
			for(Inventory inv : reportsInventories){
				for(int i = 0; i<inv.getSize(); i++){
					head = inv.getItem(i);
					if(head == null) break;
					if(head.getType().equals(Material.SKULL_ITEM)){
						if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(playerReports.getPlayer())){
							head = instance.getHead(playerReports.getPlayer());
							ItemMeta headMeta = head.getItemMeta();
							headMeta.setDisplayName("§7Reports de §a" + playerReports.getPlayer());
							List<String> lore = new ArrayList<>();
							lore.add("§fReports do player §a" + playerReports.getPlayer());
							lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
							lore.add("§fReports:");
							boolean tem = false;
							if(playerReports.getReports().containsKey(report.replace("_", " "))){
								tem = true;
								int j = 1;
								for(Entry<String, Integer> reports : playerReports.getReports().entrySet()){
									if(reports.getKey().equalsIgnoreCase(report.replace("_", " ")))
										reports.setValue(reports.getValue()+1);
									lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
									j++;
									if(j==8){
										lore.set(1, "§fVocê está vendo §a8§f/§a" + playerReports.getReports().size() + " §freports!");
										break;
									}
								}
							}else{
								playerReports.getReports().put(report.replace("_", " "), 1);
								int j = 1;
								for(Entry<String, Integer> reports : playerReports.getReports().entrySet()){
									lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
									j++;
									if(j==8){
										lore.set(1, "§fVocê está vendo §a8§f/§a" + playerReports.getReports().size() + " §freports!");
										break;
									}
								}
							}
							headMeta.setLore(lore);
							head.setItemMeta(headMeta);
							inv.setItem(i, head);
							if(tem){
								ItemStack itemReport;
								loop2 :
									for(Inventory playerInventory : playerReportsInventories.get(playerReports.getPlayer())){
										for(int j = 0; j<playerInventory.getSize(); j++){
											itemReport = playerInventory.getItem(j);
											if(itemReport == null) continue;
											if(itemReport.isSimilar(item)){
												itemReport = (ItemStack) instance.criarItem(
														instance.getConfig().getString("Config.Reports_Validos." + report.replace(" ", "_") + ".Report_Item")
														.replace("{player}", playerReports.getPlayer()).replace("{motivo}", report).replace("{motivoqnt}", playerReports.getReports().get(report.replace("_", " "))+"")
														.replace("{lastreporter}", playerReports.getLastReporter()));
												playerInventory.setItem(j, itemReport);
												break loop2;
											}
										}
									}
							}else{
								ArrayList<Inventory> playerInventories = playerReportsInventories.get(playerReports.getPlayer());
								inv = playerInventories.get(playerInventories.size()-1);
								if(inv.firstEmpty() == 53){
									inv.setItem(53, proximo);
									inv = instance.getServer().createInventory(null, 54, "§c§lPlayer - §f§l" + (playerInventories.size()+1));
									inv.setItem(45, anterior);
									inv.setItem(49, voltar);
									inv.setItem(0, (ItemStack) instance.criarItem(
											instance.getConfig().getString("Config.Reports_Validos." + report.replace(" ", "_") + ".Report_Item")
											.replace("{player}", playerReports.getPlayer()).replace("{motivo}", report).replace("{motivoqnt}", playerReports.getReports().get(report.replace("_", " "))+"")
											.replace("{lastreporter}", playerReports.getLastReporter())));
									playerInventories.add(inv);
								}else{
									inv.setItem(inv.firstEmpty(), (ItemStack) instance.criarItem(
											instance.getConfig().getString("Config.Reports_Validos." + report.replace(" ", "_") + ".Report_Item")
											.replace("{player}", playerReports.getPlayer()).replace("{motivo}", report).replace("{motivoqnt}", playerReports.getReports().get(report.replace("_", " "))+"")
											.replace("{lastreporter}", playerReports.getLastReporter())));
								}
							}
							break loop;
						}
					}
				}
			}
	}
	
	public void addOneReport(PlayerReports playerReports, String report, int qnt) {
		ItemStack head;
		loop :
			for(Inventory inv : reportsInventories){
				for(int i = 0; i<inv.getSize(); i++){
					head = inv.getItem(i);
					if(head == null) break;
					if(head.getType().equals(Material.SKULL_ITEM)){
						if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(playerReports.getPlayer())){
							head = instance.getHead(playerReports.getPlayer());
							ItemMeta headMeta = head.getItemMeta();
							headMeta.setDisplayName("§7Reports de §a" + playerReports.getPlayer());
							List<String> lore = new ArrayList<>();
							lore.add("§fReports do player §a" + playerReports.getPlayer());
							lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
							lore.add("§fReports:");
							playerReports.getReports().put(report.replace("_", " "), qnt);
							int j = 1;
							for(Entry<String, Integer> reports : playerReports.getReports().entrySet()){
								lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
								j++;
								if(j==8){
									lore.set(1, "§fVocê está vendo §a8§f/§a" + playerReports.getReports().size() + " §freports!");
									break;
								}
							}
							headMeta.setLore(lore);
							head.setItemMeta(headMeta);
							inv.setItem(i, head);
							inv = playerReportsInventories.get(playerReports.getPlayer()).get(0);
							inv.setItem(inv.firstEmpty(), (ItemStack) instance.criarItem(
									instance.getConfig().getString("Config.Reports_Validos." + report.replace(" ", "_") + ".Report_Item")
									.replace("{player}", playerReports.getPlayer()).replace("{motivo}", report).replace("{motivoqnt}", playerReports.getReports().get(report.replace("_", " "))+"")
									.replace("{lastreporter}", playerReports.getLastReporter())));
							break loop;
						}
					}
				}
			}
	}
	
	private void addAllClosedReport(PlayerReports playerClosedReports, PlayerReports playerReports) {
		ItemStack head;
		loop :
			for(Inventory inv : closedReportsInventories){
				for(int i = 0; i<inv.getSize(); i++){
					head = inv.getItem(i);
					if(head == null) break;
					if(head.getType().equals(Material.SKULL_ITEM)){
						if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(playerReports.getPlayer())){
							head = instance.getHead(playerReports.getPlayer());
							ItemMeta headMeta = head.getItemMeta();
							headMeta.setDisplayName("§7Reports de §a" + playerReports.getPlayer());
							List<String> lore = new ArrayList<>();
							lore.add("§fReports do player §a" + playerReports.getPlayer());
							lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
							lore.add("§fReports:");
							for(Map.Entry<String, Integer> playerEntry : playerReports.getReports().entrySet()){
								if(playerClosedReports.getReports().containsKey(playerEntry.getKey()))
									playerClosedReports.getReports().put(playerEntry.getKey(), playerClosedReports.getReports().get(playerEntry.getKey())+playerEntry.getValue());
								else
									playerClosedReports.getReports().put(playerEntry.getKey(), playerEntry.getValue());
							}
							int j = 1;
							for(Entry<String, Integer> reports : playerClosedReports.getReports().entrySet()){
								lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
								j++;
								if(j==8){
									lore.set(1, "§fVocê está vendo §a8§f/§a" + playerReports.getReports().size() + " §freports!");
									break;
								}
							}
							headMeta.setLore(lore);
							head.setItemMeta(headMeta);
							inv.setItem(i, head);
							inv = playerClosedReportsInventories.get(playerReports.getPlayer()).get(0);
							inv.clear();
							for(Entry<String, Integer> reports : playerClosedReports.getReports().entrySet()){
								ItemStack item = (ItemStack) instance.criarItem(
										instance.getConfig().getString("Config.Reports_Validos." + reports.getKey().replace(" ", "_") + ".Report_Item")
										.replace("{player}", playerReports.getPlayer()).replace("{motivo}", reports.getKey().replace(" ", "_")).replace("{motivoqnt}", reports.getValue()+"")
										.replace("{lastreporter}", playerReports.getLastReporter()));
								inv.setItem(inv.firstEmpty(), item);
							}
							break loop;
						}
					}
				}
			}
	}
	
	public void addClosedReport(PlayerReports playerReports, String report, int qnt) {
		ItemStack head;
		loop :
			for(Inventory inv : closedReportsInventories){
				for(int i = 0; i<inv.getSize(); i++){
					head = inv.getItem(i);
					if(head == null) break;
					if(head.getType().equals(Material.SKULL_ITEM)){
						if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(playerReports.getPlayer())){
							head = instance.getHead(playerReports.getPlayer());
							ItemMeta headMeta = head.getItemMeta();
							headMeta.setDisplayName("§7Reports de §a" + playerReports.getPlayer());
							List<String> lore = new ArrayList<>();
							lore.add("§fReports do player §a" + playerReports.getPlayer());
							lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
							lore.add("§fReports:");
							boolean tem = false;
							for(Map.Entry<String, Integer> playerEntry : playerReports.getReports().entrySet()){
								if(playerEntry.getKey().equalsIgnoreCase(report)){
									playerReports.getReports().put(playerEntry.getKey(), playerReports.getReports().get(playerEntry.getKey())+qnt);
									tem = true;
									break;
								}
							}
							if(!tem)
								playerReports.getReports().put(report, qnt);
							int j = 1;
							for(Entry<String, Integer> reports : playerReports.getReports().entrySet()){
								lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
								j++;
								if(j==8){
									lore.set(1, "§fVocê está vendo §a8§f/§a" + playerReports.getReports().size() + " §freports!");
									break;
								}
							}
							headMeta.setLore(lore);
							head.setItemMeta(headMeta);
							inv.setItem(i, head);
							inv = playerClosedReportsInventories.get(playerReports.getPlayer()).get(0);
							inv.clear();
							for(Entry<String, Integer> reports : playerReports.getReports().entrySet()){
								ItemStack item = (ItemStack) instance.criarItem(
										instance.getConfig().getString("Config.Reports_Validos." + reports.getKey().replace(" ", "_") + ".Report_Item")
										.replace("{player}", playerReports.getPlayer()).replace("{motivo}", reports.getKey().replace(" ", "_")).replace("{motivoqnt}", reports.getValue()+"")
										.replace("{lastreporter}", playerReports.getLastReporter()));
								inv.setItem(inv.firstEmpty(), item);
							}
							break loop;
						}
					}
				}
			}
	}

	public void removePlayer(String player) {
		for(PlayerReports pr : reports){
			if(pr.getPlayer().equalsIgnoreCase(player)){
				reports.remove(pr);
				removePlayerFromInventories(pr, false);
				break;
			}
		}
	}
	
	public void removePlayerReport(String player, String report) {
		for(PlayerReports pr : reports){
			if(pr.getPlayer().equalsIgnoreCase(player)){
				int i = 0;
				for(Entry<String, Integer> reports : pr.getReports().entrySet()){
					if(reports.getKey().equalsIgnoreCase(report)){
						pr.getReports().remove(reports.getKey());
						Inventory inv = playerReportsInventories.get(pr.getPlayer()).get(0);
						inv.setItem(i, null);
						ItemStack item;
						for( ; i<inv.getSize(); ++i){
							if(i == 49 || i == 53 || i == 45) continue;
							item = inv.getItem(i);
							if(item != null && !item.getType().equals(Material.AIR)) inv.setItem(i-1, item);
						}
						break;
					}
					i++;
				}
				if(pr.getReports().size() == 0){
					reports.remove(pr);
					removePlayerFromInventories(pr, false);
				}else{
					ItemStack head;
					loop :
						for(Inventory inv : reportsInventories){
							for(i = 0; i<inv.getSize(); i++){
								head = inv.getItem(i);
								if(head == null) break;
								if(head.getType().equals(Material.SKULL_ITEM)){
									if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(pr.getPlayer())){
										head = instance.getHead(pr.getPlayer());
										ItemMeta headMeta = head.getItemMeta();
										headMeta.setDisplayName("§7Reports de §a" + pr.getPlayer());
										List<String> lore = new ArrayList<>();
										lore.add("§fReports do player §a" + pr.getPlayer());
										lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
										lore.add("§fReports:");
										int j = 1;
										for(Entry<String, Integer> reports : pr.getReports().entrySet()){
											lore.add("  §f- §a" + reports.getKey() + " §f(§a" + reports.getValue() + "§f)");
											j++;
											if(j==8){
												lore.set(1, "§fVocê está vendo §a8§f/§a" + pr.getReports().size() + " §freports!");
												break;
											}
										}
										headMeta.setLore(lore);
										head.setItemMeta(headMeta);
										inv.setItem(i, head);
										break loop;
									}
								}
							}
						}
				}
				break;
			}
		}
	}
	
	public void closePlayer(String player) {
		for(PlayerReports pr : reports){
			if(pr.getPlayer().equalsIgnoreCase(player)){
				reports.remove(pr);
				removePlayerFromInventories(pr, true);
				break;
			}
		}
	}
	
	public void closePlayerReport(String player, String report) {
		for(PlayerReports pr : reports){
			if(pr.getPlayer().equalsIgnoreCase(player)){
				int i = 0;
				for(Entry<String, Integer> reports : pr.getReports().entrySet()){
					if(reports.getKey().equalsIgnoreCase(report)){
						PlayerReports cpr = getClosedPlayerReports(player);
						if(cpr == null){
							cpr = new PlayerReports(player, report.replace("_", " "), pr.getLastReporter(), instance.getSdf().format(new Date()));
							cpr.getReports().put(reports.getKey(), reports.getValue());
							addNewClosedReport(cpr);
						}else{
							if(cpr.getReports().containsKey(reports.getKey()))
								cpr.getReports().put(reports.getKey(), reports.getValue()+cpr.getReports().get(reports.getKey()));
							else
								cpr.getReports().put(reports.getKey(), reports.getValue());
							ItemStack head;
							loop :
								for(Inventory inv : closedReportsInventories){
									for(int j = 0; j<inv.getSize(); j++){
										head = inv.getItem(j);
										if(head == null) break;
										if(head.getType().equals(Material.SKULL_ITEM)){
											if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(pr.getPlayer())){
												head = instance.getHead(pr.getPlayer());
												ItemMeta headMeta = head.getItemMeta();
												headMeta.setDisplayName("§7Reports de §a" + pr.getPlayer());
												List<String> lore = new ArrayList<>();
												lore.add("§fReports do player §a" + pr.getPlayer());
												lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
												lore.add("§fReports:");
												int k = 1;
												for(Entry<String, Integer> r : cpr.getReports().entrySet()){
													lore.add("  §f- §a" + r.getKey() + " §f(§a" + r.getValue() + "§f)");
													k++;
													if(k==8){
														lore.set(1, "§fVocê está vendo §a8§f/§a" + cpr.getReports().size() + " §freports!");
														break;
													}
												}
												headMeta.setLore(lore);
												head.setItemMeta(headMeta);
												inv.setItem(j, head);
												inv = playerClosedReportsInventories.get(pr.getPlayer()).get(0);
												inv.clear();
												for(Entry<String, Integer> r : cpr.getReports().entrySet()){
													ItemStack item = (ItemStack) instance.criarItem(
															instance.getConfig().getString("Config.Reports_Validos." + r.getKey().replace(" ", "_") + ".Report_Item")
															.replace("{player}", pr.getPlayer()).replace("{motivo}", r.getKey().replace(" ", "_")).replace("{motivoqnt}", r.getValue()+"")
															.replace("{lastreporter}", pr.getLastReporter()));
													inv.setItem(inv.firstEmpty(), item);
												}
												break loop;
											}
										}
									}
								}
						}
						pr.getReports().remove(reports.getKey());
						pr.setReportsTotal(pr.getReportsTotal()-reports.getValue());
						cpr.setReportsTotal(cpr.getReportsTotal()+reports.getValue());
						cpr.setRemove(System.currentTimeMillis()+TimeUnit.DAYS.toMillis(instance.getConfig().getInt("Config.Tempo_Remover")));
						if(pr.getReports().size() != 0){
							Inventory inv = playerReportsInventories.get(pr.getPlayer()).get(0);
							inv.setItem(i, null);
							ItemStack item, head;
							for( ; i<inv.getSize(); ++i){
								if(i == 49 || i == 53 || i == 45) continue;
								item = inv.getItem(i);
								if(item != null && !item.getType().equals(Material.AIR)) inv.setItem(i-1, item);
							}
							loop :
								for(Inventory inventario : reportsInventories){
									for(int j = 0; j<inventario.getSize(); j++){
										head = inventario.getItem(j);
										if(head == null) break;
										if(head.getType().equals(Material.SKULL_ITEM)){
											if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(pr.getPlayer())){
												head = instance.getHead(pr.getPlayer());
												ItemMeta headMeta = head.getItemMeta();
												headMeta.setDisplayName("§7Reports de §a" + pr.getPlayer());
												List<String> lore = new ArrayList<>();
												lore.add("§fReports do player §a" + pr.getPlayer());
												lore.add("§fVocê está vendo §a1§f/§a1 §freport!");
												lore.add("§fReports:");
												int k = 1;
												for(Entry<String, Integer> r : pr.getReports().entrySet()){
													lore.add("  §f- §a" + r.getKey() + " §f(§a" + r.getValue() + "§f)");
													k++;
													if(k==8){
														lore.set(1, "§fVocê está vendo §a8§f/§a" + pr.getReports().size() + " §freports!");
														break;
													}
												}
												headMeta.setLore(lore);
												head.setItemMeta(headMeta);
												inventario.setItem(j, head);
												break loop;
											}
										}
									}
								}
						}else{
							removePlayer(pr.getPlayer());
						}
						break;
					}
					i++;
				}
				if(pr.getReports().size() == 0){
					reports.remove(pr);
					removePlayerFromInventories(pr, false);
				}
				break;
			}
		}
	}

	private void removePlayerFromInventories(PlayerReports pr, boolean close) {
		Inventory inv;
		ItemStack head;
		loop :
			for(int i = 0; i<reportsInventories.size(); i++){
				inv = reportsInventories.get(i);
				for(int j = 0; j<inv.getSize(); j++){
					head = inv.getItem(j);
					if(head != null && head.getType().equals(Material.SKULL_ITEM) && j != 49 && j != 53){
						if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(pr.getPlayer())){
							if(close){
								String of = ((SkullMeta) head.getItemMeta()).getOwner();
								PlayerReports playerReports = getClosedPlayerReports(of);
								if(playerReports == null){
									playerReports = pr;
									addNewClosedReport(playerReports);
								}else{
									addAllClosedReport(playerReports, pr);
								}
								playerReports.setRemove(System.currentTimeMillis()+TimeUnit.DAYS.toMillis(instance.getConfig().getInt("Config.Tempo_Remover")));
							}
							for(Entry<String, String> s : staffLook.entrySet()){
								if(s.getValue().contains("->")){
									if(s.getValue().split("->")[0].equalsIgnoreCase(pr.getPlayer())){
										instance.getServer().getPlayer(s.getKey()).getOpenInventory().close();
										staffLook.remove(s.getKey());
									}
								}else{
									if(s.getValue().equalsIgnoreCase(pr.getPlayer())){
										instance.getServer().getPlayer(s.getKey()).getOpenInventory().close();
										staffLook.remove(s.getKey());
									}
								}
							}
							playerReportsInventories.remove(((SkullMeta) head.getItemMeta()).getOwner());
							inv.setItem(j, null);
							for( ; ; i++){
								inv = reportsInventories.get(i);
								for( ; ; ++j){
									if(j == 49)
										continue;
									else if(j == 53){
										if(i+1 == reportsInventories.size()){
											inv.setItem(53, null);
											break;
										}else
											inv.setItem(52, reportsInventories.get(i+1).getItem(0));
									}else{
										if(inv.getItem(j) == null) break loop;
										inv.setItem(j-1, inv.getItem(j));
									}
								}
								if(inv.firstEmpty() == 0 && i != 0) reportsInventories.remove(i);
							}
						}
					}
				}
			}
	}
	
	public void removeClosedPlayerFromInventories(PlayerReports pr) {
		Inventory inv;
		ItemStack head;
		loop :
			for(int i = 0; i<closedReportsInventories.size(); i++){
				inv = closedReportsInventories.get(i);
				for(int j = 0; j<inv.getSize(); j++){
					head = inv.getItem(j);
					if(head != null && head.getType().equals(Material.SKULL_ITEM) && j != 49 && j != 53){
						if(((SkullMeta) head.getItemMeta()).getOwner().equalsIgnoreCase(pr.getPlayer())){
							playerClosedReportsInventories.remove(((SkullMeta) head.getItemMeta()).getOwner());
							inv.setItem(j, null);
							for( ; ; i++){
								inv = closedReportsInventories.get(i);
								for( ; ; ++j){
									if(j == 49)
										continue;
									else if(j == 53){
										if(i+1 == closedReportsInventories.size()){
											inv.setItem(53, null);
											break;
										}else
											inv.setItem(52, closedReportsInventories.get(i+1).getItem(0));
									}else{
										if(inv.getItem(j) == null) break loop;
										inv.setItem(j-1, inv.getItem(j));
									}
								}
								if(inv.firstEmpty() == 0 && i != 0) reportsInventories.remove(i);
							}
						}
					}
				}
			}
	}

}
