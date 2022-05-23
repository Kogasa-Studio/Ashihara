package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.Random;

import static kogasastudio.ashihara.block.BlockWaterField.LEVEL;

public class BlockRiceCrop extends CropBlock {
    public BlockRiceCrop() {
        super
                (
                        Properties.of(Material.PLANT)
                                .noCollission()
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                );
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        if (!worldIn.isAreaLoaded(pos, 1)) {
            // Forge: prevent loading unloaded chunks when checking neighbor's light
            return;
        }
        if (worldIn.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge() && this.isValidBonemealTarget(worldIn, pos, state, true)) {
                float f = random.nextInt(3) + 22;
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    worldIn.setBlock(pos, this.getStateForAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).is(BlockRegistryHandler.WATER_FIELD.get());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return state.is(BlockRegistryHandler.WATER_FIELD.get());
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
        boolean flag = false;
        if (worldIn.getBlockState(pos.below()).is(BlockRegistryHandler.WATER_FIELD.get())) {
            flag = !this.isMaxAge(state) && worldIn.getBlockState(pos.below()).getValue(LEVEL) > 5;
        }
        return flag;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ItemRegistryHandler.RICE_SEEDLING.get();
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {
        return this.isValidBonemealTarget(worldIn, pos, state, true);
    }
}
