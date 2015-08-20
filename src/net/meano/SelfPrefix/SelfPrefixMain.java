package net.meano.SelfPrefix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public class SelfPrefixMain  extends JavaPlugin{
	public FileConfiguration PluginConfig;
	public ConfigurationSection Players;
	public String[] PlayerList;
	public int Version;
	public void onEnable(){
		//Log开始记录
		getLogger().info("SelfPrefix 0.1,by Meano. 正在载入.");
		LoadConfig();
		PluginManager PM = Bukkit.getServer().getPluginManager();
		PM.registerEvents(new SelfPrefixListeners(this), this);
	}
	public boolean CheckAllow(String prefix){
		if(prefix.equals(prefix.replaceAll("[^a-zA-Z_0-9\\[\\]\\&\u4e00-\u9fa5]", "")))
			return true;
		else
			return false;
	}
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
						if(args.length<2){
							sender.sendMessage(ChatColor.GREEN+"用法: /sp pre &C[称号]&A 即可预览你的称号，不消耗更换称号次数，但只有20秒的时间");
							return true;
						}else if(args.length==2){
							PlayerPrefix WillChange = GetPrefix((Player) sender);
							if(WillChange!=null){
								if(WillChange.Count>0)
									if(CheckAllow(args[1])){
										if(args[1].replaceAll("[\\&\\[\\]]","").length()<10){
											sender.sendMessage(ChatColor.GREEN+"前缀可以使用，你有20秒的时间来预览你的称号");
											WillChange.Prefix = args[1];
											PrePlayerPrefix((Player) sender, WillChange);
											Bukkit.getScheduler().scheduleSyncDelayedTask(this, 
													new Runnable(){
														public void run(){
															SetPlayerPrefix((Player) sender);
															sender.sendMessage(ChatColor.GREEN+"称号预览已经结束，如果觉得满意使用命令/sp set &C[称号]&A 来保存你的称号。");
														}
											}, 20*20L);
											return true;
										}else{
											sender.sendMessage(ChatColor.RED+"抱歉，称号太长了!");
											return true;
										}
									}else{
										sender.sendMessage(ChatColor.RED+"抱歉，称号内有非法字符!");
										return true;
									}
								else{
									sender.sendMessage(ChatColor.RED+"你的称号使用次数已经用尽，无法预览称号");
									return true;
								}
							}else{
								sender.sendMessage(ChatColor.RED+"抱歉，只有捐助玩家可以使用预览称号功能！");
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
										if(args[1].replaceAll("[\\&\\[\\]]","").length()<10){
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
											sender.sendMessage(ChatColor.RED+"抱歉，称号太长了!");
											return true;
										}
									}else{
										sender.sendMessage(ChatColor.RED+"抱歉，称号内有非法字符!");
										return true;
									}
								else{
									sender.sendMessage(ChatColor.RED+"你的称号使用次数已经用尽，无法预览称号");
									return true;
								}
							}else{
								sender.sendMessage(ChatColor.RED+"抱歉，只有捐助玩家可以使用预览称号功能！");
								return true;
							}
						}
					}else
						return true;
				}else if(args[0].toLowerCase().equals("deal")){
					if(sender.isOp()){
						if(args.length==4){
							if(args[1].matches("[a-z_A-Z0-9\u4e00-\u9fa5]*")&&args[2].matches("[0-9]*")&&args[3].matches("[a-zA-Z_0-9\\[\\]\\&\u4e00-\u9fa5]*")){
								PlayerPrefix GotPrefix = new PlayerPrefix(args[3],
										Integer.parseInt(args[2]),
										true,
										args[1]);
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
				}
			}else{
				sender.sendMessage(ChatColor.RED+"缺少参数,可以使用/sp pre或者/sp set。");
				return true;
			}
		}
		return false;
	}
	//配置载入及重载
	public void LoadConfig(){
		File PluginConfigFile = new File(getDataFolder(), "config.yml");
	        if (!PluginConfigFile.exists()) {
	        	saveDefaultConfig();
	        }
	        if(PluginConfig!=null){
	        	try {
				getConfig().load(PluginConfigFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
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
	//生成称号
	public String GenDisplayName(PlayerPrefix Prefix){
		return new StringBuffer()
		.append(ChatColor.translateAlternateColorCodes('&',Prefix.Prefix))
		.append(" ")
		.append(Prefix.Name)
		.append(ChatColor.RESET)
		.toString();
	}
	//获获取配置文件中的称号
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
	//通过名称设置称号
	public void SetPlayerPrefix(String Name){
		if(Bukkit.getPlayer(Name).isOnline()){
			SetPlayerPrefix(Bukkit.getPlayer(Name));
		}
	}
	//设置称号
	@SuppressWarnings("deprecation")
	public void SetPlayerPrefix(Player player){
		PlayerPrefix Prefix;
		Prefix = GetPrefix(player);
		if(Prefix!=null)
			if(Prefix.Enable){
				Team DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
				if(DisplayName == null){
					DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getName());
				}
				DisplayName.setPrefix(ChatColor.translateAlternateColorCodes('&',Prefix.Prefix)+" ");
				DisplayName.addPlayer(player.getPlayer());
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
				player.setPlayerListName(GenDisplayName(Prefix));
				player.setDisplayName(GenDisplayName(Prefix));
			}
	}
	//删除称号
	public void RemovePlayerPrefix(Player player){
		PlayerPrefix Prefix;
		Prefix = GetPrefix(player);
		if(Prefix!=null){
			Team DisplayName = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
			if(DisplayName != null){
				DisplayName.unregister();
			}
			//DisplayName.setPrefix(ChatColor.translateAlternateColorCodes('&',Prefix.Prefix)+" ");
			//DisplayName.addPlayer(player);
			//player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			//player.setDisplayName(GenDisplayName(Prefix));
			//player.setPlayerListName(GenDisplayName(Prefix));
		}
	}
	//预览称号
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
	//保存称号
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
	//称号类
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
