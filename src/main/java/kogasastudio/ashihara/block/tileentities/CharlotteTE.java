package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.registry.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.cache.object.GeoBone;

import static kogasastudio.ashihara.utils.OptionalUtil.getWithDefault;

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

    public void lerpScale(float xScale, float yScale, Player player)
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
}
