package me.KingJesusThe5th.Main;

import java.util.ArrayList;
import java.util.List;

import me.KingJesusThe5th.Slots.Listeners.SlotPlayerListner;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Commands implements CommandExecutor{
private CasinoMain plugin;

public Commands(CasinoMain plugin){
this.plugin = plugin;
	}

public void WriteinBook(ItemStack book, String string){
	BookMeta meta = (BookMeta) book.getItemMeta();
	meta.addPage(string);
	book.setItemMeta(meta);
}
public boolean isInt(String s) {
   try { 
       Integer.parseInt(s); 
   } catch(NumberFormatException e) { 
       return false; 
   }
   return true;
}
public boolean isDouble(String s) {
	   try { 
	       Double.parseDouble(s); 
	   } catch(NumberFormatException e) { 
	       return false; 
	   }
	   return true;
	}
SlotPlayerListner solPlayerListner = new SlotPlayerListner(plugin);
public static ItemStack SlotMachineCPU = new ItemStack(Material.BOOK_AND_QUILL);
BookMeta SlotMachineMeta  = (BookMeta) SlotMachineCPU.getItemMeta();
List<String> SlotMachineLore = new ArrayList<String>();{
	SlotMachineLore.add("Place this in an item frame to create a slot machine");
	SlotMachineMeta.setLore(SlotMachineLore);
	SlotMachineMeta.setDisplayName("SlotMachine CPU");
	SlotMachineMeta.addPage("");
	SlotMachineCPU.setItemMeta(SlotMachineMeta);
}

@SuppressWarnings("deprecation")
@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
			if(commandLabel.equalsIgnoreCase("Casino")){
				Player p = (Player) sender;
				if(args.length>=1){
				if(args[0].equalsIgnoreCase("SetCpu")){
				if(p.getItemInHand().getType().equals(Material.BOOK_AND_QUILL)){
					if(args.length>=4&&isInt(args[1])&&isDouble(args[2])&&isDouble(args[3])){
					WriteinBook(p.getItemInHand(), "Material-"+Material.getMaterial(Integer.parseInt(args[1]))+"\nPayout-"+args[2]+"\nRate-"+args[3]);
					}else{
						p.sendMessage("usage:/Casino ItemId Payout Rateamount\nex:/Casino 266 5 1");
					}
				}else{
					p.sendMessage("You must be holding a book to do that");
				}
				}else if(args[0].equalsIgnoreCase("MakeCpu")){
					if(args.length>=3&&args.length<=12&&p.getInventory().firstEmpty()!=-1){
						BookMeta bm = (BookMeta) SlotMachineCPU.getItemMeta();
						StringBuilder sb = new StringBuilder(); 
						for (int i = 2 ; i < args.length; i++){
							if(!isDouble(args[i])){ 
								args[i]="0"; 
							}
						        sb.append("\nBet-");
						        sb.append(args[i].toString());
						  
						}
						bm.setPage(1, "WinChance-"+args[1]+sb.toString());
						SlotMachineCPU.setItemMeta(bm);
						p.getInventory().addItem(SlotMachineCPU);
						p.sendMessage("Here's your CPU");
					}else{
						p.sendMessage("usage:/Casino MakeCPU WinPrecent Bets(Max 12)\nex:/Casino MakeCPU 89 5 10 15 20 50 100");
					}
				}
				}else{
					p.sendMessage("Commands:\n/Casino MakeCPU\n/Casino SetCPU");
				}
			}
			return false;
}
}
