package net.yaseruxd.scuffedsouls.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class LogStrippingRecipeSerializer implements RecipeSerializer<LogStrippingRecipe> {

    @Override
    public LogStrippingRecipe fromJson(ResourceLocation id, JsonObject json) {
        // Read axe ingredient (tag-based so all axes work)
        Ingredient axe = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "axe"));

        // Read log ingredient
        Ingredient log = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "log"));

        // Read result
        JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
        ResourceLocation resultId = new ResourceLocation(GsonHelper.getAsString(resultJson, "item"));
        int count = GsonHelper.getAsInt(resultJson, "count", 1);
        ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(resultId), count);

        return new LogStrippingRecipe(id, axe, log, result);
    }

    @Override
    public @Nullable LogStrippingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient axe = Ingredient.fromNetwork(buf);
        Ingredient log = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        return new LogStrippingRecipe(id, axe, log, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, LogStrippingRecipe recipe) {
        recipe.getAxeIngredient().toNetwork(buf);
        recipe.getLogIngredient().toNetwork(buf);
        buf.writeItem(recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY));
    }
}