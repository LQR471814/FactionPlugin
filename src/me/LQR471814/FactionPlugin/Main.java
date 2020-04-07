package me.LQR471814.FactionPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.LQR471814.FactionPlugin.CustomClasses.OSValidator;
import me.LQR471814.FactionPlugin.commands.FactionChat;
import me.LQR471814.FactionPlugin.commands.FactionController;
import me.LQR471814.FactionPlugin.listeners.chat.ChatListener;
import me.LQR471814.FactionPlugin.listeners.join.JoinListener;

public final class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		System.out.println("Faction plugin is enabled.");
		String cwd = new File("").getAbsolutePath();
		
		if (OSValidator.isWindows()) {
			File file = new File(cwd + "\\factions");
			File file1 = new File(cwd + "\\PlayerData");
			File file2 = new File(cwd + "\\land");
			File file3 = new File(cwd + "\\factions\\catalog");
			file.mkdir();
			file1.mkdir();
			file2.mkdir();
			try {
				file3.createNewFile();
			} catch (IOException e) {
				System.out.println("An error has occurred");
				e.printStackTrace();
			}
			System.out.println("Windows");
		} else if (OSValidator.isMac() || OSValidator.isUnix()) {
			File file = new File(cwd + "/factions/");
			File file1 = new File(cwd + "/PlayerData/");
			File file2 = new File(cwd + "/land/");
			File file3 = new File(cwd + "/factions/catalog");
			file.mkdir();
			file1.mkdir();
			file2.mkdir();
			try {
				file3.createNewFile();
			} catch (IOException e) {
				System.out.println("An error has occurred");
				e.printStackTrace();
			}
			System.out.println("Mac or Linux");
		}
		
		ReentrantLock landLock = new ReentrantLock();
		
		getCommand("faction").setExecutor(new FactionController(landLock));
		getCommand("factionchat").setExecutor(new FactionChat());
		
		new ChatListener(this);
		new JoinListener(this, landLock);
	}
	
	public void onDisable() {
		System.out.println("Faction plugin is disabled.");
	}
}