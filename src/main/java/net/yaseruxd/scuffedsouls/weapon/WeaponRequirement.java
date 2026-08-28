package net.yaseruxd.scuffedsouls.weapon;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class WeaponRequirement {

    public final ResourceLocation item;
    public final List<StatRequirement> stats;

    public WeaponRequirement(ResourceLocation item, List<StatRequirement> stats) {
        this.item = item;
        this.stats = stats;
    }

    public record StatRequirement(
            String stat,
            double min
    ) {}
}