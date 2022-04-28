package kogasastudio.ashihara;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.interaction.loot.GLMRegistryHandler;
import kogasastudio.ashihara.interaction.recipe.RecipeTypesRegistryHandler;
import kogasastudio.ashihara.inventory.container.ContainerRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.sounds.SoundEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ashihara.MODID)
@Mod.EventBusSubscriber(modid = Ashihara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Ashihara
{
    public static final String MODID = "ashihara";
    public static final Logger LOGGER_MAIN = LogManager.getLogger();
    public static final ItemGroup ASHIHARA = new ItemGroup("group_ashihara")
    {
        @Override
        public ItemStack createIcon() {return new ItemStack(ItemRegistryHandler.ASHIHARA_ICON.get());}
    };
    public static final ItemGroup MATERIALS = new ItemGroup("group_ash_materials")
    {
        @Override
        public ItemStack createIcon() {return new ItemStack(ItemRegistryHandler.SAKURA.get());}
    };
    public static final ItemGroup BUILDING_BLOCKS = new ItemGroup("group_ash_building_blocks")
    {
        @Override
        public ItemStack createIcon() {return new ItemStack(ItemRegistryHandler.JINJA_LANTERN.get());}
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
        RecipeTypesRegistryHandler.RECIPES.register(bus);
        FluidRegistryHandler.FLUIDS.register(bus);
        GLMRegistryHandler.MODIFIERS.register(bus);
    }
}
