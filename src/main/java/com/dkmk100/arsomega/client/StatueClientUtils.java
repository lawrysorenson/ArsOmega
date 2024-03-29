package com.dkmk100.arsomega.client;

import com.dkmk100.arsomega.util.StatueUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class StatueClientUtils {
    public static Entity CreateClientPlayer(StatueUtils.StatuePlayerInfo info, Level level) {
        return StatueClientPlayer.create(info.profile, (ClientLevel) level);
    }
}
