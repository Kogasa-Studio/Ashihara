// Made with Blockbench 3.5.4
package kogasastudio.ashihara.client.models;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SujikabutoModel extends BipedModel<LivingEntity>
{
	private final ModelRenderer bo;
	private final ModelRenderer mayubisashi;
	private final ModelRenderer ni;
	private final ModelRenderer san;
	private final ModelRenderer jikoro_1;
	private final ModelRenderer jikoro_2;
	private final ModelRenderer e;
	private final ModelRenderer w;
	private final ModelRenderer s;
	private final ModelRenderer jikoro_3;
	private final ModelRenderer e2;
	private final ModelRenderer w2;
	private final ModelRenderer s2;
	private final ModelRenderer fukigae;
	private final ModelRenderer ichi;
	private final ModelRenderer shi;
	private final ModelRenderer kou;
	private final ModelRenderer rogu;
	private final ModelRenderer nana;
	private final ModelRenderer hachi;
	private final ModelRenderer fukigae2;
	private final ModelRenderer kyu;
	private final ModelRenderer ju;
	private final ModelRenderer ju_ichi;
	private final ModelRenderer ju_ni;
	private final ModelRenderer ju_san;
	private final ModelRenderer ju_yon;
	private final ModelRenderer yui;
	private final ModelRenderer ju_kou;
	private final ModelRenderer ju_rogu;
	private final ModelRenderer tatsumono;

	public SujikabutoModel(float modelsize)
	{

		super(modelsize);

		this.textureWidth = 128;
		this.textureHeight = 128;
		this.bipedHead = new ModelRenderer(this);

		this.bipedHeadwear = new ModelRenderer(this);
		this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);

		bo = new ModelRenderer(this);
		bo.setRotationPoint(0.0F, 25.0F, 0.0F);
		bo.setTextureOffset(46, 23).addBox(-5.0F, -33.0F, -5.0F, 10.0F, 3.0F, 1.0F, 0.0F, false);
		bo.setTextureOffset(46, 12).addBox(4.0F, -33.0F, -4.0F, 1.0F, 3.0F, 8.0F, 0.0F, false);
		bo.setTextureOffset(20, 40).addBox(-5.0F, -33.0F, -4.0F, 1.0F, 3.0F, 8.0F, 0.0F, false);
		bo.setTextureOffset(32, 9).addBox(-5.0F, -33.0F, 4.0F, 10.0F, 3.0F, 1.0F, 0.0F, false);
		bo.setTextureOffset(0, 16).addBox(-4.0F, -36.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);
		bo.setTextureOffset(11, 37).addBox(-2.0F, -37.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);

		mayubisashi = new ModelRenderer(this);
		mayubisashi.setRotationPoint(-0.5F, 17.75F, -6.25F);
		setRotationAngle(mayubisashi, 0.5236F, 0.0F, 0.0F);
		mayubisashi.setTextureOffset(24, 20).addBox(-1.5F, -19.6026F, 10.5F, 4.0F, 1.0F, 3.0F, 0.0F, false);

		ni = new ModelRenderer(this);
		ni.setRotationPoint(0.0F, 0.0F, 0.0F);
		mayubisashi.addChild(ni);
		setRotationAngle(ni, 0.0F, 0.6109F, -0.0524F);
		ni.setTextureOffset(23, 37).addBox(-10.4271F, -19.5526F, 8.3178F, 4.0F, 1.0F, 2.0F, 0.0F, false);

		san = new ModelRenderer(this);
		san.setRotationPoint(1.0F, 0.0F, 0.0F);
		mayubisashi.addChild(san);
		setRotationAngle(san, 0.0F, -0.6109F, 0.0524F);
		san.setTextureOffset(32, 13).addBox(6.4271F, -19.5526F, 8.3178F, 4.0F, 1.0F, 2.0F, 0.0F, false);

		jikoro_1 = new ModelRenderer(this);
		jikoro_1.setRotationPoint(0.0F, 24.0F, 0.0F);
		jikoro_1.setTextureOffset(46, 32).addBox(5.0F, -29.5F, -2.0F, 1.0F, 3.0F, 7.0F, 0.0F, false);
		jikoro_1.setTextureOffset(31, 48).addBox(-6.0F, -29.5F, -2.0F, 1.0F, 3.0F, 7.0F, 0.0F, false);
		jikoro_1.setTextureOffset(32, 5).addBox(-6.0F, -29.5F, 5.0F, 12.0F, 3.0F, 1.0F, 0.0F, false);

		jikoro_2 = new ModelRenderer(this);
		jikoro_2.setRotationPoint(0.0F, 26.0F, 0.0F);


		e = new ModelRenderer(this);
		e.setRotationPoint(7.0F, -4.0F, 0.0F);
		jikoro_2.addChild(e);
		setRotationAngle(e, 0.0F, 0.0F, -0.1745F);
		e.setTextureOffset(0, 37).addBox(3.4939F, -25.1506F, -2.0F, 1.0F, 3.0F, 9.0F, 0.0F, false);

		w = new ModelRenderer(this);
		w.setRotationPoint(-6.75F, -5.0F, 2.0F);
		jikoro_2.addChild(w);
		setRotationAngle(w, 0.0F, 0.0F, 0.1745F);
		w.setTextureOffset(35, 20).addBox(-4.4939F, -24.1506F, -4.0F, 1.0F, 3.0F, 9.0F, 0.0F, false);

		s = new ModelRenderer(this);
		s.setRotationPoint(0.0F, -4.0F, 6.9F);
		jikoro_2.addChild(s);
		setRotationAngle(s, 0.1745F, 0.0F, 0.0F);
		s.setTextureOffset(24, 16).addBox(-7.0F, -25.1506F, 3.4939F, 14.0F, 3.0F, 1.0F, 0.0F, false);

		jikoro_3 = new ModelRenderer(this);
		jikoro_3.setRotationPoint(0.0F, 26.0F, 0.0F);


		e2 = new ModelRenderer(this);
		e2.setRotationPoint(7.0F, -4.0F, 0.0F);
		jikoro_3.addChild(e2);
		setRotationAngle(e2, 0.0F, 0.0F, -0.1745F);
		e2.setTextureOffset(35, 35).addBox(4.2477F, -23.194F, -1.0F, 1.0F, 4.0F, 9.0F, 0.0F, false);

		w2 = new ModelRenderer(this);
		w2.setRotationPoint(-6.75F, -5.0F, 2.0F);
		jikoro_3.addChild(w2);
		setRotationAngle(w2, 0.0F, 0.0F, 0.1745F);
		w2.setTextureOffset(24, 24).addBox(-5.2477F, -22.194F, -3.0F, 1.0F, 4.0F, 9.0F, 0.0F, false);

		s2 = new ModelRenderer(this);
		s2.setRotationPoint(0.0F, -4.0F, 6.9F);
		jikoro_3.addChild(s2);
		setRotationAngle(s2, 0.1745F, 0.0F, 0.0F);
		s2.setTextureOffset(24, 0).addBox(-8.0F, -23.194F, 4.2477F, 16.0F, 4.0F, 1.0F, 0.0F, false);

		fukigae = new ModelRenderer(this);
		fukigae.setRotationPoint(0.0F, 24.0F, 0.0F);


		ichi = new ModelRenderer(this);
		ichi.setRotationPoint(5.5F, -5.0F, -2.5F);
		fukigae.addChild(ichi);
		setRotationAngle(ichi, 0.0F, 0.0F, -0.1745F);
		ichi.setTextureOffset(0, 56).addBox(3.4939F, -24.1506F, -1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		shi = new ModelRenderer(this);
		shi.setRotationPoint(5.5F, -5.0F, -4.5F);
		fukigae.addChild(shi);
		setRotationAngle(shi, 0.0F, -0.3491F, -0.3491F);
		shi.setTextureOffset(6, 56).addBox(6.6924F, -23.2377F, -3.9038F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		kou = new ModelRenderer(this);
		kou.setRotationPoint(5.5F, -5.0F, -4.5F);
		fukigae.addChild(kou);
		setRotationAngle(kou, 0.0F, -0.8727F, -0.5236F);
		kou.setTextureOffset(8, 68).addBox(5.9168F, -21.6216F, -10.1973F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		rogu = new ModelRenderer(this);
		rogu.setRotationPoint(5.5F, -5.0F, -4.5F);
		fukigae.addChild(rogu);
		setRotationAngle(rogu, 0.0F, -1.309F, -0.6981F);
		rogu.setTextureOffset(4, 68).addBox(1.7992F, -19.337F, -15.5277F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		nana = new ModelRenderer(this);
		nana.setRotationPoint(5.5F, -5.0F, -4.5F);
		fukigae.addChild(nana);
		setRotationAngle(nana, 0.0F, -1.6581F, -0.7854F);
		nana.setTextureOffset(0, 68).addBox(-3.8311F, -17.9308F, -17.4709F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		hachi = new ModelRenderer(this);
		hachi.setRotationPoint(5.5F, -5.0F, -4.5F);
		fukigae.addChild(hachi);
		setRotationAngle(hachi, 0.0F, -2.0944F, -0.8029F);
		hachi.setTextureOffset(12, 56).addBox(-10.9759F, -17.6191F, -16.4231F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		fukigae2 = new ModelRenderer(this);
		fukigae2.setRotationPoint(0.0F, 24.0F, 0.0F);


		kyu = new ModelRenderer(this);
		kyu.setRotationPoint(-5.5F, -5.0F, -2.5F);
		fukigae2.addChild(kyu);
		setRotationAngle(kyu, 0.0F, 0.0F, 0.1745F);
		kyu.setTextureOffset(18, 56).addBox(-4.4939F, -24.1506F, -1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		ju = new ModelRenderer(this);
		ju.setRotationPoint(-5.5F, -5.0F, -4.5F);
		fukigae2.addChild(ju);
		setRotationAngle(ju, 0.0F, 0.3491F, 0.3491F);
		ju.setTextureOffset(0, 62).addBox(-7.6924F, -23.2377F, -3.9038F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		ju_ichi = new ModelRenderer(this);
		ju_ichi.setRotationPoint(-5.5F, -5.0F, -4.5F);
		fukigae2.addChild(ju_ichi);
		setRotationAngle(ju_ichi, 0.0F, 0.8727F, 0.5236F);
		ju_ichi.setTextureOffset(12, 68).addBox(-6.9168F, -21.6216F, -10.1973F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		ju_ni = new ModelRenderer(this);
		ju_ni.setRotationPoint(-5.5F, -5.0F, -4.5F);
		fukigae2.addChild(ju_ni);
		setRotationAngle(ju_ni, 0.0F, 1.309F, 0.6981F);
		ju_ni.setTextureOffset(16, 68).addBox(-2.7992F, -19.337F, -15.5277F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		ju_san = new ModelRenderer(this);
		ju_san.setRotationPoint(-5.5F, -5.0F, -4.5F);
		fukigae2.addChild(ju_san);
		setRotationAngle(ju_san, 0.0F, 1.6581F, 0.7854F);
		ju_san.setTextureOffset(20, 68).addBox(2.8311F, -17.9308F, -17.4709F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		ju_yon = new ModelRenderer(this);
		ju_yon.setRotationPoint(-5.5F, -5.0F, -4.5F);
		fukigae2.addChild(ju_yon);
		setRotationAngle(ju_yon, 0.0F, 2.0944F, 0.8203F);
		ju_yon.setTextureOffset(6, 62).addBox(10.0933F, -17.3033F, -16.6266F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		yui = new ModelRenderer(this);
		yui.setRotationPoint(-1.0F, 17.0F, 5.5F);
		setRotationAngle(yui, 0.3491F, 0.0F, 0.0F);


		ju_kou = new ModelRenderer(this);
		ju_kou.setRotationPoint(1.5F, -0.1494F, 0.3204F);
		yui.addChild(ju_kou);
		ju_kou.setTextureOffset(0, 49).addBox(-5.0F, -25.6129F, 7.8665F, 9.0F, 6.0F, 0.0F, 0.0F, false);

		ju_rogu = new ModelRenderer(this);
		ju_rogu.setRotationPoint(1.5F, -0.1494F, 0.3204F);
		yui.addChild(ju_rogu);
		setRotationAngle(ju_rogu, 0.2618F, 0.0F, 0.0F);
		ju_rogu.setTextureOffset(40, 48).addBox(-5.0F, -16.9086F, 12.6746F, 9.0F, 6.0F, 0.0F, 0.0F, false);

		tatsumono = new ModelRenderer(this);
		tatsumono.setRotationPoint(0.0F, 25.0F, -1.0F);
		setRotationAngle(tatsumono, -0.0873F, 0.0F, 0.0F);
		tatsumono.setTextureOffset(24, 58).addBox(-8.0F, -42.9163F, -7.0174F, 16.0F, 14.0F, 0.0F, 0.0F, false);

		this.bipedHeadwear.addChild(bo);
		this.bipedHeadwear.addChild(mayubisashi);
		this.bipedHeadwear.addChild(jikoro_1);
		this.bipedHeadwear.addChild(jikoro_2);
		this.bipedHeadwear.addChild(jikoro_3);
		this.bipedHeadwear.addChild(fukigae);
		this.bipedHeadwear.addChild(fukigae2);
		this.bipedHeadwear.addChild(yui);
		this.bipedHeadwear.addChild(tatsumono);
	}
//
//	@Override
//	public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
//	{
//		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//	}
//
//	@Override
//	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
//		bo.render(matrixStack, buffer, packedLight, packedOverlay);
//		mayubisashi.render(matrixStack, buffer, packedLight, packedOverlay);
//		jikoro_1.render(matrixStack, buffer, packedLight, packedOverlay);
//		jikoro_2.render(matrixStack, buffer, packedLight, packedOverlay);
//		jikoro_3.render(matrixStack, buffer, packedLight, packedOverlay);
//		fukigae.render(matrixStack, buffer, packedLight, packedOverlay);
//		fukigae2.render(matrixStack, buffer, packedLight, packedOverlay);
//		yui.render(matrixStack, buffer, packedLight, packedOverlay);
//		tatsumono.render(matrixStack, buffer, packedLight, packedOverlay);
//		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
//	}
//
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}