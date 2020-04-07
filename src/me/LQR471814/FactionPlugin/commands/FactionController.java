package me.LQR471814.FactionPlugin.commands;

import java.io.File;  // Import the File class
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.gson.Gson;

import me.LQR471814.FactionPlugin.CustomClasses.Faction;
import me.LQR471814.FactionPlugin.CustomClasses.PlayerData;
import me.LQR471814.FactionPlugin.CustomClasses.Utils;
import me.LQR471814.FactionPlugin.threads.AutoClaim;

public class FactionController implements CommandExecutor {
	private ReentrantLock landLock;
	
	public FactionController (ReentrantLock landLock) {
		this.landLock = landLock;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (args[0].equals("autoclaim") || args[0].equals("ac")) {
				Gson gson = new Gson();
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				playerData.setAutoClaim(playerData.getAutoClaim() * -1);
				
				try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					gson.toJson(playerData, writer);
				} catch (Exception e) {
					sender.sendMessage("Well an error has occurred! ERROR_CODE: 008");
					e.printStackTrace();
				}
				
				AutoClaim autoClaimThread = new AutoClaim(sender, this.landLock);
				autoClaimThread.start();
				sender.sendMessage("Started autoclaiming!");
				return true;
			}
			
			if (args[0].equals("unclaimall") || args[0].equals("uca")) {
				Gson gson = new Gson();
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				if (playerData.getFaction().equals("")) {
					sender.sendMessage("You are not in a faction!");
					return true;
				}
				
				this.landLock.lock();
				File landFile = new File(Utils.getPath(playerData.getFaction(), "land"));
				if (landFile.delete()) {
					this.landLock.unlock();
					sender.sendMessage("All land successfully unclaimed.");
					return true;
				} else {
					this.landLock.unlock();
					sender.sendMessage("There was an error unclaiming land. ERROR_CODE: 008");
					return true;
				}
			}
			
			if (args[0].equals("unclaim") || args[0].equals("uc")) {
				Gson gson = new Gson();
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				if (playerData.getFaction().equals("")) {
					sender.sendMessage("You are not in a faction!");
					return true;
				}
				this.landLock.lock();
				String newLandData = Utils.readFile(Utils.getPath(playerData.getFaction(), "land"));
				FileWriter writeLandData = new FileWriter(Utils.getPath(playerData.getFaction(), "land"));
				if (newLandData.equals("ERROR")) {
					sender.sendMessage("You haven't claimed any land or an error has occurred! ");
					writeLandData.close();
					return true;
				}
				newLandData = newLandData.replace(String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getX()) + "," + String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getZ() + "\n"), "");
				writeLandData.write(newLandData);
				writeLandData.close();
				this.landLock.unlock();
				sender.sendMessage("Unclaimed land.");
				return true;
			}
			if (args[0].equals("claim") || args[0].equals("cl")) {
				Gson gson = new Gson();
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				if (playerData.getFaction().equals("")) {
					sender.sendMessage("You are not in a faction!");
					return true;
				}
				
				this.landLock.lock();
				String pastContents = Utils.readFile(Utils.getPath(playerData.getFaction(), "land"));
				this.landLock.unlock();
				if (!(pastContents.equals("ERROR"))) {
					if (pastContents.contains(String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getX()) + "," + String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getZ()))) {
						sender.sendMessage("This land has already been claimed by your faction!");
						return true;
					}
				}
				
				String catalogStr = Utils.readFile(Utils.getPath("catalog", "factions"));
				List<String> catalog = Arrays.asList(catalogStr.split("\n"));
				for (String factionName : catalog) {
					pastContents = Utils.readFile(Utils.getPath(factionName, "land"));
					if (pastContents.contains(String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getX()) + "," + String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getZ()))) {
						sender.sendMessage(factionName + " has already claimed this chunk!");
						return true;
					}
				}
				
				this.landLock.lock();
				FileWriter writeLandData = new FileWriter(Utils.getPath(playerData.getFaction(), "land"), true);
				this.landLock.unlock();
				writeLandData.write(String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getX()) + "," + String.valueOf(Bukkit.getServer().getPlayer(sender.getName()).getLocation().getChunk().getZ()) + "\n");
				writeLandData.close();
				sender.sendMessage("Land successfully claimed.");
				return true;
			}
			
			if (args[0].equals("chat") || args[0].equals("ch")) {
				Gson gson = new Gson();
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
					return true;
				}
				playerData.setChat(playerData.getChat() * -1);
				if (playerData.getChat() == -1) {
					sender.sendMessage("Toggled all chat!");
				} else {
					sender.sendMessage("Toggled faction chat!");
				}
				
				try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) { //Write file faction data
					gson.toJson(playerData, writer);
				} catch (IOException e) {
					sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 001");
					return true;
				}
				return true;
			}
			//--------------------------------------------------------------------------------------------------
			if (args[0].equals("disband") || args[0].equals("d")) {
				Gson gson = new Gson();
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You aren't in a faction or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				FileWriter catalogWriter = new FileWriter(Utils.getPath("catalog", "factions"));
				catalogWriter.write(Utils.readFile(Utils.getPath("catalog", "factions")).replace(playerData.getFaction(), ""));
				catalogWriter.close();
				
				Faction faction = Utils.initializeFaction();
				try (Reader reader = new FileReader(Utils.getJSONPath(playerData.getFaction(), "factions"))) {
					faction = gson.fromJson(reader, Faction.class);
				} catch (Exception e) {
					sender.sendMessage("The faction you are trying to join no longer exists. WARN_CODE: 003");
					return true;
				}
				for (String member : faction.getMembers()) {
					File memberFile = new File(Utils.getJSONPath(member, "PlayerData"));
					memberFile.delete();
				}
				
				File file = new File(Utils.getJSONPath(playerData.getFaction(), "factions"));
				if (file.delete()) {
					sender.sendMessage("Successfully disbanded " + playerData.getFaction());
					return true;
				} else {
					sender.sendMessage("An error has occurred, if this has happened please inform your server administrator. ERROR_CODE: 007");
					return true;
				}
			}
			//--------------------------------------------------------------------------------------------------
			if (args[0].equals("invitelist") || args[0].equals("invlist")) {
				Gson gson = new Gson();
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You weren't invited to any factions or an error has occurred. WARN_CODE: 002");
					return true;
				}
				if (playerData.getInvitations().size() == 0) {
					sender.sendMessage("You have no invitations.");
					return true;
				}
				String wholeInvMsg = "";
				for (String invitation : playerData.getInvitations()) {
					if (!(playerData.getInvitations().indexOf(invitation) == playerData.getInvitations().size() - 1)) {
						wholeInvMsg = wholeInvMsg + invitation + ",";
					} else {
						wholeInvMsg = wholeInvMsg + invitation;
					}
				}
				sender.sendMessage("Invitations: " + wholeInvMsg);
				return true;
			}
			//--------------------------------------------------------------------------------------------------
			if (args[0].equals("join") || args[0].equals("j")) {
				Gson gson = new Gson();
				Boolean accept = false;
				
				try {
					System.out.println("File requested: " + args[1]);
				} catch (Exception e) {
					sender.sendMessage("You forgot to include a faction");
					return true;
				}
				
				PlayerData playerData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You weren't invited to any factions or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				for (String factionName : playerData.getInvitations()) {
					if (factionName.equals(args[1])) {
						accept = true;
						break;
					}
				}
				
				if (accept == true) {
					ArrayList <String> invitations = playerData.getInvitations();
					invitations.remove(args[1]);
					playerData.setInvitations(invitations);
					playerData.setFaction(args[1]);
					try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
						gson.toJson(playerData, writer);
					} catch (IOException e) {
						sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 001");
						return true;
					}
					
					Faction faction = Utils.initializeFaction();
					try (Reader reader = new FileReader(Utils.getJSONPath(args[1], "factions"))) {
						faction = gson.fromJson(reader, Faction.class);
					} catch (Exception e) {
						sender.sendMessage("The faction you are trying to join no longer exists. WARN_CODE: 003");
						return true;
					}
					ArrayList <String> members = faction.getMembers();
					members.add(sender.getName());
					faction.setMembers(members);
					try (FileWriter writer = new FileWriter(Utils.getJSONPath(args[1], "factions"))) {
						gson.toJson(faction, writer);
					} catch (IOException e) {
						sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 001");
						return true;
					}
					sender.sendMessage("Successfully joined " + args[1]);
					return true;
				} else {
					sender.sendMessage("You weren't invited to " + args[1]);
					return true;
				}
			}
			//--------------------------------------------------------------------------------------------------
			if (args[0].equals("invite") || args[0].equals("inv")) {
				Gson gson = new Gson();
				
				try {
					System.out.println("File requested: " + args[1]);
				} catch (Exception e) {
					sender.sendMessage("You forgot to include a player");
					return true;
				}
				
				if (sender.getName().equals(args[1])) {
					sender.sendMessage("You cannot invite yourself!");
					return true;
				}
				
				File playerDataFile = new File(Utils.getJSONPath(args[1], "PlayerData"));
				Faction senderFaction = Utils.initializeFaction();
				
				PlayerData senderData = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					senderData = gson.fromJson(reader, PlayerData.class);
				} catch (Exception e) {
					sender.sendMessage("You haven't joined any factions or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				try (Reader reader = new FileReader(Utils.getJSONPath(senderData.getFaction(), "factions"))) {
					senderFaction = gson.fromJson(reader, Faction.class);
				} catch (Exception e) {
					sender.sendMessage("An error has occurred, if this happened please tell the server administrator. ERROR_CODE: 003");
					return true;
				}
				
				for (String player : senderFaction.getMembers()) {
					if (player.equals(args[1])) {
						sender.sendMessage("You cannot invite someone already in your faction!");
						return true;
					}
				}
				
				Boolean c = true;
				try {
					c = playerDataFile.createNewFile();
				} catch (IOException e1) {
					sender.sendMessage("An error has occurred, if this happened please tell the server administrator. ERROR_CODE: 005");
					return true;
				}
				
				if (c) { //File successfully created
					PlayerData playerData = new PlayerData(args[1], "", -1, -1, new ArrayList <String> ());
					ArrayList <String> invitations = playerData.getInvitations();
					invitations.add(senderData.getFaction());
					playerData.setInvitations(invitations);
					try (FileWriter writer = new FileWriter(Utils.getJSONPath(args[1], "PlayerData"))) {
						gson.toJson(playerData, writer);
					} catch (IOException e) {
						sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 002");
						return true;
					}
					sender.sendMessage("Successfully sent invitation.");
					return true;
				} else { //File already exists
					PlayerData playerData = Utils.initializePlayerData();
					try (Reader reader = new FileReader(Utils.getJSONPath(args[1], "PlayerData"))) {
						playerData = gson.fromJson(reader, PlayerData.class);
					} catch (Exception e) {
						sender.sendMessage("An error has occurred, if this happened please tell the server administrator. ERROR_CODE: 006");
						return true;
					}
					ArrayList <String> invitations = playerData.getInvitations();
					invitations.add(senderData.getFaction());
					playerData.setInvitations(invitations);
					try (FileWriter writer = new FileWriter(Utils.getJSONPath(args[1], "PlayerData"))) {
						gson.toJson(playerData, writer);
					} catch (IOException e) {
						sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 002");
						return true;
					}
					sender.sendMessage("Successfully sent invitation.");
					return true;
				}
			}
			//--------------------------------------------------------------------------------------------------
			if (args[0].equals("info") || args[0].equals("i")) {
				Gson gson = new Gson();
				Boolean self = false;
				
				try {
					System.out.println("Info requested: " + args[1]);
				} catch(Exception e) {
					self = true;
				}
				
				if (self == false) {
					Faction faction = Utils.initializeFaction();
					try (Reader reader = new FileReader(Utils.getJSONPath(args[1], "factions"))) {
						faction = gson.fromJson(reader, Faction.class);
					} catch (Exception e) {
						sender.sendMessage("That faction doesn't exist or an error has occurred. WARN_CODE: 001");
						return true;
					}
					sender.sendMessage("Name: " + faction.getName());
					String membersMessage = "";
					for (String member : faction.getMembers()) {
						if (!(faction.getMembers().indexOf(member) == faction.getMembers().size())) {
							membersMessage = membersMessage + member  + ",";
						} else {
							membersMessage = membersMessage + member;
						}
					}
					sender.sendMessage("Members: " + membersMessage);
					return true;
				} else {
					PlayerData playerData = Utils.initializePlayerData();
					Faction faction = Utils.initializeFaction();
					try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
						playerData = gson.fromJson(reader, PlayerData.class);
					} catch (Exception e) {
						sender.sendMessage("You haven't joined any factions or an error has occurred. WARN_CODE: 002");
						return true;
					}
					try (Reader reader = new FileReader(Utils.getJSONPath(playerData.getFaction(), "factions"))) {
						faction = gson.fromJson(reader, Faction.class);
					} catch (Exception e) {
						sender.sendMessage("An error has occurred, please notify the server administrator. ERROR_CODE: 003");
						return true;
					}
					sender.sendMessage("Name: " + faction.getName());
					String membersMessage = "";
					for (String member : faction.getMembers()) {
						if (!(faction.getMembers().indexOf(member) == faction.getMembers().size() - 1)) {
							membersMessage = membersMessage + member  + ",";
						} else {
							membersMessage = membersMessage + member;
						}
					}
					sender.sendMessage("Members: " + membersMessage);
					return true;
				}
			}
			//--------------------------------------------------------------------------------------------------
			if (args[0].equals("leave") || args[0].equals("l")) {
				Gson gson = new Gson();
				
				Faction faction = Utils.initializeFaction();
				PlayerData playerdata = Utils.initializePlayerData();
				
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerdata = gson.fromJson(reader, PlayerData.class);
				} catch (IOException e) {
					sender.sendMessage("You haven't joined any factions or an error has occurred. WARN_CODE: 002");
					return true;
				}
				
				try (Reader reader = new FileReader(Utils.getJSONPath(playerdata.getFaction(), "factions"))) {
					faction = gson.fromJson(reader, Faction.class);
				} catch (IOException e) {
					sender.sendMessage("That faction doesn't exist or an error has occurred. WARN_CODE: 001");
					return true;
				}
				
				ArrayList <String> members = faction.getMembers();
				if (members.remove(sender.getName()) == false) {
					sender.sendMessage("You haven't joined a faction.");
					return true;
				}
				
				faction.setMembers(members);
				
				if (faction.getMembers().size() == 0) {
					File file = new File(Utils.getJSONPath(playerdata.getFaction(), "factions"));
					file.delete();
					File file1 = new File(Utils.getJSONPath(sender.getName(), "PlayerData"));
					file1.delete();
					FileWriter catalogWriter = new FileWriter(Utils.getPath("catalog", "factions"));
					catalogWriter.write(Utils.readFile(Utils.getPath("catalog", "factions")).replace(faction.getName(), ""));
					catalogWriter.close();
					sender.sendMessage("Successfully left the faction and since nobody else was in the faction, the faction was disbanded.");
					return true;
				}
				
				try (FileWriter writer = new FileWriter(Utils.getJSONPath(playerdata.getFaction(), "factions"))) {
					gson.toJson(faction, writer);
				} catch (IOException e) {
					sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 001");
					return true;
				}
				File file1 = new File(Utils.getJSONPath(sender.getName(), "PlayerData"));
				file1.delete();
				sender.sendMessage("Succesfully left faction.");
				return true;
			}
			//--------------------------------------------------------------------------------------------------
			if (args[0].equals("create")) { //faction create faction_name
				File factionFile = new File("");
				Gson gson = new Gson();
				
				PlayerData playerdata = Utils.initializePlayerData();
				try (Reader reader = new FileReader(Utils.getJSONPath(sender.getName(), "PlayerData"))) {
					playerdata = gson.fromJson(reader, PlayerData.class);
				} catch (IOException e) {}
				
				if (!(playerdata.getFaction().equals(""))) {
					sender.sendMessage("You already created a faction, or are in one!");
					return true;
				}
				
				try {
					factionFile = new File(Utils.getJSONPath(args[1], "factions"));
				} catch (Exception e) {
					sender.sendMessage("You forgot to name your faction!");
					return true;
				}
				
				File playerDataFile = new File(Utils.getJSONPath(sender.getName(), "PlayerData"));
				try {
					factionFile.createNewFile();
					playerDataFile.createNewFile();
				} catch (IOException e1) {
					sender.sendMessage("An error occurred, please tell your server administrator. ERROR_CODE: 004");
					return true;
				}
				ArrayList <String>members = new ArrayList <String>();
				members.add(sender.getName());
				
				if (Utils.readFile(Utils.getPath("catalog", "factions")).toLowerCase().contains(args[1].toLowerCase())) {
					sender.sendMessage("This faction already exists! Pick a different name.");
					return true;
				}
				FileWriter catalogWriter = new FileWriter(Utils.getPath("catalog", "factions"));
				catalogWriter.write(Utils.readFile(Utils.getPath("catalog", "factions")) + args[1] + "\n");
				catalogWriter.close();
				
				Faction newFaction = new Faction(args[1], members);
				PlayerData newPlayerData = new PlayerData(sender.getName(), args[1], -1, -1, new ArrayList <String> ());
				
				try (FileWriter writer = new FileWriter(Utils.getJSONPath(args[1], "factions"))) { //Write file faction data
					gson.toJson(newFaction, writer);
				} catch (IOException e) {
					sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 001");
					return true;
				}
				try (FileWriter writer = new FileWriter(Utils.getJSONPath(sender.getName(), "PlayerData"))) { //Write file player data
					gson.toJson(newPlayerData, writer);
				} catch (IOException e) {
					sender.sendMessage("Well an error occurred, if this happened please tell the server administrator. ERROR_CODE: 002");
					return true;
				}
				sender.sendMessage("Created your faction and automatically made you a member!");
				return true;
			}
			sender.sendMessage("That is not an action!");
			return false;
		} catch (Exception e) {
			sender.sendMessage("You forgot to specify an action!");
			return false;
		}
		
	}
	
}
