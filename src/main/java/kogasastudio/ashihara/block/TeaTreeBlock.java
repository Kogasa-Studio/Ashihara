package kogasastudio.ashihara.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;

import static kogasastudio.ashihara.item.ItemRegistryHandler.*;
import static net.minecraft.world.item.Items.BONE_MEAL;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

public class TeaTreeBlock extends BushBlock implements BonemealableBlock
{
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    public static final BooleanProperty BLOOMED = BooleanProperty.create("bloomed");
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;

    public TeaTreeBlock()
    {
        super
                (
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.PLANT)
                                .noCollission()
                                .randomTicks()
                                .strength(0.2F)
                                .sound(SoundType.SWEET_BERRY_BUSH)
                );
        this.registerDefaultState(this.getStateDefinition().any().setValue(AGE, 0).setValue(BLOOMED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AGE, BLOOMED, AXIS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape age_0 = box(6.0d, 0.0d, 6.0d, 10.0d, 4.0d, 10.0d);
        VoxelShape age_1 = box(4.0d, 0.0d, 4.0d, 12.0d, 11.0d, 12.0d);
        VoxelShape stem_x = box(3.0d, 0.0d, 2.0d, 13.0d, 5.0d, 14.0d);
        VoxelShape stem_z = box(2.0d, 0.0d, 3.0d, 14.0d, 5.0d, 13.0d);
        VoxelShape leaves_x = box(2.0d, 5.0d, 1.0d, 14.0d, 13.0d, 15.0d);
        VoxelShape leaves_z = box(1.0d, 5.0d, 2.0d, 15.0d, 13.0d, 14.0d);

        int age = state.getValue(AGE);
        if (age == 0)
        {
            return age_0;
        }
        if (age == 1)
        {
            return age_1;
        }
        if (state.getValue(AXIS).equals(Direction.Axis.X))
        {
            return Shapes.or(stem_x, leaves_x);
        }
        if (state.getValue(AXIS).equals(Direction.Axis.Z))
        {
            return Shapes.or(stem_z, leaves_z);
        }
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(AGE) < 4;
    }

    //抄浆果丛实现减缓移动
    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
        if (state.getValue(AGE) > 1 && entityIn instanceof LivingEntity && entityIn.getType() != EntityType.FOX && entityIn.getType() != EntityType.BEE)
        {
            entityIn.makeStuckInBlock(state, new Vec3(0.8F, 1.0D, 0.8F));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random)
    {
        int age = state.getValue(AGE);
        if (age < 4 && worldIn.getRawBrightness(pos.above(), 0) >= 9 && CommonHooks.canCropGrow(worldIn, pos, state, random.nextInt(7) == 0))
        {
            if (age == 2)
            {
                state = state.setValue(BLOOMED, true);
            } else if (age == 3)
            {
                state = state.setValue(BLOOMED, false);
            }
            worldIn.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
            CommonHooks.fireCropGrowPost(worldIn, pos, state);
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        int age = state.getValue(AGE);
        if (age < 4 && stack.getItem().equals(BONE_MEAL))
        {
            return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
        }
        if (state.getValue(BLOOMED))
        {
            popResource(worldIn, pos, new ItemStack(TEA_FLOWER.get(), 1 + worldIn.random.nextInt(2)));
            worldIn.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + worldIn.random.nextFloat() * 0.4F);
            worldIn.setBlockAndUpdate(pos, state.setValue(BLOOMED, false));
            return ItemInteractionResult.SUCCESS;
        }
        if (age == 4)
        {
            popResource(worldIn, pos, new ItemStack(TEA_LEAF.get(), 1 + worldIn.random.nextInt(3)));
            popResource(worldIn, pos, new ItemStack(TEA_SEED.get(), 1 + worldIn.random.nextInt(2)));
            worldIn.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + worldIn.random.nextFloat() * 0.4F);
            worldIn.setBlockAndUpdate(pos, state.setValue(AGE, 2));
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState state)
    {
        return state.getValue(AGE) < 4;
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state)
    {
        return state.getValue(AGE) < 4;
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state)
    {
        worldIn.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec()
    {
        return simpleCodec(p -> new TeaTreeBlock());
    }
}
