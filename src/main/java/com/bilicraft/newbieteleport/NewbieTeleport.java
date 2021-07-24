package com.bilicraft.newbieteleport;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class NewbieTeleport extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void respawnSafe(PlayerRespawnEvent event){
        if(!event.getRespawnLocation().clone().add(0,-1,0).getBlock().getType().isSolid()){
            event.getRespawnLocation().clone().add(0,-1,0).getBlock().setType(Material.GLASS);
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void teleportNewbie(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this,()->{
            if (!event.getPlayer().hasPlayedBefore()) {
                if (getConfig().getLocation("location") != null) {
                    event.getPlayer().teleport(getConfig().getLocation("location"));
                    getLogger().info("Teleporting to " + getConfig().getLocation("location"));
                } else {
                    getLogger().warning("Teleport location not set!");
                }
            }
        },1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("newbieteleport.admin") && sender instanceof Player) {
            getConfig().set("location", ((Player) sender).getLocation());
            saveConfig();
            sender.sendMessage("Set!");
        }
        return true;
    }
}
