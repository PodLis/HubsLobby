package su.hubs.hubslobby

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import su.hubs.hubscore.module.loop.title.HubsTitle
import su.hubs.hubscore.module.values.PlayerData
import su.hubs.hubscore.util.StringUtils

class TipManager internal constructor(section: ConfigurationSection) {

    private val lobbyPlayerData: PlayerData = PlayerData("hubs_lobby", "tip")
    private val tips: Array<HubsTitle?>
    private var cycle: Byte = 0
    fun showRightTip(player: Player) {
        val uuid = player.uniqueId.toString()
        if (!lobbyPlayerData.selectExist(uuid)) {
            lobbyPlayerData.createAccount(uuid)
        }
        var tipNumber = lobbyPlayerData.selectIntValue(uuid, "tip")
        if (tipNumber >= tips.size) {
            tipNumber = 0
        }
        tips[tipNumber]!!.send(player)
        lobbyPlayerData.saveValue(uuid, "tip", tipNumber + 1)
    }

    fun showCustomTip(player: Player, tipNumber: Int): Boolean {
        if (tipNumber >= 0 && tipNumber < tips.size) {
            tips[tipNumber]!!.send(player)
            lobbyPlayerData.saveValue(player.uniqueId.toString(), "tip", tipNumber + 1)
            return true
        }
        return false
    }

    fun makeCycle() {
        lobbyPlayerData.saveValue("cycle", "tip", cycle.toInt())
        cycle++
    }

    init {
        lobbyPlayerData.prepareToWork(arrayOfNulls(0), intArrayOf(0), DoubleArray(0))
        tips = arrayOfNulls(section.getInt("settings.max") + 1)
        val format = section.getString("settings.title")
        val fadeIn = section.getInt("settings.fade_in")
        val stay = section.getInt("settings.stay")
        val fadeOut = section.getInt("settings.fade_out")
        for (i in tips.indices) {
            tips[i] = HubsTitle(
                    StringUtils.setPlaceholdersPrefixes(format, "number", i.toString()),
                    StringUtils.setPlaceholdersPrefixes(section.getString("subs.$i")),
                    fadeIn,
                    stay,
                    fadeOut,
                    0
            )
        }
    }
}
