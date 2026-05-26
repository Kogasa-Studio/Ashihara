package kogasastudio.ashihara.client.render;

import com.geckolib.animation.AnimationController;
import com.geckolib.animation.AnimationProcessor;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.util.ClientUtil;
import kogasastudio.ashihara.client.models.geo.PlayerProxyModel;
import kogasastudio.ashihara.client.render.state.CommonGeoRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class PlayerAnimationProxy
{
    public final Player player;
    public final PlayerProxyModel model;
    private boolean activated = false;
    private Map<String, BoneSnapshot> currentSnapshots;

    public PlayerAnimationProxy(Player player)
    {
        this.player = player;
        this.model = new PlayerProxyModel(player);
    }

    public void startProxy(BiConsumer<Player, PlayerProxyModel> modelConsumer)
    {
        this.activated = true;
        modelConsumer.accept(this.player, this.model);
    }

    public void endProxy()
    {
        this.activated = false;
    }

    public boolean isActivated()
    {
        return this.activated;
    }

    /**
     * Per-frame animation processing. Advances all GeckoLib animation controllers,
     * computes bone snapshots, and caches them for {@link #applyToModel}.
     */
    public void tick(float partialTick)
    {
        if (!activated) return;
        if (!checkAnimationStat()) { endProxy(); return; }

        GeoRenderState rs = new CommonGeoRenderState();
        long iid = this.model.hashCode();
        rs.addGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, iid);
        rs.addGeckolibData(DataTickets.ANIMATABLE_MANAGER,
            this.model.getAnimatableInstanceCache().getManagerForId(iid));
        rs.addGeckolibData(DataTickets.PARTIAL_TICK, partialTick);
        rs.addGeckolibData(DataTickets.TICK, ClientUtil.getCurrentTick());
        rs.addGeckolibData(DataTickets.ANIMATABLE_CLASS, PlayerProxyModel.class);

        AnimationProcessor.extractControllerStates(this.model, rs, this.model);

        ControllerState[] states = rs.getGeckolibData(DataTickets.ANIMATION_CONTROLLER_STATES);
        if (states == null || states.length == 0) return;

        BakedGeoModel baked = this.model.getBakedModel(this.model.getModelResource(rs));

        Map<String, BoneSnapshot> snapshotMap = new HashMap<>();
        BoneSnapshots snapshots = name -> Optional.ofNullable(
            snapshotMap.computeIfAbsent(name, k ->
                baked.getBone(k).map(BoneSnapshot::create).orElse(null)));

        for (ControllerState state : states)
        {
            AnimationProcessor.createBoneSnapshots(state, snapshots);
        }

        this.currentSnapshots = snapshotMap;
    }

    /**
     * Applies cached bone-snapshot transforms to the vanilla PlayerModel parts.
     */
    public void applyToModel(PlayerModel playerModel)
    {
        if (!activated || currentSnapshots == null) return;

        applySnapshot("head",       playerModel.head,       playerModel.hat);
        applySnapshot("body",       playerModel.body,       playerModel.jacket);
        applySnapshot("left_arm",   playerModel.leftArm,    playerModel.leftSleeve);
        applySnapshot("right_arm",  playerModel.rightArm,   playerModel.rightSleeve);
        applySnapshot("left_leg",   playerModel.leftLeg,    playerModel.leftPants);
        applySnapshot("right_leg",  playerModel.rightLeg,   playerModel.rightPants);
    }

    private void applySnapshot(String boneName, ModelPart part, ModelPart overlay)
    {
        BoneSnapshot snapshot = currentSnapshots.get(boneName);
        if (snapshot == null) return;
        addToPart(snapshot, part);
        //addToPart(snapshot, overlay);
    }

    private static void addToPart(BoneSnapshot snapshot, ModelPart part)
    {
        part.x += snapshot.getTranslateX();
        part.y -= snapshot.getTranslateY();
        part.z += snapshot.getTranslateZ();
        part.xRot += snapshot.getRotX();
        part.yRot += snapshot.getRotY();
        part.zRot += snapshot.getRotZ();
        part.xScale *= snapshot.getScaleX();
        part.yScale *= snapshot.getScaleY();
        part.zScale *= snapshot.getScaleZ();
    }

    private boolean checkAnimationStat()
    {
        boolean flag = false;
        for (AnimationController<?> controller : this.model.getAnimatableInstanceCache().getManagerForId(this.model.hashCode()).getAnimationControllers().values())
        {
            if (!(controller instanceof PlayerProxyModel.ProxiedPlayerAnimationController pController)) continue;
            if (pController.check(this.player)) flag = true;
            else pController.outro(this.model);
        }
        return flag;
    }
}
