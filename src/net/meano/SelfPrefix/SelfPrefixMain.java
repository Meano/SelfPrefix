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
		//Log��ʼ��¼
		getLogger().info("SelfPrefix 0.1,by Meano. ��������.");
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
						sender.sendMessage("��������ɹ�");
						return true;
					}
				}else if(args[0].toLowerCase().equals("pre")){
					if(sender instanceof Player){
						if(args.length<2){
							sender.sendMessage(ChatColor.GREEN+"�÷�: /sp pre &C[�ƺ�]&A ����Ԥ����ĳƺţ������ĸ����ƺŴ�������ֻ��20���ʱ��");
							return true;
						}else if(args.length==2){
							PlayerPrefix WillChange = GetPrefix((Player) sender);
							if(WillChange!=null){
								if(WillChange.Count>0)
									if(CheckAllow(args[1])){
										if(args[1].replaceAll("[\\&\\[\\]]","").length()<10){
											sender.sendMessage(ChatColor.GREEN+"ǰ׺����ʹ�ã�����20���ʱ����Ԥ����ĳƺ�");
											WillChange.Prefix = args[1];
											PrePlayerPrefix((Player) sender, WillChange);
											Bukkit.getScheduler().scheduleSyncDelayedTask(this, 
													new Runnable(){
														public void run(){
															SetPlayerPrefix((Player) sender);
															sender.sendMessage(ChatColor.GREEN+"�ƺ�Ԥ���Ѿ������������������ʹ������/sp set &C[�ƺ�]&A ��������ĳƺš�");
														}
											}, 20*20L);
											return true;
										}else{
											sender.sendMessage(ChatColor.RED+"��Ǹ���ƺ�̫����!");
											return true;
										}
									}else{
										sender.sendMessage(ChatColor.RED+"��Ǹ���ƺ����зǷ��ַ�!");
										return true;
									}
								else{
									sender.sendMessage(ChatColor.RED+"��ĳƺ�ʹ�ô����Ѿ��þ����޷�Ԥ���ƺ�");
									return true;
								}
							}else{
								sender.sendMessage(ChatColor.RED+"��Ǹ��ֻ�о�����ҿ���ʹ��Ԥ���ƺŹ��ܣ�");
								return true;
							}
						}
					}else
						return true;
				}else if(args[0].toLowerCase().equals("set")){
					if(sender instanceof Player){
						if(args.length<2){
							sender.sendMessage(ChatColor.GREEN+"�÷�: /sp set &C[�ƺ�]&A ����������ĳƺţ����ĸ����ƺŴ���������ʹ�á�");
							return true;
						}else if(args.length==2){
							PlayerPrefix WillChange = GetPrefix((Player) sender);
							if(WillChange!=null){
								if(WillChange.Count>0)
									if(CheckAllow(args[1])){
										if(args[1].replaceAll("[\\&\\[\\]]","").length()<10){
											sender.sendMessage(ChatColor.GREEN+"ǰ׺����ʹ�ã����ڸ��ĳƺš�����");
											WillChange.Prefix = args[1];
											if(WillChange.Enable){
												WillChange.Count = WillChange.Count-1;
												SavePlayerPrefix(WillChange);
												SetPlayerPrefix((Player) sender);
												sender.sendMessage(ChatColor.GREEN+"��ĳƺ�"+GenDisplayName(WillChange)+"�����ɹ���ʣ������ƺŴ���"+WillChange.Count);
												return true;
											}else{
												sender.sendMessage(ChatColor.RED+"��Ǹ���������޷����ĳƺţ�����ϵ����Ա!");
												return true;
											}
										}else{
											sender.sendMessage(ChatColor.RED+"��Ǹ���ƺ�̫����!");
											return true;
										}
									}else{
										sender.sendMessage(ChatColor.RED+"��Ǹ���ƺ����зǷ��ַ�!");
										return true;
									}
								else{
									sender.sendMessage(ChatColor.RED+"��ĳƺ�ʹ�ô����Ѿ��þ����޷�Ԥ���ƺ�");
									return true;
								}
							}else{
								sender.sendMessage(ChatColor.RED+"��Ǹ��ֻ�о�����ҿ���ʹ��Ԥ���ƺŹ��ܣ�");
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
								sender.sendMessage("�ɹ�����"+args[1]+"�ƺ�");
								return true;
							}else{
								sender.sendMessage("��������ȷ");
								return true;
							}
						}else{
							sender.sendMessage("�÷�: /sp deal ��� ���� �ƺ�");
							return true;
						}
					}else{
						return true;
					}
				}
			}else{
				sender.sendMessage(ChatColor.RED+"ȱ�ٲ���,����ʹ��/sp pre����/sp set��");
				return true;
			}
		}
		return false;
	}
	//�������뼰����
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
	        	getLogger().info("�����ļ����£�ԭ�����ļ��ѱ��ݣ�");
	        }
	        //��ȡ�����������
	        Players = PluginConfig.getConfigurationSection("Players");
	        PlayerList = Players.getKeys(false).toArray(new String[0]);
	        for(Player player : Bukkit.getOnlinePlayers()){
	        	SetPlayerPrefix(player);
	        }
	}
	//���ɳƺ�
	public String GenDisplayName(PlayerPrefix Prefix){
		return new StringBuffer()
		.append(ChatColor.translateAlternateColorCodes('&',Prefix.Prefix))
		.append(" ")
		.append(Prefix.Name)
		.append(ChatColor.RESET)
		.toString();
	}
	//���ȡ�����ļ��еĳƺ�
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
	//ͨ���������óƺ�
	public void SetPlayerPrefix(String Name){
		if(Bukkit.getPlayer(Name).isOnline()){
			SetPlayerPrefix(Bukkit.getPlayer(Name));
		}
	}
	//���óƺ�
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
	//ɾ���ƺ�
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
	//Ԥ���ƺ�
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
	//����ƺ�
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
	//�ƺ���
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
