package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockRegistryHandler;
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

import net.minecraft.item.Item.Properties;

public class ItemRiceSeedling extends Item
{
    public ItemRiceSeedling() {super(new Properties().tab(ASHIHARA));}

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        ItemStack item = context.getItemInHand();
        PlayerEntity player = context.getPlayer();
        World worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos().below();
        Direction facing = context.getClickedFace();
        if (!item.isEmpty() && Objects.requireNonNull(player).mayUseItemAt(pos.relative(facing), facing, item))
        {
            BlockState state = worldIn.getBlockState(pos.above());
            if (state.is(BlockRegistryHandler.WATER_FIELD.get()) && worldIn.getBlockState(pos.above(2)).getBlock() == Blocks.AIR)
            {
                worldIn.setBlockAndUpdate(pos.above(2), BlockRegistryHandler.RICE_CROP.get().defaultBlockState());
                if (!player.abilities.instabuild) {item.shrink(1);}
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        }
        else return ActionResultType.FAIL;
    }
}
