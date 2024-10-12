package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

public class ThinRafter extends QuarterOrientedFloor implements Interactable
{
    public ThinRafter
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        VoxelShape shape,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, model, shape, materialIn, dropsIn);
    }

    public ThinRafter
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn
    )
    {
        super(idIn, typeIn, model, materialIn, dropsIn);
    }

    @Override
    public ComponentStateDefinition handleInteraction(UseOnContext context, ComponentStateDefinition definition)
    {
        if (definition.model().equals(AdditionalModels.RED_THIN_RAFTER) && context.getItemInHand().is(Items.YELLOW_DYE))
        {
            context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.DYE_USE, SoundSource.BLOCKS);
            return new ComponentStateDefinition
            (definition.component(), definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(), definition.shape(), AdditionalModels.RED_THIN_RAFTER_PAINTED_YELLOW, definition.occupation());
        }
        return definition;
    }
}
