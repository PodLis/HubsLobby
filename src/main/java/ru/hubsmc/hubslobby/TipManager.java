package ru.hubsmc.hubslobby;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.loop.title.HubsTitle;
import ru.hubsmc.hubscore.module.values.PlayerData;
import ru.hubsmc.hubscore.util.StringUtils;

public class TipManager {

    private PlayerData lobbyPlayerData;
    private HubsTitle[] tips;
    private byte cycle;

    TipManager(ConfigurationSection section) {
        lobbyPlayerData = new PlayerData("hubs_lobby", "tip");
        lobbyPlayerData.prepareToWork(new String[0], new int[] {0}, new double[0]);
        tips = new HubsTitle[section.getInt("settings.max") + 1];
        String format = section.getString("settings.title");
        int fadeIn = section.getInt("settings.fade_in");
        int stay = section.getInt("settings.stay");
        int fadeOut = section.getInt("settings.fade_out");
        for (int i = 0; i < tips.length; i++) {
            tips[i] = new HubsTitle(
                    StringUtils.setPlaceholdersPrefixes(format, "number", String.valueOf(i)),
                    StringUtils.setPlaceholdersPrefixes(section.getString("subs." + i)),
                    fadeIn,
                    stay,
                    fadeOut,
                    0
            );
        }
    }

    public void showRightTip(Player player) {
        String UUID = player.getUniqueId().toString();
        if (!lobbyPlayerData.selectExist(UUID)) {
            lobbyPlayerData.createAccount(UUID);
        }
        int tipNumber = lobbyPlayerData.selectIntValue(UUID, "tip");
        if (tipNumber >= tips.length) {
            tipNumber = 0;
        }
        tips[tipNumber].send(player);
        lobbyPlayerData.saveValue(UUID, "tip", tipNumber + 1);
    }

    public boolean showCustomTip(Player player, int tipNumber) {
        if (tipNumber >= 0 && tipNumber < tips.length) {
            tips[tipNumber].send(player);
            lobbyPlayerData.saveValue(player.getUniqueId().toString(), "tip", tipNumber + 1);
            return true;
        }
        return false;
    }

    public void makeCycle() {
        lobbyPlayerData.saveValue("cycle", "tip", cycle);
        cycle++;
    }

}
