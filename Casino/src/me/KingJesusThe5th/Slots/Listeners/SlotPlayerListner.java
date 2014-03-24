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
import org.bukkit.event.block.SignChangeEvent;
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
	public void OnSignPlace(SignChangeEvent e){
			if(e.getLine(1).equalsIgnoreCase("SlotMachine")||e.getLine(1).equalsIgnoreCase(ChatColor.DARK_GREEN+"SlotMachine")){
				if(e.getPlayer().hasPermission("Casino.SlotMachine.Place")){
					//Checks if a player placed down a slotmachine sign and has permission
				e.setLine(1, ChatColor.DARK_GREEN+"SlotMachine");
					if(!e.getLine(2).contains("\\d+")){
						//Checks if line 2 contains a number, if not sets it
						e.setLine(2, "Bet: "+plugin.getConfig().getConfigurationSection("BetAmounts").getInt("Bet1"));
					}
				}else{
					e.setLine(1, "SlotMachine");
					//If the player doesn't have permission set it to black
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
        		//Hash map so I can track the blocks next to the lever/sign
        	final BlockState state = e.getClickedBlock().getState();
            final Lever l = (Lever) state.getData();
            if(!l.getAttachedFace().equals(BlockFace.UP)&&!l.getAttachedFace().equals(BlockFace.DOWN)){
            if(e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(CounterClockWise.get(l.getAttachedFace()), 4).getRelative(l.getAttachedFace().getOppositeFace()).getState() instanceof Sign){
            	final Sign s = (Sign) e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(CounterClockWise.get(l.getAttachedFace()), 4).getRelative(l.getAttachedFace().getOppositeFace()).getState();
            e.setCancelled(true);
            if(!l.isPowered()){
            	if(s.getLine(1).equals(ChatColor.DARK_GREEN+"SlotMachine")&&s.getLine(2).contains("Bet:")){
            	if(CasinoMain.econ.getBalance(e.getPlayer().getName())>=Integer.parseInt(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1"))){
            		CasinoMain.econ.withdrawPlayer(e.getPlayer().getName(), Double.parseDouble(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1")));
            		//Makes sure there's a slot machine sign, Also Checks/takes money from the player based on the sign
            l.setPowered(true);
            state.setData(l);
            state.update();
        	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				  public void run() {
				     l.setPowered(false);
				     state.setData(l);
			         state.update();
				  }}, 94);
        	//Changes the lever back up after the event is done
			  final Random r = new Random();
				 final double Ran =r.nextDouble()+r.nextInt(100);
			//Random number(Between 0-100, Used to check if/what the player wins
			int NumberofReels = plugin.getConfig().getInt("NumberofSlots");
    		final HashMap<Integer,ItemStack> Temp = new HashMap<Integer,ItemStack>();
    		//Just used it making sure that the last item isn't that same as the first 2
    		final HashMap<ItemStack,ItemStack> ItemSwither = new HashMap<ItemStack,ItemStack>();
    		ItemSwither.put(new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item1").getString("ItemType"))), new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+NumberofReels).getString("ItemType"))));
    		for(int y = 2; y <= NumberofReels; y++) {
    			ItemSwither.put(new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+y).getString("ItemType"))), new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+(y-1)).getString("ItemType"))));
    		}
    		//Used to get the "reel" effect, Just switch the items in the frame with the next one
        	for(int x =0;x<=45;x++){
        	 for(int y =1;y<=3;y++){
        		 final int Fy=y;
        		 final int Fx=x;
        		 //Finals because I have no idea how to code
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
								  }else{
									  i.setItem(new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item1").getString("ItemType"))));
								  }
								  }}, (x*2));
							//Randomizer(for aesthetic appeal), Just changes the items for the reel effect
							  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								  public void run() {
									  ItemFrame i =(ItemFrame) ent;
									  double WinChance[]=plugin.getRate();
									  double PercentCounter=0;
									  int WinNumber = 0;
									  for (int c = 1; c < WinChance.length+1; c++) {
										  PercentCounter=PercentCounter+WinChance[c-1];  
										  if(PercentCounter>=Ran){
											  //Checks if the player won, Sets the winning item
										    	i.setItem(new ItemStack(Material.getMaterial(plugin.getConfig().getConfigurationSection("Item"+c).getString("ItemType"))));
										    	WinNumber=c;
										    	break;
										    }
										  if(c==WinChance.length){
											//makes sure that the losing items arn't the same
												if(Fy==3||Fy==2){
													i.setItem(ItemSwither.get(ItemSwither.get(i.getItem())));
													//Just some randomness so it's less predictable
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
										   //(if it's the last bit of code running
										   if(WinNumber!=0){
											   //if they didn't lose
										   int WinAmount = 0;
												WinAmount = Integer.parseInt(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1"))*(plugin.getConfig().getConfigurationSection("Item"+WinNumber).getInt("ItemPayout"));
											   EconomyResponse r = CasinoMain.econ.depositPlayer(e.getPlayer().getName(), WinAmount);
											   //Adds money to there account based on the sign
											   e.getPlayer().sendMessage(String.format(""+ChatColor.DARK_GREEN+"Congratulations!"+ChatColor.WHITE+" You won "+ChatColor.YELLOW+"%s!", CasinoMain.econ.format(r.amount)));								
							 			if(plugin.getConfig().getConfigurationSection("Item"+WinNumber).getBoolean("Jackpot")){
							 					plugin.getServer().broadcastMessage(e.getPlayer().getDisplayName()+ChatColor.WHITE+" Just won "+ChatColor.DARK_GREEN+"$"+r.amount+ChatColor.WHITE+" at The Casino!");
												}
										   }else{
											   e.getPlayer().sendMessage(""+ChatColor.BLUE+"Better Luck next time");
										   }
									   }
								  }}, (-30*Fy)+122);
							  //Fires the events at 92 47 32
						  }
					  	  }
				 		  }
        		 		  }
        		 	      }
        				  }
        }else{
        	e.setCancelled(true);
        	e.getPlayer().sendMessage("You don't have "+ChatColor.DARK_GREEN+"$"+Integer.parseInt(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1")));
        	//If they player doesn't have the money to play send them this message
        }
        }
        }
        }
        }
        }
        }
        //End checking if the player used a lever
        //Check if they clicked a sign (Changes the bets)
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
        	//Hashmap used for checking where the lever is
    		if(e.getClickedBlock().getRelative(CounterClockWise.get(S.getAttachedFace().getOppositeFace()), 4).getType().equals(Material.LEVER)){
        		Lever l=(Lever) e.getClickedBlock().getRelative(CounterClockWise.get(S.getAttachedFace().getOppositeFace()), 4).getState().getData();
        		if(!l.isPowered()){
        			Integer[] a = new Integer[plugin.getConfig().getConfigurationSection("BetAmounts").getInt("NumberofBets")];
        			for(int x=0; x<a.length; x++){
        				a[x]=plugin.getConfig().getConfigurationSection("BetAmounts").getInt("Bet"+(x+1));
        			}
        			//Puts all the bet amounts in an array
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
        						//gets the next amount in the array
        					}
        				}
        			}
        		}
        	}
        }
        }
        }
        }
        //End of event
	}
}
