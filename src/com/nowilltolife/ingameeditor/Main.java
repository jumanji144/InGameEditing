package com.nowilltolife.ingameeditor;

import org.bukkit.plugin.java.JavaPlugin;

import com.nowilltolife.ingameeditor.commands.EditCommand;

public class Main extends JavaPlugin{
	
	public static String prefix;

	public void onEnable() {
		prefix = getConfig().getString("prefix").replaceAll("&", "§");
		saveDefaultConfig();
		getCommand("edit").setExecutor(new EditCommand());
	}

}
