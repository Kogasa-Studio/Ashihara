package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockWaterField;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

import static kogasastudio.ashihara.block.BlockExampleContainer.BLOCK_WATER_FIELD;

public class ItemKoishi extends Item
{
    public ItemKoishi()
    {
        super(new Properties().group(ItemGroup.MATERIALS));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        ItemStack item = context.getItem();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        World worldIn = context.getWorld();

        if (!item.isEmpty() && Objects.requireNonNull(player).canPlayerEdit(pos.offset(facing), facing, item))
        {
            BlockState blockState = worldIn.getBlockState(pos); //测试功能：右击草方块变钻石
            if (blockState.getBlock() == Blocks.GRASS_BLOCK)
            {
                worldIn.playSound(player, pos, SoundEvents.BLOCK_BAMBOO_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, Blocks.DIAMOND_BLOCK.getDefaultState());
                if (!player.abilities.isCreativeMode)
                {
                    item.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
            else if (blockState.getBlock() == BLOCK_WATER_FIELD)
            {
                worldIn.playSound(player, pos, SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, blockState.with(BlockWaterField.LEVEL, 6));
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        }
        else return ActionResultType.FAIL;
    }
}
