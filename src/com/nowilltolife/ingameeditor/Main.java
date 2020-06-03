package com.nowilltolife.ingameeditor;

import org.bukkit.plugin.java.JavaPlugin;

import com.nowilltolife.ingameeditor.commands.EditCommand;

public class Main extends JavaPlugin{
	
	public static String prefix;
	public static String errorprefix;

	public void onEnable() {
		saveDefaultConfig();
		prefix = getConfig().getString("prefix").replaceAll("&", "§");
		errorprefix = getConfig().getString("errorprefix").replaceAll("&", "§");
		getCommand("edit").setExecutor(new EditCommand());
	}

}
