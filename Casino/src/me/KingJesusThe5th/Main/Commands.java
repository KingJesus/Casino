package me.KingJesusThe5th.Main;

import me.KingJesusThe5th.Slots.Listeners.SlotPlayerListner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor{
private CasinoMain plugin;

public Commands(CasinoMain plugin){
this.plugin = plugin;
	}


SlotPlayerListner solPlayerListner = new SlotPlayerListner(plugin);
@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
			if(commandLabel.equalsIgnoreCase("Casino")){
				Player p = (Player) sender;
				if(p.hasPermission("Cassino.Commands")){
				if(args.length!=0){
					if(args[0].equalsIgnoreCase("SlotMachine")||args[0].equalsIgnoreCase("SlotMachines")||args[0].equalsIgnoreCase("SM")){
					p.sendMessage(""+ChatColor.YELLOW+"Slot Machine:\n"
							+ChatColor.BLUE+"Payout Rate:"+ChatColor.YELLOW+" %"+plugin.getConfig().getDouble("WinPercent"));
					
					for (int c = 1; c < plugin.getRate().length+1; c++){
						p.sendMessage(ChatColor.BLUE+plugin.getConfig().getConfigurationSection("Item"+c).getString("ItemType")+":"+ChatColor.YELLOW+" %"+((double) Math.round(plugin.getRate()[c-1]*1000))/1000+ChatColor.WHITE+" Payout:"+ChatColor.RED+plugin.getConfig().getConfigurationSection("Item"+c).getDouble("ItemPayout"));
					}
					}
				}else{
					p.sendMessage("Commands:\n"
							+ "/Cassino SlotMachines");
				}
			}
		}
			return false;
	}
}