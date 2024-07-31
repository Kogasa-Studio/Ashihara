package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.CommonHooks;

import static net.minecraft.world.item.Items.BONE_MEAL;
import static net.minecraft.world.level.block.Blocks.FARMLAND;

public class CucumberCropBlock extends AbstractCropAge7
{
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        return super.mayPlaceOn(state, worldIn, pos) || (state.is(BlockRegistryHandler.CUCUMBERS.get()) && state.getValue(AGE) > 5);
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state)
    {
        return this.isValidBonemealTarget(worldIn, pos, state);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState state)
    {
        int age = state.getValue(AGE);
        BlockState downState = worldIn.getBlockState(pos.below());
        //case down
        if (downState.is(FARMLAND))
        {
            return age < 6
                    || (age < 7 && worldIn.getBlockState(pos.above()).is(BlockRegistryHandler.CUCUMBERS.get())
                    && worldIn.getBlockState(pos.above()).getValue(AGE) > 3);
        }
        //case up
        if (downState.is(BlockRegistryHandler.CUCUMBERS.get()))
        {
            return age < 5;
        }
        return super.isValidBonemealTarget(worldIn, pos, state);
    }

    @Override
    public void growCrops(Level worldIn, BlockPos pos, BlockState state)
    {
        BlockState downState = worldIn.getBlockState(pos.below());
        int age = this.getAge(state);
        boolean isUpper = downState.is(BlockRegistryHandler.CUCUMBERS.get());

        if (isUpper && this.getAge(state) >= 5)
        {
            worldIn.setBlockAndUpdate(pos, this.getStateForAge(5));
            return;
        }

        boolean canGrowUp = !isUpper && worldIn.getBlockState(pos.above()).isAir() && age > 5;

        int i = this.getAge(state) + (isUpper ? 1 : this.getBonemealAgeIncrease(worldIn));
        if (!isUpper && i == 5) i += 1;
        int j = this.getMaxAge();

        worldIn.setBlock(pos, this.getStateForAge(Math.min(i, j)), 2);
        if (i > j && canGrowUp) worldIn.setBlock(pos.above(), this.getStateForAge(i - j), 2);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random)
    {
        if (!worldIn.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light

        BlockState downState = worldIn.getBlockState(pos.below());
        boolean isUpper = downState.is(BlockRegistryHandler.CUCUMBERS.get());

        if ((isUpper && state.getValue(AGE) > 4) || (!isUpper && state.getValue(AGE) > 6 && !worldIn.getBlockState(pos.above()).isAir()))
            return;
        if (worldIn.getRawBrightness(pos, 0) >= 9)
        {
            int age = this.getAge(state);
            int validAge = isUpper ? 5 : this.getMaxAge();
            boolean canGrowUp = !isUpper && worldIn.getBlockState(pos.above()).isAir() && age > 5;
            if (age < validAge || canGrowUp)
            {
                float f = getGrowthSpeed(state, worldIn, pos);

                if (canGrowUp && CommonHooks.canCropGrow(worldIn, pos.above(), this.getStateForAge(0), random.nextInt((int) (25.0F / f) + 1) == 0))
                {
                    worldIn.setBlockAndUpdate(pos.above(), this.getStateForAge(0));
                    CommonHooks.fireCropGrowPost(worldIn, pos.above(), this.getStateForAge(0));
                } else if (age < validAge && CommonHooks.canCropGrow(worldIn, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0))
                {
                    worldIn.setBlock(pos, this.getStateForAge(age + (!isUpper && age == 4 ? 2 : 1)), 2);
                    CommonHooks.fireCropGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        BlockState downState = worldIn.getBlockState(pos.below());
        boolean isUpper = downState.is(BlockRegistryHandler.CUCUMBERS.get());

        int age = state.getValue(AGE);
        int ageAvailable = isUpper ? 5 : 7;
        int ageTurnIn = isUpper ? 4 : 6;
        if (age < ageAvailable && stack.getItem().equals(BONE_MEAL)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (age == ageAvailable)
        {
            if (!worldIn.isClientSide())
            {
                for (ItemStack stack1 : getDrops(state, (ServerLevel) worldIn, pos, null))
                {
                    popResource(worldIn, pos, stack1);
                }
            }
            worldIn.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + worldIn.random.nextFloat() * 0.4F);
            worldIn.setBlockAndUpdate(pos, this.getStateForAge(ageTurnIn));
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }
}
