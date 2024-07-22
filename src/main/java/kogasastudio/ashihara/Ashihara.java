package kogasastudio.ashihara;

import com.mojang.logging.LogUtils;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.interaction.recipes.register.RecipeManager;
import kogasastudio.ashihara.inventory.container.ContainerRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Ashihara.MODID)
@EventBusSubscriber(modid = Ashihara.MODID, bus = EventBusSubscriber.Bus.MOD)
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
        BlockRegistryHandler.BLOCKS.register(bus);
        CreativeModeTabsRegistryHandler.TABS.register(bus);
        SoundEvents.SOUNDS.register(bus);
        ParticleRegistryHandler.PARTICLE_TYPES.register(bus);
        TERegistryHandler.TILE_ENTITIES.register(bus);
        ContainerRegistryHandler.CONTAINER_TYPES.register(bus);
        // RecipeTypesRegistryHandler.RECIPES.register(bus);
        FluidRegistryHandler.FLUIDS.register(bus);
        FluidRegistryHandler.AshiharaFluidTypes.TYPES.register(bus);
//        GLMRegistryHandler.MODIFIERS.register(bus);

//        BiomeRegistryHandler.BIOMES.register(bus);
//        WorldGenEventRegistryHandler.PLACED_FEATURE.register(bus);
//        WorldGenEventRegistryHandler.CONFIGURED_FEATURE.register(bus);

        RecipeManager.register(bus);
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {
            event.accept(ItemRegistryHandler.KOISHI);
            event.accept(ItemRegistryHandler.MINATO_AQUA);
        }
        else if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
        {
            event.accept(ItemRegistryHandler.CHARLOTTE);
        }
    }
}
