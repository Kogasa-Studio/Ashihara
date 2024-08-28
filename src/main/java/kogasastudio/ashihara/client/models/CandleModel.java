package kogasastudio.ashihara.client.models;// Made with Blockbench 3.5.4

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class CandleModel extends Model
{
    public final ModelPart candle;

    public CandleModel(ModelPart root)
    {
        super(t -> AshiharaRenderTypes.CANDLE);
        this.candle = root.getChild("candle");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("candle", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 8).addBox(-1.0F, -11.1F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.1F))
                .texOffs(8, 0).addBox(-1.0F, -13.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F))
                .texOffs(8, 4).addBox(-1.0F, -13.9F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int color)
    {
        this.candle.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, color);
    }
}