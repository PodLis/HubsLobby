package su.hubs.hubslobby

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import su.hubs.hubscore.HubsPermission
import su.hubs.hubscore.HubsPlugin
import su.hubs.hubscore.PluginUtils
import su.hubs.hubscore.exception.ConfigurationPartMissingException
import su.hubs.hubscore.exception.IncorrectConfigurationException
import su.hubs.hubscore.exception.WorldNotFoundException
import su.hubs.hubscore.module.chesterton.HubsChesterton
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem
import su.hubs.hubscore.module.chesterton.internal.menu.ChestMenu
import su.hubs.hubscore.module.loop.item.InteractItemMeta
import su.hubs.hubscore.module.loop.title.TitleAnimation
import su.hubs.hubscore.util.ConfigUtils
import su.hubs.hubscore.util.PlayerUtils
import su.hubs.hubscore.util.StringUtils
import su.hubs.hubslobby.commands.TipCommand
import su.hubs.hubslobby.listener.LobbyJoinLeaveEvent
import su.hubs.hubslobby.listener.SuccessLoginEvent
import java.io.File

class HubsLobby : HubsPlugin() {

    private var lobbyFolder: File? = null
    private var configuration: FileConfiguration? = null

    override fun afterCoreStart(): Boolean {
        instance = this
        loadFiles()
        PluginUtils.setCommandExecutorAndTabCompleter("tip", TipCommand())
        PluginUtils.registerEventsOfListener(SuccessLoginEvent())
        PluginUtils.registerEventsOfListener(LobbyJoinLeaveEvent())
        PluginUtils.logConsole("HubsLobby successfully loaded!")
        return true
    }

    override fun beforeCoreStop() {
        PluginUtils.logConsole("HubsLobby successfully disabled!")
    }

    override fun onPluginEnable() {}

    override fun onPluginDisable() {}

    override fun onPlayerJoin(player: Player?) {
        PlayerUtils.teleport(player, SPAWN_PLACE)
        player!!.inventory.setItem(0, LOBBY_MENU_ITEM)
        PluginUtils.runTaskLater({ tipManager.showRightTip(player) }, 10)
    }

    override fun onPlayerQuit(player: Player?) {}

    override fun onReload() {
        loadFiles()
    }

    override fun onStringsReload() {}

    override fun onSchedule() {
        tipManager.makeCycle()
    }

    override fun getStringData(key: String?): String? {
        return when (key) {
            "tablo" -> configuration!!.getString("tablo")
            "can_give_items" -> "false"
            else -> ""
        }
    }

    override fun getServerPermissions(): Array<HubsPermission>? {
        val perms = mutableListOf<HubsPermission>()
        for (perm in LobbyPermission.values())
            perms.add(perm)
        return perms.toTypedArray()
    }

    override fun getServerActions(): Map<String, (Player, ChestertonItem, ChestMenu, String) -> Unit>? {
        return null
    }

    private fun loadFiles() {
        lobbyFolder = File(PluginUtils.getMainFolder(), "server_lobby")
        val configuration = PluginUtils.getConfigInFolder(lobbyFolder, "config")
        try {
            val worldName = ConfigUtils.getStringInSection(configuration, "world")
            val lobbyWorld = Bukkit.getWorld(worldName) ?: throw WorldNotFoundException(worldName)
            LOGIN_PLACE = ConfigUtils.parseLocation(configuration, "login-place", lobbyWorld)
            SPAWN_PLACE = ConfigUtils.parseLocation(configuration, "spawn", lobbyWorld)
        } catch (e: IncorrectConfigurationException) {
            e.printStackTrace()
        } catch (e: ConfigurationPartMissingException) {
            e.printStackTrace()
        } catch (e: WorldNotFoundException) {
            e.printStackTrace()
        }
        try {
            val materialName = configuration.getString("menu.item")
            val name = configuration.getString("menu.name")
            val lore = configuration.getStringList("menu.lore")
            if (materialName == null || name == null || lore.isEmpty()) {
                throw ConfigurationPartMissingException("cannot form lobby menu item: material, name or lore not exists in 'config.yml'")
            }
            val material = Material.getMaterial(materialName.toUpperCase())
                    ?: throw IncorrectConfigurationException("cannot form lobby menu item: material with name '$materialName' does not exist")
            LOBBY_MENU_ITEM = ItemStack(material)
            val itemMeta = LOBBY_MENU_ITEM.itemMeta
            itemMeta!!.setDisplayName(StringUtils.replaceColor(name))
            itemMeta.lore = StringUtils.replaceColor(lore)
            val interactItemMeta = InteractItemMeta(itemMeta, true, true)
            PluginUtils.registerItemInteract(interactItemMeta) {
                val player = PluginUtils.getItemInteractAction(interactItemMeta).player
                PlayerUtils.openMenuToPlayer(player, HubsChesterton.getNavigationMenu(player))
            }
            LOBBY_MENU_ITEM.itemMeta = itemMeta
        } catch (e: ConfigurationPartMissingException) {
            e.printStackTrace()
        } catch (e: IncorrectConfigurationException) {
            e.printStackTrace()
        }

        // titles load
        val titleConfig = PluginUtils.getConfigInFolder(lobbyFolder, "titles")
        try {
            val regSec = titleConfig.getConfigurationSection("on-register")
                    ?: throw ConfigurationPartMissingException("title 'on-register' not found in 'titles.yml'")
            REGISTER_TITLE = ConfigUtils.loadAnimatedTitle(regSec)
            val logSec = titleConfig.getConfigurationSection("on-login")
                    ?: throw ConfigurationPartMissingException("title 'on-login' not found in 'titles.yml'")
            LOGIN_TITLE = ConfigUtils.loadAnimatedTitle(logSec)
            val tipSec = titleConfig.getConfigurationSection("tips")
                    ?: throw ConfigurationPartMissingException("section 'tips' not found in 'titles.yml'")
            tipManager = TipManager(tipSec)
        } catch (e: ConfigurationPartMissingException) {
            e.printStackTrace()
        }
        this.configuration = configuration
    }

    companion object {
        lateinit var instance: HubsLobby
        lateinit var tipManager: TipManager
            private set
        lateinit var LOGIN_PLACE: Location
        lateinit var SPAWN_PLACE: Location
        lateinit var REGISTER_TITLE: TitleAnimation
        lateinit var LOGIN_TITLE: TitleAnimation
        lateinit var LOBBY_MENU_ITEM: ItemStack
    }
}
