package kogasastudio.ashihara.client.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MortarItemDisplay extends GeoObjectRenderer<SimpleInternalControlGeoModel>
{
    public Map<Integer, ItemStack> items = new HashMap<>(8);
    public MortarItemDisplay(GeoModel<SimpleInternalControlGeoModel> model)
    {
        super(model);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SimpleInternalControlGeoModel animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        if (bone.getName().equals("level0") && items.get(0) != null) renderer.render(items.get(0), ItemDisplayContext.FIXED, false, poseStack, bufferSource, packedLight, packedOverlay, renderer.getModel(items.get(0), null, null, 0));
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    public void syncItem(ItemStackHandler inventory)
    {
        this.items.clear();
        int k = 0;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i).copy();
            if (!stack.isEmpty())
            {
                this.items.put(k, stack);
                k += 1;
            }
        }
    }
}
