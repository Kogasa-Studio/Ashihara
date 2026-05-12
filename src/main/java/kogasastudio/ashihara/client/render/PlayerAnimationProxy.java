package kogasastudio.ashihara.client.render;

import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import kogasastudio.ashihara.client.models.geo.PlayerProxyModel;
import kogasastudio.ashihara.client.render.state.CommonGeoRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import com.geckolib.animation.AnimationController;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlayerAnimationProxy
{
    public final Player player;
    public final PlayerProxyModel model;
    private PlayerModel playerModel;
    private boolean activated = false;

    public PlayerAnimationProxy(Player player)
    {
        this.player = player;
        this.model = new PlayerProxyModel(player);
    }

    /**
     * Actually triggers player animation.
     * @param modelConsumer play your animation via lambda here.
     */
    public void startProxy(BiConsumer<Player, PlayerProxyModel> modelConsumer)
    {
        this.activated = true;
        modelConsumer.accept(this.player, this.model);
    }

    public void endProxy()
    {
        this.activated = false;
    }

    public void proxy()
    {
        if (!activated) return;
        if (!checkAnimationStat())
        {
            endProxy();
            return;
        }
        PlayerModel playerModel = (PlayerModel) ((LivingEntityRenderer<?, ?, ?>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel();
        BakedGeoModel bakedGeoModel = model.getBakedModel(model.getModelResource(new CommonGeoRenderState()));
        bakedGeoModel.getBone("head").ifPresent(b -> {syncBones(b, playerModel.head);syncBones(b, playerModel.hat);});
        bakedGeoModel.getBone("body").ifPresent(b -> {syncBones(b, playerModel.body);syncBones(b, playerModel.jacket);});
        bakedGeoModel.getBone("left_arm").ifPresent(b -> {syncBones(b, playerModel.leftArm);syncBones(b, playerModel.leftSleeve);});
        bakedGeoModel.getBone("right_arm").ifPresent(b -> {syncBones(b, playerModel.rightArm);syncBones(b, playerModel.rightSleeve);});
        bakedGeoModel.getBone("left_leg").ifPresent(b -> {syncBones(b, playerModel.leftLeg);syncBones(b, playerModel.leftPants);});
        bakedGeoModel.getBone("right_leg").ifPresent(b -> {syncBones(b, playerModel.rightLeg);syncBones(b, playerModel.rightPants);});
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

    private void syncBones(GeoBone oriBone, ModelPart part)
    {
        if (oriBone.frameSnapshot == null) return;
        BoneSnapshot bone = oriBone.frameSnapshot;
        part.x += bone.getTranslateX();
        part.y -= bone.getTranslateY();
        part.z += bone.getTranslateZ();
        part.xRot += (bone.getRotX());
        part.yRot += (bone.getRotY());
        part.zRot += (bone.getRotZ());
        part.xScale *= bone.getScaleX();
        part.yScale *= bone.getScaleY();
        part.zScale *= bone.getScaleZ();
    }
}
