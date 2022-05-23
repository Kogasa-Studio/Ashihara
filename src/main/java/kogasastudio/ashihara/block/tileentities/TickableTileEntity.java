package kogasastudio.ashihara.block.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author DustW
 **/
public interface TickableTileEntity {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getTicker() {
        return (Level level, BlockPos bp, BlockState bs, T be) -> ((TickableTileEntity) be).tick();
    }

    static <T extends BlockEntity> BlockEntityTicker<T> orEmpty(BlockEntityType<?> type1, BlockEntityType<?> type2) {
        return type1 == type2 ? getTicker() : null;
    }
}
