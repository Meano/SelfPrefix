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
	//��ҵ�½�¼�
	@EventHandler(priority = EventPriority.HIGH , ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event){
		//SPM.getLogger().info("�ƺ��¼�");
		Player JoinPlayer = event.getPlayer();
		if(JoinPlayer.hasPermission("SelfPrefix.Show")){
			SPM.SetPlayerPrefix(JoinPlayer);
		}else{
			SPM.RemovePlayerPrefix(JoinPlayer);
		}
	}
}