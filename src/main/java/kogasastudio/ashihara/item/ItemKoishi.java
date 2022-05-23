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

import net.minecraft.item.Item.Properties;

public class ItemKoishi extends Item
{
    public ItemKoishi()
    {
        super(new Properties().tab(ItemGroup.TAB_MATERIALS));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        ItemStack item = context.getItemInHand();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        World worldIn = context.getLevel();
        TileEntity te = worldIn.getBlockEntity(pos);

        if (!item.isEmpty() && Objects.requireNonNull(player).mayUseItemAt(pos.relative(facing), facing, item))
        {
            BlockState blockState = worldIn.getBlockState(pos); //测试功能：右击草方块变钻石
            if (blockState.getBlock() == Blocks.GRASS_BLOCK)
            {
                worldIn.playSound(player, pos, SoundEvents.BAMBOO_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, Blocks.DIAMOND_BLOCK.defaultBlockState());
                if (!player.abilities.instabuild)
                {
                    item.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
            else if (blockState.getBlock() == BlockRegistryHandler.WATER_FIELD.get())
            {
                worldIn.playSound(player, pos, SoundEvents.BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, blockState.setValue(BlockWaterField.LEVEL, 6));
                return ActionResultType.SUCCESS;
            }
            else if (te != null)
            {
                CompoundNBT nbt = te.serializeNBT();
                player.sendMessage(new TranslationTextComponent((worldIn.isClientSide() ? "client: " : "server: ") + nbt.toString()), UUID.randomUUID());
                return ActionResultType.SUCCESS;
            }
            else
            {
                Random rand = context.getLevel().getRandom();
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
