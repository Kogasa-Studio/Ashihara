package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static kogasastudio.ashihara.block.BlockWaterField.LEVEL;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockRiceCrop extends CropsBlock
{
    public BlockRiceCrop()
    {
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
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getRawBrightness(pos, 0) >= 9)
        {
            int i = this.getAge(state);
            if (i < this.getMaxAge() && this.isValidBonemealTarget(worldIn, pos, state, true))
            {
                float f = random.nextInt(3) + 22;
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0))
                {
                    worldIn.setBlock(pos, this.getStateForAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {return worldIn.getBlockState(pos.below()).getBlock().is(BlockRegistryHandler.WATER_FIELD.get());}

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos)
    {return state.getBlock().is(BlockRegistryHandler.WATER_FIELD.get());}

    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        boolean flag = false;
        if (worldIn.getBlockState(pos.below()).getBlock().is(BlockRegistryHandler.WATER_FIELD.get()))
        {flag = !this.isMaxAge(state) && worldIn.getBlockState(pos.below()).getValue(LEVEL) > 5;}
        return flag;
    }

    @Override
    protected IItemProvider getBaseSeedId() {return ItemRegistryHandler.RICE_SEEDLING.get();}

    @Override
    public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return this.isValidBonemealTarget(worldIn, pos, state, true);
    }
}
