package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.IFluidHandler;
import kogasastudio.ashihara.block.trees.TreeGrowers;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.Objects;

import static kogasastudio.ashihara.fluid.FluidRegistryHandler.SOY_MILK;
import static net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class ItemMinatoAqua extends Item
{
    public ItemMinatoAqua()
    {
        super(new Properties().food(new FoodProperties.Builder().nutrition(8).build()));
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

        if (te instanceof IFluidHandler)
        {
            FluidTank tank = ((IFluidHandler) te).getTank();
            if (!tank.isEmpty()) tank.getFluid().setAmount(tank.getFluidAmount() + 100);
            else tank.fill(new FluidStack(SOY_MILK.get(), 100), EXECUTE);
            te.setChanged();
            return InteractionResult.SUCCESS;
        }

        if (!item.isEmpty() && Objects.requireNonNull(playerIn).mayUseItemAt(pos.relative(direction), direction, item) && !world.isClientSide())
        {
            ServerLevel worldIn = (ServerLevel) world;
            tree.growTree(worldIn, worldIn.getChunkSource().getGenerator(), pos, BlockRegistryHandler.CHERRY_LOG.get().defaultBlockState(), rand);
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }
}
