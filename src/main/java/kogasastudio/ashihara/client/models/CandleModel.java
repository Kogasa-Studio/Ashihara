package kogasastudio.ashihara.client.models;// Made with Blockbench 3.5.4

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CandleModel extends Model
{
	private final ModelRenderer candle;

	public CandleModel()
	{
		super(RenderType::entitySolid);
		texWidth = 16;
		texHeight = 16;

		candle = new ModelRenderer(this);
		candle.setPos(0.0F, 24.0F, 0.0F);
		candle.texOffs(0, 0).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
		candle.texOffs(6, 8).addBox(-1.0F, -11.1F, -1.0F, 2.0F, 3.0F, 2.0F, 0.1F, false);
		candle.texOffs(8, 0).addBox(-1.0F, -12.9F, -1.0F, 2.0F, 2.0F, 2.0F, 0.2F, false);
		candle.texOffs(8, 4).addBox(-1.0F, -13.7F, -1.0F, 2.0F, 1.0F, 2.0F, 0.3F, false);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		candle.render(matrixStack, buffer, packedLight, packedOverlay);
	}
}