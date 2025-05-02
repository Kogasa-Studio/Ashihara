package kogasastudio.ashihara.block.tileentities.util;

import kogasastudio.ashihara.block.tileentities.IRenderInWorldToolTip;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.cache.object.GeoBone;

import static kogasastudio.ashihara.utils.OptionalUtil.getWithDefault;

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
        model.stopTriggeredAnim(player, model.hashCode(), UIPanelModel.OUTRO, UIPanelModel.OUTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.FLOAT, UIPanelModel.FLOAT);
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

    public void reScale(float scaleX, float scaleY, Player player)
    {
        model.triggerInternal
        (
            player, model.hashCode(),
            new InternalControlGeoModel.InternalAnimationBuilder("scale", Animation.LoopType.HOLD_ON_LAST_FRAME)
            .startBone("scale_sim")
            .lerpSingle
            (
                InternalControlGeoModel.InternalAnimationBuilder.VarType.SCALE,
                40,
                getWithDefault(1f, model.getBone("scale_sim"), GeoBone::getScaleX),
                scaleX,
                getWithDefault(1f, model.getBone("scale_sim"), GeoBone::getScaleY),
                scaleY,
                1, 1,
                EasingType.EASE_IN_OUT_QUAD
            )
            .endBone()
            .build()
        );
    }
}
