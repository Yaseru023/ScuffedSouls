package net.yaseruxd.scuffedsouls.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.yaseruxd.scuffedsouls.ScuffedSouls;

public class ModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ScuffedSouls.MODID);

    public static final RegistryObject<CreativeModeTab> SCUFFED_SOULS_TAB =
            CREATIVE_TABS.register("scuffedsouls_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("ScuffedSouls"))
                            .icon(() -> new ItemStack(ModItems.TORMENTED_SOUL.get()))
                            .displayItems((params, output) -> {

                                output.accept(ModItems.TORMENTED_SOUL.get());
                                output.accept(ModItems.WEEPING_CORE.get());
                            })
                            .build());
}