package com.dkmk100.arsomega.items;

import net.minecraft.item.Item;

public class BasicItem extends Item {
    public BasicItem(Properties properties) {
        super(properties);
    }
    public BasicItem(Properties properties, String name)
    {
        super(properties);
        setRegistryName(name);
    }
}
