package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.BlockWaterField;
import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ItemKoishi extends Item {
    public ItemKoishi() {
        super(new Properties().tab(CreativeModeTab.TAB_MATERIALS));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack item = context.getItemInHand();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Level worldIn = context.getLevel();
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!item.isEmpty() && Objects.requireNonNull(player).mayUseItemAt(pos.relative(facing), facing, item)) {
            BlockState blockState = worldIn.getBlockState(pos); //测试功能：右击草方块变钻石
            if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
                worldIn.playSound(player, pos, SoundEvents.BAMBOO_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, Blocks.DIAMOND_BLOCK.defaultBlockState());
                if (!player.getAbilities().instabuild) {
                    item.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (blockState.getBlock() == BlockRegistryHandler.WATER_FIELD.get()) {
                worldIn.playSound(player, pos, SoundEvents.BAMBOO_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockAndUpdate(pos, blockState.setValue(BlockWaterField.LEVEL, 6));
                return InteractionResult.SUCCESS;
            } else if (te != null) {
                CompoundTag nbt = te.serializeNBT();
                player.sendMessage(new TranslatableComponent((worldIn.isClientSide() ? "client: " : "server: ") + nbt.toString()), UUID.randomUUID());
                return InteractionResult.SUCCESS;
            } else {
                Random rand = context.getLevel().getRandom();
                for (int i = 0; i < 8; i += 1) {
                    worldIn.addParticle(new GenericParticleData(new Vec3(0, 0, 0), 0, ParticleRegistryHandler.SAKURA.get()), (double) pos.getX() + 0.5D, (double) pos.getY() + 2.1D, (double) pos.getZ() + 0.5D, rand.nextFloat() / 2.0F, 0, rand.nextFloat() / 2.0F);
                }
                return InteractionResult.SUCCESS;
            }
        } else return InteractionResult.PASS;
    }
}
