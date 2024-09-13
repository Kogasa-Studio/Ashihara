package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class BuildingComponentItem extends BlockItem
{
    private final Supplier<? extends BuildingComponent> component;

    public BuildingComponentItem(Supplier<? extends BuildingComponent> componentIn)
    {
        super(BlockRegistryHandler.MULTI_BUILT_BLOCK.get(), new Properties());
        this.component = componentIn;
    }

    public BuildingComponent getComponent()
    {
        return this.component.get();
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState)
    {
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(this.getBlock()))
        {
            BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
            if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlace(pContext, this.getComponent()))
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
        BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlace(pContext, this.getComponent())) b = InteractionResult.SUCCESS;
        return b;
    }

    @Override
    public String getDescriptionId()
    {
        return this.getOrCreateDescriptionId();
    }
}
