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

public class ItemMinatoAqua extends Item
{
    public ItemMinatoAqua()
    {
        super
        (
            new Properties()
            .group(ItemGroup.MATERIALS)
            .food(new Food.Builder().hunger(8).build())
        );
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        TileEntity te = world.getTileEntity(pos);
        PlayerEntity playerIn = context.getPlayer();
        ItemStack item = context.getItem();
        Direction direction = context.getFace();
        Tree tree = new CherryBlossomTree();
        Random rand = context.getWorld().getRandom();

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
            te.markDirty();
            return ActionResultType.SUCCESS;
        }

        if (!item.isEmpty() && Objects.requireNonNull(playerIn).canPlayerEdit(pos.offset(direction), direction, item) && !world.isRemote())
        {
            ServerWorld worldIn = (ServerWorld)world;
            tree.attemptGrowTree(worldIn, worldIn.getChunkProvider().getChunkGenerator(), pos, BlockRegistryHandler.CHERRY_LOG.get().getDefaultState(), rand);
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
    }
}
