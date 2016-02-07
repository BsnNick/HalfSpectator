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
	private double _version = 1.0;
	
	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(this, this);
		
		System.out.println("[HalfSpectator] HalfSpectator v" + _version + " by bsn_nick has been enabled!");
	}
	
	public void onDisable()
	{
		System.out.println("[HalfSpectator] HalfSpectator v" + _version + " by bsn_nick has been disabled!");
	}
	
    private Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException
    {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = "net.minecraft.server." + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        
        return nmsClass;
    }
    
    private Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        return con;
    }
    
	private void sendPacket(Player player)
	{
		try
		{
			Class<?> packetClass = this.getNMSClass("PacketPlayOutGameStateChange");
	        Constructor<?> packetConstructor = packetClass.getConstructor(int.class, float.class);
	        Object packet = packetConstructor.newInstance(3, 3);
	        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", this.getNMSClass("Packet"));
	        sendPacket.invoke(this.getConnection(player), packet);
		}
		catch (Exception e)
		{
			System.out.println("[HalfSpectator] Error: The packet could not be sent! [1]");
		}
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent event)
	{
		if (event.getPlayer().hasPermission("partspec.cmd"))
		{
			if (event.getMessage().equalsIgnoreCase("/partspec"))
			{
				event.setCancelled(true);
				
				sendPacket(event.getPlayer());
				
				event.getPlayer().sendMessage(ChatColor.GOLD + "PartSpec> " + ChatColor.WHITE + "You are now partially spectating! Re-log to deactivate.");
			}
			else if (event.getMessage().equalsIgnoreCase("/partspecall"))
			{
				event.setCancelled(true);
				
				for (Player players : Bukkit.getOnlinePlayers())
				{
					sendPacket(players);
					
					event.getPlayer().sendMessage(ChatColor.GOLD + "PartSpec> " + ChatColor.WHITE + "You are now partially spectating! Re-log to deactivate.");
				}
				
				event.getPlayer().sendMessage(ChatColor.GOLD + "PartSpec> " + ChatColor.WHITE + "Everyone is now partially spectating!");
			}
		}
	}
}