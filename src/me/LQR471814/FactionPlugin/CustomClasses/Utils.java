package me.LQR471814.FactionPlugin.CustomClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {
	public static String readFile(String filename) {
	    try {
	    	File file = new File(filename);
	    	BufferedReader reader = new BufferedReader(new FileReader(file));
	    	String st; 
	    	String data = "";
	    	try {
				while ((st = reader.readLine()) != null) {
					data = data + st + "\n";
				}
				reader.close();
				return data;
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	return "ERROR";
	    } catch (FileNotFoundException e) {
	    	//System.out.println("An error occurred. File Reading failed.");
	    	return "ERROR";
	    }
	}
	
	public static PlayerData initializePlayerData() {
		return new PlayerData("", "", -1, -1, new ArrayList <String> ());
	}
	
	public static Faction initializeFaction() {
		return new Faction("", new ArrayList <String> ());
	}
	
	public static String getJSONPath(String filename, String directory) {
		String cwd = new File("").getAbsolutePath();
		
		if (OSValidator.isWindows()) {
			return cwd + "\\" + directory + "\\" + filename + ".json";
		} else if (OSValidator.isUnix() || OSValidator.isMac()) {
			return cwd + "/" + directory + "/" + filename + ".json" + "/";
		}
		return "";
	}
	
	public static String getPath(String filename, String directory) {
		String cwd = new File("").getAbsolutePath();
		
		if (OSValidator.isWindows()) {
			return cwd + "\\" + directory + "\\" + filename;
		} else if (OSValidator.isUnix() || OSValidator.isMac()) {
			return cwd + "/" + directory + "/" + filename + "/";
		}
		return "";
	}
}
