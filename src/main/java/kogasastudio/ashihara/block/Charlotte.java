package kogasastudio.ashihara.block;

import kogasastudio.ashihara.client.gui.CharlotteScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class Charlotte extends Block
{
    public Charlotte()
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

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.isClientSide())
        {
            //Minecraft.getInstance().setScreen(new CharlotteScreen());
            return InteractionResult.SUCCESS;
        }
        else return InteractionResult.PASS;
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {return 1.0F;}

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());}
}
