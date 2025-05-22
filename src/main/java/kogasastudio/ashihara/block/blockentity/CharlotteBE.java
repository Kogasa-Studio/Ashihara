package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.block.blockentity.util.ToolTipController;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class CharlotteBE extends AshiharaMachineBE implements IRenderInWorldToolTip
{
    public UIPanelModel model;
    public ToolTipController toolTipController;

    public CharlotteBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.CHARLOTTE_BE.get(), pos, state);
    }

    @Override
    public void init(Player player)
    {
        this.model = new UIPanelModel(player);
        this.toolTipController = new ToolTipController<>(this, this.model);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
    }

    @Override
    public ToolTipController<?> getToolTipController()
    {
        return this.toolTipController;
    }
}
