package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemExmpleContainer;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFallenSakura extends Block
{
    public BlockFallenSakura()
    {
        super
        (
            Properties.create(Material.PLANTS)
            .hardnessAndResistance(0.1F)
            .sound(SoundType.PLANT)
            .notSolid()
            .doesNotBlockMovement()
        );
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext)
    {
        return !(useContext.getItem().getItem() == ItemRegistryHandler.ITEM_FALLEN_SAKURA.get());
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!player.isCreative())
        {
            ItemStack itemM = player.getHeldItem(Hand.MAIN_HAND);
            ItemStack itemO = player.getHeldItem(Hand.OFF_HAND);
            Random rand = new Random();
            if (itemM.getItem() instanceof ShearsItem || itemO.getItem() instanceof ShearsItem)
            {
                worldIn.destroyBlock(pos, false);
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 0.5, pos.getZ(), new ItemStack(ItemRegistryHandler.ITEM_FALLEN_SAKURA.get(), rand.nextInt(2) + 1));
            }
            else if
            (EnchantmentHelper.getEnchantments(itemM).containsValue(Enchantments.SILK_TOUCH)
            ||EnchantmentHelper.getEnchantments(itemO).containsValue(Enchantments.SILK_TOUCH))
            {
                worldIn.destroyBlock(pos, false);
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 0.5, pos.getZ(), new ItemStack(ItemRegistryHandler.ITEM_FALLEN_SAKURA.get(), rand.nextInt(2) + 1));
            }
            else
            {
                worldIn.destroyBlock(pos, false);
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 0.5, pos.getZ(), new ItemStack(ItemExmpleContainer.SAKURA, rand.nextInt(2) + 1));
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return !worldIn.getBlockState(pos.down()).matchesBlock(Blocks.AIR);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }
}
