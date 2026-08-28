package net.yaseruxd.scuffedsouls.playerclass;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public enum PlayerClass {

    KNIGHT(
            "Knight",
            "Shield raised, oath unbroken.\nThe wall that does not yield.",
            10, 6, 8, 5, 4, 7, 5, 5,
            List.of(
                    epicFight("iron_longsword"),
                    vanillaItem("shield"),
                    vanillaItem("iron_helmet"),
                    vanillaItem("chainmail_chestplate"),
                    vanillaItem("chainmail_leggings"),
                    vanillaItem("chainmail_boots"),
                    vanillaItem("bread", 5)
            )
    ),

    // PLACEHOLDER: no confirmed dual-scimitar/curved-blade item ID yet — using iron_tachi as stand-in
    MERCENARY(
            "Mercenary",
            "No banner, no cause.\nJust steel for hire, and speed to spare.",
            5, 6, 5, 5, 7, 14, 6, 5,
            List.of(
                    epicFight("iron_tachi"),
                    vanillaItem("leather_helmet"),
                    vanillaItem("leather_chestplate"),
                    vanillaItem("leather_leggings"),
                    vanillaItem("leather_boots"),
                    vanillaItem("bread", 3)
            )
    ),

    WARRIOR(
            "Warrior",
            "Strength answers every question\nthe sword cannot.",
            16, 8, 6, 4, 3, 5, 7, 6,
            List.of(
                    epicFight("iron_greatsword"),
                    vanillaItem("leather_chestplate"),
                    vanillaItem("leather_leggings"),
                    vanillaItem("bread", 3)
            )
    ),

    // PLACEHOLDER: no confirmed spear item ID yet — using magistuItem("iron_claymore") as stand-in
    HERALD(
            "Herald",
            "A spear in one hand,\na prayer in the other.",
            7, 6, 7, 7, 5, 12, 5, 4,
            List.of(
                    epicFight("iron_spear"),
                    vanillaItem("shield"),
                    vanillaItem("chainmail_chestplate"),
                    vanillaItem("leather_leggings"),
                    vanillaItem("leather_boots"),
                    vanillaItem("bread", 3)
            )
    ),

    THIEF(
            "Thief",
            "Take what you can.\nTrust nothing you can't outrun.",
            5, 5, 5, 5, 6, 8, 5, 8,
            List.of(
                    vanillaItem("bow"),
                    epicFight("iron_dagger"),
                    vanillaItem("arrow", 32),
                    vanillaItem("leather_helmet"),
                    vanillaItem("leather_chestplate"),
                    vanillaItem("bread", 3)
            )
    ),

    // PLACEHOLDER: no confirmed thrusting-sword (estoc-style) item ID yet — using iron_dagger as stand-in
    ASSASSIN(
            "Assassin",
            "Unseen, until the blade\nis already through you.",
            5, 5, 5, 6, 6, 8, 5, 5,
            List.of(
                    epicFight("iron_dagger"),
                    vanillaItem("leather_chestplate"),
                    vanillaItem("leather_boots"),
                    vanillaItem("bread", 3)
            )
    ),

    SORCERER(
            "Sorcerer",
            "Knowledge bought with silence,\nspent as soul arrows.",
            4, 5, 4, 10, 10, 7, 5, 7,
            List.of(
                    spellbook("electrocute", "chain_lightning", "shockwave"),
                    vanillaItem("leather_helmet"),
                    vanillaItem("leather_leggings"),
                    vanillaItem("bread", 3)
            )
    ),

    PYROMANCER(
            "Pyromancer",
            "Flame answers where words\nand iron fail.",
            7, 6, 4, 10, 10, 5, 5, 4,
            List.of(
                    vanillaItem("iron_axe"),
                    spellbook("firebolt", "fireball", "fire_breath"),
                    vanillaItem("leather_chestplate"),
                    vanillaItem("leather_leggings"),
                    vanillaItem("bread", 3)
            )
    ),

    // PLACEHOLDER: no confirmed mace item ID yet — using magistuItem("club") as stand-in
    CLERIC(
            "Cleric",
            "Faith mends what the world\nkeeps breaking.",
            16, 5, 4, 10, 10, 4, 5, 7,
            List.of(
                    magistuItem("iron_mace"),
                    vanillaItem("shield"),
                    spellbook("guiding_bolt", "divine_smite", "blessing_of_life", "healing_circle"),
                    vanillaItem("leather_chestplate"),
                    vanillaItem("leather_leggings"),
                    vanillaItem("bread", 5)
            )
    ),

    DEPRIVED(
            "Deprived",
            "Nothing given.\nEverything to prove.",
            5, 5, 5, 5, 5, 5, 5, 5,
            List.of(
                    magistuItem("club"),
                    vanillaItem("bread", 1)
            )
    );

    // -----------------------------------------------------------------------

    public final String displayName;
    public final String lore;
    public final int str, vit, end, intel, mag, dex, sta, lck;
    public final List<ItemStack> startingItems;

    PlayerClass(String displayName, String lore,
                int str, int vit, int end, int intel, int mag, int dex, int sta, int lck,
                List<ItemStack> startingItems) {
        this.displayName        = displayName;
        this.lore               = lore;
        this.str                = str;
        this.vit                = vit;
        this.end                = end;
        this.intel              = intel;
        this.mag                = mag;
        this.dex                = dex;
        this.sta                = sta;
        this.lck                = lck;
        this.startingItems      = startingItems;
    }

    private static ItemStack vanillaItem(String id) {
        return vanillaItem(id, 1);
    }

    private static ItemStack vanillaItem(String id, int count) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item, count);
    }
    //EpicKnight Items
    public static ItemStack magistuItem(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("magistuarmory", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }
    //Jet and Elias Items
    public static ItemStack womItem(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("wom", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }
    //EpicFight Items
    public static ItemStack epicFight(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("epicfight", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }
    //EpicFight Dawn Day Items
    public static ItemStack epicFightDD(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("epicfight_dd", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }
    //Iron Spells Items
    private static ItemStack spellbook(String... spellIds) {
        var item = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("irons_spellbooks", "copper_spell_book"));
        if (item == null) return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(item);
        CompoundTag tag = new CompoundTag();
        CompoundTag container = new CompoundTag();
        container.putInt("maxSpells", 5);
        container.putBoolean("mustEquip", true);
        container.putBoolean("spellWheel", true);

        net.minecraft.nbt.ListTag dataList = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < spellIds.length; i++) {
            CompoundTag spell = new CompoundTag();
            spell.putInt("index", i);
            spell.putString("id", "irons_spellbooks:" + spellIds[i]);
            spell.putInt("level", 1);
            dataList.add(spell);
        }
        container.put("data", dataList);
        tag.put("irons_spellbooks:spell_container", container);
        stack.setTag(tag);
        return stack;
    }
}