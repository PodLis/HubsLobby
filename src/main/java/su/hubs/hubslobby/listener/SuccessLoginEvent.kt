package su.hubs.hubslobby.listener

import fr.xephi.authme.events.LoginEvent
import fr.xephi.authme.events.LogoutEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import su.hubs.hubscore.listener.JoinEvent

class SuccessLoginEvent : Listener {
    @EventHandler
    fun onLogin(event: LoginEvent) {
        JoinEvent.onJoinOrLogin(event.player)
    }

    @EventHandler
    fun onLogout(event: LogoutEvent) {
        event.player.kickPlayer("LOGOUT IS NOT ALLOWED HERE!")
    }
}
