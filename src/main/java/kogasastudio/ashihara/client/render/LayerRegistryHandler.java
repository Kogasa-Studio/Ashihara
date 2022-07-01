package kogasastudio.ashihara.client.render;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.MillStoneModel;
import kogasastudio.ashihara.client.models.PailItemModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Ashihara.MODID, value = Dist.CLIENT)
public class LayerRegistryHandler
{
    public static final ModelLayerLocation PAIL_ITEM = register("pail_item");
    public static final ModelLayerLocation MILL_STONE = register("mill_stone");
    @SubscribeEvent
    public static void registerLDs(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(PAIL_ITEM, PailItemModel::createBodyLayer);
        event.registerLayerDefinition(MILL_STONE, MillStoneModel::createBodyLayer);
        LOGGER_MAIN.info("######## LAYERS REGISTERED ########");
    }

    private static ModelLayerLocation register(String path)
    {
        return register(path, "main");
    }

    private static ModelLayerLocation register(String path, String part)
    {
        return new ModelLayerLocation(new ResourceLocation(Ashihara.MODID, path), part);
    }
}