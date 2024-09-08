package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BuildingComponentItem extends BlockItem
{
    public final BuildingComponent component;

    public BuildingComponentItem(BuildingComponent componentIn)
    {
        super(BlockRegistryHandler.MULTI_BUILT_BLOCK.get(), new Properties());
        this.component = componentIn;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState)
    {
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(this.getBlock()))
        {
            BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
            if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlace(pContext, this.component))
            {
                return false;
            }
        }
        return super.canPlace(pContext, pState);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext)
    {
        InteractionResult b = super.place(pContext);
        if (b != InteractionResult.FAIL && b != InteractionResult.PASS)
        {
            BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
            if (blockEntity instanceof MultiBuiltBlockEntity be) be.tryPlace(pContext, this.component);
        }
        return b;
    }
}
