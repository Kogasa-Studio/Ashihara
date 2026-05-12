package kogasastudio.ashihara.item;

import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.block.blockentity.IFluidHandler;
import kogasastudio.ashihara.block.trees.TreeGrowers;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Objects;

import static kogasastudio.ashihara.fluid.FluidRegistryHandler.SOY_MILK;

public class MinatoAqua extends Item
{
    public MinatoAqua(Properties properties)
    {
        super(properties);
    }

    public MinatoAqua()
    {
        this(new Properties().food(new FoodProperties.Builder().nutrition(8).build()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity te = world.getBlockEntity(pos);
        Player playerIn = context.getPlayer();
        ItemStack item = context.getItemInHand();
        Direction direction = context.getClickedFace();
        var tree = TreeGrowers.CHERRY_BLOSSOM;
        RandomSource rand = context.getLevel().getRandom();

        if (te instanceof IFluidHandler fluidHolder)
        {
            BEFluidStackHandler<?> tank = fluidHolder.getTank();
            FluidResource soyMilk = FluidResource.of(SOY_MILK.get());
            try (Transaction tx = Transaction.openRoot())
            {
                tank.insert(soyMilk, 100, tx);
                tx.commit();
            }
            te.setChanged();
            return InteractionResult.SUCCESS;
        }

        if (!item.isEmpty() && Objects.requireNonNull(playerIn).mayUseItemAt(pos.relative(direction), direction, item) && !world.isClientSide())
        {
            ServerLevel worldIn = (ServerLevel) world;
            tree.growTree(worldIn, worldIn.getChunkSource().getGenerator(), pos, Blocks.CHERRY_LOG.get().defaultBlockState(), rand);
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }
}
