// Made with Blockbench 3.5.4
package kogasastudio.ashihara.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MillStoneModel extends Model
{
	private final ModelRenderer stone;
	private final ModelRenderer handle;

	public MillStoneModel()
	{
		super(RenderType::getEntitySolid);

		textureWidth = 32;
		textureHeight = 32;

		stone = new ModelRenderer(this);
		stone.setRotationPoint(0.0F, 19.0F, 0.0F);//19
		stone.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 4.0F, 5.0F, 0.0F, false);
		stone.setTextureOffset(0, 16).addBox(1.0F, -4.0F, -3.0F, 3.0F, 4.0F, 2.0F, 0.0F, false);
		stone.setTextureOffset(0, 16).addBox(-4.0F, -4.0F, -3.0F, 3.0F, 4.0F, 2.0F, 0.0F, false);
		stone.setTextureOffset(0, 9).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 1.0F, 0.0F, false);

		ModelRenderer edges = new ModelRenderer(this);
		edges.setRotationPoint(0.0F, 0.0F, 0.0F);
		stone.addChild(edges);
		edges.setTextureOffset(0, 14).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
		edges.setTextureOffset(0, 14).addBox(-4.0F, -5.0F, 3.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
		edges.setTextureOffset(12, 12).addBox(3.0F, -5.0F, -3.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
		edges.setTextureOffset(12, 12).addBox(-4.0F, -5.0F, -3.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);

		handle = new ModelRenderer(this);
		handle.setRotationPoint(0.0F, 24.0F, 0.0F);//24
		handle.setTextureOffset(18, 9).addBox(4.0F, -7.5F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
		handle.setTextureOffset(10, 19).addBox(5.5F, -12.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		stone.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		handle.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}