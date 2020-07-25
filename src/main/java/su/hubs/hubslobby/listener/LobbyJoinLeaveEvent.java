package su.hubs.hubslobby.listener;

import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.listener.LeaveEvent;
import su.hubs.hubslobby.HubsLobby;

import static ru.hubsmc.hubscore.util.PlayerUtils.*;

public class LobbyJoinLeaveEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        setGameMode(player, GameMode.ADVENTURE);
        teleport(player, HubsLobby.LOGIN_PLACE);
        clearInventory(player);
        if (AuthMeApi.getInstance().isRegistered(player.getName())) {
            PluginUtils.runTaskLater(() -> HubsLobby.LOGIN_TITLE.play(player, p -> AuthMeApi.getInstance().isAuthenticated(player)), 20);
        } else {
            PluginUtils.runTaskLater(() -> HubsLobby.REGISTER_TITLE.play(player, p -> AuthMeApi.getInstance().isAuthenticated(player)), 20);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (AuthMeApi.getInstance().isAuthenticated(event.getPlayer())) {
            LeaveEvent.onHubsQuit(event.getPlayer());
        }
    }

}
