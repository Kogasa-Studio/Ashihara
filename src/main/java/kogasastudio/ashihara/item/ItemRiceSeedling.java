package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockExampleContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Objects;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemRiceSeedling extends Item
{
    public ItemRiceSeedling() {super(new Properties().group(ASHIHARA));}

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        ItemStack item = context.getItem();
        PlayerEntity player = context.getPlayer();
        World worldIn = context.getWorld();
        BlockPos pos = context.getPos().down();
        Direction facing = context.getFace();
        if (!item.isEmpty() && Objects.requireNonNull(player).canPlayerEdit(pos.offset(facing), facing, item))
        {
            BlockState state = worldIn.getBlockState(pos.up());
            if (state.getBlock() == BlockExampleContainer.BLOCK_WATER_FIELD && worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR)
            {
                worldIn.setBlockState(pos.up(2), BlockExampleContainer.BLOCK_RICE_CROP.getDefaultState());
                if (!player.abilities.isCreativeMode) {item.shrink(1);}
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        }
        else return ActionResultType.FAIL;
    }
}
