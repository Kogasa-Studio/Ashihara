package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemExmpleContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static kogasastudio.ashihara.block.BlockWaterField.LEVEL;

public class BlockRiceCrop extends CropsBlock
{
    public BlockRiceCrop()
    {
        super
        (
            Properties.create(Material.PLANTS)
            .doesNotBlockMovement()
            .tickRandomly()
            .zeroHardnessAndResistance()
            .sound(SoundType.CROP)
        );
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getLightSubtracted(pos, 0) >= 9)
        {
            int i = this.getAge(state);
            if (i < this.getMaxAge() && this.canGrow(worldIn, pos, state, true))
            {
                float f = random.nextInt(3) + 22;
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0))
                {
                    worldIn.setBlockState(pos, this.withAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos)
    {return state.getBlock().matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD);}

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        boolean flag = false;
        if (worldIn.getBlockState(pos.down()).getBlock().matchesBlock(BlockExampleContainer.BLOCK_WATER_FIELD))
        {flag = !this.isMaxAge(state) && worldIn.getBlockState(pos.down()).get(LEVEL) > 5;}
        return flag;
    }

    @Override
    protected IItemProvider getSeedsItem() {return ItemExmpleContainer.ITEM_RICE_SEEDLING;}

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        Random rand = new Random();
        if (this.getAge(state) == this.getMaxAge())
        {
            list.add(new ItemStack(ItemExmpleContainer.RICE_CROP, rand.nextInt(3) + 1));
            return list;
        }
        else if (this.getAge(state) <= 2)
        {
            list.add(new ItemStack(ItemExmpleContainer.ITEM_RICE_SEEDLING));
            return list;
        }
        else return Collections.emptyList();
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return this.canGrow(worldIn, pos, state, true);
    }
}
