// Made with Blockbench 4.2.5
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports
package kogasastudio.ashihara.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class MillStoneModel extends Model
{
    private final ModelPart stone;
    private final ModelPart handle;

    public MillStoneModel(ModelPart root)
    {
        super(RenderType::entitySolid);
        this.stone = root.getChild("stone");
        this.handle = root.getChild("handle");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("stone", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(1.0F, -4.0F, -3.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-4.0F, -4.0F, -3.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(-4.0F, -5.0F, 3.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 12).addBox(-4.0F, -5.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(12, 12).addBox(3.0F, -5.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));

        partdefinition.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(18, 9).addBox(4.0F, 17.0F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 19).addBox(5.5F, 12.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        stone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        handle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}