package su.hubs.hubslobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.hubsmc.hubscore.HubsPlugin;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.exception.ConfigurationPartMissingException;
import ru.hubsmc.hubscore.exception.IncorrectConfigurationException;
import ru.hubsmc.hubscore.exception.WorldNotFoundException;
import ru.hubsmc.hubscore.module.chesterton.HubsChesterton;
import ru.hubsmc.hubscore.module.loop.item.InteractItemMeta;
import ru.hubsmc.hubscore.module.loop.title.TitleAnimation;
import ru.hubsmc.hubscore.util.ConfigUtils;
import ru.hubsmc.hubscore.util.PlayerUtils;
import su.hubs.hubslobby.commands.TipCommand;
import su.hubs.hubslobby.listener.LobbyJoinLeaveEvent;
import su.hubs.hubslobby.listener.SuccessLoginEvent;

import java.io.File;
import java.util.List;

import static ru.hubsmc.hubscore.PluginUtils.getItemInteractAction;
import static ru.hubsmc.hubscore.PluginUtils.registerItemInteract;
import static ru.hubsmc.hubscore.util.PlayerUtils.teleport;
import static ru.hubsmc.hubscore.util.StringUtils.replaceColor;

public final class HubsLobby extends HubsPlugin {

    private File lobbyFolder;
    private FileConfiguration configuration;
    private static TipManager tipManager;
    public static Location LOGIN_PLACE;
    public static Location SPAWN_PLACE;
    public static TitleAnimation REGISTER_TITLE;
    public static TitleAnimation LOGIN_TITLE;
    public static ItemStack LOBBY_MENU_ITEM;

    @Override
    public boolean afterCoreStart() {
        loadFiles();

        PluginUtils.setCommandExecutorAndTabCompleter("tip", new TipCommand());
        PluginUtils.registerEventsOfListener(new SuccessLoginEvent());
        PluginUtils.registerEventsOfListener(new LobbyJoinLeaveEvent());

        PluginUtils.logConsole("HubsLobby successfully loaded!");
        return true;
    }

    @Override
    public void beforeCoreStop() {
        PluginUtils.logConsole("HubsLobby successfully disabled!");
    }

    @Override
    public void onPluginEnable() {
    }

    @Override
    public void onPluginDisable() {
    }

    @Override
    public void onPlayerJoin(Player player) {
        teleport(player, SPAWN_PLACE);
        player.getInventory().setItem(0, LOBBY_MENU_ITEM);
        PluginUtils.runTaskLater(() -> tipManager.showRightTip(player), 10);
    }

    @Override
    public void onPlayerQuit(Player player) {
    }

    @Override
    public void onReload() {
        loadFiles();
    }

    @Override
    public void onStringsReload() {
    }

    @Override
    public void onSchedule() {
        tipManager.makeCycle();
    }

    @Override
    public String getStringData(String s) {
        switch (s) {
            case "tablo":
                return configuration.getString("tablo");
            case "can_give_items":
                return "false";
            default:
                return "";
        }
    }

    private void loadFiles() {

        lobbyFolder = new File(PluginUtils.getMainFolder(), "server_lobby");
        configuration = PluginUtils.getConfigInFolder(lobbyFolder, "config");

        try {
            String worldName = ConfigUtils.getStringInSection(configuration, "world");
            World lobbyWorld = Bukkit.getWorld(worldName);
            if (lobbyWorld == null) throw new WorldNotFoundException(worldName);
            LOGIN_PLACE = ConfigUtils.parseLocation(configuration, "login-place", lobbyWorld);
            SPAWN_PLACE = ConfigUtils.parseLocation(configuration, "spawn", lobbyWorld);
        } catch (IncorrectConfigurationException | ConfigurationPartMissingException | WorldNotFoundException e) {
            e.printStackTrace();
        }

        try {
            String materialName = configuration.getString("menu.item");
            String name = configuration.getString("menu.name");
            List<String> lore = configuration.getStringList("menu.lore");
            if (materialName == null || name == null || lore.isEmpty()) {
                throw new ConfigurationPartMissingException("cannot form lobby menu item: material, name or lore not exists in 'config.yml'");
            }
            Material material = Material.getMaterial(materialName.toUpperCase());
            if (material == null) {
                throw new IncorrectConfigurationException("cannot form lobby menu item: material with name '" + materialName + "' does not exist");
            }
            LOBBY_MENU_ITEM = new ItemStack(material);
            ItemMeta itemMeta = LOBBY_MENU_ITEM.getItemMeta();
            itemMeta.setDisplayName(replaceColor(name));
            itemMeta.setLore(replaceColor(lore));
            InteractItemMeta interactItemMeta = new InteractItemMeta(itemMeta, true, true);
            registerItemInteract(interactItemMeta, () -> {
                Player player = getItemInteractAction(interactItemMeta).getPlayer();
                PlayerUtils.openMenuToPlayer(player, HubsChesterton.getNavigationMenu(player));
            });
            LOBBY_MENU_ITEM.setItemMeta(itemMeta);
        } catch (ConfigurationPartMissingException | IncorrectConfigurationException e) {
            e.printStackTrace();
        }

        // titles load
        FileConfiguration titleConfig = PluginUtils.getConfigInFolder(lobbyFolder, "titles");
        try {

            ConfigurationSection regSec = titleConfig.getConfigurationSection("on-register");
            if (regSec == null) {
                throw new ConfigurationPartMissingException("title 'on-register' not found in 'titles.yml'");
            }
            REGISTER_TITLE = ConfigUtils.loadAnimatedTitle(regSec);

            ConfigurationSection logSec = titleConfig.getConfigurationSection("on-login");
            if (logSec == null) {
                throw new ConfigurationPartMissingException("title 'on-login' not found in 'titles.yml'");
            }
            LOGIN_TITLE = ConfigUtils.loadAnimatedTitle(logSec);

            ConfigurationSection tipSec = titleConfig.getConfigurationSection("tips");
            if (tipSec == null) {
                throw new ConfigurationPartMissingException("section 'tips' not found in 'titles.yml'");
            }
            tipManager = new TipManager(tipSec);

        } catch (ConfigurationPartMissingException e) {
            e.printStackTrace();
        }

    }

    public static TipManager getTipManager() {
        return tipManager;
    }

}
