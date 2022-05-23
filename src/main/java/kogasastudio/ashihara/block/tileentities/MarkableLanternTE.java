package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class MarkableLanternTE extends AshiharaMachineTE
{
    public MarkableLanternTE(BlockPos pos, BlockState state) {
        super(TERegistryHandler.MARKABLE_LANTERN_TE.get(), pos, state);
    }

    //获取所有纹章的rl列表
    private static final ArrayList<ResourceLocation> textures = new ArrayList<>
            (Minecraft.getInstance().getResourceManager().listResources("textures/icons/", s -> s.endsWith(".png")));
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
        setChanged();
    }

    @Override
    public void load(CompoundTag nbt) {
        pointer = nbt.getInt("pointer");
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putInt("pointer", pointer);
        super.saveAdditional(compound);
    }
}
