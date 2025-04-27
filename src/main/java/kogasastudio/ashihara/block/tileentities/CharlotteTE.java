package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.tileentities.util.RenderSwitch;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.registry.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.cache.object.GeoBone;

import static kogasastudio.ashihara.utils.OptionalUtil.getWithDefault;

public class CharlotteTE extends AshiharaMachineTE implements IRenderSwitchable
{
    public UIPanelModel model;
    public RenderSwitch renderSwitch = new RenderSwitch(this::initSwitch, this::hide, this::check);

    public CharlotteTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.CHARLOTTE_BE.get(), pos, state);
    }

    private void initSwitch(Player player)
    {
        this.model = new UIPanelModel(player);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.FLOAT, UIPanelModel.FLOAT);
        lerpScale(8, 8, player);
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

    public void lerpScale(float xScale, float yScale, Player player)
    {
        if (model == null) return;
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
                xScale,
                getWithDefault(1f, model.getBone("scale_sim"), GeoBone::getScaleY),
                yScale,
                1, 1,
                EasingType.EASE_IN_OUT_QUAD
            )
            .endBone()
            .build()
        );
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
    }

    @Override
    public RenderSwitch getSwitch()
    {
        return renderSwitch;
    }
}
