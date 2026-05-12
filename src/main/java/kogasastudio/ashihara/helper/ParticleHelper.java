package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.particles.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ParticleHelper
{
    /**
     * 在给定坐标生成破坏给定方块的粒子
     * @param state 给定方块
     * @param amount 预计生成数量，一般为10
     */
    public static void spawnBlockDestruction(Level level, double x, double y, double z, BlockState state, int amount)
    {
        for (int i = 0; i < amount; i += 1)
        {
            level.addParticle
            (
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                x,
                y,
                z,
                ((double) level.getRandom().nextFloat() - 0.5D) * 0.2D,
                ((double) level.getRandom().nextFloat() - 0.5D) * 0.2D,
                ((double) level.getRandom().nextFloat() - 0.5D) * 0.2D
            );
        }
    }

    public static void spawnItemStackDestruction(Level level, ItemStack stack, Vec3 pos, int amount, double xMul, double yMul, double zMul)
    {
        RandomSource random = level.getRandom();
        ParticleOptions data = stack.getItem() instanceof BlockItem
        ? new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock().defaultBlockState())
        : new ItemParticleOption(ParticleTypes.ITEM, stack.getItem());
        for (int i = 0; i < amount; i += 1)
        {
            level.addParticle
            (
                data,
                pos.x(),
                pos.y(),
                pos.z(),
                ((double) random.nextFloat() - 0.5D) * 0.2D * xMul,
                ((double) random.nextFloat()) * 0.2D * yMul,
                ((double) random.nextFloat() - 0.5D) * 0.2D * zMul
            );
        }
    }

    public static void spawnItemStackDestruction(Level level, ItemStack stack, Vec3 pos, int amount, double yMul)
    {
        spawnItemStackDestruction(level, stack, pos, amount, 1, yMul, 1);
    }

    public static void spawnItemStackDestruction(Level level, ItemStack stack, Vec3 pos, int amount)
    {
        spawnItemStackDestruction(level, stack, pos, amount, 1);
    }

    public static void drawAABB(AABB aabb, Level level)
    {
        for (double i = aabb.minX; i < aabb.maxX; i += 0.02)
        {
            double offset = Ashihara.RANDOM.nextIntBetweenInclusive(-90, 90) / 1000d;
            level.addParticle(new DustParticleOptions(0x196884, 1f), i + offset, aabb.minY + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), i + offset, aabb.maxY + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), i + offset, aabb.minY + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), i + offset, aabb.maxY + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
        }
        for (double i = aabb.minY; i < aabb.maxY; i += 0.02)
        {
            double offset = Ashihara.RANDOM.nextIntBetweenInclusive(-90, 90) / 1000d;
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.minX + offset, i + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.maxX + offset, i + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.minX + offset, i + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.maxX + offset, i + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
        }
        for (double i = aabb.minZ; i < aabb.maxZ; i += 0.02)
        {
            double offset = Ashihara.RANDOM.nextIntBetweenInclusive(-90, 90) / 1000d;
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.minX + offset, aabb.minY + offset, i + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.maxX + offset, aabb.minY + offset, i + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.minX + offset, aabb.maxY + offset, i + offset, 0.0D, 0.0D, 0.0D);
            level.addParticle(new DustParticleOptions(0x196884, 1f), aabb.maxX + offset, aabb.maxY + offset, i + offset, 0.0D, 0.0D, 0.0D);
        }
    }
}
