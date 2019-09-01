package net.meano.SelfPrefix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import net.meano.PlayerManager.BukkitMain;
import net.meano.PlayerManager.PlayerInfo;

public class SelfPrefixMain extends JavaPlugin{
	public FileConfiguration PluginConfig;
	public ConfigurationSection Players;
	public String[] PlayerList;
	public int Version;
	public List<Player> PrefixPlayerList;
	public ItemStack HonourBerry;
	
	public ItemStack GetHonourBerry() {
		ItemStack Berry = new ItemStack(Material.SWEET_BERRIES, 1);
		ItemMeta BerryMeta = Berry.getItemMeta();
		BerryMeta.setDisplayName("荣誉果实");
		ArrayList<String> BerryLores = new ArrayList<String>();
		BerryLores.add(ChatColor.translateAlternateColorCodes('&', "&4&l--[ &8&n荣誉果实&4&l ]--"));
		BerryLores.add("----------------");
		BerryLores.add("品尝预览称号");
		BerryLores.add("食用获得称号");
		BerryMeta.setLore(BerryLores);
		Berry.setItemMeta(BerryMeta);
		return Berry;
	}
	
	public BukkitMain PMB;
	
	public void onEnable(){
		LoadConfig();
		PluginManager PM = Bukkit.getServer().getPluginManager();
		PMB = (BukkitMain) PM.getPlugin("PlayerManager");
		if(PMB != null) {
			getLogger().info("PlayerManager Found!");
		}
		PM.registerEvents(new SelfPrefixListeners(this), this);
		PrefixPlayerList = new ArrayList<Player>();

		HonourBerry = GetHonourBerry();
		//ShapedRecipe HonourBerryCube = new ShapedRecipe(); // new ShapedRecipe(this.PortalStar).shape(new String[] { "*#*", "#%#", "*#*" }).setIngredient('#', Material.EMERALD).setIngredient('*', Material.OBSIDIAN).setIngredient('%', Material.GOLDEN_APPLE);
		//Bukkit.getServer().addRecipe(portalCube);

	}
	
	// 检查称号是否允许
	public boolean CheckAllow(String prefix){
		if(prefix.equals(prefix.replaceAll("[^a-zA-Z_0-9\\[\\]\\&\u4e00-\u9fa5]", "")))
			return true;
		else
			return false;
	}
	
	// 命令处理
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("SelfPrefix")){
			if(args.length>0){
				if(args[0].toLowerCase().equals("rl")){
					if(sender.isOp()){
						LoadConfig();
						sender.sendMessage("配置载入成功");
						return true;
					}
				}else if(args[0].toLowerCase().equals("pre")){
					if(sender instanceof Player){
						if(PrefixPlayerList.contains(sender)){
							sender.sendMessage(ChatColor.RED + "称号正在预览中，请按TAB键查看自己的称号效果，本次称号预览完成后方可进行下一次预览！");
							return true;
						}else if(args.length<2){
							sender.sendMessage(ChatColor.GREEN+"用法: /sp pre &C[称号]&A 即可预览你的称号，不消耗更换称号次数，但只有20秒的时间");
							return true;
						}else if(args.length==2){
							PlayerPrefix WillChange = GetPrefix((Player) sender);
							if(WillChange!=null){
								if(WillChange.Count>0)
									if(CheckAllow(args[1])){
										if(args[1].length()<16){
											sender.sendMessage(ChatColor.GREEN+"前缀可以使用，你有20秒的时间来预览你的称号");
											WillChange.Prefix = args[1];
											PrePlayerPrefix((Player) sender, WillChange);
											PrefixPlayerList.add((Player) sender);
											Bukkit.getScheduler().scheduleSyncDelayedTask(this, 
													new Runnable(){
														public void run(){
															SetPlayerPrefix((Player) sender);
															PrefixPlayerList.remove((Player) sender);
															sender.sendMessage(ChatColor.GREEN+"称号预览已经结束，如果觉得满意使用命令/sp set &C[称号]&A 来保存你的称号。");
														}
											}, 20*20L);
											return true;
										}else{
											sender.sendMessage(ChatColor.RED+"抱歉，称号太长了，称号内容最多可15个字符!");
											return true;
										}
									}else{
										sender.sendMessage(ChatColor.RED+"抱歉，称号内有不允许使用的字符，可使用字符有英文大小写字母、数字、英文符号（下划线、左右方括号、&颜色改变符号）、中文字符!");
										return true;
									}
								else{
									sender.sendMessage(ChatColor.RED+"你的称号使用次数已经用尽，无法预览称号");
									return true;
								}
							}else{
								sender.sendMessage(ChatColor.RED+"抱歉，只有捐助玩家可以使用预览称号功能！");
								sender.sendMessage(ChatColor.RED+"已经捐助的玩家请联系管理员更新称号次数！");
								return true;
							}
						}
					}else
						return true;
				}else if(args[0].toLowerCase().equals("set")){
					if(sender instanceof Player){
						if(args.length<2){
							sender.sendMessage(ChatColor.GREEN+"用法: /sp set &C[称号]&A 即可设置你的称号，消耗更换称号次数，谨慎使用。");
							return true;
						}else if(args.length==2){
							PlayerPrefix WillChange = GetPrefix((Player) sender);
							if(WillChange!=null){
								if(WillChange.Count>0)
									if(CheckAllow(args[1])){
										if(args[1].length()<16){
											sender.sendMessage(ChatColor.GREEN+"前缀可以使用，正在更改称号。。。");
											WillChange.Prefix = args[1];
											if(WillChange.Enable){
												WillChange.Count = WillChange.Count-1;
												SavePlayerPrefix(WillChange);
												SetPlayerPrefix((Player) sender);
												sender.sendMessage(ChatColor.GREEN+"你的称号"+GenDisplayName(WillChange)+"更换成功，剩余更换称号次数"+WillChange.Count);
												return true;
											}else{
												sender.sendMessage(ChatColor.RED+"抱歉，你现在无法更改称号，请联系管理员!");
												return true;
											}
										}else{
											sender.sendMessage(ChatColor.RED+"抱歉，称号太长了，称号内容最多可15个字符!");
											return true;
										}
									}else{
										sender.sendMessage(ChatColor.RED+"抱歉，称号内有不允许使用的字符，可使用字符有英文大小写字母、数字、英文符号（下划线、左右方括号、&颜色改变符号）、中文字符!");
										return true;
									}
								else{
									sender.sendMessage(ChatColor.RED+"称号使用次数已经用尽，无法预览称号");
									return true;
								}
							}else{
								sender.sendMessage(ChatColor.RED+"抱歉，只有捐助玩家可以使用预览称号功能！");
								sender.sendMessage(ChatColor.RED+"已经捐助的玩家请联系管理员更新称号次数！");
								return true;
							}
						}
					}else
						return true;
				}else if(args[0].toLowerCase().equals("deal")){
					if(sender.isOp()){
						if(args.length==4){
							if(args[1].matches("[a-z_A-Z0-9\u4e00-\u9fa5]*")&&args[2].matches("[0-9]*")&&args[3].matches("[a-zA-Z_0-9\\[\\]\\&\u4e00-\u9fa5]*")){
								PlayerPrefix GotPrefix = new PlayerPrefix(args[3], Integer.parseInt(args[2]), true, args[1]);
								SavePlayerPrefix(GotPrefix);
								LoadConfig();
								sender.sendMessage("成功设置"+args[1]+"称号");
								return true;
							}else{
								sender.sendMessage("参数不正确");
								return true;
							}
						}else if(args.length == 3){
							if(args[1].matches("[a-z_A-Z0-9\u4e00-\u9fa5]*")&&args[2].matches("[0-9]*")){
								PlayerPrefix GotPrefix = new PlayerPrefix("&a[Fantastic]&c", Integer.parseInt(args[2]), true, args[1]);
								SavePlayerPrefix(GotPrefix);
								LoadConfig();
								sender.sendMessage("成功设置"+args[1]+"称号");
								return true;
							}else{
								sender.sendMessage("参数不正确");
								return true;
							}
						}else{
							sender.sendMessage("用法: /sp deal 玩家 次数 称号");
							return true;
						}
					}else{
						return true;
					}
				}else if(args[0].toLowerCase().equals("rm")){
					if(sender.isOp()){
						if(args.length==2){
							if(args[1].matches("[a-z_A-Z0-9\u4e00-\u9fa5]*")){
								//getConfig().getConfigurationSection("Players").getKeys(false).remove(args[1]);
								//for(String s:getConfig().getConfigurationSection("Players").getKeys(false).toArray(new String[0])){
								//	Bukkit.broadcastMessage(s);
								//}
								if(getConfig().get("Players." + args[1]) != null){
									getConfig().set("Players." + args[1], null);
									saveConfig();
									LoadConfig();
									if(Bukkit.getPlayer(args[1]).isOnline())
										RemovePlayerPrefix(Bukkit.getPlayer(args[1]));
									sender.sendMessage(ChatColor.GREEN + "成功删除"+args[1]+"的称号");
								}else{
									sender.sendMessage(ChatColor.RED + "删除"+args[1]+"的称号失败，未存储此玩家称号。");
								}
								return true;
							}else{
								sender.sendMessage("玩家ID参数不正确");
								return true;
							}
						}else{
							sender.sendMessage("用法: /sp rm 玩家ID");
							return true;
						}
					}
				}
			}
			sender.sendMessage(ChatColor.RED+"========缺少参数，可使用以下命令========");
			if(sender.isOp()){
				sender.sendMessage(ChatColor.GREEN + "/sp rl <重载配置>");
				sender.sendMessage(ChatColor.GREEN + "/sp rm 玩家ID <移除玩家ID存储节点>");
				sender.sendMessage(ChatColor.GREEN + "/sp deal 玩家ID 次数 称号 <设置玩家称号次数及初始称号>");
			}
			sender.sendMessage(ChatColor.GREEN + "/sp pre 称号 <预览玩家自己的称号>");
			sender.sendMessage(ChatColor.GREEN + "/sp set 称号 <设置玩家自己的称号>");
			return true;
		}
		return false;
	}

	// 配置载入及重载
	public void LoadConfig(){
		File PluginConfigFile = new File(getDataFolder(), "config.yml");
	        if (!PluginConfigFile.exists()) {
	        	saveDefaultConfig();
	        }
	        if(PluginConfig!=null){
	        	try {
	        		getConfig().load(PluginConfigFile);
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        }else{
	        	PluginConfig = getConfig();
	        }
	        Version = PluginConfig.getInt("Config.Version");
	        if(Version!=1){
	        	PluginConfigFile.renameTo(new File(getDataFolder(),"Version."+Version+".bak.cofig.yml"));
	        	saveDefaultConfig();
	        	getLogger().info("配置文件更新！原配置文件已备份！");
	        }
	        //获取玩家配置区块
	        Players = PluginConfig.getConfigurationSection("Players");
	        PlayerList = Players.getKeys(false).toArray(new String[0]);
	        for(Player player : Bukkit.getOnlinePlayers()){
	        	SetPlayerPrefix(player);
	        }
	}

	// 生成称号
	public String GenDisplayName(PlayerPrefix Prefix){
		return new StringBuffer()
		.append(ChatColor.translateAlternateColorCodes('&',Prefix.Prefix))
		.append(" ")
		.append(Prefix.Name)
		.append(ChatColor.RESET)
		.toString();
	}

	// 获取配置文件中的称号
	public PlayerPrefix GetPrefix(Player P){
		for(String PFind:PlayerList){
			if(PFind.toLowerCase().equals(P.getName().toLowerCase())){
				PlayerPrefix GotPrefix = new PlayerPrefix(Players.getString(PFind+".Prefix"),
						Players.getInt(PFind+".Count"),
						Players.getBoolean(PFind+".Enable"),
						PFind);
				return GotPrefix;
			}
		}
		return null;
	}
	
	public void SetPlayerPrefix(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if(player!= null && player.isOnline() && PMB.PlayerMap.containsKey(uuid.toString()))
		{
			PlayerInfo playerInfo = PMB.PlayerMap.get(uuid.toString());
			if(playerInfo.GetInteger("Merit") < 1000)
				return;
			String playerName = player.getName();
			Team prefixTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
			if(prefixTeam == null){
				prefixTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getName());
			}
			prefixTeam.setPrefix(ChatColor.GOLD.toString());
			prefixTeam.setColor(ChatColor.GOLD);
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			player.setPlayerListName(ChatColor.GOLD + playerName + ChatColor.RESET);
			player.setDisplayName(ChatColor.GOLD + playerName + ChatColor.RESET);
		}
	}

	//通过名称设置称号
	public void SetPlayerPrefix(String Name){
		if(Bukkit.getPlayer(Name).isOnline()){
	
			//SetPlayerPrefix(Bukkit.getPlayer(Name));
		}
	}

	// 设置称号
	public void SetPlayerPrefix(Player player){
		PlayerPrefix Prefix;
		Prefix = GetPrefix(player);
		if(Prefix!=null && player.hasPermission("SelfPrefix.Show")){
			if(Prefix.Enable){
				Team DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
				if(DisplayName == null){
					DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getName());
				}
				DisplayName.setPrefix(ChatColor.translateAlternateColorCodes('&',Prefix.Prefix)+" ");
				DisplayName.addEntry(player.getName());
				//DisplayName.addPlayer(player.getPlayer());
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
				player.setPlayerListName(GenDisplayName(Prefix));
				player.setDisplayName(GenDisplayName(Prefix));
			}
		}else{
			RemovePlayerPrefix(player);
		}
	}

	// 删除称号
	public void RemovePlayerPrefix(Player player){
		Team DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
		if(DisplayName != null){
			DisplayName.unregister();
			player.setDisplayName(null);
			player.setPlayerListName(null);
		}
	}
	
	// 预览称号
	@SuppressWarnings("deprecation")
	public void PrePlayerPrefix(Player player,PlayerPrefix Prefix){
		Team DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
		if(DisplayName == null){
			DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getName());
		}
		DisplayName.setPrefix(ChatColor.translateAlternateColorCodes('&',Prefix.Prefix)+" ");
		DisplayName.addPlayer(player);
		player.setDisplayName(GenDisplayName(Prefix));
		player.setPlayerListName(GenDisplayName(Prefix));
	}

	// 保存称号
	public boolean SavePlayerPrefix(PlayerPrefix Prefix){
		for(String PFind:PlayerList){
			if(PFind.toLowerCase().equals(Prefix.Name.toLowerCase())){
				Players.set(PFind+".Count", Prefix.Count);
				Players.set(PFind+".Prefix", Prefix.Prefix);
				Players.set(PFind+".Enable", Prefix.Enable);
				try {
					PluginConfig.save(new File(getDataFolder(), "config.yml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		Players.createSection(Prefix.Name);
		Players.set(Prefix.Name+".Count", Prefix.Count);
		Players.set(Prefix.Name+".Prefix", Prefix.Prefix);
		Players.set(Prefix.Name+".Enable", Prefix.Enable);
		try {
			PluginConfig.save(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	// 称号类
	public class PlayerPrefix{
		public PlayerPrefix(String P, int C, boolean E ,String N){
			Prefix = P;
			Count = C;
			Enable = E;
			Name = N;
		}
		String Prefix;
		int Count;
		boolean Enable;
		String Name;
	}
}
