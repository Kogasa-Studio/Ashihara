package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.registry.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class CharlotteTE extends AshiharaMachineTE
{
    private float progress = 0f;
    private boolean doRender = false;

    public UIPanelModel model = new UIPanelModel();
    public CharlotteTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.CHARLOTTE_BE.get(), pos, state);
    }

    public void switchRender(Player player)
    {
        if (!this.doRender) init(player);
        this.doRender = !doRender;
    }

    public boolean doRender() {return doRender;}

    public void init(Player player)
    {
        model.stopTriggeredAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
    }
}
