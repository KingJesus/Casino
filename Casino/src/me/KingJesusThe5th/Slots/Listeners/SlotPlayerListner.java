package me.KingJesusThe5th.Slots.Listeners;



import java.util.HashMap;
import java.util.Random;

import me.KingJesusThe5th.Main.CasinoMain;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

public class SlotPlayerListner implements Listener{
	private CasinoMain plugin;
	public SlotPlayerListner(CasinoMain plugin) {
		this.plugin = plugin;
	}
	public void SpawnItemFrame(Player p,Block b, BlockFace face, ItemStack item, Rotation rotation){
	ItemFrame i = (ItemFrame) p.getWorld().spawn(b.getLocation(), ItemFrame.class);
	if(face != BlockFace.EAST){
		i.teleport(b.getRelative(face).getLocation());
		i.setFacingDirection(face,true);
		i.setItem(item);
	if(rotation!=null){
		i.setRotation(rotation);
			}
		}
	}


	@EventHandler
	public void OnInteract(final PlayerInteractEvent e){
        if(e.hasBlock()){
         if(e.getClickedBlock().getType().equals(Material.LEVER)){
        	 if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
         		final HashMap<BlockFace,BlockFace> CounterClockWise = new HashMap<BlockFace,BlockFace>();
        		CounterClockWise.put(BlockFace.NORTH, BlockFace.WEST);
        		CounterClockWise.put(BlockFace.WEST, BlockFace.SOUTH);
        		CounterClockWise.put(BlockFace.SOUTH, BlockFace.EAST);
        		CounterClockWise.put(BlockFace.EAST, BlockFace.NORTH);        	
        	final BlockState state = e.getClickedBlock().getState();
            final Lever l = (Lever) state.getData();
            if(!l.getAttachedFace().equals(BlockFace.UP)&&!l.getAttachedFace().equals(BlockFace.DOWN)){
            if(e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(CounterClockWise.get(l.getAttachedFace()), 4).getRelative(l.getAttachedFace().getOppositeFace()).getState() instanceof Sign){
            	final Sign s = (Sign) e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(CounterClockWise.get(l.getAttachedFace()), 4).getRelative(l.getAttachedFace().getOppositeFace()).getState();
            	if(s.getLine(1).equals(ChatColor.DARK_GREEN+"SlotMachine")&&s.getLine(2).contains("Bet:")){
            	if(CasinoMain.econ.getBalance(e.getPlayer().getName())>=Integer.parseInt(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1"))){
            		CasinoMain.econ.withdrawPlayer(e.getPlayer().getName(), Double.parseDouble(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1")));
            e.setCancelled(true);
            if(!l.isPowered()){
            l.setPowered(true);
            state.setData(l);
            state.update();
        	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				  public void run() {
				     l.setPowered(false);
				     state.setData(l);
			         state.update();
				  }}, 94);
        	//Lever check/change
			  final Random r = new Random();
				 final double Ran =r.nextDouble()+r.nextInt(100);
			//Randomizer -start
			int NumberofReels = plugin.getConfig().getInt("NumberofSlots");
			//To be changeable soon
    		final HashMap<Integer,ItemStack> Temp = new HashMap<Integer,ItemStack>();
    		final HashMap<ItemStack,ItemStack> ItemSwither = new HashMap<ItemStack,ItemStack>();
    		ItemSwither.put(new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item1").getString("ItemType"))), new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+NumberofReels).getString("ItemType"))));
    		for(int y = 2; y <= NumberofReels; y++) {
    			ItemSwither.put(new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+y).getString("ItemType"))), new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+(y-1)).getString("ItemType"))));
    		}
        	for(int x =0;x<=45;x++){
        	 for(int y =1;y<=3;y++){
        		 final int Fy=y;
        		 final int Fx=x;
        		 if(Fy==1||Fy==2&&x<=30||Fy==3&&x<=15){
        		 final Block b =e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(CounterClockWise.get(l.getAttachedFace()), Fy).getRelative(l.getAttachedFace().getOppositeFace());
				 for(final Entity ent :b.getChunk().getEntities()){
					  if(ent.getLocation().getBlock().getLocation().equals(b.getLocation())){
						  if(ent.getType().equals(EntityType.ITEM_FRAME)){
							  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								  public void run() {
								  ItemFrame i=(ItemFrame) ent;
								  if(ItemSwither.containsKey(i.getItem())){
									  i.setItem(ItemSwither.get(i.getItem()));
								  }
								  }}, (x*2));
							//Randomizer(for aesthetic appeal) -end
							//Calculates if the player won and sets the itemframes to the winning item
							  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								  public void run() {
									  ItemFrame i =(ItemFrame) ent;
									  double WinChance[]=plugin.getRate();
									  double PercentCounter=0;
									  int WinNumber = 0;
									  for (int c = 1; c < WinChance.length+1; c++) {
										  PercentCounter=PercentCounter+WinChance[c-1];  
										  if(PercentCounter>=Ran){
										    	i.setItem(new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+c).getString("ItemType"))));
										    	WinNumber=c;
										    	break;
										    }
										  if(c==WinChance.length){
											//makes sure that the losing items arn't the same
												if(Fy==3||Fy==2){
													i.setItem(ItemSwither.get(ItemSwither.get(i.getItem())));
													Temp.put(Fy, i.getItem());
												}else{
													if(Temp.get(3).getType()==Temp.get(2).getType()){
														i.setItem(ItemSwither.get(Temp.get(2)));
													}
												}
										  }
									  }
									//Calculate how much to Reward the player and do it
									   if(Fy==1&&Fx==45){
										   if(WinNumber!=0){
										   int WinAmount = 0;
												WinAmount = Integer.parseInt(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1"))*(plugin.getConfig().getConfigurationSection("Item"+WinNumber).getInt("ItemPayout"));
											   EconomyResponse r = CasinoMain.econ.depositPlayer(e.getPlayer().getName(), WinAmount);
											   e.getPlayer().sendMessage(String.format(""+ChatColor.DARK_GREEN+"Congratulations!"+ChatColor.WHITE+" You won "+ChatColor.YELLOW+"%s!", CasinoMain.econ.format(r.amount)));								
							 			if(plugin.getConfig().getConfigurationSection("Item"+WinNumber).getBoolean("Jackpot")){
							 					plugin.getServer().broadcastMessage(e.getPlayer().getDisplayName()+ChatColor.WHITE+" Just won "+ChatColor.DARK_GREEN+"$"+r.amount+ChatColor.WHITE+" at The Casino!");
												}
										   }else{
											   e.getPlayer().sendMessage(""+ChatColor.BLUE+"Better Luck next time");
										   }
									   }
								  }}, (-30*Fy)+122);
							  
						  }
					  	  }
				 		  }
        		 		  }
        		 	      }
        				  }
        }
        }else{
        	e.setCancelled(true);
        	e.getPlayer().sendMessage("You don't have "+ChatColor.DARK_GREEN+"$"+Integer.parseInt(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1")));
        }
        }
        }
        }
        }
        }
        //End of Slot pull
        //Check if they clicked the right sign and there's a lever
        if(e.getClickedBlock().getState() instanceof Sign){
        	Sign s=(Sign) e.getClickedBlock().getState();
        	if(s.getLine(1).equals(ChatColor.DARK_GREEN+"SlotMachine")&&s.getLine(2).contains("Bet:")){
        org.bukkit.material.Sign S= (org.bukkit.material.Sign) e.getClickedBlock().getState().getData();
        if(S.isWallSign()){
     		final HashMap<BlockFace,BlockFace> CounterClockWise = new HashMap<BlockFace,BlockFace>();
    		CounterClockWise.put(BlockFace.NORTH, BlockFace.WEST);
    		CounterClockWise.put(BlockFace.WEST, BlockFace.SOUTH);
    		CounterClockWise.put(BlockFace.SOUTH, BlockFace.EAST);
    		CounterClockWise.put(BlockFace.EAST, BlockFace.NORTH);        	
        	if(e.getClickedBlock().getRelative(CounterClockWise.get(S.getAttachedFace().getOppositeFace()), 4).getType().equals(Material.LEVER)){
        		Lever l=(Lever) e.getClickedBlock().getRelative(CounterClockWise.get(S.getAttachedFace().getOppositeFace()), 4).getState().getData();
        		if(!l.isPowered()){
        			//Gets the bet amounts and changes them
        			Integer[] a = new Integer[plugin.getConfig().getConfigurationSection("BetAmounts").getInt("NumberofBets")];
        			for(int x=0; x<a.length; x++){
        				a[x]=plugin.getConfig().getConfigurationSection("BetAmounts").getInt("Bet"+(x+1));
        			}
        			for(int x = 0; x < a.length; x++){
        				if(a[x].equals(Integer.parseInt(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1")))){
        					{
        						if(x+1==a.length){
        							s.setLine(2, "Bet: "+a[0]);
        							s.update();
        							break;
        						}else{
        							s.setLine(2, "Bet: "+a[x+1]);
        							s.update();
        							break;
        						}
        					}
        				}
        			}
        			//else do nothing
        		}
        	}
        }
        }
        }
        }
        //End of BetChange
	}
}
