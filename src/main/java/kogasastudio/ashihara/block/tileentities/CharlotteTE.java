package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.tileentities.util.ToolTipController;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.registry.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class CharlotteTE extends AshiharaMachineTE implements IRenderInWorldToolTip
{
    public UIPanelModel model;
    public ToolTipController toolTipController;

    public CharlotteTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.CHARLOTTE_BE.get(), pos, state);
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
