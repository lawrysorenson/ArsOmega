package com.dkmk100.arsomega.client.models;

import com.dkmk100.arsomega.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;

public class ColoredItemModel<T extends Item & IAnimatable> extends AnimatedGeoModel<T> {
    String name;
    String textureName;

    public ColoredItemModel(String name){
        super();
        this.name = name;
        this.textureName = name;
    }

    public ColoredItemModel(String name, String textureName){
        super();
        this.name = name;
        this.textureName = textureName;
    }


    @Override
    public ResourceLocation getModelResource(T o) {
        return ResourceUtil.getModelResource(name);
    }

    @Override
    public ResourceLocation getTextureResource(T o) {
        return ResourceUtil.getItemTextureResource(textureName);
    }

    @Override
    public ResourceLocation getAnimationResource(T o) {
        return ResourceUtil.getAnimationResource(name);
    }
}
