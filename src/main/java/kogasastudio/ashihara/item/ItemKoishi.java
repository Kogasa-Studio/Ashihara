package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.BlockWaterField;
import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

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
        TileEntity te = worldIn.getTileEntity(pos);

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
            else if (blockState.getBlock() == BlockRegistryHandler.BLOCK_WATER_FIELD.get())
            {
                worldIn.playSound(player, pos, SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, blockState.with(BlockWaterField.LEVEL, 6));
                return ActionResultType.SUCCESS;
            }
            else if (te != null)
            {
                CompoundNBT nbt = te.serializeNBT();
                player.sendMessage(new TranslationTextComponent((worldIn.isRemote() ? "client: " : "server: ") + nbt.toString()), UUID.randomUUID());
                return ActionResultType.SUCCESS;
            }
            else
            {
                Random rand = new Random();
                for (int i = 0;i<8;i+=1)
                {
                    worldIn.addParticle(new GenericParticleData(new Vector3d(0,0,0), 0, ParticleRegistryHandler.SAKURA.get()), (double)pos.getX() + 0.5D, (double)pos.getY() + 2.1D, (double)pos.getZ() + 0.5D, rand.nextFloat() / 2.0F, 0, rand.nextFloat() / 2.0F);
                }
                return ActionResultType.SUCCESS;
            }
        }
        else return ActionResultType.PASS;
    }
}
