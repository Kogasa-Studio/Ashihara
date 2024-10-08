package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import java.util.List;
import java.util.function.Supplier;

public abstract class BuildingComponent
{
    public final String id;
    public final BuildingComponents.Type type;
    public final List<ItemStack> drops;

    public final Supplier<BaseMultiBuiltBlock> material;
    public SoundType sound;

    public BuildingComponent(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn, Supplier<BaseMultiBuiltBlock> materialIn, SoundType soundIn)
    {
        this.id = idIn;
        this.type = typeIn;
        this.drops = dropsIn;
        this.sound = soundIn;
        this.material = materialIn;
    }

    public BuildingComponent(String idIn, BuildingComponents.Type typeIn, Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn)
    {
        this(idIn, typeIn, dropsIn, materialIn, SoundType.WOOD);
    }

    public abstract ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context);

    public Supplier<BaseMultiBuiltBlock> getMaterial() {return material;}

    public SoundType getSoundType() {return this.sound;}

    public BuildingComponent setSound(SoundType soundIn)
    {
        this.sound = soundIn;
        return this;
    }
}
