package net.yaseruxd.scuffedsouls.playerclass;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public enum PlayerClass {

    KNIGHT(
            "Knight",
            "Forged in iron, unbroken by war.\nShield raised. Oath kept.",
            7, 6, 8, 5, 4, 7, 5, 5,
            List.of(
                    epicFight("iron_longsword"),
                    vanillaItem("shield"),
                    bksArmor("banished_knight_helmet"),       // PLACEHOLDER: confirm exact ID
                    bksArmor("banished_knight_chestplate"),   // PLACEHOLDER
                    bksArmor("banished_knight_leggings"),     // PLACEHOLDER
                    bksArmor("banished_knight_boots"),        // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of()  // no spells
    ),

    WARRIOR(
            "Warrior",
            "Strength is the only truth.\nEverything else is noise.",
            9, 8, 6, 4, 3, 5, 7, 6,
            List.of(
                    erAnim("iron_battleaxe"),       // PLACEHOLDER: confirm exact ID
                    vanillaItem("shield"),
                    bksArmor("iron_chestplate"), // PLACEHOLDER
                    bksArmor("iron_leggings"),   // PLACEHOLDER
                    bksArmor("iron_boots"),   // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of()
    ),

    WANDERER(
            "Wanderer",
            "No home. No lord. No burden.\nJust the blade and the road.",
            5, 6, 5, 5, 7, 9, 6, 5,
            List.of(
                    epicFight("iron_tachi"),
                    bksArmor("ronin_helmet"),     // PLACEHOLDER
                    bksArmor("ronin_chestplate"), // PLACEHOLDER
                    bksArmor("ronin_leggings"),   // PLACEHOLDER
                    bksArmor("ronin_boots"),      // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of()
    ),

    THIEF(
            "Thief",
            "Take what you can.\nTrust nothing you can't outrun.",
            5, 5, 5, 5, 6, 7, 5, 8,
            List.of(
                    vanillaItem("bow"),
                    epicFight("iron_dagger"),
                    vanillaItem("arrow", 64),
                    bksArmor("assassin_helmet"),     // PLACEHOLDER
                    bksArmor("assassin_chestplate"), // PLACEHOLDER
                    bksArmor("assassin_leggings"),   // PLACEHOLDER
                    bksArmor("assassin_boots"),      // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of()
    ),

    BANDIT(
            "Bandit",
            "No finesse. No mercy.\nJust weight and the will to swing it.",
            12, 7, 5, 3, 3, 4, 6, 5,
            List.of(
                    vanillaItem("iron_sword"),      // PLACEHOLDER: confirm exact ID
                    bksArmor("fallen_knight_chestplate"), // PLACEHOLDER
                    bksArmor("fallen_knight_leggings"),   // PLACEHOLDER
                    bksArmor("fallen_knight_boots"),      // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of()
    ),

    HUNTER(
            "Hunter",
            "Patient. Precise. Unseen.\nThe arrow finds its mark.",
            5, 6, 6, 4, 4, 8, 6, 6,
            List.of(
                    vanillaItem("bow"),
                    epicFight("iron_longsword"),
                    vanillaItem("arrow", 32),
                    bksArmor("exile_soldier_helmet"),     // PLACEHOLDER
                    bksArmor("exile_soldier_chestplate"), // PLACEHOLDER
                    bksArmor("exile_soldier_leggings"),   // PLACEHOLDER
                    bksArmor("exile_soldier_boots"),   // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of()
    ),

    SORCERER(
            "Sorcerer",
            "Knowledge bought with silence,\nspent as soul arrows.",
            4, 5, 4, 9, 9, 7, 5, 7,
            List.of(
                    bkSpell("academy_glintstone_staff"),
                    bksArmor("preceptor_helmet"),     // PLACEHOLDER
                    bksArmor("preceptor_chestplate"), // PLACEHOLDER
                    bksArmor("preceptor_leggings"),   // PLACEHOLDER
                    bksArmor("preceptor_boots"),   // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of(
                    "glintstone_arc",   // PLACEHOLDER: confirm spell ID
                    "great_glintstone_comet"      // PLACEHOLDER: confirm spell ID
            )
    ),

    PYROMANCER(
            "Pyromancer",
            "Flame answers where words\nand steel fail.",
            7, 6, 4, 8, 7, 5, 5, 4,
            List.of(
                    vanillaItem("iron_axe"),       // PLACEHOLDER
                    bksArmor("confessor_helmet"),     // PLACEHOLDER
                    bksArmor("confessor_chestplate"), // PLACEHOLDER
                    bksArmor("confessor_leggings"),   // PLACEHOLDER
                    bksArmor("confessor_boots"),   // PLACEHOLDER
                    vanillaItem("bread", 16)
            ),
            List.of(
                    "fire_bolt",
                    "fireball"
            )
    ),

    CLERIC(
            "Cleric",
            "Faith mends what the world\nkeeps breaking.",
            7, 5, 4, 9, 8, 4, 5, 7,
            List.of(
                    magistuItem("iron_heavymace"),
                    magistuItem("iron_heatershield"),
                    bksArmor("cleric_helmet"),
                    bksArmor("cleric_chestplate"),
                    bksArmor("cleric_leggings"),
                    bksArmor("cleric_boots"),
                    vanillaItem("bread", 16)
            ),
            List.of(
                    "heal",
                    "orders_bolt",
                    "golden_ray"
            )
    ),

    DEPRIVED(
            "Deprived",
            "Nothing given.\nEverything to prove.",
            5, 5, 5, 5, 5, 5, 5, 5,
            List.of(
                    magistuItem("club"),
                    bksArmor("prisoner_helmet"),
                    bksArmor("prisoner_chestplate"),
                    bksArmor("prisoner_leggings"),
                    bksArmor("prisoner_boots"),
                    vanillaItem("bread", 16)
            ),
            List.of()
    );

    // -----------------------------------------------------------------------

    public final String displayName;
    public final String lore;
    public final int str, vit, end, intel, mag, dex, sta, lck;
    public final List<ItemStack> startingItems;
    public final List<String> startingSpells;

    PlayerClass(String displayName, String lore,
                int str, int vit, int end, int intel, int mag, int dex, int sta, int lck,
                List<ItemStack> startingItems,
                List<String> startingSpells) {
        this.displayName    = displayName;
        this.lore           = lore;
        this.str            = str;
        this.vit            = vit;
        this.end            = end;
        this.intel          = intel;
        this.mag            = mag;
        this.dex            = dex;
        this.sta            = sta;
        this.lck            = lck;
        this.startingItems  = startingItems;
        this.startingSpells = startingSpells;
    }

    // --- Item helpers ---

    private static ItemStack vanillaItem(String id) { return vanillaItem(id, 1); }

    private static ItemStack vanillaItem(String id, int count) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item, count);
    }

    // BKS Armor mod
    public static ItemStack bksArmor(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("bks_armor", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }

    // EpicFight weapons
    public static ItemStack epicFight(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("epicfight", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }

    // ER Animations weapons
    public static ItemStack erAnim(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("er_animations", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }

    // EpicKnights Weapon
    public static ItemStack magistuItem(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("magistuarmory", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }

    // EpicKnights Weapon
    public static ItemStack bkSpell(String id) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("bks_spellcraft", id));
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }
}