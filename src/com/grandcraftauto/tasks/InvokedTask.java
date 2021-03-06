package com.grandcraftauto.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import com.grandcraftauto.core.Main;
import com.grandcraftauto.game.InvokedGroup;
import com.grandcraftauto.game.VillagerType;
import com.grandcraftauto.game.missions.objectives.EscapeCopsObjective;
import com.grandcraftauto.game.player.GPlayer;
import com.grandcraftauto.utils.ObjectType;
import com.grandcraftauto.utils.TextUtils;
import com.grandcraftauto.utils.Utils;

public class InvokedTask extends BukkitRunnable{
	
	Main main = Main.getInstance();
	
	private String gray = ChatColor.GRAY + "";
	private String gold = ChatColor.GOLD + "";
	
	private Player player;
	private GPlayer gplayer;
	private VillagerType type;
	private String gangName;
	double wantedLevel = 1;
	public InvokedTask(Player p, InvokedGroup group){
		player = p;
		gplayer = new GPlayer(p);
		type = group.getVillagerType();
		gangName = group.getGangName();
	}
	
	private int seconds = 0;
	private boolean prevLost = false;
	
	public void run(){
		if(seconds < this.getTimerMax()){
			if(type == VillagerType.COP){
				/*
				if(seconds > 1){
					gplayer.setBarHealth((float) (100F - Utils.getPercent(seconds, this.getTimerMax())));
				}else{
					gplayer.setBarHealth(100F);
				}
				*/
				if(seconds == 4){
					gplayer.sendMessage("You are now losing the cops!");
					prevLost = true;
				}else if(seconds == 0){
					if(prevLost == true){
						gplayer.sendMessage("The cops have spotted you!");
						prevLost = false;
					}
				}
			}
			seconds++;
			for(Entity e : player.getNearbyEntities(16, 16, 16)){
				if(e instanceof Villager){
					Villager v = (Villager) e;
					if(VillagerType.fromProfession(v.getProfession()) == type){
						if(v.getCustomName() != null && gangName != null && (!gangName.contains(v.getCustomName()) && !v.getCustomName().contains(gangName))){
							return;
						}
						seconds = 0;
						if(v.getLocation().distance(player.getLocation()) > 8){
							if(this.getRandomInt() != 1){
								Utils.setNavigation(player.getLocation().add(Utils.attemptSetAsNegative(Utils.randInt(0, 2)), 0, Utils.attemptSetAsNegative(Utils.randInt(0, 2))), v);
							}
						}
						int shootState = 15;
						if(Utils.getMetadata(v, "shootState", ObjectType.INT) != null){
							shootState = (int) Utils.getMetadata(v, "shootState", ObjectType.INT);
						}
						if(shootState >= 15){
							if(this.getRandomInt() != 1){
								Utils.shootBullet(v, player);
							}
							Utils.setMetadata(v, "shootState", 0);
						}else{
							Utils.setMetadata(v, "shootState", (shootState + 1));
						}
					}
				}
			}
		}else{
			this.cancel();
			this.clearTask(true);
		}
	}
	
	/**
	 * Clear the task
	 */
	public void clearTask(boolean sendMessage){
		if(sendMessage == true){
			if(type == VillagerType.COP){
				gplayer.sendMessage("You have lost the cops!");
				gplayer.removeBar();
				gplayer.addXP(3);
				if(gplayer.hasMission() == true){
					if(gplayer.getObjective() instanceof EscapeCopsObjective){
						EscapeCopsObjective obj = (EscapeCopsObjective) gplayer.getObjective();
						int timesEscaped = obj.getTimesEscaped(player.getName()) + 1;
						if(timesEscaped >= obj.getTimesToEscape()){
							gplayer.advanceObjective();
						}else{
							obj.setTimesEscaped(player.getName(), timesEscaped);
						}
					}
				}
			}
		}
		if(main.invoked.containsKey(player.getName())){
			main.invoked.remove(player.getName());
		}
	}
	
	/**
	 * Set the timer
	 * @param t - The value to set the timer as
	 */
	public void setTimer(int t){
		seconds = t;
	}
	
	/**
	 * Get the max the timer can be
	 * @return The max the timer can be
	 */
	public int getTimerMax(){
		int max = (15 * 10);
		if(type == VillagerType.COP){
			max = (int) (max * this.getWantedLevel());
		}
		return max;
	}
	
	/**
	 * Get the villager type that the player has invoked
	 * @return The villager type that the player has invoked
	 */
	public VillagerType getVillagerType(){
		return type;
	}
	
	/**
	 * Get the player's wanted level
	 * @return The player's wanted level
	 */
	public double getWantedLevel(){
		return wantedLevel;
	}
	
	/**
	 * Set the player's wanted level
	 * @param level - The value to set the player's wanted level as
	 */
	public void setWantedLevel(double level){
		wantedLevel = Utils.round((float) level, 1);
		if(wantedLevel == 1){
			player.sendMessage(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(1));
			//gplayer.sendBar(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(1), 100F);
		}else if(wantedLevel == 2){
			player.sendMessage(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(2));
			//gplayer.sendBar(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(2), 100F);
		}else if(wantedLevel == 3){
			player.sendMessage(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(3));
			//gplayer.sendBar(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(3), 100F);
		}else if(wantedLevel == 4){
			player.sendMessage(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(4));
			//gplayer.sendBar(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(4), 100F);
		}else if(wantedLevel == 5){
			player.sendMessage(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(5));
			//gplayer.sendBar(gold + ChatColor.ITALIC + "Wanted Level " + ChatColor.RESET + gold + TextUtils.getArrow() + gray + " " + this.getStars(5), 100F);
		}
	}
	
	/**
	 * Get how much wanted level to add
	 * @return The amount of wanted level to add
	 */
	public double getWantedLevelToAdd(){
		return 0.1;
	}
	
	/**
	 * Convert the wanted level into a string of stars
	 * @param level - The wanted level
	 * @return The string of stars
	 */
	public String getStars(int level){
		String stars = "";
		for(int x = 1; x <= level; x++){
			stars = stars + TextUtils.getStar();
		}
		return stars;
	}
	
	/**
	 * Get a random integer based on the wanted level
	 * @return A random integer based on the wanted level
	 */
	public int getRandomInt(){
		int random = 0;
		if(wantedLevel >= 1 && wantedLevel < 2){
			random = Utils.randInt(1, 12);
		}else if(wantedLevel >= 2 && wantedLevel < 3){
			random = Utils.randInt(1, 16);
		}else if(wantedLevel >= 3 && wantedLevel < 4){
			random = Utils.randInt(1, 20);
		}else if(wantedLevel >= 4 && wantedLevel < 5){
			random = Utils.randInt(1, 24);
		}else if(wantedLevel >= 5){
			random = Utils.randInt(1, 28);
		}
		return random;
	}
}
