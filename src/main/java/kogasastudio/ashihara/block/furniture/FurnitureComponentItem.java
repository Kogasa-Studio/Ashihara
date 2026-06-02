package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.utils.GridSnapHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class FurnitureComponentItem extends BlockItem
{
    private final Supplier<? extends FurnitureComponent> component;

    public FurnitureComponentItem(Supplier<? extends FurnitureComponent> componentIn, Supplier<BaseMultiBuiltBlock> block)
    {
        this(componentIn, block, new Properties());
    }

    public FurnitureComponentItem(Supplier<? extends FurnitureComponent> componentIn, Supplier<BaseMultiBuiltBlock> block, Properties properties)
    {
        super(block.get(), properties);
        this.component = componentIn;
    }

    public FurnitureComponent getComponent()
    {
        return this.component.get();
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState)
    {
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(this.getBlock()))
        {
            BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
            if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(pContext, this.getComponent()))
            {
                return false;
            }
        }
        return super.canPlace(pContext, pState);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext)
    {
        Player player = pContext.getPlayer();
        BlockPlaceContext context = player != null ? new SnappedUseOnContext(pContext, GridSnapHelper.getGridStep(player)) : pContext;
        InteractionResult b = super.place(pContext);
        BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
        {
            b = InteractionResult.SUCCESS;
        }
        else
        {
            BlockEntity be2 = context.getLevel().getBlockEntity(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
            if (be2 instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
            {
                b = InteractionResult.SUCCESS;
            }
        }
        return b;
    }
}
