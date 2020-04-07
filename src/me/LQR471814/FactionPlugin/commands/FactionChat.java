package me.LQR471814.FactionPlugin.commands;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.Gson;

import me.LQR471814.FactionPlugin.CustomClasses.Faction;
import me.LQR471814.FactionPlugin.CustomClasses.OSValidator;
import me.LQR471814.FactionPlugin.CustomClasses.PlayerData;
import me.LQR471814.FactionPlugin.CustomClasses.Utils;

public class FactionChat implements CommandExecutor {
	public String getPath(String filename, String type) {
		String cwd = new File("").getAbsolutePath();
		
		if (OSValidator.isWindows()) {
			return cwd + "\\" + type + "\\" + filename + ".json";
		} else if (OSValidator.isUnix() || OSValidator.isMac()) {
			return cwd + "/" + type + "/" + filename + ".json" + "/";
		}
		return "ERROR";
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Gson gson = new Gson();
		
		if (args.length == 0) {
			sender.sendMessage("You forgot to include a message!");
			return true;
		}
		
		Faction faction = new Faction("", new ArrayList <String> ());
		PlayerData playerData = Utils.initializePlayerData();
		try (Reader reader = new FileReader(this.getPath(sender.getName(), "PlayerData"))) {
			playerData = gson.fromJson(reader, PlayerData.class);
		} catch (Exception e) {
			sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
			return true;
		}
		
		try (Reader reader = new FileReader(this.getPath(playerData.getFaction(), "factions"))) {
			faction = gson.fromJson(reader, Faction.class);
		} catch (Exception e) {
			sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 004");
			return true;
		}
		
		for (String member : faction.getMembers()) {
			Player p = Bukkit.getServer().getPlayer(member); 
			p.sendMessage("[FACTION] " + sender.getName() + ": " + args[0]);
		}
		System.out.println("[FACTION] " + sender.getName() + ": " + args[0]);
		
		return true;
	}
}
