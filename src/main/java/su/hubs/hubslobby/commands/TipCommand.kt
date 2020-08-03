package su.hubs.hubslobby.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.util.MessageUtils
import su.hubs.hubslobby.HubsLobby
import su.hubs.hubslobby.LobbyCommand

class TipCommand : LobbyCommand("tip", null, true, 0) {

    override fun onHubsCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (strings.isEmpty()) {
            HubsLobby.tipManager.showRightTip(commandSender as Player)
            return true
        }
        try {
            if (!HubsLobby.tipManager.showCustomTip(commandSender as Player, strings[0].toInt())) {
                MessageUtils.sendWrongUsageMessage(commandSender, "/tip [номер]")
            }
        } catch (e: NumberFormatException) {
            MessageUtils.sendWrongUsageMessage(commandSender, "/tip [номер]")
        }
        return true
    }

    override fun onHubsComplete(commandSender: CommandSender, command: Command, s: String, strings: Array<String>) = null
}
