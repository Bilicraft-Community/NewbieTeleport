package com.bilicraft.newbieteleport;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
        getLogger().info("Schedule the map pregen...");
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iris pregen 6000 "+BetterRTP.getInstance().getSettings().rtpOnFirstJoin_World), 1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void respawnSafe(PlayerRespawnEvent event) {
        if (!event.getRespawnLocation().clone().add(0, -1, 0).getBlock().getType().isSolid()) {
            event.getRespawnLocation().clone().add(0, -1, 0).getBlock().setType(Material.GLASS);
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void teleportNewbie(PlayerJoinEvent event) {

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (!event.getPlayer().hasPlayedBefore()) {
                if (getConfig().getLocation("location") != null) {
                    event.getPlayer().teleport(getConfig().getLocation("location"));
                    getLogger().info("Teleporting to " + getConfig().getLocation("location"));
                } else {
                    getLogger().warning("Teleport location not set!");
                }
            } else {
                getLogger().info("Logged in at: " + event.getPlayer().getLocation());
                if (event.getPlayer().getLocation().getWorld().getName().equals("exchange")) {
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "正在重新启动传送，请稍后...");
                    getLogger().info("为玩家 " + event.getPlayer().getName() + " 重新启动传送...");
                    Location bedLocation = event.getPlayer().getBedSpawnLocation();
                    if (bedLocation != null) {
                        getLogger().info("传送：" + event.getPlayer().getName() + " 到 " + bedLocation);
                        event.getPlayer().teleportAsync(bedLocation).thenAccept((a) -> {
                            if (a) {
                                getLogger().info("传送：" + event.getPlayer().getName() + " 到 " + bedLocation + " 成功");
                            } else {
                                getLogger().info("传送：" + event.getPlayer().getName() + " 到 " + bedLocation + " 失败");
                            }
                        });
                    } else {
                        getLogger().info("传送：" + event.getPlayer().getName() + " 到 Random");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "请稍等，我们正在为你寻找合适的位置进行传送，请允许最多60秒...");
                        BetterRTP.getInstance().getCmd().tp(event.getPlayer(), Bukkit.getConsoleSender(), BetterRTP.getInstance().getSettings().rtpOnFirstJoin_World, null, RTP_TYPE.JOIN);
                    }
                }
            }
        }, 1);
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
