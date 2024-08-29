package kogasastudio.ashihara;

import com.mojang.logging.LogUtils;
//import io.github.tt432.eyelib.client.render.visitor.BuiltInBrModelRenderVisitors;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.interaction.recipes.register.RecipeManager;
import kogasastudio.ashihara.inventory.container.ContainerRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.registry.Features;
import kogasastudio.ashihara.sounds.SoundEvents;
import kogasastudio.ashihara.registry.WorldGenEventRegistryHandler;
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
    public static final RandomSource RANDOM = RandomSource.create();

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
        ContainerRegistryHandler.CONTAINER_TYPES.register(modEventBus);
        // RecipeTypesRegistryHandler.RECIPES.register(bus);
//        GLMRegistryHandler.MODIFIERS.register(bus);

//        BiomeRegistryHandler.BIOMES.register(bus);
        Features.FEATURES.register(modEventBus);
        WorldGenEventRegistryHandler.PLACED_FEATURE.register(modEventBus);
        WorldGenEventRegistryHandler.CONFIGURED_FEATURE.register(modEventBus);

        RecipeManager.register(modEventBus);

        //BuiltInBrModelRenderVisitors.VISITORS.register(modEventBus);
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
