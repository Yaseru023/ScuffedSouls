package net.yaseruxd.scuffedsouls.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "scuffedsouls");

    public static final RegistryObject<LogStrippingRecipeSerializer> LOG_STRIPPING_SERIALIZER =
            SERIALIZERS.register("log_stripping", LogStrippingRecipeSerializer::new);
}
