package su.hubs.hubslobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.hubs.hubscore.HubsCommand;
import su.hubs.hubslobby.HubsLobby;

import java.util.List;

import static su.hubs.hubscore.util.MessageUtils.sendWrongUsageMessage;

public class TipCommand extends HubsCommand {

    public TipCommand() {
        super("tip", null, true, 0);
    }

    @Override
    public boolean onHubsCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            HubsLobby.getTipManager().showRightTip((Player) commandSender);
            return true;
        }
        try {
            if (!HubsLobby.getTipManager().showCustomTip( (Player) commandSender, Integer.parseInt(strings[0]) )) {
                sendWrongUsageMessage(commandSender, "/tip [номер]");
            }
        } catch (NumberFormatException e) {
            sendWrongUsageMessage(commandSender, "/tip [номер]");
        }
        return true;
    }

    @Override
    public List<String> onHubsComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

}
