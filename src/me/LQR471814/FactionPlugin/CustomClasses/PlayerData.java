package me.LQR471814.FactionPlugin.CustomClasses;

import java.util.ArrayList;

public class PlayerData {
	private String name;
	private String faction;
	private Integer chat;
	private Integer autoClaim;
	private ArrayList <String> invitations;
	public PlayerData(String name, String faction, Integer chat, Integer autoClaim, ArrayList <String> invitations) {
		this.name = name;
		this.faction = faction;
		this.chat = chat;
		this.autoClaim = autoClaim;
		this.invitations = invitations;
	}
	public String getName() {
		return this.name;
	}
	public String getFaction() {
		return this.faction;
	}
	
	public ArrayList <String> getInvitations() {
		return this.invitations;
	}
	
	public Integer getChat() {
		return this.chat;
	}
	
	public Integer getAutoClaim() {
		return this.autoClaim;
	}
	
	public void setInvitations(ArrayList <String> invitations) {
		this.invitations = invitations;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setFaction(String faction) {
		this.faction = faction;
	}
	
	public void setChat(Integer chat) {
		this.chat = chat;
	}
	
	public void setAutoClaim(Integer autoClaim) {
		this.autoClaim = autoClaim;
	}
}
