package kogasastudio.ashihara.helper;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 存放一些关于粒子的实用方法
 */
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
                            ((double) level.random.nextFloat() - 0.5D) * 0.2D,
                            ((double) level.random.nextFloat() - 0.5D) * 0.2D,
                            ((double) level.random.nextFloat() - 0.5D) * 0.2D
                    );
        }
    }
}
