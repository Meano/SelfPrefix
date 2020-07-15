package net.meano.SelfPrefix;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SelfPrefixListeners implements Listener{
	SelfPrefixMain SPM;
	public SelfPrefixListeners(SelfPrefixMain GetPlugin){
		SPM = GetPlugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH , ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player JoinPlayer = event.getPlayer();
		SPM.UpdatePlayerPrefix(JoinPlayer.getUniqueId());
	}
}