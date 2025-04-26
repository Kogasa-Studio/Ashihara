package kogasastudio.ashihara;

import com.mojang.logging.LogUtils;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.registry.TERegistryHandler;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.item.GuideBook;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.loading.ReloadableResources;
import kogasastudio.ashihara.registry.*;
import kogasastudio.ashihara.sounds.SoundEvents;
import kogasastudio.ashihara.utils.json.JsonUtils;
import kogasastudio.ashihara.utils.json.serializer.GuideBookPageSerializer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Mod(Ashihara.MODID)
public class Ashihara
{
    public static final String MODID = "ashihara";
    public static final Logger LOGGER_MAIN = LogUtils.getLogger();
    public static final RandomSource RANDOM = RandomSource.createThreadSafe();

    public static RandomSource getRandom() {return RANDOM;}

    public static int getRandomBounded(int startIndex, int endIndex) {return RANDOM.nextInt(endIndex - startIndex) + startIndex;}

    public Ashihara(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::addCreative);

        ItemRegistryHandler.ITEMS.register(modEventBus);
        BlockRegistryHandler.BLOCKS.register(modEventBus);
        FluidRegistryHandler.FLUIDS.register(modEventBus);
        FluidRegistryHandler.AshiharaFluidTypes.TYPES.register(modEventBus);
        CreativeModeTabsRegistryHandler.TABS.register(modEventBus);
        SoundEvents.SOUNDS.register(modEventBus);
        ParticleRegistryHandler.PARTICLE_TYPES.register(modEventBus);
        TERegistryHandler.TILE_ENTITIES.register(modEventBus);
//        GLMRegistryHandler.MODIFIERS.register(bus);

//        BiomeRegistryHandler.BIOMES.register(bus);
        Features.FEATURES.register(modEventBus);
        WorldGenEventRegistryHandler.PLACED_FEATURE.register(modEventBus);
        WorldGenEventRegistryHandler.CONFIGURED_FEATURE.register(modEventBus);
        DataComponentTypes.ATTACHMENT_TYPES.register(modEventBus);
        RecipeTypes.register(modEventBus);
        RecipeSerializers.register(modEventBus);
        /*try
        {
            Map<Integer, GuideBook.Page> iMap = new HashMap<>();
            iMap.put(0, new GuideBook.Page(0, new GuideBook.Page.TextField[]{new GuideBook.Page.TextField(0, 0, 5, 10, 0x000000, 3, true, "小猫崽子")}, new GuideBook.Page.Illustration[]{}));
            iMap.put(1, new GuideBook.Page(0, new GuideBook.Page.TextField[]{new GuideBook.Page.TextField(0, 0, 5, 10, 0x000000, 3, true, "小猫崽子2")}, new GuideBook.Page.Illustration[]{}));
            iMap.put(2, new GuideBook.Page(0, new GuideBook.Page.TextField[]{new GuideBook.Page.TextField(0, 0, 5, 10, 0x000000, 3, true, "小猫崽子3")}, new GuideBook.Page.Illustration[]{}));
            JsonUtils.writeToJson(JsonUtils.INSTANCE.pretty, Path.of("test/zh_cn.json"), GuideBookPageSerializer.serializeAll(iMap).getAsJsonObject());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }*/

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ReloadableResources.register();
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {
            event.accept(ItemRegistryHandler.KOISHI);
            event.accept(ItemRegistryHandler.MINATO_AQUA);
        } else if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
        {
            event.accept(ItemRegistryHandler.CHARLOTTE);
        }
    }
}
