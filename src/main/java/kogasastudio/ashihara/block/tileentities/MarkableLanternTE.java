package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.client.render.AshiharaAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MarkableLanternTE extends AshiharaMachineTE
{
    //获取处理过的可用来渲染的rl
    private static final List<ResourceLocation> cookedTextures = AshiharaAtlas.ALL_ICON;
    //当前纹章在列表中的下标
    private int pointer = 0;

    public MarkableLanternTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.MARKABLE_LANTERN_TE.get(), pos, state);
    }

    public ResourceLocation getIcon()
    {
        return pointer >= cookedTextures.size() ? cookedTextures.get(0) : cookedTextures.get(pointer);
    }

    //用来循环更改纹章
    public void nextIcon()
    {
        if (pointer >= cookedTextures.size() - 1) pointer = 0;
        else pointer += 1;
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        pointer = pTag.getInt("pointer");
        super.loadAdditional(pTag, pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries)
    {
        compound.putInt("pointer", pointer);
        super.saveAdditional(compound, pRegistries);
    }
}
