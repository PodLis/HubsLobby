package su.hubs.hubslobby

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.HubsPermission

enum class LobbyPermission(private val perm: String) : HubsPermission {

    ;

    override fun senderHasPerm(sender: CommandSender): Boolean {
        return (sender as? Player)?.hasPermission(perm) ?: true
    }

    override fun getPerm(): String {
        return perm
    }

}
