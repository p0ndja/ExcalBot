package me.palapon2545.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import me.palapon2545.main.pluginMain;
import me.confuser.barapi.BarAPI;
import me.palapon2545.main.ActionBarAPI;

public class pluginMain extends JavaPlugin implements Listener {

	public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	public final Logger logger = Logger.getLogger("Minecraft");
	public pluginMain plugin;

	String cl = "§";
	String sv = ChatColor.BLUE + "Server> " + ChatColor.GRAY;
	String pp = ChatColor.BLUE + "Portal> " + ChatColor.GRAY;
	String np = ChatColor.RED + "You don't have permission or op!";
	String wp = ChatColor.RED + "Player not found.";
	String type = ChatColor.GRAY + "Type: " + ChatColor.GREEN;
	String non = ChatColor.GRAY + " is not number";
	String tc = ChatColor.BLUE + "" + ChatColor.BOLD + "Teleport Charger: ";
	String ct = tc + ChatColor.RED + "Cancelled!";
	String cd = ChatColor.AQUA + "" + ChatColor.BOLD + "[Countdown]: " + ChatColor.WHITE;
	String nn = ChatColor.GRAY + " is not number.";

	public void onDisable() {
		Bukkit.broadcastMessage(sv + "ExcalBot System: " + ChatColor.RED + ChatColor.BOLD + "Disable");
		for (Player player1 : Bukkit.getOnlinePlayers()) {
			player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_PLING, 10, 0);
		}
		removeBarAll();
		saveConfig();
	}

	public void onEnable() {
		Bukkit.broadcastMessage(sv + "ExcalBot System: " + ChatColor.GREEN + ChatColor.BOLD + "Enable");
		File warpfiles;
		File reportfiles;
		try {
			reportfiles = new File(getDataFolder() + File.separator + "/ReportDatabase/");
			warpfiles = new File(getDataFolder() + File.separator + "/WarpDatabase/");
			if (!reportfiles.exists()) {
				reportfiles.mkdirs();
			}
			if (!warpfiles.exists()) {
				warpfiles.mkdirs();
			}
		} catch (SecurityException e) {
			return;
		}
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		ActionBarAPI.run();
		getConfig().options().copyDefaults(true);
		getConfig().set("warp", null);
		getConfig().set("event.warpstatus", "false");
		getConfig().set("event.name", "none");
		getConfig().set("event.join", "false");
		getConfig().set("event.queuelist", null);
		for (Player player1 : Bukkit.getOnlinePlayers()) {
			player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		}
		Bukkit.broadcastMessage("");
		String version = Bukkit.getPluginManager().getPlugin("ExcalBot").getDescription().getVersion();
		Bukkit.broadcastMessage("ExcalBot's patch version: " + ChatColor.GREEN + version);
		List<String> author = Bukkit.getPluginManager().getPlugin("ExcalBot").getDescription().getAuthors();
		Bukkit.broadcastMessage("Developer: " + ChatColor.GOLD + author);
		Bukkit.broadcastMessage("");
		BukkitScheduler s = getServer().getScheduler();
		saveConfig();
		s.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Countdown();
				AutoRestart();
			}
		}, 0L, 20L);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args) {
		String message = "";
			if (CommandLabel.equalsIgnoreCase("force") || CommandLabel.equalsIgnoreCase("ExcalBot:force")) {
				if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender || sender instanceof Player) {
					if (args.length != 0) {
						if (args[0].equalsIgnoreCase("all")) {
							if (sender instanceof Player) {
								Player p = (Player) sender;
								if (p.isOp() || p.hasPermission("main.force")) {
									message = "";
									for (int i = 1; i != args.length; i++)
										message += args[i] + " ";
									message = message.replaceAll("&", cl);
									for (Player p1 : Bukkit.getOnlinePlayers()) {
										p1.chat(message);
									}
									p.sendMessage(sv + "You forced all online player: " + ChatColor.WHITE + message);
									yes(p);
								} else {
									p.sendMessage(sv + np);
									no(p);
								}

							} else {
								message = "";
								for (int i = 1; i != args.length; i++)
									message += args[i] + " ";
								message = message.replaceAll("&", cl);
								for (Player p1 : Bukkit.getOnlinePlayers()) {
									p1.chat(message);
								}
								logger.info("[ExcalBot] '/force' : You forced all online player: " + message);	
							}
						} else if (Bukkit.getServer().getPlayer(args[0]) != null) {
							Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();
							if (sender instanceof Player) {
								Player p = (Player) sender;
								if (p.isOp() || p.hasPermission("main.force")) {
									message = "";
									for (int i = 1; i != args.length; i++)
										message += args[i] + " ";
									message = message.replaceAll("&", cl);
									targetPlayer.chat(message);
									p.sendMessage(sv + "You forced player " + ChatColor.GREEN + targetPlayerName + ChatColor.GRAY + " : " + ChatColor.WHITE + message);
									yes(p);
								} else {
									p.sendMessage(sv + np);
									no(p);
								}
							} else {
								message = "";
								for (int i = 1; i != args.length; i++)
									message += args[i] + " ";
								message = message.replaceAll("&", cl);
								targetPlayer.chat(message);
								logger.info("[ExcalBot] '/force' : You forced " + targetPlayerName + ": " + ChatColor.AQUA + message);	
							}
						} else {
							if (sender instanceof Player) {
								Player p = (Player) sender;
								p.sendMessage(sv + wp);
								no(p);
							} else {
							logger.info("[ExcalBot] '/force' : Player not found");
							}
						}
					} else {
						if (sender instanceof Player) {
							Player p = (Player) sender;
							p.sendMessage(sv + type + "/force [player|all] [message]");
							no(p);
						} else {
							logger.info("[ExcalBot] '/force' : Type: /force [player|all] [message]");	
						}
					}	
				}
			}
			if (CommandLabel.equalsIgnoreCase("clearchat") || CommandLabel.equalsIgnoreCase("ExcalBot:clearchat")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (p.hasPermission("main.clearchat") || p.hasPermission("main.*") || p.isOp()) {
						int x1 = 1;
						int x2 = 500;
						for (long x = x1; x <= x2; x++) {
							for (Player p1 : Bukkit.getOnlinePlayers()) {
								p1.sendMessage("");
							}
						}
						Bukkit.broadcastMessage(sv + "Chat has been cleaned by " + ChatColor.GREEN + p.getName());
					} else {
						p.sendMessage(sv + np);
						no(p);
					}
				} else {
					int x1 = 1;
					int x2 = 500;
					for (long x = x1; x <= x2; x++) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.sendMessage("");
						}
					}
					Bukkit.broadcastMessage(sv + "Chat has been cleaned by " + ChatColor.GREEN + "CONSOLE");
				}
			}
			if (CommandLabel.equalsIgnoreCase("bc") || CommandLabel.equalsIgnoreCase("ExcalBot:bc")
					|| CommandLabel.equalsIgnoreCase("broadcast")
					|| CommandLabel.equalsIgnoreCase("ExcalBot:broadcast")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.broadcast")) {
						if (args.length == 0 || args[0].isEmpty()) {
							player.sendMessage(sv + type + "/broadcast [text]");
							no(player);
						} else if (args.length != 0) {
							for (String part : args) {
								if (message != "")
									message += " ";
								message += part;
							}
							message = message.replaceAll("&", cl);
							Bukkit.broadcastMessage("");
							Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Broadcast> " + ChatColor.WHITE + message);
							Bukkit.broadcastMessage("");
						} else {
							player.sendMessage(sv + np);
							no(player);
						}
					}	
				} else {
					if (args.length!=0) {
						for (String part : args) {
							if (message != "")
								message += " ";
							message += part;
						}
						message = message.replaceAll("&", cl);
						Bukkit.broadcastMessage("");
						Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Broadcast> " + ChatColor.WHITE + message);
						Bukkit.broadcastMessage("");
					} else {
						logger.info("[ExcalBot] '/broadcast' : Type : '/broadcast [message]");
					}
				}
				
			}
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String playerName = player.getName();
			File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
					File.separator + "PlayerDatabase/" + playerName);
			File f = new File(userdata, File.separator + "config.yml");
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
			if (CommandLabel.equalsIgnoreCase("event") || CommandLabel.equalsIgnoreCase("ExcalBot:event")) {
				String evn = getConfig().getString("event.name");
				String evj = getConfig().getString("event.join");
				String evs = getConfig().getString("event.queuelist." + playerName);
				String re = "";
				String status = "";
				if (evj.equalsIgnoreCase("true")) {
					status = ChatColor.GREEN + "Yes";
				}
				if (evj.equalsIgnoreCase("false")) {
					status = ChatColor.RED + "No";
				}
				if (evs.equalsIgnoreCase(null) || evs.equalsIgnoreCase("false")) {
					re = ChatColor.GRAY + "" + ChatColor.ITALIC + "Not Reserve";
				}
				if (evs.equalsIgnoreCase("true")) {
					re = ChatColor.LIGHT_PURPLE + "Reserved";
				}
				if (args.length == 0) {
					player.sendMessage(
							"---------" + ChatColor.LIGHT_PURPLE + "[Event]" + ChatColor.WHITE + "---------");
					player.sendMessage("Name: " + ChatColor.AQUA + evn);
					player.sendMessage("Reservation: " + status);
					player.sendMessage("Status: " + re);
					player.sendMessage("");
					player.sendMessage(
							"'/event warp' " + ChatColor.GOLD + "-" + ChatColor.YELLOW + " Warp to event location");
					player.sendMessage("'/event reserve'  " + ChatColor.GOLD + "-" + ChatColor.YELLOW
							+ " Add/Cancel your reservation");
					player.sendMessage("");
				} else {
					if (args[0].equalsIgnoreCase("warp")) {
						String warp = getConfig().getString("event.warp");
						String warpstatus = getConfig().getString("event.warpstatus");
						if (warp != null && warpstatus.equalsIgnoreCase("true")) {
							double x = getConfig().getDouble("event.warp.x");
							double y = getConfig().getDouble("event.warp.y");
							double z = getConfig().getDouble("event.warp.z");
							double yaw = getConfig().getDouble("event.warp.yaw");
							double pitch = getConfig().getDouble("event.warp.pitch");
							String world = getConfig().getString("event.warp.world");
							World p = Bukkit.getWorld(world);
							Location loc = new Location(p, x, y, z);
							loc.setPitch((float) pitch);
							loc.setYaw((float) yaw);
							player.teleport(loc);
							player.sendMessage(pp + "Teleported to " + ChatColor.YELLOW + "Event's Spectate Location"
									+ ChatColor.GRAY + ".");
							player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 10, 0);
						} else {
							player.sendMessage(pp + ChatColor.YELLOW + "Event's Warp Location isn't available yet");
							no(player);
						}
					}
					if (args[0].equalsIgnoreCase("reserve")) {
						String evl = getConfig().getString("event.queuelist." + playerName);
						if (evj.equalsIgnoreCase("true")) {
							if (evl.equalsIgnoreCase("false")
									|| getConfig().getString("event.queuelist." + playerName) == null
									|| evl.equalsIgnoreCase(null)) {
								getConfig().set("event.queuelist." + playerName, "true");
								saveConfig();
								player.sendMessage(pp + "You reserved event's reserve slot");
							} else {
								getConfig().set("event.queuelist." + playerName, "false");
								saveConfig();
								player.sendMessage(pp + "You canceled your event's reserve slot");
							}
						} else {
							player.sendMessage(pp + "You can't do it at this time (Reservation has been locked)");
						}
					}
				}
			}
			if (CommandLabel.equalsIgnoreCase("eventadmin") || CommandLabel.equalsIgnoreCase("ExcalBot:eventadmin")) {
				if (player.isOp() || player.hasPermission("main.eventadmin") || player.hasPermission("main.*")) {
					if (args.length == 0) {
						player.sendMessage("------------------");
						player.sendMessage("'/eventadmin setname' " + ChatColor.GOLD + "-" + ChatColor.YELLOW
								+ " Set event's name");
						player.sendMessage("'/eventadmin setwarp' " + ChatColor.GOLD + "-" + ChatColor.YELLOW
								+ " Set event's warp location");
						player.sendMessage("'/eventadmin reserve' " + ChatColor.GOLD + "-" + ChatColor.YELLOW
								+ " Open/Close reservation system");
						player.sendMessage(
								"'/eventadmin close' " + ChatColor.GOLD + "-" + ChatColor.YELLOW + " Close event");
						player.sendMessage("'/eventadmin warpplayer' " + ChatColor.GOLD + "-" + ChatColor.YELLOW
								+ " Warp Reserved Player to your location");
						player.sendMessage("------------------");
					} else {
						if (args[0].equalsIgnoreCase("setwarp")) {
							Location pl = player.getLocation();
							double plx = pl.getX();
							double ply = pl.getY();
							double plz = pl.getZ();
							double plpitch = pl.getPitch();
							double plyaw = pl.getYaw();
							String plw = pl.getWorld().getName();
							getConfig().set("event.warp.world", plw);
							getConfig().set("event.warp.x", plx);
							getConfig().set("event.warp.y", ply);
							getConfig().set("event.warp.z", plz);
							getConfig().set("event.warp.pitch", plpitch);
							getConfig().set("event.warp.yaw", plyaw);
							getConfig().set("event.warpstatus", "true");
							saveConfig();
							player.sendMessage(pp + ChatColor.GREEN + "Set new event's warp location");
						}
						if (args[0].equalsIgnoreCase("reserve")) {
							if (getConfig().getString("event.join").equalsIgnoreCase("false")) {
								getConfig().set("event.join", "true");
								saveConfig();
								player.sendMessage(pp + "Event Reserve: " + ChatColor.GREEN + "Enable");
							} else {
								getConfig().set("event.join", "false");
								saveConfig();
								player.sendMessage(pp + "Event Reserve: " + ChatColor.RED + "Disable");
							}
						}
						if (args[0].equalsIgnoreCase("warpplayer")) {
							Location pl = player.getLocation();
							double plx = pl.getX();
							double ply = pl.getY();
							double plz = pl.getZ();
							double plpitch = pl.getPitch();
							double plyaw = pl.getYaw();
							String plw = pl.getWorld().getName();
							World p = Bukkit.getWorld(plw);
							Location loc = new Location(p, plx, ply, plz);
							loc.setPitch((float) plpitch);
							loc.setYaw((float) plyaw);
							for (Player o : Bukkit.getOnlinePlayers()) {
								String join = getConfig().getString("event.queuelist." + o.getName());
								if (join.equalsIgnoreCase("true")) {
									o.teleport(loc);
									player.sendMessage(
											pp + "Admin teleport you to " + ChatColor.YELLOW + "Event's Location");
								} else {

								}
							}
						}
						if (args[0].equalsIgnoreCase("setname")) {
							message = "";
							for (int i = 1; i != args.length; i++)
								message += args[i] + " ";
							message = message.replaceAll("&", cl);
							getConfig().set("event.name", message);
							saveConfig();
							player.sendMessage(pp + "Set event's name to " + ChatColor.YELLOW + "' " + message + "'");
						}
						if (args[0].equalsIgnoreCase("close")) {
							String evn = getConfig().getString("event.name");
							Bukkit.broadcastMessage(pp + "Event " + ChatColor.YELLOW + evn + ChatColor.GRAY
									+ "has been " + ChatColor.RED + "closed");
							getConfig().set("event.warpstatus", "false");
							getConfig().set("event.name", "none");
							getConfig().set("event.join", "false");
							for (Player p : Bukkit.getOnlinePlayers()) {
								getConfig().set("event.queuelist." + p.getName(), "false");
							}
							saveConfig();
						}
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}

			}
			if (CommandLabel.equalsIgnoreCase("gamemode") || CommandLabel.equalsIgnoreCase("ExcalBot:gamemode")
					|| CommandLabel.equalsIgnoreCase("gm") || CommandLabel.equalsIgnoreCase("ExcalBot:gm")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.gamemode")) {
					if (args.length == 0) {
						player.sendMessage(sv + type + "/gamemode [mode] [player] (/gm)");
						player.sendMessage(ChatColor.GREEN + "Available Mode: ");
						player.sendMessage(ChatColor.WHITE + "- " + ChatColor.GREEN + "Survival , S , 0");
						player.sendMessage(ChatColor.WHITE + "- " + ChatColor.GREEN + "Creative , C , 1");
						player.sendMessage(ChatColor.WHITE + "- " + ChatColor.GREEN + "Adventure , A , 2");
						player.sendMessage(ChatColor.WHITE + "- " + ChatColor.GREEN + "Spectator , SP , 3");
					}
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("1") || args[0].startsWith("c")) {
							player.setGameMode(GameMode.CREATIVE);
							player.sendMessage(
									sv + "Your gamemode has been updated to " + ChatColor.GREEN + "Creative.");
						} else if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s")
								|| args[0].startsWith("su")) {
							player.setGameMode(GameMode.SURVIVAL);
							player.sendMessage(
									sv + "Your gamemode has been updated to " + ChatColor.YELLOW + "Survival.");
						} else if (args[0].equalsIgnoreCase("2") || args[0].startsWith("a")) {
							player.setGameMode(GameMode.ADVENTURE);
							player.sendMessage(
									sv + "Your gamemode has been updated to " + ChatColor.LIGHT_PURPLE + "Adventure.");
						} else if (args[0].equalsIgnoreCase("3") || args[0].startsWith("sp")) {
							player.setGameMode(GameMode.SPECTATOR);
							player.sendMessage(
									sv + "Your gamemode has been updated to " + ChatColor.AQUA + "Spectator.");
						}
					}
					if (args.length == 2) {
						if (Bukkit.getServer().getPlayer(args[1]) != null) {
							Player targetPlayer = player.getServer().getPlayer(args[1]);
							String targetPlayerName = targetPlayer.getName();
							if ((args[0].equalsIgnoreCase("1")) || (args[0].equalsIgnoreCase("c"))
									|| (args[0].equalsIgnoreCase("creative"))) {
								targetPlayer.setGameMode(GameMode.CREATIVE);
								targetPlayer.sendMessage(
										sv + "Your gamemode has been updated to " + ChatColor.GREEN + "Creative.");
								player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s " + ChatColor.GRAY
										+ "gamemode has been updated to " + ChatColor.GREEN + "Creative.");
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 10, 0);
							} else if ((args[0].equalsIgnoreCase("0")) || (args[0].equalsIgnoreCase("s"))
									|| (args[0].equalsIgnoreCase("survival"))) {
								targetPlayer.setGameMode(GameMode.SURVIVAL);
								targetPlayer.sendMessage(
										sv + "Your gamemode has been updated to " + ChatColor.YELLOW + "Survival.");
								player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s " + ChatColor.GRAY
										+ "gamemode has been updated to " + ChatColor.YELLOW + "Survival.");
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 10, 0);
							} else if ((args[0].equalsIgnoreCase("2")) || (args[0].equalsIgnoreCase("a"))
									|| (args[0].equalsIgnoreCase("adventure"))) {
								targetPlayer.setGameMode(GameMode.ADVENTURE);
								targetPlayer.sendMessage(sv + "Your gamemode has been updated to "
										+ ChatColor.LIGHT_PURPLE + "Adventure.");
								player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s " + ChatColor.GRAY
										+ "gamemode has been updated to " + ChatColor.LIGHT_PURPLE + "Adventure.");
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 10, 0);
							} else if ((args[0].equalsIgnoreCase("3")) || (args[0].equalsIgnoreCase("sp"))
									|| (args[0].equalsIgnoreCase("spectator"))) {
								targetPlayer.setGameMode(GameMode.SPECTATOR);
								targetPlayer.sendMessage(
										sv + "Your gamemode has been updated to " + ChatColor.AQUA + "Spectator.");
								player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s " + ChatColor.GRAY
										+ "gamemode has been updated to " + ChatColor.AQUA + "Spectator.");
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 10, 0);
							}
						} else {
							player.sendMessage(sv + wp);
							no(player);
						}
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("heal") || CommandLabel.equalsIgnoreCase("ExcalBot:heal")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.heal")) {
					if (args.length == 0) {
						player.setFoodLevel(40);
						for (PotionEffect Effect : player.getActivePotionEffects()) {
							player.removePotionEffect(Effect.getType());
						}
						player.setHealth(20);
						player.setFoodLevel(40);
						player.sendMessage(sv + ChatColor.LIGHT_PURPLE + "You have been healed!");
						yes(player);
					} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("all")) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								for (PotionEffect Effect : p.getActivePotionEffects()) {
									p.removePotionEffect(Effect.getType());
								}
								p.setHealth(20);
								p.setFoodLevel(40);
								p.sendMessage(sv + ChatColor.LIGHT_PURPLE + "You have been healed!");
								yes(p);
							}
							player.sendMessage(sv + ChatColor.LIGHT_PURPLE + "You healed " + ChatColor.YELLOW
									+ "all online player" + "!");
						} else if (Bukkit.getServer().getPlayer(args[0]) != null) {
							Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();
							for (PotionEffect Effect : targetPlayer.getActivePotionEffects()) {
								targetPlayer.removePotionEffect(Effect.getType());
							}
							targetPlayer.setHealth(20);
							targetPlayer.setFoodLevel(40);
							targetPlayer.sendMessage(sv + ChatColor.LIGHT_PURPLE + "You have been healed!");
							yes(targetPlayer);
							player.sendMessage(sv + ChatColor.LIGHT_PURPLE + "You healed " + ChatColor.YELLOW
									+ targetPlayerName + "!");
						} else {
							player.sendMessage(sv + wp);
							no(player);
						}
					} else {
						player.sendMessage(sv + type + "/heal [player]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);

				}
			}
			if (CommandLabel.equalsIgnoreCase("fly") || CommandLabel.equalsIgnoreCase("ExcalBot:fly")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.fly")) {
					if (args.length == 0) {
						if (player.getAllowFlight() == false) {
							player.setAllowFlight(true);
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
							player.sendMessage(sv + "You " + ChatColor.GREEN + "grant " + ChatColor.YELLOW + playerName
									+ "'s ability " + ChatColor.GRAY + "to fly. ");
						} else if (player.getAllowFlight() == true) {
							player.setAllowFlight(false);
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 0);
							player.sendMessage(sv + "You " + ChatColor.RED + "revoke " + ChatColor.YELLOW + playerName
									+ "'s ability " + ChatColor.GRAY + "to fly. ");
						}
					} else if (args.length == 1) {
						if (Bukkit.getServer().getPlayer(args[0]) != null) {
							Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();
							if (player.getAllowFlight() == false) {
								targetPlayer.setAllowFlight(true);
								player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
								player.sendMessage(sv + "You " + ChatColor.GREEN + "grant " + ChatColor.YELLOW
										+ targetPlayerName + "'s ability " + ChatColor.GRAY + "to fly. ");
							} else if (player.getAllowFlight() == true) {
								targetPlayer.setAllowFlight(false);
								player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 0);
								player.sendMessage(sv + "You " + ChatColor.RED + "revoke " + ChatColor.YELLOW
										+ targetPlayerName + "'s ability " + ChatColor.GRAY + "to fly. ");
							}
						} else {
							player.sendMessage(sv + wp);
							no(player);
						}
					} else {
						player.sendMessage(sv + type + "/fly [player]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("stuck") || CommandLabel.equalsIgnoreCase("ExcalBot:stuck")) {
				Location pl = player.getLocation();
				double x = pl.getX();
				double y = (pl.getY() + 0.1);
				double z = pl.getZ();
				double pitch = pl.getPitch();
				double yaw = pl.getYaw();
				World p = pl.getWorld();
				Location loc = new Location(p, x, y, z);
				loc.setPitch((float) pitch);
				loc.setYaw((float) yaw);
				player.teleport(loc);
				player.sendMessage(sv + ChatColor.YELLOW + "You have been resend your location.");
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
			}
			if (CommandLabel.equalsIgnoreCase("day") || CommandLabel.equalsIgnoreCase("ExcalBot:day")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.time")) {
					World w = ((Player) sender).getWorld();
					player.sendMessage(sv + "Set time to " + ChatColor.GOLD + "Day " + ChatColor.GRAY + ChatColor.ITALIC
							+ "(1000 ticks)");
					yes(player);
					w.setTime(1000);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("midday") || CommandLabel.equalsIgnoreCase("ExcalBot:midday")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.time")) {
					player.sendMessage(sv + "Set time to " + ChatColor.GOLD + "Midday " + ChatColor.GRAY
							+ ChatColor.ITALIC + "(6000 ticks)");
					World w = ((Player) sender).getWorld();
					yes(player);
					w.setTime(6000);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("night") || CommandLabel.equalsIgnoreCase("ExcalBot:night")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.time")) {
					World w = ((Player) sender).getWorld();
					player.sendMessage(sv + "Set time to " + ChatColor.GOLD + "Night " + ChatColor.GRAY
							+ ChatColor.ITALIC + "(13000 ticks)");
					yes(player);
					w.setTime(13000);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("midnight") || CommandLabel.equalsIgnoreCase("ExcalBot:midnight")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.time")) {
					World w = ((Player) sender).getWorld();
					player.sendMessage(sv + "Set time to " + ChatColor.GOLD + "Midnight " + ChatColor.GRAY
							+ ChatColor.ITALIC + "(18000 ticks)");
					yes(player);
					w.setTime(18000);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("ping") || CommandLabel.equalsIgnoreCase("ExcalBot:ping")) {
				int ping = getPing(player);
				if (args.length == 0) {
					if (ping < 31) {
						ChatColor color = ChatColor.AQUA;
						player.sendMessage(sv + "Your ping is " + color + ping + ChatColor.GRAY + " ms.");
					}
					if (ping > 30 && ping < 81) {
						ChatColor color = ChatColor.GREEN;
						player.sendMessage(sv + "Your ping is " + color + ping + ChatColor.GRAY + " ms.");
					}
					if (ping > 80 && ping < 151) {
						ChatColor color = ChatColor.GOLD;
						player.sendMessage(sv + "Your ping is " + color + ping + ChatColor.GRAY + " ms.");
					}
					if (ping > 150 && ping < 501) {
						ChatColor color = ChatColor.RED;
						player.sendMessage(sv + "Your ping is " + color + ping + ChatColor.GRAY + " ms.");
					}
					if (ping > 500) {
						ChatColor color = ChatColor.DARK_RED;
						player.sendMessage(sv + "Your ping is " + color + ping + ChatColor.GRAY + " ms.");
					}
				} else if (args.length == 1) {
					if (player.getServer().getPlayer(args[0]) != null) {
						Player targetPlayer = player.getServer().getPlayer(args[0]);
						String targetPlayerName = targetPlayer.getName();
						int ping2 = getPing(targetPlayer);
						if (ping2 < 31) {
							ChatColor color = ChatColor.AQUA;
							player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s ping" + ChatColor.GRAY
									+ " is " + color + ping2 + ChatColor.GRAY + " ms.");
						} else if (ping2 > 30 && ping < 81) {
							ChatColor color = ChatColor.GREEN;
							player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s ping" + ChatColor.GRAY
									+ " is " + color + ping2 + ChatColor.GRAY + " ms.");
						} else if (ping2 > 80 && ping < 151) {
							ChatColor color = ChatColor.GOLD;
							player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s ping" + ChatColor.GRAY
									+ " is " + color + ping2 + ChatColor.GRAY + " ms.");
						} else if (ping2 > 150 && ping < 501) {
							ChatColor color = ChatColor.RED;
							player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s ping" + ChatColor.GRAY
									+ " is " + color + ping2 + ChatColor.GRAY + " ms.");
						} else if (ping2 > 500) {
							ChatColor color = ChatColor.DARK_RED;
							player.sendMessage(sv + ChatColor.YELLOW + targetPlayerName + "'s ping" + ChatColor.GRAY
									+ " is " + color + ping2 + ChatColor.GRAY + " ms.");
						}
					} else {
						player.sendMessage(ChatColor.BLUE + "Server>" + ChatColor.GRAY + wp);
						no(player);
					}
				}
			}
			if (CommandLabel.equalsIgnoreCase("world") || CommandLabel.equalsIgnoreCase("ExcalBot:world")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.world")) {
					double x = player.getLocation().getX();
					double y = player.getLocation().getY();
					double z = player.getLocation().getZ();
					double pitch = player.getLocation().getPitch();
					double yaw = player.getLocation().getYaw();
					if (args.length == 2 || args.length == 1) {
						if (Bukkit.getWorld(args[0]) != null) {
							World w = Bukkit.getWorld(args[0]);
							if (args.length == 1) {
								Location loc = new Location(w, x, y, z);
								loc.setPitch((float) pitch);
								loc.setYaw((float) yaw);
								player.teleport(loc);
								player.sendMessage(sv + "Sent " + ChatColor.YELLOW + playerName + ChatColor.GRAY
										+ " to world " + ChatColor.AQUA + args[0]);
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 10, 0);
							} else if (args.length == 2 && !args[1].isEmpty()) {
								if (Bukkit.getServer().getPlayer(args[1]) != null) {
									Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
									String targetPlayerName = targetPlayer.getName();
									Location loc = new Location(w, x, y, z);
									loc.setPitch((float) pitch);
									loc.setYaw((float) yaw);
									player.teleport(loc);
									player.sendMessage(sv + "Sent " + ChatColor.YELLOW + targetPlayerName
											+ ChatColor.GRAY + " to world " + ChatColor.AQUA + args[0]);
									targetPlayer.sendMessage(sv + "You have been sent to world " + ChatColor.GREEN
											+ args[0] + ChatColor.GRAY + " by " + ChatColor.YELLOW + playerName);
									targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_CHICKEN_EGG, 10, 0);
								} else {
									player.sendMessage(sv + wp);
									no(player);
								}
							} else {
								player.sendMessage(sv + type + "/world [world]");
								no(player);
							}
						} else {
							player.sendMessage(
									sv + "World " + ChatColor.YELLOW + args[0] + ChatColor.GRAY + " not found.");
							no(player);
						}
					} else {
						player.sendMessage(sv + type + "/world [world]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("sun")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.climate")) {
					World w = ((Player) sender).getWorld();
					if (args.length == 1) {
						if (Bukkit.getServer().getWorld(args[0]) != null) {
							w = Bukkit.getServer().getWorld(args[0]);
						} else {
							w = ((Player) sender).getWorld();
						}
					}
					w.setThundering(false);
					w.setStorm(false);
					player.sendMessage(sv + "Set weather to " + ChatColor.GOLD + "Sunny" + ChatColor.GRAY + " at world "
							+ ChatColor.GREEN + w.getName() + ChatColor.GRAY + ".");
					yes(player);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("rain")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.climate")) {
					World w = ((Player) sender).getWorld();
					if (args.length == 1) {
						if (Bukkit.getServer().getWorld(args[0]) != null) {
							w = Bukkit.getServer().getWorld(args[0]);
						} else {
							w = ((Player) sender).getWorld();
						}
					}
					w.setThundering(false);
					w.setStorm(true);
					player.sendMessage(sv + "Set weather to " + ChatColor.AQUA + "Rain" + ChatColor.GRAY + " at world "
							+ ChatColor.GREEN + w.getName() + ChatColor.GRAY + ".");
					yes(player);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("storm")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.climate")) {
					World w = ((Player) sender).getWorld();
					if (args.length == 1) {
						if (Bukkit.getServer().getWorld(args[0]) != null) {
							w = Bukkit.getServer().getWorld(args[0]);
						} else {
							w = ((Player) sender).getWorld();
						}
					}
					w.setThundering(true);
					w.setStorm(true);
					player.sendMessage(sv + "Set weather to " + ChatColor.BLUE + "Storm" + ChatColor.GRAY + " at world "
							+ ChatColor.GREEN + w.getName() + ChatColor.GRAY + ".");
					yes(player);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("platewarp")) {
				Location loc = player.getLocation();
				loc.setY(loc.getY());
				Location locs = player.getLocation();
				locs.setY(loc.getY() - 2);
				Block block = loc.getBlock();
				Block blocks = locs.getBlock();
				String w = getConfig().getString("WarpState." + playerName);
				if (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE) {
					if (blocks.getType() == Material.SIGN_POST || blocks.getType() == Material.WALL_SIGN) {
						Sign sign = (Sign) blocks.getState();
						if (sign.getLine(0).equalsIgnoreCase("[tp]")) {
							if (w.equalsIgnoreCase("1")) {
								ActionBarAPI.send(player, tc + ChatColor.GOLD + "▃ " + ChatColor.GRAY + "▄ ▅ ▆ ▇");
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.3);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								getConfig().set("WarpState." + playerName, "2");
								saveConfig();
								getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									@Override
									public void run() {
										player.performCommand("platewarp");
									}
								}, 10);
							} else if (w.equalsIgnoreCase("2")) {
								if (player.isSneaking() == false) {
									getConfig().set("WarpState." + playerName, "false");
									saveConfig();
									ActionBarAPI.send(player, ct);
									no(player);
								} else {
									ActionBarAPI.send(player, tc + ChatColor.GOLD + "▃ ▄ " + ChatColor.GRAY + "▅ ▆ ▇");
									player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.5);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									getConfig().set("WarpState." + playerName, "3");
									saveConfig();
									getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

										@Override
										public void run() {
											player.performCommand("platewarp");
										}
									}, 10);
								}
							} else if (w.equalsIgnoreCase("3")) {
								if (player.isSneaking() == false) {
									getConfig().set("WarpState." + playerName, "false");
									saveConfig();
									ActionBarAPI.send(player, ct);
									no(player);
								} else {
									ActionBarAPI.send(player, tc + ChatColor.GOLD + "▃ ▄ ▅ " + ChatColor.GRAY + "▆ ▇");
									player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.7);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);

									getConfig().set("WarpState." + playerName, "4");
									saveConfig();
									getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
										@Override
										public void run() {
											player.performCommand("platewarp");
										}
									}, 10);
								}
							} else if (w.equalsIgnoreCase("4")) {
								if (player.isSneaking() == false) {
									getConfig().set("WarpState." + playerName, "false");
									saveConfig();
									ActionBarAPI.send(player, ct);
									no(player);
								} else {
									ActionBarAPI.send(player, tc + ChatColor.GOLD + "▃ ▄ ▅ ▆ " + ChatColor.GRAY + "▇");
									player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.9);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);

									getConfig().set("WarpState." + playerName, "5");
									saveConfig();
									getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

										@Override
										public void run() {
											player.performCommand("platewarp");
										}
									}, 10);
								}
							} else if (w.equalsIgnoreCase("5")) {
								if (player.isSneaking() == false) {
									getConfig().set("WarpState." + playerName, "false");
									saveConfig();
									ActionBarAPI.send(player, ct);
									no(player);
								} else {
									ActionBarAPI.send(player, tc + ChatColor.GOLD + "▃ ▄ ▅ ▆ ▇");
									player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 1.2);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
									player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);

									getConfig().set("WarpState." + playerName, "6");
									saveConfig();
									getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
										@Override
										public void run() {
											player.performCommand("platewarp");
										}
									}, 15);
								}
							} else if (w.equalsIgnoreCase("6")) {
								if (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE) {

									Location loc2 = player.getLocation();
									loc2.setY(loc.getY() - 2);
									Block block2 = loc2.getBlock();
									if ((block2.getType() == Material.SIGN_POST
											|| block2.getType() == Material.WALL_SIGN)) {
										Location loc3 = player.getLocation();
										loc3.setY(loc.getY() - 3);
										Block block3 = loc3.getBlock();
										Sign s1 = (Sign) block2.getState();
										Sign s2 = (Sign) block3.getState();
										if (s1.getLine(0).equalsIgnoreCase("[tp]")
												&& s2.getLine(0).equalsIgnoreCase("[world]")) {
											if (block3.getType() == Material.SIGN_POST
													|| block3.getType() == Material.WALL_SIGN) {
												World world = Bukkit
														.getWorld(s2.getLine(1) + s2.getLine(2) + s2.getLine(3));
												if (world != null) {
													Location pl = player.getLocation();
													double xh = Integer.parseInt(s1.getLine(1));
													double yh = Integer.parseInt(s1.getLine(2));
													double zh = Integer.parseInt(s1.getLine(3));
													double x = xh + 0.5;
													double y = yh;
													double z = zh + 0.5;
													double yaw = pl.getYaw();
													double pitch = pl.getPitch();
													Location loca = new Location(world, x, y, z);
													loca.setPitch((float) pitch);
													loca.setYaw((float) yaw);
													player.teleport(loca);
													ActionBarAPI.send(player,
															ChatColor.GREEN + "" + ChatColor.BOLD + "Teleport!");
													yes(player);
												} else {
													ActionBarAPI.send(player,
															ChatColor.RED + "World " + ChatColor.WHITE + s2.getLine(1)
																	+ s2.getLine(2) + s2.getLine(3) + ChatColor.RED
																	+ " not found");
													no(player);
												}
											} else {
											}
										} else {
										}
									} else {
									}
								} else {
								}
								getConfig().set("WarpState." + playerName, "false");
								saveConfig();
							}
						}
					}
				} else {
					getConfig().set("WarpState." + playerName, "false");
					saveConfig();
					ActionBarAPI.send(player, ct);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("countdown") || CommandLabel.equalsIgnoreCase("ExcalBot:countdown") || CommandLabel.equalsIgnoreCase("cd") || CommandLabel.equalsIgnoreCase("Excalbot:cd")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.countdown")) {
					if (args.length != 0) {
						if (args[0].equalsIgnoreCase("start")) {
							if (args.length == 2) {
								if (isInt(args[1])) {
									long i = Integer.parseInt(args[1]);
									player.sendMessage(sv + "Set timer to " + ChatColor.YELLOW + args[1] + " seconds");
									getConfig().set("countdown_msg", "Undefined");
									getConfig().set("count_start_count", i);
									getConfig().set("count", i);
									saveConfig();
								} else

								{
									player.sendMessage(sv + ChatColor.YELLOW + args[1] + nn);
								}
							} else if (args.length > 2) {
								if (isInt(args[1])) {
									long l = Integer.parseInt(args[1]);
									for (int i = 2; i != args.length; i++)
										message += args[i] + " ";
									message = message.replaceAll("&", cl);
									getConfig().set("count_start_count", l);
									getConfig().set("count", l);
									getConfig().set("countdown_msg", message);
									saveConfig();
									player.sendMessage(sv + "Set timer to " + ChatColor.YELLOW + args[1]
											+ " seconds with message " + ChatColor.GREEN + message);
								} else

								{
									player.sendMessage(sv + ChatColor.YELLOW + args[1] + nn);
								}

							} else {
								player.sendMessage(sv + type + "/countdown start [second] [message]");
							}
						}
						if (args[0].equalsIgnoreCase("stop")) {
							player.sendMessage(sv + "Stopped Countdown");
							if (getServer().getPluginManager().isPluginEnabled("BarAPI") == true) {
								sendBarAll(cd + "Countdown has been cancelled");
								removeBarAll();
							}
							getConfig().set("countdown_msg", "Undefined");
							getConfig().set("count", -1);
							getConfig().set("count_start_count", -1);
							saveConfig();
						}
					} else {
						player.sendMessage(sv + type + "/countdown [start/stop] [second]");
					}
				} else {
					player.sendMessage(sv + np);
				}
			}
			if (CommandLabel.equalsIgnoreCase("mute")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.mute")) {
					if (args.length > 1) {
						if (Bukkit.getServer().getPlayer(args[0]) != null) {

							Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();
							File userdata1 = new File(
									Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
									File.separator + "PlayerDatabase/" + targetPlayerName);
							File f1 = new File(userdata1, File.separator + "config.yml");
							FileConfiguration playerData1 = YamlConfiguration.loadConfiguration(f1);
							String muteis = playerData1.getString("mute.is");
							if (muteis.equalsIgnoreCase("false")) {
								message = "";
								for (int i = 1; i != args.length; i++)
									message += args[i] + " ";
								message = message.replaceAll("&", cl);
								Bukkit.broadcastMessage(ChatColor.BLUE + "Chat> " + ChatColor.GRAY + "Player "
										+ ChatColor.YELLOW + playerName + ChatColor.RED + " revoke " + ChatColor.YELLOW
										+ targetPlayerName + "'s ability " + ChatColor.GRAY + "to chat. ");
								Bukkit.broadcastMessage(ChatColor.BLUE + "Chat> " + ChatColor.GRAY + "Reason: "
										+ ChatColor.YELLOW + message);
								targetPlayer.sendMessage(sv + "You have been muted.");
								targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
										1, 1);
								try {
									playerData1.set("mute.is", "true");
									playerData1.set("mute.reason", message);
									playerData1.save(f1);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							if (muteis.equalsIgnoreCase("true")) {
								Bukkit.broadcastMessage(ChatColor.BLUE + "Chat> " + ChatColor.GRAY + "Player "
										+ ChatColor.YELLOW + playerName + ChatColor.GREEN + " grant " + ChatColor.YELLOW
										+ targetPlayerName + "'s ability " + ChatColor.GRAY + "to chat. ");
								player.sendMessage(sv + "You " + ChatColor.GREEN + "grant " + ChatColor.YELLOW
										+ targetPlayerName + "'s ability " + ChatColor.GRAY + "to chat. ");
								targetPlayer.sendMessage(sv + "You have been unmuted.");
								targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
										1, 1);
								try {
									playerData1.set("mute.is", "false");
									playerData1.set("mute.reason", "none");
									playerData1.save(f1);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

						} else {
							player.sendMessage(sv + wp);
							no(player);
						}
					} else {
						player.sendMessage(sv + type + "/mute [player] [reason]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("warn")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.warn")) {
					if (args.length > 1) {
						if (Bukkit.getServer().getPlayer(args[0]) != null) {
							Player targetPlayer = player.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();
							File userdata1 = new File(
									Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
									File.separator + "PlayerDatabase/" + targetPlayerName);
							File f1 = new File(userdata1, File.separator + "config.yml");
							FileConfiguration playerData1 = YamlConfiguration.loadConfiguration(f1);
							int countwarn = playerData1.getInt("warn");
							message = "";
							for (int i = 1; i != args.length; i++)
								message += args[i] + " ";
							message = message.replaceAll("&", cl);
							int countnew = countwarn + 1;
							if (countnew == 4) {
								countnew = 3;
								Bukkit.broadcastMessage(sv + targetPlayerName + " has been banned");
								Bukkit.broadcastMessage(sv + "Reason: " + ChatColor.YELLOW + message);
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
										"ban " + targetPlayerName + " " + message);
							} else {
								Bukkit.broadcastMessage(sv + targetPlayerName + " has been warned (" + countnew + ")");
								Bukkit.broadcastMessage(sv + "Reason: " + ChatColor.YELLOW + message);
							}
							try {
								playerData1.set("warn", countnew);
								playerData1.save(f1);
							} catch (IOException e) {
								e.printStackTrace();
							}
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
							}
						} else {
							player.sendMessage(sv + wp);
							no(player);
						}

					} else {
						player.sendMessage(sv + type + "/warn [player] [reason]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("resetwarn")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.warn")) {
					if (args.length == 1) {
						if (Bukkit.getServer().getPlayer(args[0]) != null) {
							Player targetPlayer = player.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();
							File userdata1 = new File(
									Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
									File.separator + "PlayerDatabase/" + targetPlayerName);
							File f1 = new File(userdata1, File.separator + "config.yml");
							FileConfiguration playerData1 = YamlConfiguration.loadConfiguration(f1);
							message = "";
							for (int i = 1; i != args.length; i++) // catch args[0]
																	// -> i = 0
								message += args[i] + " ";
							message = message.replaceAll("&", cl);
							Bukkit.broadcastMessage(sv + ChatColor.YELLOW + playerName + ChatColor.GRAY + " reset "
									+ targetPlayerName + "'s warned (0)");
							try {
								playerData1.set("warn", 0);
								playerData1.save(f1);
							} catch (IOException e) {
								e.printStackTrace();
							}
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
							}
						} else {
							player.sendMessage(sv + wp);
						}
					} else {
						player.sendMessage(sv + type + "/resetwarn [player]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("wiki") || CommandLabel.equalsIgnoreCase("ExcalBot:wiki")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rule")) {
						player.sendMessage(sv + "System is not ready.");
					} else if (args[0].equalsIgnoreCase("warn")) {
						player.sendMessage(sv + "System is not ready.");
					} else {
						player.sendMessage(ChatColor.BLUE + "Wiki> " + ChatColor.GRAY + "Topic " + ChatColor.YELLOW
								+ args[0] + ChatColor.GRAY + " not found!");
					}
				} else {
					player.sendMessage(ChatColor.BLUE + "Wiki> " + ChatColor.GRAY + "Welcome to " + ChatColor.GREEN
							+ ChatColor.BOLD + "WIKI - The Information center");
					player.sendMessage(ChatColor.BLUE + "Wiki> " + ChatColor.GREEN + "Available Topic: "
							+ ChatColor.YELLOW + "No-Topic");
					player.sendMessage(ChatColor.BLUE + "Wiki> " + ChatColor.GRAY + "Please choose your topic by type: "
							+ ChatColor.YELLOW + "/wiki [topic]");
					player.sendMessage(ChatColor.RED + "ADS> " + ChatColor.WHITE + "Wiki's Writter Wanted! Contact "
							+ ChatColor.LIGHT_PURPLE + "@SMD_SSG_PJ");
				}
			}
			if (CommandLabel.equalsIgnoreCase("invisible")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.invisible")) {
					String invi = playerData.getString("Invisible");
					if (invi.equalsIgnoreCase("false")) {
						try {
							playerData.set("Invisible", "true");
							playerData.save(f);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage(sv + "You're now " + ChatColor.AQUA + "invisible.");
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.hasPermission("main.seeinvisible") || p.isOp() || p.hasPermission("main.*")) {
								p.showPlayer(player);
							} else {
								p.hidePlayer(player);
							}
						}
					}
					if (invi.equalsIgnoreCase("true")) {
						try {
							playerData.set("Invisible", "false");
							playerData.save(f);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage(sv + "You're now " + ChatColor.GREEN + "visible.");
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.showPlayer(player);
						}
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("adminchat") || CommandLabel.equalsIgnoreCase("ac")
					|| CommandLabel.equalsIgnoreCase("ExcalBot:ac")
					|| CommandLabel.equalsIgnoreCase("ExcalBot:adminchat")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.adminchat")) {
					if (args.length != 0) {
						for (String part : args) {
							if (message != "")
								message += " ";
							message += part;
						}
						message = message.replaceAll("&", cl);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.isOp() || p.hasPermission("main.*") || p.hasPermission("main.adminchat")) {
								p.sendMessage(ChatColor.RED + "AdminChat> " + player.getDisplayName() + " "
										+ ChatColor.WHITE + message);
								p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
							} else {

							}
						}
					} else {
						player.sendMessage(sv + type + "/adminchat [message]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("freeze") || CommandLabel.equalsIgnoreCase("ExcalBot:freeze")) {
				if (player.isOp() || player.hasPermission("main.*") || player.hasPermission("main.freeze")) {
					if (args.length == 1) {
						if (Bukkit.getServer().getPlayer(args[0]) != null) {
							Player targetPlayer = player.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();
							File userdata1 = new File(
									Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
									File.separator + "PlayerDatabase/" + targetPlayerName);
							File f1 = new File(userdata1, File.separator + "config.yml");
							FileConfiguration playerData1 = YamlConfiguration.loadConfiguration(f1);
							String freeze = playerData1.getString("freeze");
							if (freeze.equalsIgnoreCase("true")) {
								try {
									playerData1.set("freeze", "false");
									playerData1.save(f1);
								} catch (IOException e) {
									e.printStackTrace();
								}
								player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
								targetPlayer.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
								player.sendMessage(sv + "You " + ChatColor.GREEN + "grant " + ChatColor.YELLOW
										+ targetPlayerName + "'s ability " + ChatColor.GRAY + "to move.");
								targetPlayer.setAllowFlight(false);
							}
							if (freeze.equalsIgnoreCase("false")) {
								try {
									playerData1.set("freeze", "true");
									playerData1.save(f1);
								} catch (IOException e) {
									e.printStackTrace();
								}
								player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 0);
								player.sendMessage(sv + "You " + ChatColor.RED + "revoke " + ChatColor.YELLOW
										+ targetPlayerName + "'s ability " + ChatColor.GRAY + "to move.");
								targetPlayer.setAllowFlight(true);
								no(targetPlayer);
							}
						}

					} else {
						player.sendMessage(sv + type + "/freeze [player]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("closechunk")) {
				if (player.isOp()) {
					for (World w : Bukkit.getWorlds()) {
						for (Chunk c : w.getLoadedChunks()) {
							c.unload(true);
						}
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("hat")) {
				if (player.isOp()) {
					ItemStack i = (ItemStack) player.getItemInHand();
					player.getInventory().setHelmet(i);
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("wild")) {
				int x = new Random().nextInt(20000);
				int z = new Random().nextInt(20000);
				int v = new Random().nextInt(1);
				int w = new Random().nextInt(1);
				int xn = 0;
				int zn = 0;
				if (v == 0) {
					xn = 0 - x;
				}
				if (v == 1) {
					xn = 0 + x;
				}
				if (w == 0) {
					zn = 0 - z; 
				}
				if (w == 1) {
					zn = 0 + z;
				}
				World item = Bukkit.getWorld("item");
				if (item == null) {
					player.sendMessage(sv + "Unable to run command.");
					no(player);
				} else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
					getConfig().set("before_wild." + playerName + ".x", player.getLocation().getX());
					getConfig().set("before_wild." + playerName + ".z", player.getLocation().getZ());
					getConfig().set("before_wild." + playerName + ".world", player.getWorld().getName());
					saveConfig();
					Location loc = new Location(item, xn, 255, zn);
					loc.setPitch((float) player.getLocation().getPitch());
					loc.setYaw((float) player.getLocation().getYaw());
					player.teleport(loc);	
					player.sendMessage(sv + "Teleport randomly to world " + ChatColor.YELLOW + "item" + ChatColor.GRAY + ".");
					yes(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("back")) {
				World item = Bukkit.getWorld("item");
				if (item == null) {
					player.sendMessage(sv + "Unable to run command.");
					no(player);
				} else {
					if (player.getLocation().getWorld() == item) {
						long x = getConfig().getLong("before_wild." + playerName + ".x");
						long z = getConfig().getLong("before_wild." + playerName + ".z");
						String w = getConfig().getString("before_wild." + playerName + ".world");
						World wn = Bukkit.getWorld(w);
						Location loc = new Location(wn, x, 255, z);
						loc.setPitch((float) player.getLocation().getPitch());
						loc.setYaw((float) player.getLocation().getYaw());
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
						player.teleport(loc);
						player.sendMessage(sv + "Teleport back to " + ChatColor.YELLOW + "last location before use /wild" + ChatColor.GRAY + ".");
						yes(player);
					} else {
						player.sendMessage(sv + "You're not in world " + ChatColor.YELLOW + "item" + ChatColor.GRAY + ".");
						no(player);
					}
				}
			}
			if (CommandLabel.equalsIgnoreCase("warp") || CommandLabel.equalsIgnoreCase("SMDMain:warp")) {
				File warpfiles;
				try {
					warpfiles = new File(getDataFolder() + File.separator + "/WarpDatabase/");
					if (!warpfiles.exists()) {
						warpfiles.mkdirs();
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				File path = new File(Bukkit.getServer().getPluginManager().getPlugin("SMDMain").getDataFolder(),
						File.separator + "WarpDatabase/");
				if (args.length == 0) {
					player.sendMessage(sv + "List warp: " + ChatColor.GREEN + getConfig().getStringList("listwarp"));
				}
				if (args.length == 1) {
					File warpdata = new File(Bukkit.getServer().getPluginManager().getPlugin("SMDMain").getDataFolder(),
							File.separator + "WarpDatabase/");
					File f1 = new File(warpdata, File.separator + args[0] + ".yml");
					FileConfiguration warpData = YamlConfiguration.loadConfiguration(f1);
					if (f1.exists()) {
						double plx = warpData.getDouble("x");
						double ply = warpData.getDouble("y");
						double plz = warpData.getDouble("z");
						double plyaw = warpData.getDouble("yaw");
						double plpitch = warpData.getDouble("pitch");
						World plw = Bukkit.getWorld(warpData.getString("world"));
						Location loc = new Location(plw, plx, ply, plz);
						loc.setPitch((float) plpitch);
						loc.setYaw((float) plyaw);
						player.teleport(loc);
						player.sendMessage(sv + "Teleported to Warp " + ChatColor.GREEN + args[0]);
						yes(player);
					} else {
						player.sendMessage(sv + "Warp " + ChatColor.YELLOW + args[0] + ChatColor.GRAY + " not found!");
						no(player);
					}
				}
			}
			if (CommandLabel.equalsIgnoreCase("setwarp")) {
				if (player.isOp() || player.hasPermission("main.setwarp") || player.hasPermission("main.*")) {
					if (args.length == 1) {
						File warpdata = new File(
								Bukkit.getServer().getPluginManager().getPlugin("SMDMain").getDataFolder(),
								File.separator + "WarpDatabase/");
						File f1 = new File(warpdata, File.separator + args[0] + ".yml");
						FileConfiguration warpData = YamlConfiguration.loadConfiguration(f1);
						if (!f1.exists()) {
							Location pl = player.getLocation();
							double plx = pl.getX();
							double ply = pl.getY();
							double plz = pl.getZ();
							double plpitch = pl.getPitch();
							double plyaw = pl.getYaw();
							String plw = pl.getWorld().getName();
							try {
								warpData.set("x", plx);
								warpData.set("y", ply);
								warpData.set("z", plz);
								warpData.set("yaw", plyaw);
								warpData.set("pitch", plpitch);
								warpData.set("world", plw);
								warpData.save(f1);
							} catch (IOException e) {
								e.printStackTrace();
							}
							addList("listwarp", args[0]);
							player.sendMessage(
									sv + "Set warp " + ChatColor.YELLOW + args[0] + ChatColor.GRAY + " complete!");
							yes(player);
						} else {
							player.sendMessage(
									sv + "Warp " + ChatColor.RED + args[0] + ChatColor.GRAY + " already using!");
							no(player);
						}
					} else {
						player.sendMessage(sv + type + "/setwarp [name]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("removewarp")) {
				if (player.isOp() || player.hasPermission("main.warp") || player.hasPermission("main.*")) {
					if (args.length == 1) {
						File warpdata = new File(
								Bukkit.getServer().getPluginManager().getPlugin("SMDMain").getDataFolder(),
								File.separator + "WarpDatabase/");
						File f1 = new File(warpdata, File.separator + args[0] + ".yml");
						FileConfiguration warpData = YamlConfiguration.loadConfiguration(f1);
						if (f1.exists()) {
							f1.delete();
							removeList("listwarp", args[0]);
							player.sendMessage(
									sv + "Remove warp " + ChatColor.YELLOW + args[0] + ChatColor.GRAY + " complete!");
							yes(player);
						} else {
							player.sendMessage(sv + "Warp " + ChatColor.RED + args[0] + ChatColor.GRAY + " not found!");
							no(player);
						}
					} else {
						player.sendMessage(sv + type + "/removewarp [name]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("report")) {
				long a = getConfig().getLong("report_count");
				long b = a + 1;
				File report = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
						File.separator + "ReportDatabase/");
				File file = new File(report, File.separator + b + ".yml");
				FileConfiguration reportData = YamlConfiguration.loadConfiguration(file);

				if (args.length > 1) {
					if (Bukkit.getServer().getOfflinePlayer(args[0]) != null) {
						Player target = (Player) Bukkit.getServer().getOfflinePlayer(args[0]);
						String c = b + "";
						message = "";
						for (int i = 1; i != args.length; i++)
							message += args[i] + " ";
						message = message.replaceAll("&", cl);
						player.sendMessage(sv + "You " + ChatColor.RED + "report " + ChatColor.LIGHT_PURPLE + args[0]);
						player.sendMessage(sv + "Report ID: " + ChatColor.LIGHT_PURPLE + b);
						player.sendMessage(sv + "Status: " + ChatColor.YELLOW + "Pending");
						player.sendMessage(sv + "Offender: " + ChatColor.AQUA + target.getName());
						player.sendMessage(sv + "Reporter: " + ChatColor.GREEN + playerName);
						player.sendMessage(sv + "Description: " + ChatColor.WHITE + message);
						getConfig().set("report_count", b);
						try {
							reportData.createSection("Report");
							reportData.set("Report.ID", b);
							reportData.set("Report.Reporter", playerName);
							reportData.set("Report.Offender", args[0]);
							reportData.set("Report.Status", "Pending");
							reportData.set("Report.Description", message);
							reportData.createSection("Inspector");
							reportData.set("Inspector", "none");
							reportData.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
						addList("unread_report", c);
						saveConfig();
					} else {
						player.sendMessage(sv + wp);
						no(player);
					}
				} else {
					player.sendMessage(
							sv + type + "/report [player] [reason]");
					no(player);
				}
			}
			
			if (CommandLabel.equalsIgnoreCase("listreport")) {
				if (player.hasPermission("main.*") || player.hasPermission("main.report") || player.isOp()) {
					player.sendMessage(
							sv + "Unread report ID: " + ChatColor.YELLOW + getConfig().getStringList("unread_report"));
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("checkreport")) {
				if (player.hasPermission("main.*") || player.hasPermission("main.report") || player.isOp()) {
					if (args.length == 1) {
						File report = new File(
								Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
								File.separator + "ReportDatabase/");
						File file = new File(report, File.separator + args[0] + ".yml");
						FileConfiguration reportData = YamlConfiguration.loadConfiguration(file);
						if (file.exists()) {
							long id = reportData.getLong("Report.ID");
							String reporter = reportData.getString("Report.Reporter");
							String offender = reportData.getString("Report.Offender");
							String status = reportData.getString("Report.Status");
							String description = reportData.getString("Report.Description");
							player.sendMessage(sv + "ID: " + ChatColor.LIGHT_PURPLE + id);
							player.sendMessage(sv + "Reporter: " + ChatColor.GREEN + reporter);
							player.sendMessage(sv + "Offender: " + ChatColor.AQUA + offender);
							player.sendMessage(sv + "Status: " + ChatColor.YELLOW + status);
							player.sendMessage(sv + "Inspector: " + ChatColor.GOLD + playerName);
							player.sendMessage(sv + "Description: " + ChatColor.WHITE + description);
							try {
								reportData.set("Inspector", playerName);
								reportData.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
							Bukkit.broadcastMessage(sv + "Report ID " + args[0] + " has received by " + playerName);
							yesAll();
						} else {
							player.sendMessage(sv + "Report not found.");
						}
					} else {
						player.sendMessage(sv + type + "/checkreport [id]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}

			}
			if (CommandLabel.equalsIgnoreCase("closereport")) {
				if (player.hasPermission("main.*") || player.hasPermission("main.report") || player.isOp()) {
					if (args.length == 1) {
						File report = new File(
								Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
								File.separator + "ReportDatabase/");
						File file = new File(report, File.separator + args[0] + ".yml");
						FileConfiguration reportData = YamlConfiguration.loadConfiguration(file);
						if (file.exists()) {
							try {
								reportData.set("Report.Status", "Close");
								reportData.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
							removeList("unread_report", args[0]);
							Bukkit.broadcastMessage(sv + "Report ID " + args[0] + " has closed by " + playerName);
							yesAll();
						} else {
							player.sendMessage(sv + "");
						}
					} else {
						player.sendMessage(sv + type + "/closereport [id]");
						no(player);
					}
				} else {
					player.sendMessage(sv + np);
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("tell") || CommandLabel.equalsIgnoreCase("ExcalBot:tell")
					|| CommandLabel.equalsIgnoreCase("whisper") || CommandLabel.equalsIgnoreCase("ExcalBot:whisper")
					|| CommandLabel.equalsIgnoreCase("w") || CommandLabel.equalsIgnoreCase("ExcalBot:w")
					|| CommandLabel.equalsIgnoreCase("t") || CommandLabel.equalsIgnoreCase("ExcalBot:t")) {
				if (args.length > 1) {
					if (Bukkit.getServer().getPlayer(args[0]) != null) {
						message = "";
						for (int i = 1; i != args.length; i++)
							message += args[i] + " ";
						message = message.replaceAll("&", cl);
						Player p = Bukkit.getServer().getPlayer(args[0]);
						if (p == player) {
							player.sendMessage(sv + "Are you kidding? You can't talking with yourself!");
							no(player);
						} else {
							p.sendMessage(ChatColor.AQUA + playerName + ChatColor.WHITE + " ➡ " + ChatColor.GREEN
									+ "You" + ChatColor.WHITE + ": " + message);
							player.sendMessage(ChatColor.AQUA + "You" + ChatColor.WHITE + " ➡ " + ChatColor.GREEN
									+ p.getName() + ChatColor.WHITE + ": " + message);
							getConfig().set("chat_last_send." + playerName, p.getName());
							getConfig().set("chat_last_send." + p.getName(), playerName);
							saveConfig();
						}
					} else {
						player.sendMessage(sv + wp);
						no(player);
					}
				} else {
					player.sendMessage(sv + type + "/tell [player] [message]");
					no(player);
				}
			}
			if (CommandLabel.equalsIgnoreCase("reply") || CommandLabel.equalsIgnoreCase("ExcalBot:reply")
					|| CommandLabel.equalsIgnoreCase("r") || CommandLabel.equalsIgnoreCase("ExcalBot:r")) {
				if (args.length > 0) {
					if (!getConfig().getString("chat_last_send." + playerName).equalsIgnoreCase("none")) {
						message = "";
						for (int i = 0; i != args.length; i++)
							message += args[i] + " ";
						message = message.replaceAll("&", cl);
						Player p = Bukkit.getServer().getPlayer(getConfig().getString("chat_last_send." + playerName));
						p.sendMessage(ChatColor.AQUA + playerName + ChatColor.WHITE + " ➡ " + ChatColor.GREEN + "You"
								+ ChatColor.WHITE + ": " + message);
						player.sendMessage(ChatColor.AQUA + "You" + ChatColor.WHITE + " ➡ " + ChatColor.GREEN
								+ p.getName() + ChatColor.WHITE + ": " + message);
					} else {
						player.sendMessage(sv + "You didn't talk to anyone yet!");
						no(player);
					}
				} else {
					player.sendMessage(sv + type + "/reply [message]");
					no(player);
				}
			}
		}

		return true;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
				File.separator + "PlayerDatabase/" + playerName);
		File f = new File(userdata, File.separator + "config.yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		if (!f.exists()) {
			try {
				playerData.createSection("warn");
				playerData.set("warn", 0);
				playerData.createSection("mute");
				playerData.set("mute.is", "false");
				playerData.set("mute.reason", "none");
				playerData.createSection("freeze");
				playerData.set("freeze", "false");
				playerData.createSection("uuid");
				playerData.set("uuid", player.getUniqueId().toString());;
				playerData.createSection("Invisible");
				playerData.set("Invisible", "false");
				playerData.createSection("gamemode");
				getConfig().set("event.queuelist." + playerName, "false");
				saveConfig();
				playerData.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (f.exists()) {
			String invi = playerData.getString("Invisible");
			if (invi.equalsIgnoreCase("true")) {
				player.sendMessage(sv + "You're now " + ChatColor.AQUA + "invisible.");
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.hasPermission("main.seeinvisible") || p.isOp() || p.hasPermission("main.*")) {
						p.showPlayer(player);
					} else {
						p.hidePlayer(player);
					}
				}
			}
			try {
				playerData.createSection("uuid");
				playerData.set("uuid", player.getUniqueId().toString());
				playerData.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int countwarn = playerData.getInt("warn");
			if (countwarn > 0) {
				player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "ALERT!" + ChatColor.RED
						+ " You have been warned " + ChatColor.YELLOW + countwarn + " time(s).");
				player.sendMessage(ChatColor.RED + "If you get warned 3 time, You will be " + ChatColor.DARK_RED
						+ ChatColor.BOLD + "BANNED.");
			}
		}
		String evs = getConfig().getString("event.queuelist." + playerName);
		if (evs == null || evs.isEmpty()) {
			getConfig().set("event.queuelist." + playerName, "false");
			saveConfig();
		}
		getConfig().set("WarpState." + playerName, "false");
		saveConfig();
		player.sendMessage("");
		String version = Bukkit.getPluginManager().getPlugin("ExcalBot").getDescription().getVersion();
		player.sendMessage(ChatColor.BOLD + "ExcalBot's Patch Version: " + version);
		player.sendMessage("");
	}

	@EventHandler
	public void onPlayerPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
				File.separator + "PlayerDatabase/" + playerName);
		File f = new File(userdata, File.separator + "config.yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		String freeze = playerData.getString("freeze");
		if (freeze.equalsIgnoreCase("true")) {
			event.setCancelled(true);
			ActionBarAPI.send(player, ChatColor.AQUA + "You're " + ChatColor.BOLD + "FREEZING");
		}
	}

	@EventHandler
	public void onPlayerBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
				File.separator + "PlayerDatabase/" + playerName);
		File f = new File(userdata, File.separator + "config.yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		String freeze = playerData.getString("freeze");
		if (freeze.equalsIgnoreCase("true")) {
			event.setCancelled(true);
			ActionBarAPI.send(player, ChatColor.AQUA + "You're " + ChatColor.BOLD + "FREEZING");
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		String m = message.replaceAll("&", cl);
		Player player = event.getPlayer();
		String playerName = player.getName();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
				File.separator + "PlayerDatabase/" + playerName);
		File f = new File(userdata, File.separator + "config.yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		String muteis = playerData.getString("mute.is");
		String mutere = playerData.getString("mute.reason");
		if (muteis.equalsIgnoreCase("true")) {
			player.sendMessage(ChatColor.BLUE + "Chat> " + ChatColor.GRAY + "You have been muted.");
			player.sendMessage(ChatColor.BLUE + "Chat> " + ChatColor.YELLOW + "Reason: " + ChatColor.GRAY + mutere);
			no(player);
			event.setCancelled(true);
		}
		event.setFormat(ChatColor.YELLOW + playerName + ChatColor.GRAY + ": " + ChatColor.RESET + m);
		if (message.equalsIgnoreCase("!help")) {
			Bukkit.broadcastMessage("[ExcalBot] Hello! " + playerName + ". There're 6 avalible commands: " + ChatColor.GREEN + "!help, !discord, !facebook (!fb), !group (!g), !donate and !about.");
		}
		if (message.equalsIgnoreCase("!discord")) {
			Bukkit.broadcastMessage("[ExcalBot] Here is discord invitation link! " + ChatColor.AQUA + ChatColor.UNDERLINE + "bit.ly/discordx");
		}
		if (message.equalsIgnoreCase("!fb") || message.equalsIgnoreCase("!facebook")) {
			Bukkit.broadcastMessage("[ExcalBot] Here is our fanpage link! " + ChatColor.GOLD + ChatColor.UNDERLINE + "fb.com/ExcaliburTHGuild");
		}
		if (message.equalsIgnoreCase("!group") || message.equalsIgnoreCase("!g")) {
			Bukkit.broadcastMessage("[ExcalBot] Here is our group link! " + ChatColor.GREEN + ChatColor.UNDERLINE + "fb.com/group/XCLBTH");
		}
		if (message.equalsIgnoreCase("!donate")) {
			Bukkit.broadcastMessage("[ExcalBot] You can donate by " + ChatColor.BOLD + "True" + ChatColor.GOLD + ChatColor.BOLD + "Wallet" + ChatColor.GREEN + " 090-8508007");
		}
		if (message.equalsIgnoreCase("!about")) {
			Bukkit.broadcastMessage("[ExcalBot] This bot has been developed by p0N67a");
			Bukkit.broadcastMessage("[ExcalBot] text2");
			Bukkit.broadcastMessage("[ExcalBot] text3");
			Bukkit.broadcastMessage("[ExcalBot] text4");
			Bukkit.broadcastMessage("[ExcalBot] text5");
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
				File.separator + "PlayerDatabase/" + playerName);
		File f = new File(userdata, File.separator + "config.yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		String freeze = playerData.getString("freeze");
		if (freeze.equalsIgnoreCase("true")) {
			event.setCancelled(true);
			ActionBarAPI.send(player, ChatColor.AQUA + "You're " + ChatColor.BOLD + "FREEZING");
			player.setAllowFlight(true);
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
				File.separator + "PlayerDatabase/" + playerName);
		File f = new File(userdata, File.separator + "config.yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		String freeze = playerData.getString("freeze");
		if (freeze.equalsIgnoreCase("true")) {
			event.setCancelled(true);
			ActionBarAPI.send(player, ChatColor.AQUA + "You're " + ChatColor.BOLD + "FREEZING");
		}
	}

	@EventHandler
	public void onPlayerLeft(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		getConfig().set("Teleport." + playerName, "None");
		getConfig().set("event.queuelist." + playerName, "false");
		getConfig().set("chat_last_send." + playerName, "none");
		saveConfig();
		int n = Bukkit.getServer().getOnlinePlayers().size();
		if (n == 0 || n < 0) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		} else {
			return;
		}
	}

	@EventHandler
	public void PlayerChangeSign(SignChangeEvent event) {
		Player player = event.getPlayer();
		String l0 = event.getLine(0).toLowerCase();
		String line0 = event.getLine(0);
		if (l0.endsWith("[tp]") || l0.endsWith("[sell]") || l0.endsWith("[buy]") || l0.endsWith("[luckyclick]")
				|| l0.endsWith("[cmd]") || l0.endsWith("[buyquota]")) {
			if (!player.isOp() && !player.hasPermission("main.sign")) {
				event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "Sorry" + ChatColor.RESET + ", but");
				event.setLine(1, "You " + ChatColor.BOLD + "need" + ChatColor.RESET + " perm.");
				event.setLine(2, "or op to create sign with");
				event.setLine(3, "'" + line0 + "'" + " prefix!");
				player.sendMessage(sv + np);
				Bukkit.broadcastMessage(sv + "Player " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY
						+ " try to create sign " + ChatColor.RED + ChatColor.BOLD + line0);
			}
		}
	}

	@EventHandler
	public void PlayerStandOnPlate(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		Location loc = player.getLocation();
		loc.setY(loc.getY());
		String w = getConfig().getString("WarpState." + playerName);
		Block block = loc.getBlock();
		if (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE) {
			Location loc2 = player.getLocation();
			loc2.setY(loc.getY() - 2);
			Block block2 = loc2.getBlock();
			if ((block2.getType() == Material.SIGN_POST) || (block2.getType() == Material.WALL_SIGN)) {
				Sign sign = (Sign) block2.getState();
				if (sign.getLine(0).equalsIgnoreCase("[tp]")) {
					if (!w.equalsIgnoreCase("false")) {
						// Mean player currently stand on plate, No sending
						// holding shift message
					} else {
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 10));
						ActionBarAPI.send(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Hold " + ChatColor.GREEN
								+ ChatColor.BOLD + ChatColor.UNDERLINE + "Shift" + ChatColor.AQUA + " to teleport.");
					}
				}
				if (sign.getLine(0).equalsIgnoreCase("[cmd]")) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 10));
					ActionBarAPI.send(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Hold " + ChatColor.GREEN
							+ ChatColor.BOLD + ChatColor.UNDERLINE + "Shift" + ChatColor.AQUA + " to perform command.");
				}
			}
		}
	}

	@EventHandler
	public void PlayerUsePlate(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		String w = getConfig().getString("WarpState." + playerName);
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("ExcalBot").getDataFolder(),
				File.separator + "PlayerDatabase/" + playerName);
		File f = new File(userdata, File.separator + "config.yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		Location loc = player.getLocation();
		Location loc2 = player.getLocation();
		Location loc3 = player.getLocation();
		Location loc4 = player.getLocation();
		loc.setY(loc.getY());
		loc2.setY(loc.getY() - 2);
		loc3.setY(loc.getY() - 3);
		loc4.setY(loc.getY() - 4);
		Block block = loc.getBlock();
		Block block2 = loc2.getBlock();
		Block block3 = loc3.getBlock();
		Block block4 = loc4.getBlock();
		if (event.isSneaking() == true) {
			if ((block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE)
					&& (block2.getType() == Material.SIGN_POST || block2.getType() == Material.WALL_SIGN)
					&& (block3.getType() == Material.SIGN_POST || block3.getType() == Material.WALL_SIGN)) {
				Sign s1 = (Sign) block2.getState();
				Sign s2 = (Sign) block3.getState();
				if (s1.getLine(0).equalsIgnoreCase("[tp]") && s2.getLine(0).equalsIgnoreCase("[world]")) {
					if (w.equalsIgnoreCase("false")) {
						getConfig().set("WarpState." + playerName, "1");
						saveConfig();
						player.performCommand("platewarp");
					}
				} else if (s1.getLine(0).equalsIgnoreCase("[cmd]")) {
					if (!s1.getLine(1).isEmpty()) {
						String l1 = s1.getLine(1);
						String l2 = s1.getLine(2);
						String l3 = s1.getLine(3);
						if (l1.startsWith("*")) {
							getServer().dispatchCommand(getServer().getConsoleSender(),
									l1.replaceAll("*", "") + l2 + l3);
							if (s2 != null) {
								if (s2.getLine(0).equalsIgnoreCase("[pay]")) {
									long targetPlayerMoney = playerData.getLong("money");
									if (isInt(s2.getLine(1)) && Integer.parseInt(s2.getLine(1)) > 0) {
										long n = (long) (targetPlayerMoney - Integer.parseInt(s2.getLine(1)));
										if (n < 0) {
											n = 0;
										}
										try {
											playerData.set("money", n);
											playerData.save(f);
										} catch (IOException e) {
											e.printStackTrace();
										}
										player.sendMessage(sv + "You paid " + ChatColor.GREEN + s2.getLine(1)
												+ " Coin(s) " + ChatColor.GRAY + "to " + ChatColor.AQUA + "CONSOLE"
												+ ChatColor.GRAY + ".");
										yes(player);
									}
								}
							} else {
								return;
							}
						}
						if (!l1.startsWith("*")) {
							player.performCommand(l1.replaceAll("$", playerName) + l2.replaceAll("$", playerName)
									+ l3.replaceAll("$", playerName));
							if (s2 != null) {
								if (s2.getLine(0).equalsIgnoreCase("[pay]")) {
									long targetPlayerMoney = playerData.getLong("money");
									if (isInt(s2.getLine(1)) && Integer.parseInt(s2.getLine(1)) > 0) {
										long n = (long) (targetPlayerMoney - Integer.parseInt(s2.getLine(1)));
										if (n < 0) {
											n = 0;
										}
										try {
											playerData.set("money", n);
											playerData.save(f);
										} catch (IOException e) {
											e.printStackTrace();
										}
										player.sendMessage(sv + "You paid " + ChatColor.GREEN + s2.getLine(1)
												+ " Coin(s) " + ChatColor.GRAY + "to " + ChatColor.AQUA + "CONSOLE"
												+ ChatColor.GRAY + ".");
										yes(player);
									}
								}
							} else {
								return;
							}
						}
					} else {
						ActionBarAPI.send(player, "This plate isn't " + ChatColor.RED + "ready");
					}
				} else {
					return;
				}
			} else {
				return;
			}
		} else

		{
			return;
		}
	}

	public void playCircularEffect(Location location, Effect effect, boolean v) {
		for (int i = 0; i <= 8; i += ((!v && (i == 3)) ? 2 : 1))
			location.getWorld().playEffect(location, effect, i);
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void checkDay() {
		long c = getConfig().getLong("count");
		if (c > 86399) {
			long d = c / 86400;
			long hm = c % 86400;
			long h = hm / 3600;
			long bm = c % 3600;
			long m = bm / 60;
			long s = bm % 60;
			String day = "";
			String hour = "";
			String minute = "";
			String second = "";
			
			if (d > 1) {
				day = d + " days ";
			}
			if (d == 1) {
				day = d + " days ";
			}
			if (d == 0) {
				hour = "";
			}
			
			if (h > 1) {
				hour = h + " hours ";
			}
			if (h == 1) {
				hour = h + " hour ";
			}
			if (h == 0) {
				hour = "";
			}
			
			if (m > 1) {
				minute = m + " minutes ";
			}
			if (m == 1) {
				minute = m + " minute ";
			}
			if (m == 0) {
				minute = "";
			}
			
			if (s > 1) {
				second = s + " seconds";
			}
			if (s == 1) {
				second = s + " second";
			}
			if (s == 0) {
				second = "";
			}
			
			if (getServer().getPluginManager().isPluginEnabled("BarAPI") == true) {
				sendBarAll(cd + day + hour + minute + second);
			} else {
				ActionBarAPI.sendToAll(cd + day + hour + minute + second);
			}
			
		} else {
			checkHour();
		}
	}

	public void checkHour() {
		long c = getConfig().getLong("count");
		if (c > 3599) {
			long h = c / 3600;
			long bm = c % 3600;
			long m = bm / 60;
			long s = bm % 60;
			String hour = "";
			String minute = "";
			String second = "";
			
			if (h > 1) {
				hour = h + " hours ";
			}
			if (h == 1) {
				hour = h + " hour ";
			}
			if (h == 0) {
				hour = "";
			}
			
			if (m > 1) {
				minute = m + " minutes ";
			}
			if (m == 1) {
				minute = m + " minute ";
			}
			if (m == 0) {
				minute = "";
			}
			
			if (s > 1) {
				second = s + " seconds";
			}
			if (s == 1) {
				second = s + " second";
			}
			if (s == 0) {
				second = "";
			}
			
			if (getServer().getPluginManager().isPluginEnabled("BarAPI") == true) {
				sendBarAll(cd + hour + minute + second);
			} else {
				ActionBarAPI.sendToAll(cd + hour + minute + second);
			}
			
		} else {
			checkMin();
		}
	}

	public void checkMin() {
		long c = getConfig().getLong("count");
		long a = getConfig().getLong("count_start_count");
		long value = c;
		long m = value / 60;
		long s = value % 60;
		String minute = "";
		String second = "";
		if (c > 59 && c < 3600) {
			if (m > 1) {
				minute = m + " minutes ";
			}
			if (m == 1) {
				minute = m + " minute ";
			}
			if (m == 0) {
				minute = "";
			}
			
			if (s > 1) {
				second = s + " seconds";
			}
			if (s == 1) {
				second = s + " second";
			}
			if (s == 0) {
				second = "";
			}
			
			if (getServer().getPluginManager().isPluginEnabled("BarAPI") == true) {
				sendBarAll(cd + minute + second);
			} else {
				ActionBarAPI.sendToAll(cd + minute + second);
			}
			
		} else {
			checkSec();
		}

	}

	public void checkSec() {
		long c = getConfig().getInt("count");
		long a = getConfig().getInt("count_start_count");
		String second = "";
		if (c > 5) {
			second = c + " seconds";
		}
		if (c == 5) {
			second = ChatColor.AQUA + "" + c + " seconds";
		}
		if (c == 4) {
			second = ChatColor.GREEN + "" + c + " seconds";
		}
		if (c == 3) {
			second = ChatColor.YELLOW + "" + c + " seconds";
		}
		if (c == 2) {
			second = ChatColor.GOLD + "" + c + " seconds";
		}
		if (c == 1) {
			second = ChatColor.RED + "" + c + " second";
		}
		if (c == 0) {
			second = ChatColor.LIGHT_PURPLE + "TIME UP!";
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					removeBarAll();
				}
			}, 60);
		}
		if (c >= 0) {
			if (getServer().getPluginManager().isPluginEnabled("BarAPI") == true) {
				sendBarAll(cd + second);
			} else {
				ActionBarAPI.sendToAll(cd + second);
			}	
		}

	}

	public void Countdown() {
		long c = getConfig().getLong("count");
		long a = getConfig().getLong("count_start_count");
		long cn = (long) c;
		long an = (long) a;
		long n = c - 1;
		long value = c;
		long h = value / 3600;
		long m = value % 3600;
		long s = m % 60;
		String st = getConfig().getString("countdown_msg_toggle");
		String ms = getConfig().getString("countdown_msg").replaceAll("&", cl);
		if (ms.equalsIgnoreCase("Undefined")) {
			checkDay();
		} else {
			if (s % 4 == 0) {
				if (c < 11) {
					getConfig().set("countdown_msg_toggle", "u");
					saveConfig();
				} else {
					if (st.equalsIgnoreCase("d")) {
						getConfig().set("countdown_msg_toggle", "u");
						saveConfig();
					}
					if (st.equalsIgnoreCase("u")) {
						getConfig().set("countdown_msg_toggle", "d");
						saveConfig();
					}
				}
			}
			if (st.equalsIgnoreCase("d")) {
				if (getServer().getPluginManager().isPluginEnabled("BarAPI") == true) {
					// long p = cn / an;
					// Bukkit.broadcastMessage("debug_percent");
					sendBarAll(cd + ms);
				} else {
					ActionBarAPI.sendToAll(cd + ms);
				}
			} else {
				checkDay();
			}
		}
		if (c == -1) {
			getConfig().set("count", -1);
			saveConfig();
		} else {
			getConfig().set("count", n);
			saveConfig();
		}
	}

	public static int getPing(Player p) {
		Class<?> CPClass;
		String bpName = Bukkit.getServer().getClass().getPackage().getName(),
				version = bpName.substring(bpName.lastIndexOf(".") + 1, bpName.length());
		try {
			CPClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
			Object CraftPlayer = CPClass.cast(p);
			Method getHandle = CraftPlayer.getClass().getMethod("getHandle", new Class[0]);
			Object EntityPlayer = getHandle.invoke(CraftPlayer, new Object[0]);
			Field ping = EntityPlayer.getClass().getDeclaredField("ping");
			return ping.getInt(EntityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void no(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 0);
	}

	public void yes(Player p) {
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
	}

	public void yesAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
		}
	}

	public void addList(String key, String... element) {
		List<String> list = getConfig().getStringList(key);
		list.addAll(Arrays.asList(element));
		getConfig().set(key, list);
		saveConfig();
	}

	public void removeList(String key, String... element) {
		List<String> list = getConfig().getStringList(key);
		list.removeAll(Arrays.asList(element));
		getConfig().set(key, list);
		saveConfig();
	}

	public void sendBar(Player p, String s) {
		BarAPI.setMessage(p, s);
	}

	public void sendBarAll(String s) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			BarAPI.setMessage(p, s);
		}
	}

	public void setBarHealth(Player p, float percent) {
		BarAPI.setHealth(p, percent);
	}

	public void setBarHealthAll(float percent) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			BarAPI.setHealth(p, percent);
		}
	}

	public void removeBar(Player p) {
		BarAPI.removeBar(p);
	}

	public void removeBarAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			BarAPI.removeBar(p);
		}
	}
	
	public void AutoRestart() {
		Date date = new Date(); 
		int l = 60 - date.getSeconds(); 
		if (date.getHours() == 23) { 
			if (date.getMinutes() == 59) { 
				if (date.getSeconds() == 0) { 
					getConfig().set("count", 30);
					getConfig().set("count_start_count", 30);
					getConfig().set("countdown_msg", "Automatic Restart Server");
					saveConfig();
				} else if (date.getSeconds() == 30) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
				} else if (date.getSeconds() == 40) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
				}
			}
		}
	}

}