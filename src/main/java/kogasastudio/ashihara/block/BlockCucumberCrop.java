package kogasastudio.ashihara.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static net.minecraft.block.Blocks.FARMLAND;
import static net.minecraft.item.Items.BONE_MEAL;
import static net.minecraftforge.common.ForgeHooks.onCropsGrowPre;

public class BlockCucumberCrop extends AbstractCropAge7
{
    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return super.isValidGround(state, worldIn, pos) || (state.matchesBlock(BlockRegistryHandler.CUCUMBERS.get()) && state.get(AGE) > 5);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return this.canGrow(worldIn, pos, state, false);
    }

    @Override
    public boolean ticksRandomly(BlockState state) {return true;}

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        int age = state.get(AGE);
        BlockState downState = worldIn.getBlockState(pos.down());
        //case down
        if (downState.matchesBlock(FARMLAND))
        {
            return age < 6
            || (age < 7 && worldIn.getBlockState(pos.up()).matchesBlock(BlockRegistryHandler.CUCUMBERS.get())
                    && worldIn.getBlockState(pos.up()).get(AGE) > 3);
        }
        //case up
        if (downState.matchesBlock(BlockRegistryHandler.CUCUMBERS.get()))
        {
            return age < 5;
        }
        return super.canGrow(worldIn, pos, state, isClient);
    }

    @Override
    public void grow(World worldIn, BlockPos pos, BlockState state)
    {
        BlockState downState = worldIn.getBlockState(pos.down());
        int age = this.getAge(state);
        boolean isUpper = downState.matchesBlock(BlockRegistryHandler.CUCUMBERS.get());
        boolean canGrowUp = !isUpper && worldIn.getBlockState(pos.up()).isAir() && age > 5;

        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        if (!isUpper && i == 5) i += 1;
        int j = this.getMaxAge();

        worldIn.setBlockState(pos, this.withAge(Math.min(i, j)), 2);
        if (i > j && canGrowUp) worldIn.setBlockState(pos.up(), this.withAge(i - j), 2);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light

        BlockState downState = worldIn.getBlockState(pos.down());
        boolean isUpper = downState.matchesBlock(BlockRegistryHandler.CUCUMBERS.get());

        if ((isUpper && state.get(AGE) > 4) || (!isUpper && worldIn.getBlockState(pos.up()).isAir())) return;
        if (worldIn.getLightSubtracted(pos, 0) >= 9)
        {
            int age = this.getAge(state);
            int validAge = isUpper ? 5 : this.getMaxAge();
            boolean canGrowUp = !isUpper && worldIn.getBlockState(pos.up()).isAir() && age > 5;
            if (age < validAge || canGrowUp)
            {
                float f = getGrowthChance(this, worldIn, pos);

                if (canGrowUp &&
                    onCropsGrowPre(worldIn, pos.up(), this.withAge(0), random.nextInt((int)(25.0F / f) + 1) == 0))
                {
                    worldIn.setBlockState(pos.up(), this.withAge(0));
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos.up(), this.withAge(0));
                }
                else if (onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0))
                {
                    worldIn.setBlockState(pos, this.withAge(age + (!isUpper && age == 4 ? 2 : 1)), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        BlockState downState = worldIn.getBlockState(pos.down());
        boolean isUpper = downState.matchesBlock(BlockRegistryHandler.CUCUMBERS.get());

        int age = state.get(AGE);
        int ageAvailable = isUpper ? 5 : 7;
        int ageTurnIn = isUpper ? 4 : 6;
        ItemStack stack = player.getHeldItem(handIn);
        if (age < ageAvailable && stack.getItem().equals(BONE_MEAL)) return ActionResultType.PASS;
        if (age == ageAvailable)
        {
            if (!worldIn.isRemote())
            {
                for (ItemStack stack1 : getDrops(state, (ServerWorld) worldIn, pos, null))
                {
                    spawnAsEntity(worldIn, pos, stack1);
                }
            }
            worldIn.playSound(player, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            worldIn.setBlockState(pos, this.withAge(ageTurnIn));
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
