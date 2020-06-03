package com.nowilltolife.ingameeditor.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

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
				} else if (args[0].equals("save")) {
					if (session.containsKey(sender.getName())) {
						File file = session.get(sender.getName());
						if(configs.containsKey(sender.getName())) {
						try {
							YamlConfiguration config = configs.get(sender.getName());
							config.save(file);
							sender.sendMessage(Main.prefix + "§7File saved!");
						} catch (IOException e) {
							sender.sendMessage("§Error while trying to save file. Is the file still there?");
							e.printStackTrace();
						}
						}else {
							sender.sendMessage(Main.prefix + "§7Non Yaml files don't need to be saved!");
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else if (args[0].equals("reload")) {
					if (session.containsKey(sender.getName())) {
						File file = session.get(sender.getName());
						if(configs.containsKey(sender.getName())) {
						try {
							configs.get(sender.getName()).load(file);
						} catch (IOException | InvalidConfigurationException e) {
							sender.sendMessage("§cError while trying to reload. Is the config valid?");
							e.printStackTrace();
						}
						sender.sendMessage(Main.prefix + "§7File reloaded!");
						}else {
							sender.sendMessage(Main.prefix + "§7Non Yaml files don't need to be reloaded!");
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else if (args[0].equals("close")) {
					if (session.containsKey(sender.getName())) {
						session.remove(sender.getName());
						sender.sendMessage(Main.prefix + "Session closed!");
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else {
					help(sender);
				}
			} else if (args.length == 2) {
				if (args[0].equals("start")) {
					if (PluginUtils.getPluginByName(args[1]) != null) {
						File config = new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/config.yml");
						session.put(sender.getName(), config);
						configs.put(sender.getName(), YamlConfiguration.loadConfiguration(config));
						sender.sendMessage(Main.prefix + "§7Started editing session with the file: §aconfig.yml §7from the plugin: §a" + PluginUtils.getPluginByName(args[1]).getName());
					}else if(new File(args[1]).exists()){
						File config = new File(args[1]);
						session.put(sender.getName(), config);
						sender.sendMessage(
								Main.prefix + "§7Started editing session with the file: §a" + config.getName());
					}
				} else if (args[0].equals("get")) {
					if(session.containsKey(sender.getName())) {
					if (configs.containsKey(sender.getName())) {
						StringBuilder builder = new StringBuilder();
						for (int i = 1; i < args.length; i++) {
							builder.append(" " + args[i]);
						}
						sender.sendMessage(Main.prefix + "§7The value of §a" + builder.substring(1) + " §7is: '§a" + configs.get(sender.getName()).get(builder.substring(1)) + "§7'");
					   }else {
						   sender.sendMessage(Main.prefix + "§7Not supported for Non-Yaml files!");
					   }
					}else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				}else if (args[0].equals("read")) {
					if (session.containsKey(sender.getName())) {
						int min = Integer.parseInt(args[1]);
						try {
							List<String> lines = Files.readAllLines(Paths.get(session.get(sender.getName()).getAbsolutePath()));
							int max = lines.size();
							if(min <= max && min > 0) {
						    for(int i = min;i < max + 1;i++) {
						    	
						    	if(Main.getPlugin(Main.class).getConfig().getBoolean("showlinenumbers")) {
						    		sender.sendMessage(i + " " + lines.get(i-1));
						    	}else {
						    		sender.sendMessage(lines.get(i-1));
						    	}
						    }
							}else {
								sender.sendMessage("§cError the file only has " + lines.size() + " line(s)!");
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else {
					help(sender);
				}
			} else if (args.length > 2) {
				if (args[0].equals("start")) {
					StringBuilder builder = new StringBuilder();
					for (int i = 2; i < args.length; i++) {
						builder.append(" " + args[i]);
					}
					String name = builder.substring(1);
					if (PluginUtils.getPluginByName(args[1]) != null) {
						if (args[2].contains(".yml") && new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + name).exists()) {
							File config = new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + name);
							session.put(sender.getName(), config);
							configs.put(sender.getName(), YamlConfiguration.loadConfiguration(config));
							sender.sendMessage(
									Main.prefix + "§7Started editing session with the file: §a" + config.getName() + " §7from the plugin: §a" + PluginUtils.getPluginByName(args[1]).getName());
						} else if (new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + name + ".yml").exists()) {
							File config = new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + name + ".yml");
							session.put(sender.getName(), config);
							configs.put(sender.getName(), YamlConfiguration.loadConfiguration(config));
							sender.sendMessage(
									Main.prefix + "§7Started editing session with the file: §a" + config.getName() + " §7from the plugin: §a" + PluginUtils.getPluginByName(args[1]).getName());
						} else if(new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + name).exists()){
							File config = new File(PluginUtils.getPluginByName(args[1]).getDataFolder() + "/" + name);
							session.put(sender.getName(), config);
							sender.sendMessage(
									Main.prefix + "§7Started editing session with the file: §a" + config.getName() + " §7from the plugin: §a" + PluginUtils.getPluginByName(args[1]).getName());
						}else {
							sender.sendMessage("§cError file not found!");
						}
					}else if(new File(name).exists()){
						File config = new File(name);
						session.put(sender.getName(), config);
						sender.sendMessage(
								Main.prefix + "§7Started editing session with the file: §a" + config.getName());
					}
				} else if (args[0].equals("set")) {
					if (session.containsKey(sender.getName())) {
						if(configs.containsKey(sender.getName())) {
						String previous = (String) configs.get(sender.getName()).get(args[1]).toString();
						StringBuilder builder = new StringBuilder();
						for (int i = 2; i < args.length; i++) {
							builder.append(" " + args[i]);
						}
						configs.get(sender.getName()).set(args[1], builder.substring(1));
						sender.sendMessage(Main.prefix + "§7You changed the value of §a" + args[1] + " §7from: '§a" + previous + "§7' to '§a" + builder.substring(1) + "§7'");
						}else {
							   sender.sendMessage(Main.prefix + "§7Not supported for Non-Yaml files!");
						}
					}else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else if (args[0].equals("read")) {
					if (session.containsKey(sender.getName())) {
						int min = Integer.parseInt(args[1]);
						int max = Integer.parseInt(args[2]);
						try {
							List<String> lines = Files.readAllLines(Paths.get(session.get(sender.getName()).getAbsolutePath()));
							if(lines.size() >= max) {
							if(min <= lines.size() && min > 0 && max > 0) {
						    for(int i = min;i < max + 1;i++) {
						    	if(Main.getPlugin(Main.class).getConfig().getBoolean("showlinenumbers")) {
						    		sender.sendMessage(i + " " + lines.get(i-1));
						    	}else {
						    		sender.sendMessage(lines.get(i-1));
						    	}
						    }
							}else {
								sender.sendMessage("§cError the file only has " + lines.size() + " line(s)!");
							}
							}else {
								sender.sendMessage("§cError the file only has " + lines.size() + " line(s)!");
							}
						} catch (IOException e) {
							sender.sendMessage("§Error while trying to save file. Is the file still there?");
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else if (args[0].equals("write")) {
					if (session.containsKey(sender.getName())) {
						int line = Integer.parseInt(args[1]);
						try {
							List<String> lines = Files.readAllLines(Paths.get(session.get(sender.getName()).getAbsolutePath()));
							if(line >= lines.size()) {
								String previous = lines.get(line-1);
								StringBuilder builder = new StringBuilder();
								for (int i = 2; i < args.length; i++) {
									builder.append(" " + args[i]);
								}
								lines.set(line-1, builder.substring(1));
								Files.write(Paths.get(session.get(sender.getName()).getAbsolutePath()), lines, Charset.forName("UTF-8"));
								sender.sendMessage(Main.prefix + "§7You changed the content of line §a" + args[1] + " §7from: '§a" + previous + "§7' to '§a" + builder.substring(1) + "§7'");
							}
						} catch (IOException e) {
							sender.sendMessage("§Error while trying to save file. Is the file still there?");
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(Main.prefix + "§7You dont have a editing session open!");
					}
				} else {
					help(sender);
				}
			}
		}
		return false;

		}

	public void help(CommandSender sender) {
		sender.sendMessage(Main.prefix + "Help");
		sender.sendMessage("§aBase Commands:");
		sender.sendMessage("§a> /edit start <plugin> §7| starts a edit session with the config.yml of given plugin (aslong as it has one)");
		sender.sendMessage("§a> /edit start <plugin> <configfile> §7| starts a edit session with the given Yaml file from the plugin");
		sender.sendMessage("§a> /edit start <plugin> <file> §7| starts a edit session with the given file contained in that plugins folder");
		sender.sendMessage("§a> /edit start <file> §7| (experimental) starts a edit session with the given file");
		sender.sendMessage("§aIn-Edit Commands");
		sender.sendMessage("§a> /edit get <section> §7| gets the value from a given section (only for Yaml file editing)");
		sender.sendMessage("§a> /edit set <section> <value> §7| sets the value of a given section (only for Yaml file editing)");
		sender.sendMessage("§a> /edit read <linestart> [lineend] §7| reads the file from the starting line to the end or to the given end line");
		sender.sendMessage("§a> /edit write <line> <text> §7| Writes text to the given line in the text");
		sender.sendMessage("§a> /edit save §7| saves the config file (only for Yaml file editing)");
		sender.sendMessage("§a> /edit reload §7| reloads the config file (only for Yaml file editing)");
		sender.sendMessage("§a> /edit close §7| closes the edit session");
	}
}
