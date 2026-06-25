package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.SnappedUseOnContext;
import kogasastudio.ashihara.utils.GridSnapHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionHand;

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

    public FurnitureComponent getComponent() { return this.component.get(); }

    @Override
    public InteractionResult use(net.minecraft.world.level.Level level, Player player, InteractionHand hand)
    {
        if (hand.equals(InteractionHand.OFF_HAND) && !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
            return InteractionResult.FAIL;
        return InteractionResult.PASS;
    }

    @Override
    public boolean canPlace(BlockPlaceContext pContext, BlockState pState)
    {
        InteractionHand hand = pContext.getHand();
        Player player = pContext.getPlayer();
        if (hand.equals(InteractionHand.OFF_HAND) && player != null && !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
            return false;
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(this.getBlock()))
        {
            BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
            if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(pContext, this.getComponent()))
                return false;
        }
        return super.canPlace(pContext, pState);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext)
    {
        ItemStack stack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        InteractionHand hand = pContext.getHand();
        if (hand.equals(InteractionHand.OFF_HAND) && player != null && !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
            return InteractionResult.FAIL;

        BlockPlaceContext context = new SnappedUseOnContext(pContext, GridSnapHelper.getGridStep(player), false);
        InteractionResult b = super.place(pContext);
        BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        boolean placed = false;
        if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
        {
            if (b != InteractionResult.SUCCESS) pContext.getItemInHand().consume(1, player);
            placed = true;
            b = InteractionResult.SUCCESS;
        }
        else
        {
            BlockEntity be2 = context.getLevel().getBlockEntity(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
            if (be2 instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
            {
                if (b != InteractionResult.SUCCESS) pContext.getItemInHand().consume(1, player);
                placed = true;
                b = InteractionResult.SUCCESS;
            }
        }
        if (!placed && b == InteractionResult.SUCCESS)
            pContext.getLevel().removeBlock(pContext.getClickedPos(), false);
        return b;
    }
}
