package me.LQR471814.FactionPlugin.threads;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import me.LQR471814.FactionPlugin.CustomClasses.Utils;

public class LocationChecker extends Thread {
	private Player player;
	private ReentrantLock landLock;

	public LocationChecker(Player player, ReentrantLock landLock) {
		this.player = player;
		this.landLock = landLock;
	}
	
	public void run() {
		try {
			Integer x = 0;
			
			this.landLock.lock();
			String catalogStr = Utils.readFile(Utils.getPath("catalog", "factions"));
			this.landLock.unlock();
			List <String> catalog = Arrays.asList(catalogStr.split("\n"));
			
			Map <String, List<String>> lands = new HashMap<String, List<String>>();
			for (String faction : catalog) {
				this.landLock.lock();
				String landStr = Utils.readFile(Utils.getPath(faction, "land"));
				this.landLock.unlock();
				List <String> land = Arrays.asList(landStr.split("\n"));
				lands.put(faction, land);
			}
			String lastFaction = "";
			
			Boolean a = false;
			while (true) {
				x += 1;
				
				if (Bukkit.getServer().getPlayerExact(player.getName()) == null) {
					return;
				}
				
				Chunk playerChunk = player.getLocation().getChunk();
				
				a = false;
				for (String faction : lands.keySet()) {
					if (a == true) {
						break;
					}
					for (String chunkCoordsStr : lands.get(faction)) {
						try {
							if (Integer.parseInt(Arrays.asList(chunkCoordsStr.split(",")).get(0)) == playerChunk.getX() && Integer.parseInt(Arrays.asList(chunkCoordsStr.split(",")).get(1)) == playerChunk.getZ()) {
								if (!(lastFaction.equals(faction))) {
									player.sendTitle(faction, "", 10, 50, 10);
								}
								lastFaction = faction;
								if (x >= 5) { //Reread file every 1 second (5 * 200 = 1000 milliseconds)
									x = 0;
									this.landLock.lock();
									catalogStr = Utils.readFile(Utils.getPath("catalog", "factions"));
									this.landLock.unlock();
									catalog = Arrays.asList(catalogStr.split("\n"));
									
									lands = new HashMap<String, List<String>>();
									for (String factionName : catalog) {
										this.landLock.lock();
										String landStr = Utils.readFile(Utils.getPath(factionName, "land"));
										this.landLock.unlock();
										List <String> land = Arrays.asList(landStr.split("\n"));
										lands.put(factionName, land);
									}
								}
								a = true;
								break;
							}
						} catch (Exception e) {}
					}
				}
				
				if (a == true) {
					try { //Check every Minecraft game tick or 200 milliseconds
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				if (x >= 5) { //Reread file every 1 second (5 * 200 = 1000 milliseconds)
					x = 0;
					this.landLock.lock();
					catalogStr = Utils.readFile(Utils.getPath("catalog", "factions"));
					this.landLock.unlock();
					catalog = Arrays.asList(catalogStr.split("\n"));
					
					lands = new HashMap<String, List<String>>();
					for (String faction : catalog) {
						this.landLock.lock();
						String landStr = Utils.readFile(Utils.getPath(faction, "land"));
						this.landLock.unlock();
						List <String> land = Arrays.asList(landStr.split("\n"));
						lands.put(faction, land);
					}
					
				}
				
				lastFaction = "";
				try { //Check every Minecraft game tick or 200 milliseconds
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
