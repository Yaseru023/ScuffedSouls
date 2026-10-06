package net.yaseruxd.scuffedsouls.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yaseruxd.scuffedsouls.ScuffedSouls;
import net.yaseruxd.scuffedsouls.entity.MemorCustos;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ScuffedSouls.MODID);

    public static final RegistryObject<EntityType<MemorCustos>> MEMORCUSTOS =
            ENTITY_TYPES.register("memorcustos",
                    () -> EntityType.Builder.<MemorCustos>of(MemorCustos::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)        // same as player hitbox
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build("memorcustos")
            );
}