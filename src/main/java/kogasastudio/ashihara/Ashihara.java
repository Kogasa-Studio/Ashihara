package kogasastudio.ashihara;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.interaction.loot.GLMRegistryHandler;
import kogasastudio.ashihara.interaction.recipes.register.RecipeManager;
import kogasastudio.ashihara.inventory.container.ContainerRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.sounds.SoundEvents;
import kogasastudio.ashihara.world.WorldGenEventRegistryHandler;
import kogasastudio.ashihara.world.biomes.BiomeRegistryHandler;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ashihara.MODID)
@Mod.EventBusSubscriber(modid = Ashihara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Ashihara {
    public static final String MODID = "ashihara";
    public static final Logger LOGGER_MAIN = LogManager.getLogger();
    public static final CreativeModeTab ASHIHARA = new CreativeModeTab("group_ashihara") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistryHandler.ASHIHARA_ICON.get());
        }
    };
    public static final CreativeModeTab MATERIALS = new CreativeModeTab("group_ash_materials") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistryHandler.SAKURA.get());
        }
    };
    public static final CreativeModeTab BUILDING_BLOCKS = new CreativeModeTab("group_ash_building_blocks") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistryHandler.JINJA_LANTERN.get());
        }
    };

    public Ashihara()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistryHandler.ITEMS.register(bus);
        BlockRegistryHandler.BLOCKS.register(bus);
        SoundEvents.SOUNDS.register(bus);
        ParticleRegistryHandler.PARTICLE_TYPES.register(bus);
        TERegistryHandler.TILE_ENTITIES.register(bus);
        ContainerRegistryHandler.CONTAINER_TYPES.register(bus);
        // RecipeTypesRegistryHandler.RECIPES.register(bus);
        FluidRegistryHandler.FLUIDS.register(bus);
        GLMRegistryHandler.MODIFIERS.register(bus);

        BiomeRegistryHandler.BIOMES.register(bus);
        WorldGenEventRegistryHandler.PLACED_FEATURE.register(bus);
        WorldGenEventRegistryHandler.CONFIGURED_FEATURE.register(bus);

        RecipeManager.register(bus);
    }
}
