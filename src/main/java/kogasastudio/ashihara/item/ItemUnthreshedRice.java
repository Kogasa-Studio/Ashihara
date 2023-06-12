package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemUnthreshedRice extends Item
{
    public ItemUnthreshedRice()
    {
        super(new Properties().tab(ASHIHARA));
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack item = context.getItemInHand();
        Player player = context.getPlayer();
        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos().below();
        Direction facing = context.getClickedFace();
        if (!item.isEmpty() && Objects.requireNonNull(player).mayUseItemAt(pos.relative(facing), facing, item))
        {
            BlockState state = worldIn.getBlockState(pos.above());
            if (state.getBlock() == Blocks.FARMLAND && worldIn.getBlockState(pos.above(2)).getBlock() == Blocks.AIR)
            {
                worldIn.setBlockAndUpdate(pos.above(2), BlockRegistryHandler.IMMATURE_RICE.get().defaultBlockState());
                if (!player.isCreative())
                {
                    item.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else return InteractionResult.PASS;
        } else return InteractionResult.FAIL;
    }
}
