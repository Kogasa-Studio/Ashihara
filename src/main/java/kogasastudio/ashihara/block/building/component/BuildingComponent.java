package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;

import java.util.List;
import java.util.Map;

public abstract class BuildingComponent
{
    public final String id;
    public final BuildingComponents.Type type;
    public final List<ItemStack> drops;
    public final SoundType sound;

    public BuildingComponent(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn, SoundType soundIn)
    {
        this.id = idIn;
        this.type = typeIn;
        this.drops = dropsIn;
        this.sound = soundIn;
    }

    public BuildingComponent(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn)
    {
        this(idIn, typeIn, dropsIn, SoundType.WOOD);
    }

    public abstract ModelStateDefinition getModelDefinition(MultiBuiltBlockEntity beIn, UseOnContext context);

    public SoundType getSoundType() {return this.sound;}
}
