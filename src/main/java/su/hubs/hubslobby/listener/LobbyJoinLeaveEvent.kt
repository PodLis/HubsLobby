package su.hubs.hubslobby.listener

import fr.xephi.authme.api.v3.AuthMeApi
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import su.hubs.hubscore.PluginUtils
import su.hubs.hubscore.listener.LeaveEvent
import su.hubs.hubscore.util.PlayerUtils
import su.hubs.hubslobby.HubsLobby

class LobbyJoinLeaveEvent : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        PlayerUtils.setGameMode(player, GameMode.ADVENTURE)
        PlayerUtils.teleport(player, HubsLobby.LOGIN_PLACE)
        PlayerUtils.clearInventory(player)
        if (AuthMeApi.getInstance().isRegistered(player.name)) {
            PluginUtils.runTaskLater({ HubsLobby.LOGIN_TITLE.play(player) { AuthMeApi.getInstance().isAuthenticated(player) } }, 20)
        } else {
            PluginUtils.runTaskLater({ HubsLobby.REGISTER_TITLE.play(player) { AuthMeApi.getInstance().isAuthenticated(player) } }, 20)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (AuthMeApi.getInstance().isAuthenticated(event.player)) {
            LeaveEvent.onHubsQuit(event.player)
        }
    }
}
