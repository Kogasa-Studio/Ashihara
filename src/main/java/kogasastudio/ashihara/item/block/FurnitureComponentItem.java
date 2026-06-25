package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.MultiBlockFurniture;
import kogasastudio.ashihara.block.furniture.SnappedUseOnContext;
import kogasastudio.ashihara.helper.ShapeHelper;
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
        else if (this.getComponent() instanceof MultiBlockFurniture)
        {
            var phantom = MultiBuiltBlockEntity.makePhantom(pContext.getClickedPos());
            var ctx = new SnappedUseOnContext(pContext, GridSnapHelper.getGridStep(player), false);
            var def = this.getComponent().definite(phantom, ctx);
            if (def != null)
            {
                var bb = def.shape().bounds();
                for (int x = (int) Math.floor(bb.minX); x <= (int) Math.ceil(bb.maxX) - 1; x++)
                    for (int y = (int) Math.floor(bb.minY); y <= (int) Math.ceil(bb.maxY) - 1; y++)
                        for (int z = (int) Math.floor(bb.minZ); z <= (int) Math.ceil(bb.maxZ) - 1; z++)
                        {
                            if (x == 0 && y == 0 && z == 0) continue;
                            if (ShapeHelper.sliceShape(def.shape(), 1, new net.minecraft.core.Vec3i(x, y, z)).isEmpty())
                                continue;
                            var target = pContext.getClickedPos().offset(x, y, z);
                            if (pContext.getLevel().getBlockEntity(target) instanceof MultiBuiltBlockEntity)
                                continue;
                            if (!pContext.getLevel().getBlockState(target).canBeReplaced())
                                return false;
                        }
            }
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
