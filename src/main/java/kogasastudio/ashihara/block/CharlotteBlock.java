package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.CharlotteTE;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CharlotteBlock extends Block implements EntityBlock
{
    public CharlotteBlock()
    {
        super
        (
            BlockBehaviour.Properties.of()
            .noOcclusion()
            .strength(1.0F)
            .mapColor(DyeColor.PINK)
            .sound(SoundType.WOOL)
        );
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {return 1.0F;}

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());}

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.is(ItemRegistryHandler.RICE.asItem()) && level.isClientSide())
        {
            CharlotteTE te = (CharlotteTE) level.getBlockEntity(pos);
            if (te != null)
            {
                if (stack.getCount() == 1)
                {
                    te.switchRender(player);
                }
                if (stack.getCount() > 1)
                {
                    te.lerpScale(stack.getCount() * 8, stack.getCount() * 4, player);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CharlotteTE(pos, state);
    }
}
