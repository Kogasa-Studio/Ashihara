package kogasastudio.ashihara.block.tileentities.util;

import kogasastudio.ashihara.block.tileentities.IRenderInWorldToolTip;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animation.AnimationController;

public class ToolTipController<B extends BlockEntity & IRenderInWorldToolTip>
{
    protected final B be;
    protected final UIPanelModel model;
    protected final RenderSwitch renderSwitch = new RenderSwitch(this::initSwitch, this::hide, this::check);
    protected BlockEntityRenderer<B> renderer;

    public RenderSwitch getRenderSwitch()
    {
        return renderSwitch;
    }

    public ToolTipController(B be, UIPanelModel model)
    {
        this.be = be;
        this.model = model;
    }

    private void initSwitch(Player player)
    {
        model.triggerAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.FLOAT, UIPanelModel.FLOAT);
        reScale(player);
    }

    private void hide(Player player)
    {
        model.stopTriggeredAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.OUTRO, UIPanelModel.OUTRO);
    }

    private boolean check()
    {
        if (this.model != null && this.model.getBone("main").isPresent())
        {
            return !this.model.getAnimatableInstanceCache().getManagerForId(this.model.hashCode()).getAnimationControllers().get(UIPanelModel.OUTRO).getAnimationState().equals(AnimationController.State.PAUSED);//.getBone("main").get().getScaleX() > 0.01;
        }
        return false;
    }

    public void reScale(Player player)
    {
        this.be.reScale(player);
    }
}
