package kogasastudio.ashihara;

import com.mojang.logging.LogUtils;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.BlockEntities;
import kogasastudio.ashihara.registry.MenuTypes;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.registry.*;
import kogasastudio.ashihara.registry.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(Ashihara.MODID)
public class Ashihara
{
    public static final String MODID = "ashihara";
    public static final Logger LOGGER_MAIN = LogUtils.getLogger();
    public static final RandomSource RANDOM = RandomSource.createThreadSafe();
    public static final double TEST_VALUE = 0d;

    public static RandomSource getRandom() {return RANDOM;}

    public static int getRandomBounded(int startIndex, int endIndex) {return RANDOM.nextInt(endIndex - startIndex) + startIndex;}

    public Ashihara(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::addCreative);

        Items.ITEMS.register(modEventBus);
        Blocks.BLOCKS.register(modEventBus);
        FluidRegistryHandler.FLUIDS.register(modEventBus);
        FluidRegistryHandler.AshiharaFluidTypes.TYPES.register(modEventBus);
        CreativeModeTabsRegistryHandler.TABS.register(modEventBus);
        SoundEvents.SOUNDS.register(modEventBus);
        ParticleRegistryHandler.PARTICLE_TYPES.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        MenuTypes.MENU_TYPES.register(modEventBus);
//        GLMRegistryHandler.MODIFIERS.register(bus);

//        BiomeRegistryHandler.BIOMES.register(bus);
        Features.FEATURES.register(modEventBus);
        WorldGenEventRegistryHandler.PLACED_FEATURE.register(modEventBus);
        WorldGenEventRegistryHandler.CONFIGURED_FEATURE.register(modEventBus);
        DataComponentTypes.ATTACHMENT_TYPES.register(modEventBus);
        RecipeTypes.register(modEventBus);
        RecipeSerializers.register(modEventBus);
        MolangValues.registerMolangValues();
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
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {
            event.accept(Items.KOISHI);
            event.accept(Items.MINATO_AQUA);
        } else if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
        {
            event.accept(Items.CHARLOTTE);
        }
    }
}
