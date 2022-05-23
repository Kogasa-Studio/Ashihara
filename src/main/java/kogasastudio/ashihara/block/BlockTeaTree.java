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

import net.minecraft.block.AbstractBlock.Properties;

public class BlockTeaTree extends BushBlock implements IGrowable
{
    public BlockTeaTree()
    {
        super
        (
            Properties.of(Material.PLANT)
            .noCollission()
            .randomTicks()
            .strength(0.2F)
            .sound(SoundType.SWEET_BERRY_BUSH)
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(AGE, 0).setValue(BLOOMED, false));
    }

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    public static final BooleanProperty BLOOMED = BooleanProperty.create("bloomed");
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {builder.add(AGE, BLOOMED, AXIS);}

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape age_0 = box(6.0d, 0.0d, 6.0d, 10.0d, 4.0d, 10.0d);
        VoxelShape age_1 = box(4.0d, 0.0d, 4.0d, 12.0d, 11.0d, 12.0d);
        VoxelShape stem_x = box(3.0d, 0.0d, 2.0d, 13.0d, 5.0d, 14.0d);
        VoxelShape stem_z = box(2.0d, 0.0d, 3.0d, 14.0d, 5.0d, 13.0d);
        VoxelShape leaves_x = box(2.0d, 5.0d, 1.0d, 14.0d, 13.0d, 15.0d);
        VoxelShape leaves_z = box(1.0d, 5.0d, 2.0d, 15.0d, 13.0d, 14.0d);

        int age = state.getValue(AGE);
        if (age == 0) return age_0;
        if (age == 1) return age_1;
        if (state.getValue(AXIS).equals(Direction.Axis.X)) return VoxelShapes.or(stem_x, leaves_x);
        if (state.getValue(AXIS).equals(Direction.Axis.Z)) return VoxelShapes.or(stem_z, leaves_z);
        return VoxelShapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {return state.getValue(AGE) < 4;}

    //抄浆果丛实现减缓移动
    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (state.getValue(AGE) > 1 && entityIn instanceof LivingEntity && entityIn.getType() != EntityType.FOX && entityIn.getType() != EntityType.BEE)
        {
            entityIn.makeStuckInBlock(state, new Vector3d(0.8F, 1.0D, 0.8F));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        int age = state.getValue(AGE);
        if (age < 4 && worldIn.getRawBrightness(pos.above(), 0) >= 9 && onCropsGrowPre(worldIn, pos, state,random.nextInt(7) == 0))
        {
            if (age == 2) state = state.setValue(BLOOMED, true);
            else if (age == 3) state = state.setValue(BLOOMED, false);
            worldIn.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
            onCropsGrowPost(worldIn, pos, state);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        int age = state.getValue(AGE);
        ItemStack stack = player.getItemInHand(handIn);
        if (age < 4 && stack.getItem().equals(BONE_MEAL)) return ActionResultType.PASS;
        if (state.getValue(BLOOMED))
        {
            popResource(worldIn, pos, new ItemStack(TEA_FLOWER.get(), 1 + worldIn.random.nextInt(2)));
            worldIn.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.random.nextFloat() * 0.4F);
            worldIn.setBlockAndUpdate(pos, state.setValue(BLOOMED, false));
            return ActionResultType.SUCCESS;
        }
        if (age == 4)
        {
            popResource(worldIn, pos, new ItemStack(TEA_LEAF.get(), 1 + worldIn.random.nextInt(3)));
            popResource(worldIn, pos, new ItemStack(TEA_SEED.get(), 1 + worldIn.random.nextInt(2)));
            worldIn.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.random.nextFloat() * 0.4F);
            worldIn.setBlockAndUpdate(pos, state.setValue(AGE, 2));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return state.getValue(AGE) < 4;
    }

    @Override
    public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return state.getValue(AGE) < 4;
    }

    @Override
    public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {
        worldIn.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1));
    }
}
