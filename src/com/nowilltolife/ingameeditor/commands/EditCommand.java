package com.nowilltolife.ingameeditor.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.nowilltolife.ingameeditor.Main;
import com.nowilltolife.ingameeditor.utils.PluginUtils;

public class EditCommand implements CommandExecutor {

	public static HashMap<String, File> session = new HashMap<>();
	public static HashMap<String, Plugin> plugins = new HashMap<>();
	public static HashMap<String, YamlConfiguration> configs = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("ingameedit.edit")) {
			if (args.length == 0) {
				help(sender);
			} else if (args.length == 1) {
				if (args[0].equals("help")) {
					help(sender);
				} else if (args[0].equals("show")) {
					if (session.containsKey(sender.getName())) {
						Scanner scan;
						try {
							scan = new Scanner(session.get(sender.getName()));
							while (scan.hasNext()) {
								sender.sendMessage(scan.nextLine());
							}
							scan.close();
						} catch (FileNotFoundException e) {
							sender.sendMessage("§cError while reading file. Is the file still there?");
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else if(args[0].equals("save")) {
					if (session.containsKey(sender.getName())) {
						try {
							YamlConfiguration config = configs.get(sender.getName());
							config.save(session.get(sender.getName()));
							sender.sendMessage(Main.prefix + "§7File saved!");
						} catch (IOException e) {
							sender.sendMessage("§Error while trying to save file. Is the file still there?");
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				}else if(args[0].equals("reload")) {
					if (session.containsKey(sender.getName())) {
						try {
							configs.get(sender.getName()).load(session.get(sender.getName()));
						} catch (IOException | InvalidConfigurationException e) {
							sender.sendMessage("§cError while trying to reload. Is the config valid?");
							e.printStackTrace();
						}
						sender.sendMessage(Main.prefix + "§7File reloaded+!");
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				}
				else if(args[0].equals("close")) {					
					if (session.containsKey(sender.getName())) {
						session.remove(sender.getName());
						sender.sendMessage(Main.prefix + "Session closed!");
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				}else {
					help(sender);
				}
			} else if (args.length == 2) {
				if (args[0].equals("start")) {
					if (PluginUtils.getPluginByName(args[1]) != null) {
						File config = new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/config.yml");
						session.put(sender.getName(), config);
						configs.put(sender.getName(), YamlConfiguration.loadConfiguration(config));
						sender.sendMessage(Main.prefix
								+ "§7Started editing session with the file: §aconfig.yml §7from the plugin: §a"
								+ PluginUtils.getPluginByName(args[1]).getName());
					}
				}
				else if (args[0].equals("show")) {
					if (session.containsKey(sender.getName())) {
						int number = 0;
						try {
						number = Integer.parseInt(args[1]);
						}catch (NumberFormatException e) {
							sender.sendMessage("§cError expected a number");
						}
						Scanner scan;
						try {
							scan = new Scanner(session.get(sender.getName()));
							int i = 0;
							while (scan.hasNext()) {
								if(i == number) {
									scan.close();
									return false;
								}
								sender.sendMessage(scan.nextLine());
								i++;
								
							}
						} catch (FileNotFoundException e) {
							sender.sendMessage("§cError while reading file. Is the file still there?");
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				}else if(args[0].equals("get")) {
					if(session.containsKey(sender.getName())) {
						StringBuilder builder = new StringBuilder();
						for(int i = 1;i < args.length; i++) {
							builder.append(" " + args[i]);
						}
						sender.sendMessage(Main.prefix + "§7The value of §a" + builder.substring(1) + " §7is: '§a" + configs.get(sender.getName()).get(builder.substring(1)) + "§7'");
					}
				}else {
					help(sender);
				}
			} else if (args.length > 2) {
				if (args[0].equals("start")) {
					if (PluginUtils.getPluginByName(args[1]) != null) {
						if(args[2].contains(".yml") && new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + args[2]).exists()) {
						File config = new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + args[2]);
						session.put(sender.getName(), config);
						configs.put(sender.getName(), YamlConfiguration.loadConfiguration(config));
						sender.sendMessage(Main.prefix
								+ "§7Started editing session with the file: §a"+config.getName()+" §7from the plugin: §a"
								+ PluginUtils.getPluginByName(args[1]).getName());
					}else if(new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + args[2] + ".yml").exists()) {
						File config = new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + args[2] + ".yml");
						session.put(sender.getName(), config);
						configs.put(sender.getName(), YamlConfiguration.loadConfiguration(config));
						sender.sendMessage(Main.prefix
								+ "§7Started editing session with the file: §a"+config.getName()+" §7from the plugin: §a"
								+ PluginUtils.getPluginByName(args[1]).getName());
					}else {
						sender.sendMessage("§cError file not found!");
					}
					}
				}else if(args[0].equals("set")) {
					if(session.containsKey(sender.getName())) {
						String previous = (String) configs.get(sender.getName()).get(args[1]).toString();
						StringBuilder builder = new StringBuilder();
						for(int i = 2;i < args.length; i++) {
							builder.append(" " + args[i]);
						}
						configs.get(sender.getName()).set(args[1], builder.substring(1));
						sender.sendMessage(Main.prefix + "§7You changed the value of §a" + args[1] + " §7from: '§a" + previous + "§7' to '§a" + builder.substring(1) + "§7'");
						
					}
				}else {
					help(sender);
				}
			}
		}
		return false;
	}
	
	public void help(CommandSender sender) {
		sender.sendMessage(Main.prefix + "Help");
		sender.sendMessage("§aBase Commands:");
		sender.sendMessage(
				"§a> /edit start <plugin> §7| starts a edit session with the config.yml of given plugin (aslong as it has one)");
		sender.sendMessage(
				"§a> /edit start <plugin> <configfile> §7| starts a edit session with the given Yaml file from the plugin");
		sender.sendMessage("§aIn-Edit Commands");
		sender.sendMessage(
				"§a> /edit show [size] §7| Shows the content of the entire file or only to a given legnth");
		sender.sendMessage("§a> /edit get <section> §7| gets the value from a given section");
		sender.sendMessage("§a> /edit set <section> <value>§7| sets the value of a given section");
		sender.sendMessage("§a> /edit save §7| saves the config file");
		sender.sendMessage("§a> /edit reload §7| reloads the config file");
		sender.sendMessage("§a> /edit close §7| closes the edit session");
	}
}
