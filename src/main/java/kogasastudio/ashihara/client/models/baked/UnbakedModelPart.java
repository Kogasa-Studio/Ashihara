package kogasastudio.ashihara.client.models.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.SimpleUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.Function;

/**
 * @author TT432
 */
public class UnbakedModelPart extends SimpleUnbakedGeometry<UnbakedModelPart>
{
    private final ModelPart modelPart;
    private final Material material;

    public UnbakedModelPart(ModelPart modelPart, Material material)
    {
        this.modelPart = modelPart;
        this.material = material;
    }

    @Override
    protected void addQuads(@NotNull IGeometryBakingContext owner, @NotNull IModelBuilder<?> modelBuilder, @NotNull ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform)
    {
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();

        poseStack.mulPose(modelTransform.getRotation().getMatrix());

        bakeModelPart(modelBuilder, poseStack, modelPart, spriteGetter.apply(material));

        poseStack.popPose();
    }

    private void bakeModelPart(IModelBuilder<?> builder, PoseStack poseStack, ModelPart modelPart, TextureAtlasSprite texture)
    {
        poseStack.pushPose();

        PoseStack.Pose last = poseStack.last();
        PartPose initialPose = modelPart.getInitialPose();
        Matrix4f pose = last.pose();
        pose.translate(initialPose.x, initialPose.y, initialPose.z);
        pose.rotateZYX(initialPose.zRot, initialPose.yRot, initialPose.xRot);
        last.normal().rotateZYX(initialPose.zRot, initialPose.yRot, initialPose.xRot);

        for (ModelPart.Cube cube : modelPart.cubes)
        {
            for (ModelPart.Polygon polygon : cube.polygons)
            {
                builder.addUnculledFace(bakePolygon(last, polygon, texture));
            }
        }

        for (ModelPart value : modelPart.children.values())
        {
            bakeModelPart(builder, poseStack, value, texture);
        }

        poseStack.popPose();
    }

    private BakedQuad bakePolygon(PoseStack.Pose last, ModelPart.Polygon polygon, TextureAtlasSprite texture)
    {
        Vector3f normal = polygon.normal;
        QuadBakingVertexConsumer buffered = newBuffer(texture, normal);

        for (ModelPart.Vertex vertex : polygon.vertices)
        {
            var tPosition = last.pose().transformPosition(vertex.pos, new Vector3f()).div(16);
            var tNormal = last.normal().transform(normal, new Vector3f()).div(16);
            var uv = mapUV(vertex.u, vertex.v, texture.getU0(), texture.getV0(), texture.getU1(), texture.getV1());

            buffered.addVertex(tPosition.x, tPosition.y, tPosition.z,
                    0xFF_FF_FF_FF,
                    uv.x, uv.y,
                    OverlayTexture.NO_OVERLAY, 0,
                    tNormal.x, tNormal.y, tNormal.z);
        }

        return buffered.bakeQuad();
    }

    private static QuadBakingVertexConsumer newBuffer(TextureAtlasSprite texture, Vector3f normal)
    {
        QuadBakingVertexConsumer consumer = new QuadBakingVertexConsumer();
        consumer.setSprite(texture);
        consumer.setShade(true);
        Direction nearest = Direction.getNearest(normal.x, normal.y, normal.z);
        consumer.setDirection(nearest);
        return consumer;
    }

    private static Vector2f mapUV(float su, float sv, float u0, float v0, float u1, float v1)
    {
        float u = su * (u1 - u0) + u0;
        float v = sv * (v1 - v0) + v0;
        return new Vector2f(u, v);
    }
}