package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MarkableLanternTE extends AshiharaMachineTE
{
    public MarkableLanternTE() {super(TERegistryHandler.MARKABLE_LANTERN_TE.get());}

    //获取所有纹章的rl列表
    private static final ArrayList<ResourceLocation> textures = new ArrayList<>
            (Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/icons/", s -> s.endsWith(".png")));
    //获取处理过的可用来渲染的rl
    private static final ArrayList<ResourceLocation> cookedTextures = RenderHelper.cookTextureRLs(textures);
    //当前纹章在列表中的下标
    private int pointer = 0;

    public ResourceLocation getIcon()
    {
        return cookedTextures.get(pointer);
    }

    //用来循环更改纹章
    public void nextIcon()
    {
        if (pointer == cookedTextures.size() - 1) pointer = 0; else pointer += 1;
        markDirty();
    }

    //数据同步保存
    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        pointer = nbt.getInt("pointer");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putInt("pointer", pointer);
        return super.write(compound);
    }

    @Override
    public CompoundNBT getUpdateTag() {return this.write(new CompoundNBT());}

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {this.read(state, tag);}

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {return new SUpdateTileEntityPacket(this.pos, -1, this.write(new CompoundNBT()));}

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {this.read(this.getBlockState(), pkt.getNbtCompound());}
}
