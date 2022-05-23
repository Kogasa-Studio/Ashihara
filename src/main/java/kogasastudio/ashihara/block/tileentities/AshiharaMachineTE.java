package kogasastudio.ashihara.block.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public class AshiharaMachineTE extends TileEntity
{
    public AshiharaMachineTE(TileEntityType<?> type) {super(type);}

    @Override
    public CompoundNBT getUpdateTag() {return this.save(new CompoundNBT());}

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {this.load(state, tag);}

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {return new SUpdateTileEntityPacket(this.worldPosition, -1, this.save(new CompoundNBT()));}

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {this.load(this.getBlockState(), pkt.getTag());}
}
