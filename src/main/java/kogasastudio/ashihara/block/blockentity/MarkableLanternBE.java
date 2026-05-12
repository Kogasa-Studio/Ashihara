package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.client.render.AshiharaAtlas;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class MarkableLanternBE extends AshiharaMachineBE
{
    //获取处理过的可用来渲染的rl
    private static final List<Identifier> cookedTextures = AshiharaAtlas.ALL_ICON;
    //当前纹章在列表中的下标
    private int pointer = 0;

    public MarkableLanternBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.MARKABLE_LANTERN_BE.get(), pos, state);
    }

    public Identifier getIcon()
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
    protected void loadAdditional(ValueInput input)
    {
        pointer = input.getIntOr("pointer", 0);
        super.loadAdditional(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        output.putInt("pointer", pointer);
        super.saveAdditional(output);
    }
}
