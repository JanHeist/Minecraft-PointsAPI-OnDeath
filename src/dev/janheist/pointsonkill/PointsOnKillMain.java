package dev.janheist.pointsonkill;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import me.BukkitPVP.PointsAPI.PointsAPI;

public class PointsOnKillMain extends JavaPlugin implements Listener, CommandExecutor {

    public boolean active = true;
    public int points = 50;
    private final String PREFIX = "§7[§aPointsOnKill§7] ";
    private FileConfiguration config = this.getConfig();


    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        config.addDefault("active", true);
        config.addDefault("points", 50);
        config.options().copyDefaults(true);
        saveConfig();

        active = config.getBoolean("active");
        points = config.getInt("points");

        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("pok").setExecutor(this);

        System.out.println("|                      |");
        System.out.println("| PointsOnKill enabled |");
        System.out.println("|                      |");

    }

    @Override
    public void onDisable() {
        config.set("active", active);
        config.set("points", points);
        saveConfig();

        System.out.println("|                       |");
        System.out.println("| PointsOnKill disabled |");
        System.out.println("|                       |");
    }

    @Override
    public boolean onCommand(CommandSender p, Command cmd, String label, String[] args) {
        if(args.length >= 1 && p.isOp()) {
            String msg = args[0].toLowerCase();
            if(msg.startsWith("enable")) {
                active = true;
                p.sendMessage(PREFIX + "§aSpieler bekommen nun beim Tod §e" + points + "§a Punkte.");
            } else if(msg.startsWith("disable")) {
                active = false;
                p.sendMessage(PREFIX + "§cSpieler bekommen nun beim Tod keine Punkte.");
            } else if(msg.startsWith("points") && args.length >= 2) {
                try {
                    points = Integer.parseInt(args[1]);
                    p.sendMessage(PREFIX + "§aPunkte bei Tod auf §e" + points + " §agesetzt.");
                } catch (NumberFormatException e) {
                    p.sendMessage(PREFIX + "§cFEHLER: Zahl konnte nicht erkannt werden.");
                }
            } else if(msg.startsWith("info")) {
                p.sendMessage(PREFIX + "§7Plugin by Mexykaner");
                p.sendMessage("§7Aktiv: " + (active ? "§aJa" : "§cNein"));
                p.sendMessage("§7Aktuelle Punktzahl: §e" + points);
                p.sendMessage(PREFIX);
            } else {
                p.sendMessage(PREFIX + "§cRichtige Verwendung: /pok [§eenable, disable, points 50, info§c]");
            }


        } else {
            if(p.isOp() || !(p instanceof Player)) {
                p.sendMessage(PREFIX + "§cRichtige Verwendung: /pok [§eenable, disable, points 50, info§c]");
            } else {
                p.sendMessage(PREFIX + "§cKeine Rechte");
            }
        }
        return true;
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e)
    {
        if(active)
            addAnnouncePoints(e.getEntity(), points);
    }

    private void addAnnouncePoints(Player p, int amount) {
        PointsAPI.addPoints(p, points);
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.isOp()){
                player.sendMessage("§e" + p.getName() + " §7 ist gestorben und hat §e" + points + " §7Punkte erhalten. Neuer Punktestand: " +
                        PointsAPI.getPoints(p));
            }
        }
    }

}
