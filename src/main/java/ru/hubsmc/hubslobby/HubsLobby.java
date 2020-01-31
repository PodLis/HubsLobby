package ru.hubsmc.hubslobby;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hubsmc.hubscore.HubsPlugin;
import ru.hubsmc.hubscore.PluginUtils;

public final class HubsLobby extends HubsPlugin {

    @Override
    public void afterCoreStart() {
        PluginUtils.logConsole("Hello, i HubsLobby!");
    }

    @Override
    public void beforeCoreStop() {
        PluginUtils.logConsole("Buy, i HubsLobby!");
    }

    @Override
    public void onPluginEnable() {
        PluginUtils.logConsole("Hello, i Lobby!");
    }

    @Override
    public void onPluginDisable() {
        PluginUtils.logConsole("Buy, i Lobby!");
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.getInventory().setItem(8, new ItemStack(Material.DIAMOND));
    }

    @Override
    public void onPlayerQuit(Player player) {
    }

    @Override
    public void onSchedule() {
    }

}
