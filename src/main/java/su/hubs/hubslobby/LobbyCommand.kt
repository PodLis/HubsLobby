package su.hubs.hubslobby

import su.hubs.hubscore.HubsCommand

abstract class LobbyCommand(name: String, permission: LobbyPermission?, mustBePlayer: Boolean, minArgs: Int, vararg aliases: String) : HubsCommand(HubsLobby.instance, name, permission, mustBePlayer, minArgs, *aliases)