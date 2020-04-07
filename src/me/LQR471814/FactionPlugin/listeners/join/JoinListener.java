package me.LQR471814.FactionPlugin.listeners.join;

import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.LQR471814.FactionPlugin.Main;
import me.LQR471814.FactionPlugin.threads.LocationChecker;

public class JoinListener implements Listener {
	@SuppressWarnings("unused")
	private Main plugin;
	private ReentrantLock landLock;
	
	public JoinListener(Main plugin, ReentrantLock landLock) {
		this.plugin = plugin;
		this.landLock = landLock;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		LocationChecker checker = new LocationChecker(p, landLock);
		checker.start();
	}
}