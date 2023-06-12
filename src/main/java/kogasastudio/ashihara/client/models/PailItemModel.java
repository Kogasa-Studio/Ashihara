package kogasastudio.ashihara.client.models;
// Made with Blockbench 4.2.5

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class PailItemModel extends Model
{
    private final ModelPart edges;
    private final ModelPart handle;
    private final ModelPart bone;

    public PailItemModel(ModelPart root)
    {
        super(RenderType::entitySolid);
        this.edges = root.getChild("edges");
        this.handle = root.getChild("handle");
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("edges", CubeListBuilder.create().texOffs(28, 9).addBox(2.5F, -9.5F, -13.5F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(2.5F, -9.5F, -12.5F, 1.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(35, 35).addBox(2.5F, -9.5F, -4.5F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 18).addBox(11.5F, -9.5F, -12.5F, 1.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 23.5F, 8.5F));

        partdefinition.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(0, 9).addBox(12.0F, -8.0F, -9.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.0F, -8.0F, -9.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 10).addBox(4.0F, -7.5F, -8.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 16.0F, 8.0F));

        partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.5F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        edges.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        handle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}