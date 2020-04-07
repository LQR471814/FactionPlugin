package me.LQR471814.FactionPlugin.CustomClasses;

import java.util.ArrayList;

public class Faction {
	private String name;
	private ArrayList <String> members;
	public Faction(String name, ArrayList<String> members) {
		this.name = name;
		this.members = members;
	}
	public String getName() {
		return this.name;
	}
	public ArrayList <String> getMembers() {
		return this.members;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMembers(ArrayList <String> members) {
		this.members = members;
	}
}