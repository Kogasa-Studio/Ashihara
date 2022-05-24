package kogasastudio.ashihara.block.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
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

    // todo 将子类的 sync 提上来
    protected final void sync() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        //                                                                  todo create 的单参调用 getUpdateTag
        var packet = ClientboundBlockEntityDataPacket.create(this);
        ((ServerLevel) this.level).getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition), false)
                .forEach(k -> k.connection.send(packet));
    }

    // todo handleUpdateTag / handlePacket 是默认实现，所以删了

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
