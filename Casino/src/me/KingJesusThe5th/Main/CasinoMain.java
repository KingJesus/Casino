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
	    public double[] getModifyer(Double[] Payouts){
	    	double [] Modifyer =new double[Payouts.length];
	    for(int x=0;x<Modifyer.length;x++){
	    	Modifyer[x]=(1/(Math.sqrt(Payouts[x])));
	    }
	    	return Modifyer;
	    }
		public double[] getRate(Double[] Payouts, double WinPercent){
			double[] ItemHitChance=new double[Payouts.length];
			double counter=0;
			for(double M: getModifyer(Payouts)){
				counter=M+counter;
			}
			counter=Payouts.length/counter;
			for(int x=0;x<Payouts.length;x++){
				double ItemPayout=Payouts[x];
				double ItemRate=getModifyer(Payouts)[x];
				ItemHitChance[x]=(1/(ItemPayout*Payouts.length))*counter*ItemRate*WinPercent;
			}

			return ItemHitChance;
		}
}
