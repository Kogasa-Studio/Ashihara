package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.IFluidHandler;
import kogasastudio.ashihara.block.trees.CherryBlossomTree;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;
import java.util.Random;

import static kogasastudio.ashihara.fluid.FluidRegistryHandler.SOY_MILK;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

import net.minecraft.item.Item.Properties;

public class ItemMinatoAqua extends Item
{
    public ItemMinatoAqua()
    {
        super
        (
            new Properties()
            .tab(ItemGroup.TAB_MATERIALS)
            .food(new Food.Builder().nutrition(8).build())
        );
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        TileEntity te = world.getBlockEntity(pos);
        PlayerEntity playerIn = context.getPlayer();
        ItemStack item = context.getItemInHand();
        Direction direction = context.getClickedFace();
        Tree tree = new CherryBlossomTree();
        Random rand = context.getLevel().getRandom();

        if (te instanceof IFluidHandler)
        {
            ((IFluidHandler) te).getTank().ifPresent
            (
                tank ->
                {
                    if (!tank.isEmpty()) tank.getFluid().setAmount(tank.getFluidAmount() + 100);
                    else tank.fill(new FluidStack(SOY_MILK.get(), 100), EXECUTE);
                }
            );
            te.setChanged();
            return ActionResultType.SUCCESS;
        }

        if (!item.isEmpty() && Objects.requireNonNull(playerIn).mayUseItemAt(pos.relative(direction), direction, item) && !world.isClientSide())
        {
            ServerWorld worldIn = (ServerWorld)world;
            tree.growTree(worldIn, worldIn.getChunkSource().getGenerator(), pos, BlockRegistryHandler.CHERRY_LOG.get().defaultBlockState(), rand);
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
    }
}
