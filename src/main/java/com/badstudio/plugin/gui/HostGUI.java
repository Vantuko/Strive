package com.badstudio.plugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.badstudio.plugin.Main;

import java.util.Objects;

public class HostGUI implements Listener {

    private final Main plugin;

    public HostGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openMainGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "Menú Purga");

        // Botón para configurar jugadores vivos (lana roja)
        ItemStack configItem = new ItemStack(Material.RED_WOOL);
        ItemMeta configMeta = configItem.getItemMeta();
        assert configMeta != null;
        configMeta.setDisplayName(ChatColor.RED + "Configurar jugadores vivos");
        configItem.setItemMeta(configMeta);
        inv.setItem(2, configItem);

        // Botón para iniciar la purga (lana verde)
        ItemStack iniciarItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta iniciarMeta = iniciarItem.getItemMeta();
        assert iniciarMeta != null;
        iniciarMeta.setDisplayName(ChatColor.GREEN + "Iniciar");
        iniciarItem.setItemMeta(iniciarMeta);
        inv.setItem(6, iniciarItem);

        player.openInventory(inv);
    }

    // Abre el inventario de opciones para configurar la cantidad de jugadores vivos (64, 80, 100)
    public void openOptionsGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "Elige jugadores vivos");

        // Opción 1: 64 jugadores
        ItemStack option64 = new ItemStack(Material.RED_WOOL);
        ItemMeta meta64 = option64.getItemMeta();
        assert meta64 != null;
        meta64.setDisplayName(ChatColor.GREEN + "1 jugador");
        option64.setItemMeta(meta64);
        inv.setItem(2, option64);

        // Opción 2: 80 jugadores
        ItemStack option80 = new ItemStack(Material.ORANGE_WOOL);
        ItemMeta meta80 = option80.getItemMeta();
        assert meta80 != null;
        meta80.setDisplayName(ChatColor.GREEN + "80 jugadores");
        option80.setItemMeta(meta80);
        inv.setItem(4, option80);

        // Opción 3: 100 jugadores
        ItemStack option100 = new ItemStack(Material.GREEN_WOOL);
        ItemMeta meta100 = option100.getItemMeta();
        assert meta100 != null;
        meta100.setDisplayName(ChatColor.GREEN + "100 jugadores");
        option100.setItemMeta(meta100);
        inv.setItem(6, option100);

        player.openInventory(inv);
    }

    // Maneja los clics en los inventarios creados
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        // Menú principal ("Menú Purga")
        if (title.equals(ChatColor.DARK_RED + "Menú Purga")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            String displayName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();

            if (displayName.contains("Configurar")) {
                openOptionsGUI(player);
            } else if (displayName.contains("Iniciar")) {

                plugin.getPurgeUtil().startPurge(player);
                player.closeInventory();
            }
        }
        // Menú de opciones ("Elige jugadores vivos")
        else if (title.equals(ChatColor.DARK_RED + "Elige jugadores vivos")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            String displayName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
            int jugadoresVivos = 0;
            if (displayName.contains("1")) {
                jugadoresVivos = 1;
            } else if (displayName.contains("80")) {
                jugadoresVivos = 80;
            } else if (displayName.contains("100")) {
                jugadoresVivos = 100;
            }
            if (jugadoresVivos > 0) {
                plugin.getConfig().set("purga.jugadores_maximos", jugadoresVivos);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Número de jugadores vivos configurado a: " + jugadoresVivos);
                player.closeInventory();
            }
        }
    }
}
