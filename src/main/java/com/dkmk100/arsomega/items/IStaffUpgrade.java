package com.dkmk100.arsomega.items;

import net.minecraft.world.item.ItemStack;

public interface IStaffUpgrade extends IStaffComponent{
    @Override
    default ModularStaff.StaffComponentType getType(ItemStack component){
        return ModularStaff.StaffComponentType.UPGRADE;
    }
    //return -1 for no limit
    int getMaxCount();
}
