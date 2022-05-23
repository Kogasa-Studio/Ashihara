package kogasastudio.ashihara.block.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AshiharaMachineTE extends BlockEntity {

    public AshiharaMachineTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        var result = new CompoundTag();
        this.saveAdditional(result);
        return result;
    }

    // todo handleUpdateTag / getPacket / handlePacket 是默认实现，所以删了
}
