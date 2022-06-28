package kogasastudio.ashihara.client.models;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PailItemModel extends Model
{
	private final ModelRenderer edges;
	private final ModelRenderer handle;
	private final ModelRenderer bone;

	public PailItemModel()
	{
		super(RenderType::getEntitySolid);

		textureWidth = 64;
		textureHeight = 64;

		edges = new ModelRenderer(this);
		edges.setRotationPoint(-7.5F, 23.5F, 8.5F);
		edges.setTextureOffset(28, 9).addBox(2.5F, -9.5F, -13.5F, 10.0F, 10.0F, 1.0F, 0.0F, false);
		edges.setTextureOffset(0, 9).addBox(2.5F, -9.5F, -12.5F, 1.0F, 10.0F, 8.0F, 0.0F, false);
		edges.setTextureOffset(35, 35).addBox(2.5F, -9.5F, -4.5F, 10.0F, 10.0F, 1.0F, 0.0F, false);
		edges.setTextureOffset(18, 18).addBox(11.5F, -9.5F, -12.5F, 1.0F, 10.0F, 8.0F, 0.0F, false);

		handle = new ModelRenderer(this);
		handle.setRotationPoint(-8.0F, 16.0F, 8.0F);
		handle.setTextureOffset(0, 9).addBox(12.0F, -8.0F, -9.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
		handle.setTextureOffset(0, 0).addBox(3.0F, -8.0F, -9.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
		handle.setTextureOffset(10, 10).addBox(4.0F, -7.5F, -8.5F, 8.0F, 1.0F, 1.0F, 0.0F, false);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone.setTextureOffset(0, 0).addBox(-4.0F, -1.5F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		edges.render(matrixStack, buffer, packedLight, packedOverlay);
		handle.render(matrixStack, buffer, packedLight, packedOverlay);
		bone.render(matrixStack, buffer, packedLight, packedOverlay);
	}
}