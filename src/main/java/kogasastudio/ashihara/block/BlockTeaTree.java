package kogasastudio.ashihara.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static kogasastudio.ashihara.item.ItemRegistryHandler.*;
import static net.minecraft.item.Items.BONE_MEAL;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_AXIS;
import static net.minecraftforge.common.ForgeHooks.onCropsGrowPost;
import static net.minecraftforge.common.ForgeHooks.onCropsGrowPre;

public class BlockTeaTree extends BushBlock implements IGrowable
{
    public BlockTeaTree()
    {
        super
        (
            Properties.create(Material.PLANTS)
            .doesNotBlockMovement()
            .tickRandomly()
            .hardnessAndResistance(0.2F)
            .sound(SoundType.SWEET_BERRY_BUSH)
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0).with(BLOOMED, false));
    }

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    public static final BooleanProperty BLOOMED = BooleanProperty.create("bloomed");
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {builder.add(AGE, BLOOMED, AXIS);}

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape age_0 = makeCuboidShape(6.0d, 0.0d, 6.0d, 10.0d, 4.0d, 10.0d);
        VoxelShape age_1 = makeCuboidShape(4.0d, 0.0d, 4.0d, 12.0d, 11.0d, 12.0d);
        VoxelShape stem_x = makeCuboidShape(3.0d, 0.0d, 2.0d, 13.0d, 5.0d, 14.0d);
        VoxelShape stem_z = makeCuboidShape(2.0d, 0.0d, 3.0d, 14.0d, 5.0d, 13.0d);
        VoxelShape leaves_x = makeCuboidShape(2.0d, 5.0d, 1.0d, 14.0d, 13.0d, 15.0d);
        VoxelShape leaves_z = makeCuboidShape(1.0d, 5.0d, 2.0d, 15.0d, 13.0d, 14.0d);

        int age = state.get(AGE);
        if (age == 0) return age_0;
        if (age == 1) return age_1;
        if (state.get(AXIS).equals(Direction.Axis.X)) return VoxelShapes.or(stem_x, leaves_x);
        if (state.get(AXIS).equals(Direction.Axis.Z)) return VoxelShapes.or(stem_z, leaves_z);
        return VoxelShapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(AXIS, context.getPlacementHorizontalFacing().getAxis());
    }

    @Override
    public boolean ticksRandomly(BlockState state) {return state.get(AGE) < 4;}

    //抄浆果丛实现减缓移动
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (state.get(AGE) > 1 && entityIn instanceof LivingEntity && entityIn.getType() != EntityType.FOX && entityIn.getType() != EntityType.BEE)
        {
            entityIn.setMotionMultiplier(state, new Vector3d(0.8F, 1.0D, 0.8F));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        int age = state.get(AGE);
        if (age < 4 && worldIn.getLightSubtracted(pos.up(), 0) >= 9 && onCropsGrowPre(worldIn, pos, state,random.nextInt(7) == 0))
        {
            if (age == 2) state = state.with(BLOOMED, true);
            else if (age == 3) state = state.with(BLOOMED, false);
            worldIn.setBlockState(pos, state.with(AGE, age + 1));
            onCropsGrowPost(worldIn, pos, state);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        int age = state.get(AGE);
        ItemStack stack = player.getHeldItem(handIn);
        if (age < 4 && stack.getItem().equals(BONE_MEAL)) return ActionResultType.PASS;
        if (state.get(BLOOMED))
        {
            spawnAsEntity(worldIn, pos, new ItemStack(TEA_FLOWER.get(), 1 + worldIn.rand.nextInt(2)));
            worldIn.playSound(player, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            worldIn.setBlockState(pos, state.with(BLOOMED, false));
            return ActionResultType.SUCCESS;
        }
        if (age == 4)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(TEA_LEAF.get(), 1 + worldIn.rand.nextInt(3)));
            spawnAsEntity(worldIn, pos, new ItemStack(TEA_SEED.get(), 1 + worldIn.rand.nextInt(2)));
            worldIn.playSound(player, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            worldIn.setBlockState(pos, state.with(AGE, 2));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return state.get(AGE) < 4;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return state.get(AGE) < 4;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {
        worldIn.setBlockState(pos, state.with(AGE, state.get(AGE) + 1));
    }
}
