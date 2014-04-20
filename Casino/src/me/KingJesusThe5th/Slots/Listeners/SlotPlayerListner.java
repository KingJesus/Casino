package me.KingJesusThe5th.Slots.Listeners;



import java.util.HashMap;
import java.util.Random;
import me.KingJesusThe5th.Main.CasinoMain;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.Lever;

public class SlotPlayerListner implements Listener{
	private CasinoMain plugin;
	public SlotPlayerListner(CasinoMain plugin) {
		this.plugin = plugin;
	}
	public void spawnItemFrame(Player p,Block b, BlockFace face, ItemStack item, Rotation rotation){
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
	public ItemFrame getItemFrame(Location l){
		for(Entity e:l.getChunk().getEntities()){
			if(e.getLocation().getBlock().equals(l.getBlock())){
				if(e.getType().equals(EntityType.ITEM_FRAME)){
					return (ItemFrame) e;
				}
			}
		}
		return null;
	}
	public BlockFace getCounterClockWiseBlocK(BlockFace bf){
		HashMap<BlockFace,BlockFace> CounterClockWise = new HashMap<BlockFace,BlockFace>();
		CounterClockWise.put(BlockFace.NORTH, BlockFace.WEST);
		CounterClockWise.put(BlockFace.WEST, BlockFace.SOUTH);
		CounterClockWise.put(BlockFace.SOUTH, BlockFace.EAST);
		CounterClockWise.put(BlockFace.EAST, BlockFace.NORTH);
		if(!bf.equals(BlockFace.DOWN)&&!bf.equals(BlockFace.UP)){
			return CounterClockWise.get(bf);
		}else{
			return null;
		}
	}
	public ItemStack getBookFromSlotMachineItemFrame(BlockFace attachedleverbf, Location leverlocation){
		return getItemFrame(leverlocation.getBlock().getRelative(attachedleverbf, 2).getRelative(getCounterClockWiseBlocK(attachedleverbf), 2).getLocation()).getItem();
	}
	public Material[] getMaterialsFromBook(ItemStack book){
		BookMeta bm = (BookMeta) book.getItemMeta(); 
		int StringCounter=0;
		for(int x=1;bm.getPageCount()>=x;x++){
			String[] lines = bm.getPage(x).split("\n");
			for(String l:lines){
				if(l.startsWith("Material-")){
					StringCounter++;
				}
			}
		}
		Material[] q= new Material[StringCounter];
		for(int x=1;bm.getPageCount()>=x;x++){
			String[] lines = bm.getPage(x).split("\n");
			for(String l:lines){
				
				if(l.startsWith("Material-")){
					for (int i = 0; i <= q.length; i++) {
						if(q[i]==null){
							q[i]=Material.getMaterial(l.replace("Material-", ""));
							break;
						}
							
						}
					}
				}
			}
		
		return q;
	}
    public Double[] getDoubleFromBook(ItemStack book, String stringtogetfrom){
    	BookMeta bm = (BookMeta) book.getItemMeta(); 
		int StringCounter=0;
		for(int x=1;bm.getPageCount()>=x;x++){
			String[] lines = bm.getPage(x).split("\n");
			for(String l:lines){
				if(l.startsWith(stringtogetfrom)){
					StringCounter++;
				}
			}
		}
		Double[] q= new Double[StringCounter];
		for(int x=1;bm.getPageCount()>=x;x++){
			String[] lines = bm.getPage(x).split("\n");
			for(String l:lines){
				
				if(l.startsWith(stringtogetfrom)){
					for (int i = 0; i < q.length; i++) {
						if(q[i]==null){
							q[i]= Double.valueOf(l.replace(stringtogetfrom, ""));
							break;
						}
							
						}
					}
				}
			}
		return q;
    }

	@EventHandler
	public void OnSignPlace(SignChangeEvent e){
			if(e.getLine(1).equalsIgnoreCase("SlotMachine")||e.getLine(1).equalsIgnoreCase(ChatColor.DARK_GREEN+"SlotMachine")){
				if(e.getPlayer().hasPermission("Casino.SlotMachine.Place")){
					//Checks if a player placed down a slotmachine sign and has permission
				e.setLine(1, ChatColor.DARK_GREEN+"SlotMachine");
				org.bukkit.material.Sign s = (org.bukkit.material.Sign) e.getBlock().getState().getData();
				Location lever = e.getBlock().getRelative(getCounterClockWiseBlocK(s.getAttachedFace().getOppositeFace()),4).getLocation();
						e.setLine(2, "Bet: "+getDoubleFromBook(getBookFromSlotMachineItemFrame(s.getAttachedFace(), lever), "Bet-")[0]);
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
        		
        	final BlockState state = e.getClickedBlock().getState();
            final Lever l = (Lever) state.getData();
            if(!l.getAttachedFace().equals(BlockFace.UP)&&!l.getAttachedFace().equals(BlockFace.DOWN)){
            if(e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(getCounterClockWiseBlocK(l.getAttachedFace()), 4).getRelative(l.getAttachedFace().getOppositeFace()).getState() instanceof Sign){
            	final Sign s = (Sign) e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(getCounterClockWiseBlocK(l.getAttachedFace()), 4).getRelative(l.getAttachedFace().getOppositeFace()).getState();
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
			final Material[] Mat = getMaterialsFromBook(getBookFromSlotMachineItemFrame(l.getAttachedFace(), e.getClickedBlock().getLocation()));
    		final HashMap<Integer,ItemStack> Temp = new HashMap<Integer,ItemStack>();
    		//Just used it making sure that the last item isn't that same as the first 2
    		final HashMap<ItemStack,ItemStack> ItemSwither = new HashMap<ItemStack,ItemStack>();
    		ItemSwither.put(new ItemStack(Mat[0]), new ItemStack(Mat[Mat.length-1]));
    		for(int y = 1; y < Mat.length; y++) {
    			ItemSwither.put(new ItemStack(Mat[y]), new ItemStack(Mat[y-1]));
    		}
    		//Used to get the "reel" effect, Just switch the items in the frame with the next one
        	for(int x =0;x<=45;x++){
        	 for(int y =1;y<=3;y++){
        		 final int Fy=y;
        		 final int Fx=x;
        		 //Finals because I have no idea how to code
        		 if(Fy==1||Fy==2&&x<=30||Fy==3&&x<=15){
        		 final Block b =e.getClickedBlock().getRelative(l.getAttachedFace()).getRelative(getCounterClockWiseBlocK(l.getAttachedFace()), Fy).getRelative(l.getAttachedFace().getOppositeFace());
        		 		if(!getItemFrame(b.getLocation()).equals(null)){
							  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								  public void run() {
								  ItemFrame i= getItemFrame(b.getLocation());
								  if(ItemSwither.containsKey(i.getItem())){
									  i.setItem(ItemSwither.get(i.getItem()));
								  }else{
									  i.setItem(new ItemStack(Mat[0]));
								  }
								  }}, (x*2));
							//Randomizer(for aesthetic appeal), Just changes the items for the reel effect
							  
							  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								  public void run() {
									  ItemFrame i = getItemFrame(b.getLocation());
									  double WinChance[]=plugin.getRate(Mat.length);
									  double PercentCounter=0;
									  boolean Win = false;
									  int ArrayCounter=0;
									  for (int c = 0; c <= WinChance.length-1; c++) {
										  ArrayCounter++;
										   PercentCounter=PercentCounter+WinChance[c];
										  if(PercentCounter>=Ran){
											  //Checks if the player won, Sets the winning item
											  	i.setItem(new ItemStack(Mat[c]));
										    	Win=true;
										    	break;
										    }
										  if(c+1==WinChance.length){
											//makes sure that the losing items arn't the same
										
											  if(Fy==3||Fy==2){
													i.setItem(ItemSwither.get(i.getItem()));
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
										   if(Win){
											   //if they didn't lose
										   double WinAmount = 0;
										   WinAmount = Double.parseDouble(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1"))*(getDoubleFromBook(getBookFromSlotMachineItemFrame(l.getAttachedFace(), e.getClickedBlock().getLocation()), "Payout-")[ArrayCounter-1]);
											   EconomyResponse r = CasinoMain.econ.depositPlayer(e.getPlayer().getName(), WinAmount);
											   //Adds money to there account based on the sign
											   e.getPlayer().sendMessage(String.format(""+ChatColor.DARK_GREEN+"Congratulations!"+ChatColor.WHITE+" You won "+ChatColor.YELLOW+"%s!", CasinoMain.econ.format(r.amount)));								
							 			//if(plugin.getConfig().getConfigurationSection("Item"+WinNumber).getBoolean("Jackpot")){
							 					plugin.getServer().broadcastMessage(e.getPlayer().getDisplayName()+ChatColor.WHITE+" Just won "+ChatColor.DARK_GREEN+"$"+r.amount+ChatColor.WHITE+" at The Casino!");
											//	}
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
    		if(e.getClickedBlock().getRelative(getCounterClockWiseBlocK(S.getAttachedFace().getOppositeFace()), 4).getType().equals(Material.LEVER)){
    		    Block leverblock =e.getClickedBlock().getRelative(getCounterClockWiseBlocK(S.getAttachedFace().getOppositeFace()), 4);
        		Lever l=(Lever) leverblock.getState().getData();
        		if(!l.isPowered()){
        			Double[] a = getDoubleFromBook(getBookFromSlotMachineItemFrame(l.getAttachedFace(), leverblock.getLocation()), "Bet-");
        			for(int x = 0; x < a.length; x++){
        				if(a[x].equals(Double.parseDouble(s.getLine(2).replaceFirst(".*?(\\d+).*", "$1")))){	
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
        
        //End of event
	}
}
