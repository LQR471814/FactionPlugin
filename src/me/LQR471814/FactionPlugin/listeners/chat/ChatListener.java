package me.LQR471814.FactionPlugin.listeners.chat;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.gson.Gson;

import me.LQR471814.FactionPlugin.Main;
import me.LQR471814.FactionPlugin.CustomClasses.Faction;
import me.LQR471814.FactionPlugin.CustomClasses.OSValidator;
import me.LQR471814.FactionPlugin.CustomClasses.PlayerData;
import me.LQR471814.FactionPlugin.CustomClasses.Utils;

public class ChatListener implements Listener {
	@SuppressWarnings("unused")
	private Main plugin;
	
	public ChatListener(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public String getPath(String filename, String type) {
		String cwd = new File("").getAbsolutePath();
		
		if (OSValidator.isWindows()) {
			return cwd + "\\" + type + "\\" + filename + ".json";
		} else if (OSValidator.isUnix() || OSValidator.isMac()) {
			return cwd + "/" + type + "/" + filename + ".json" + "/";
		}
		return "ERROR";
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		Player sender = event.getPlayer();
		Gson gson = new Gson();
		
		PlayerData playerData = Utils.initializePlayerData();
		try (Reader reader = new FileReader(this.getPath(sender.getName(), "PlayerData"))) {
			playerData = gson.fromJson(reader, PlayerData.class);
		} catch (Exception e) {
			sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
		}
		
		if (playerData.getChat() == 1) {
			Faction faction = new Faction("", new ArrayList <String> ());
			try (Reader reader = new FileReader(this.getPath(playerData.getFaction(), "factions"))) {
				faction = gson.fromJson(reader, Faction.class);
			} catch (Exception e) {
				sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 004");
			}
			
			for (String member : faction.getMembers()) {
				Player p = Bukkit.getServer().getPlayer(member); 
				p.sendMessage("[FACTION] " + sender.getName() + ": " + event.getMessage());
				System.out.println("[FACTION] " + sender.getName() + ": " + event.getMessage());
			}
		} else {
			if (playerData.getFaction() == "") {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(sender.getName() + ": " + event.getMessage());
				}
				System.out.println(sender.getName() + ": " + event.getMessage());
			} else {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage("[" + playerData.getFaction() + "] " + sender.getName() + ": " + event.getMessage());
				}
				System.out.println("[" + playerData.getFaction() + "] " + sender.getName() + ": " + event.getMessage());
			}
		}
		
	}
}
