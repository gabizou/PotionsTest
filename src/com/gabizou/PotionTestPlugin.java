package com.gabizou;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionTestPlugin extends JavaPlugin implements Listener {

  private boolean cancellAll = false;
    
    @Override
    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length>0&&sender instanceof Player){
            ((Player)sender).addPotionEffect(PotionEffectType.HEAL.createEffect(20, 1));
            return true;
        }
        cancellAll = !cancellAll;
        sender.sendMessage("Cancell All = "+cancellAll);
        return true;
    }

    @EventHandler
    public void onPotion(EntityPotionEffectChangeEvent event){
        Entity entity = event.getEntity();
        getServer().broadcastMessage("Entity = "+entity.getClass().toString());
        getServer().broadcastMessage("Effect = " + event.getEffect());
        getServer().broadcastMessage("Location = " + event.getLocation());
        getServer().broadcastMessage("Gain = " + event.isGainingEffect());
        getServer().broadcastMessage("Ambient = " + event.isAmbient());
        getServer().broadcastMessage("Cause = " + event.getCause().name());
        if(cancellAll){
            getServer().broadcastMessage("CANCELLED");
            event.setCancelled(true);
        }
        getServer().broadcastMessage(ChatColor.GOLD+"====================================");
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBeacon(EntityPotionEffectChangeEvent event) {
        if (event.getCause() == EntityPotionEffectChangeEvent.Cause.BEACON) {
            event.setCancelled(true);
            // Test for what happens when we add potion effects after cancelling the event
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) event.getEntity();
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000, 1));
            }
        }
    }
    
    /*
     * This is added simply to make sure the events WILL FIRE semi-recursively
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEffect(EntityPotionEffectChangeEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            // Here we will either crash the server or make the server skip a 
            // few ticks. This is BAD PROGRAMMING on the plugin developer's side
            LivingEntity le = (LivingEntity) event.getEntity();
            event.setCancelled(true);
            le.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,1000,1));
            
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent event){
        Entity e =event.getRightClicked();
        if(e instanceof LivingEntity){
            getServer().broadcastMessage("CLICKED "+e.getClass());
            LivingEntity l = (LivingEntity)e;
            for(PotionEffect effect : l.getActivePotionEffects()){
                getServer().broadcastMessage(effect.toString());
            }
        }
    }

}