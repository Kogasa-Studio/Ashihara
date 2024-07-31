package kogasastudio.ashihara.client.render;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.CandleModel;
import kogasastudio.ashihara.client.models.MillStoneModel;
import kogasastudio.ashihara.client.models.PailItemModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Ashihara.MODID, value = Dist.CLIENT)
public class LayerRegistryHandler
{
    public static final ModelLayerLocation PAIL_ITEM = register("pail_item");
    public static final ModelLayerLocation MILL_STONE = register("mill_stone");
    public static final ModelLayerLocation CANDLE = register("candle");

    @SubscribeEvent
    public static void registerLDs(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(PAIL_ITEM, PailItemModel::createBodyLayer);
        event.registerLayerDefinition(MILL_STONE, MillStoneModel::createBodyLayer);
        event.registerLayerDefinition(CANDLE, CandleModel::createBodyLayer);
        LOGGER_MAIN.info("######## LAYERS REGISTERED ########");
    }

    private static ModelLayerLocation register(String path)
    {
        return register(path, "main");
    }

    private static ModelLayerLocation register(String path, String part)
    {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, path), part);
    }
}