package kogasastudio.ashihara;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.sounds.SoundEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ashihara.MODID)
public class Ashihara
{
    public static final String MODID = "ashihara";
    public static final Logger LOGGER_MAIN = LogManager.getLogger();
    public static final ItemGroup ASHIHARA = new ItemGroup("group_ashihara")
    {
        @Override
        public ItemStack createIcon() { return new ItemStack(ItemRegistryHandler.ASHIHARA_ICON.get()); }
    };

    public Ashihara()
    {
        ItemRegistryHandler.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockRegistryHandler.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SoundEvents.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ParticleRegistryHandler.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
