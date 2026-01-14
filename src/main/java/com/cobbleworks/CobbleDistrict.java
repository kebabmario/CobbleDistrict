package com.cobbleworks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CobbleDistrict extends JavaPlugin implements CommandExecutor {

    private Location districtLocation;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadDistrictLocation();

        // Register both commands to this class
        this.getCommand("setdistrict").setExecutor(this);
        this.getCommand("district").setExecutor(this);

        getLogger().info("CobbleDistrict enabled!");
    }

    @Override
    public void onDisable() {
        saveDistrictLocation();
        getLogger().info("CobbleDistrict disabled!");
    }

    private void loadDistrictLocation() {
        FileConfiguration config = getConfig();
        if (config.contains("district.world")) {
            String worldName = config.getString("district.world");
            double x = config.getDouble("district.x");
            double y = config.getDouble("district.y");
            double z = config.getDouble("district.z");
            float yaw = (float) config.getDouble("district.yaw");
            float pitch = (float) config.getDouble("district.pitch");

            districtLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        }
    }

    private void saveDistrictLocation() {
        if (districtLocation != null) {
            FileConfiguration config = getConfig();
            config.set("district.world", districtLocation.getWorld().getName());
            config.set("district.x", districtLocation.getX());
            config.set("district.y", districtLocation.getY());
            config.set("district.z", districtLocation.getZ());
            config.set("district.yaw", districtLocation.getYaw());
            config.set("district.pitch", districtLocation.getPitch());
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("setdistrict")) {
            if (!player.hasPermission("cobbledistrict.set")) {
                player.sendMessage("§cYou don't have permission to set the district.");
                return true;
            }

            districtLocation = player.getLocation();
            saveDistrictLocation();
            player.sendMessage("§aCobbleDistrict location set to your current position!");
            player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 30, 0.6, 0.6, 0.6, 0.15);
            return true;
        }

        if (cmd.equals("district")) {
            if (!player.hasPermission("cobbledistrict.tp")) {
                player.sendMessage("§cYou don't have permission to teleport to the district.");
                return true;
            }

            if (districtLocation == null) {
                player.sendMessage("§cThe CobbleDistrict has not been set yet. An operator needs to use /setdistrict.");
                return true;
            }

            player.teleport(districtLocation);
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 60, 0.7, 0.7, 0.7, 0.12);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.1f, 0.9f);
            player.sendMessage("§bWhoosh! Teleported to the §6CobbleDistrict§b!");
            return true;
        }

        return false;
    }
}