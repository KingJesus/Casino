package me.KingJesusThe5th.Main;

import java.util.logging.Logger;

import me.KingJesusThe5th.Slots.Listeners.SlotPlayerListner;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CasinoMain extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static CasinoMain plugin;
	public final SlotPlayerListner SPlayerListner = new SlotPlayerListner(this);
	public final Commands Cmds = new Commands(this);
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.SPlayerListner, this);
		getCommand("Casino").setExecutor(new Commands(this));
		saveDefaultConfig();
        setupPermissions();
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
	}
	public void onDisable(){
		getServer().getScheduler().cancelTasks(this);
	}
	//--Vault--
	 public static Economy econ = null;
	    private boolean setupEconomy() {
	        if (getServer().getPluginManager().getPlugin("Vault") == null) {
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	            return false;
	        }
	        econ = rsp.getProvider();
	        return econ != null;
	    }
	    public static Permission perms = null;
	    private static final Logger log = Logger.getLogger("Minecraft");
	    private boolean setupPermissions() {
	        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	        perms = rsp.getProvider();
	        return perms != null;
	    }
	    //--Vault--
	    //SlotMachine
	    //Gets the % chance for each item to win and puts it in the array
		public double[] getRate(){
			int NumberofReels = getConfig().getInt("NumberofSlots");
			double[] ItemHitChance=new double[NumberofReels];
			double WinPercent = getConfig().getDouble("WinPercent");
			double Modifyer=0;
			for(int x=1;x<=NumberofReels;x++){
				Modifyer=getConfig().getConfigurationSection("Item"+x).getDouble("Rate")+Modifyer;
			}
			Modifyer=NumberofReels/Modifyer;
			for(int x=1;x<=NumberofReels;x++){
				double ItemPayout =getConfig().getConfigurationSection("Item"+x).getDouble("ItemPayout");
				double ItemRate=getConfig().getConfigurationSection("Item"+x).getDouble("Rate");
				ItemHitChance[x-1]=(1/(ItemPayout*NumberofReels))*Modifyer*ItemRate*WinPercent;
			}

			return ItemHitChance;
		}
}
