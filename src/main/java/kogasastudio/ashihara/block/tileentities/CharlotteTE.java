package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.registry.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.cache.object.GeoBone;

import static kogasastudio.ashihara.utils.OptionalUtil.getWithDefault;

public class CharlotteTE extends AshiharaMachineTE
{
    private boolean doRender = false;
    public boolean forHiding = false;

    public UIPanelModel model;

    public CharlotteTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.CHARLOTTE_BE.get(), pos, state);
    }

    public void switchRender(Player player)
    {
        if (!this.doRender) init(player);
        else hide(player);
    }

    public boolean doRender() {return doRender;}

    public void disableRender()
    {
        this.doRender = false;
        this.forHiding = false;
    }

    public void init(Player player)
    {
        this.model = new UIPanelModel(player);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.FLOAT, UIPanelModel.FLOAT);
        lerpScale(8, 8, player);
        this.doRender = true;
        this.forHiding = false;
    }

    public void hide(Player player)
    {
        model.stopTriggeredAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.INTRO);
        model.triggerAnim(player, model.hashCode(), UIPanelModel.INTRO, UIPanelModel.OUTRO);
        this.forHiding = true;
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

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
    }
}
