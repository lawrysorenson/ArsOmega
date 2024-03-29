package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.client.StatueClientUtils;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class StatueUtils {

    public static ItemStack CreateStatueItem(Entity entity){
        ItemStack stack = new ItemStack(RegistryHandler.STATUE_ITEM.get());

        CompoundTag blockTag = new CompoundTag();

        CompoundTag entityTag = new CompoundTag();
        //use custom save function to fix issues with player
        saveEntity(entity,entityTag);
        blockTag.put("entity",entityTag);

        String entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        blockTag.putString("entity_backup_id",entityBackupId);

        CompoundTag tag = stack.getOrCreateTag();

        tag.put("BlockEntityTag", blockTag);

        return stack;
    }

    //always save players even though they are marked as don't save
    static String getSaveId(Entity entity){
        if(entity instanceof Player){
            return EntityType.getKey(entity.getType()).toString();
        }
        else{
            return entity.getEncodeId();
        }
    }

    //fix some issues related to saving players
    public static void saveEntity(Entity entity, CompoundTag compound){
        String s = getSaveId(entity);
        if (s == null) {
            return;
        } else {
            compound.putString("id", s);
            entity.saveWithoutId(compound);
        }
    }

    public static Entity CreateClientPlayer(StatuePlayerInfo info, Level level) {
        return StatueClientUtils.CreateClientPlayer(info,level);
    }

    public static class StatuePlayerInfo{
        public String name;

        public UUID uuid;

        public GameProfile profile;
        private StatuePlayerInfo(@NotNull String name, @NotNull UUID uuid, @NotNull CompoundTag profile){
            this.name = name;
            this.uuid = uuid;
            this.profile = NbtUtils.readGameProfile(profile);
        }

        private StatuePlayerInfo(@NotNull Player player){
            this.uuid = player.getUUID();
            this.name = player.getGameProfile().getName();
            this.profile = player.getGameProfile();
            //this.tag = new CompoundTag();
            //NbtUtils.writeGameProfile(this.tag,player.getGameProfile());
        }

        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            tag.putString("name", name);
            tag.putUUID("uuid", uuid);
            CompoundTag profileTag = new CompoundTag();
            NbtUtils.writeGameProfile(profileTag, profile);
            tag.put("profile", profileTag);

            return tag;
        }

        public static StatuePlayerInfo load(CompoundTag tag){
            if(tag != null && tag.contains("name") && tag.contains("uuid") && tag.contains("profile")){
                return new StatuePlayerInfo(tag.getString("name"),tag.getUUID("uuid"),tag.getCompound("profile"));
            }
            return null;
        }

        @Deprecated
        public @Nullable
        static StatuePlayerInfo of(@Nullable UUID uuid, @Nullable CompoundTag profile){
            if(uuid == null || profile == null){
                return null;
            }
            else{
                return new StatuePlayerInfo("player", uuid, profile);
            }
        }

        public @Nullable
        static StatuePlayerInfo of(@Nullable Player player){
            if(player == null){
                return null;
            }
            else{
                return new StatuePlayerInfo(player);
            }
        }

    }
}
