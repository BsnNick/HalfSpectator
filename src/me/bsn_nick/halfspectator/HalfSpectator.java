package me.bsn_nick.halfspectator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author bsn_nick (000Nick)
 */
public class HalfSpectator extends JavaPlugin implements Listener
{
	private String name = this.getDescription().getName();
	private String version = this.getDescription().getVersion();
	private String author = this.getDescription().getAuthors().get(0);
	
	public void onEnable()
	{
		System.out.println("Enabling " + name + " " + version + " by " + author + "...");
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		System.out.println(name + " " + version + " by " + author + " has been enabled!");
	}
	
	public void onDisable()
	{
		System.out.println(name + " " + version + " by " + author + " has been disabled!");
	}
    
	private void sendPacket(Player player)
	{
		try
		{
	        Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + "PlayerConnection").getMethod("sendPacket", Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + "Packet")).invoke(player.getClass().getMethod("getHandle").invoke(player).getClass().getField("playerConnection").get(player.getClass().getMethod("getHandle").invoke(player)), Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + "PacketPlayOutGameStateChange").getConstructor(int.class, float.class).newInstance(3, 3));
	        
	        player.sendMessage(ChatColor.GOLD + "HalfSpectator> " + ChatColor.WHITE + "You are now half spectator! Reconnect to deactivate.");
		}
		catch (Exception e)
		{
			System.out.println("[HalfSpectator] There was a problem sending the packet. See \"" + this.getDataFolder().getAbsolutePath() + "\" for more information.");
		}
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().equalsIgnoreCase("/halfspec"))
		{
			if (event.getPlayer().hasPermission("halfspec.cmd"))
			{
                event.setCancelled(true);
				
				sendPacket(event.getPlayer());
			}
		}
		
		if (event.getMessage().equalsIgnoreCase("/halfspecall"))
		{
			if (event.getPlayer().hasPermission("halfspec.cmdall"))
			{
				event.setCancelled(true);
				
				for (Player players : Bukkit.getOnlinePlayers())
				{
					sendPacket(players);
				}
				
				event.getPlayer().sendMessage(ChatColor.GOLD + "HalfSpectator> " + ChatColor.WHITE + "Everyone is now half spectator!");
			}
		}
	}
}