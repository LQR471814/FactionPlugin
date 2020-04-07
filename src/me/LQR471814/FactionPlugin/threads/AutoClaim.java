package me.LQR471814.FactionPlugin.threads;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.google.gson.Gson;

import me.LQR471814.FactionPlugin.CustomClasses.PlayerData;
import me.LQR471814.FactionPlugin.CustomClasses.Utils;

public class AutoClaim extends Thread{
	private CommandSender sender;
	private ReentrantLock landLock;

	public AutoClaim(CommandSender sender, ReentrantLock landLock) {
		this.sender = sender;
		this.landLock = landLock;
	}
	
	public void run() {
		Gson gson = new Gson();
		
		PlayerData playerData = Utils.initializePlayerData();
		try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
			playerData = gson.fromJson(reader, PlayerData.class);
		} catch (Exception e) {
			sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
			return;
		}
		
		Integer x = 0;
		while (true) {
			try {
				x += 1;
				
				if (playerData.getAutoClaim() == -1) {
					sender.sendMessage("Stopped auto claiming!");
					return;
				}
				
				if (playerData.getFaction().equals("")) {
					sender.sendMessage("You are not in a faction!");
					playerData.setAutoClaim(playerData.getAutoClaim() * -1);
					try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
						gson.toJson(playerData, writer);
					} catch (Exception e) {
						sender.sendMessage("Well an error has occurred! ERROR_CODE: 008");
						e.printStackTrace();
					}
					return;
				}
				
				this.landLock.lock();
				String pastContents = Utils.readFile(Utils.getPath(playerData.getFaction(), "land"));
				this.landLock.unlock();
					if (!(pastContents.equals("ERROR"))) {
						if (pastContents.contains(String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getX()) + "," + String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getZ()))) {
							if (x >= 5) {
								try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
									playerData = gson.fromJson(reader, PlayerData.class);
								} catch (Exception e) {
									sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
									playerData.setAutoClaim(playerData.getAutoClaim() * -1);
									try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
										gson.toJson(playerData, writer);
									} catch (Exception err) {
										sender.sendMessage("Well an error has occurred! ERROR_CODE: 008");
										err.printStackTrace();
									}
									return;
								}
							}
							try { //Check every Minecraft game tick or 200 milliseconds
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							continue;
						}
			
				String catalogStr = Utils.readFile(Utils.getPath("catalog", "factions"));
				List<String> catalog = Arrays.asList(catalogStr.split("\n"));
				for (String factionName : catalog) {
					pastContents = Utils.readFile(Utils.getPath(factionName, "land"));
					if (pastContents.contains(String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getX()) + "," + String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getZ()))) {
						if (x >= 5) {
							try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
								playerData = gson.fromJson(reader, PlayerData.class);
							} catch (Exception e) {
								sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
								playerData.setAutoClaim(playerData.getAutoClaim() * -1);
								try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
									gson.toJson(playerData, writer);
								} catch (Exception err) {
									sender.sendMessage("Well an error has occurred! ERROR_CODE: 008");
									err.printStackTrace();
								}
								return;
							}
						}
						try { //Check every Minecraft game tick or 200 milliseconds
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
						}
					}
				}
				
				this.landLock.lock();
				try {
					FileWriter writeLandData = new FileWriter(Utils.getPath(playerData.getFaction(), "land"), true);
					writeLandData.write(String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getX()) + "," + String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getZ()) + "\n");
					writeLandData.close();
				} catch (IOException e1) {
				}
				this.landLock.unlock();
				sender.sendMessage("Land successfully claimed.");
				
				if (x >= 5) {
					try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
						playerData = gson.fromJson(reader, PlayerData.class);
					} catch (Exception e) {
						sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
						playerData.setAutoClaim(playerData.getAutoClaim() * -1);
						try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
							gson.toJson(playerData, writer);
						} catch (Exception err) {
							sender.sendMessage("Well an error has occurred! ERROR_CODE: 008");
							err.printStackTrace();
						}
						return;
					}
				}
				
				try { //Check every Minecraft game tick or 200 milliseconds
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {}
		}
	}
}
