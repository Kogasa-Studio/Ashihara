package kogasastudio.ashihara.block.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AshiharaMachineTE extends BlockEntity {

    public AshiharaMachineTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        var result = new CompoundTag();
        this.saveAdditional(result, registries);
        return result;
    }

    /**
     * Send update tag to client.
     */
    protected final void sync() {
        if (this.level == null
                || this.level.isClientSide()
                || !(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        var packet = this.getUpdatePacket();
        serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition), false)
                .forEach(k -> k.connection.send(packet));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
