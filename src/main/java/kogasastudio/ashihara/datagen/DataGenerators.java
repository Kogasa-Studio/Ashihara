package kogasastudio.ashihara.datagen;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.datagen.loot.AshiharaLootTableProvider;
import kogasastudio.ashihara.datagen.tags.AshiharaBlockTags;
import kogasastudio.ashihara.datagen.tags.AshiharaItemTags;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

/**
 * @author DustW
 **/
@Mod.EventBusSubscriber(modid = DataGenerators.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators
{
    public static final String MOD_ID = Ashihara.MODID;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
//        generator.addProvider(event.includeServer(), new TutRecipes(generator));
        generator.addProvider(event.includeServer(), new AshiharaLootTableProvider(generator.getPackOutput()));
        AshiharaBlockTags blockTags = new AshiharaBlockTags(generator, event.getLookupProvider());
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new AshiharaItemTags(generator.getPackOutput(), event.getLookupProvider(), blockTags));
        // generator.addProvider(new TutBlockStates(generator, event.getExistingFileHelper()));
        // generator.addProvider(new TutItemModels(generator, event.getExistingFileHelper()));
    }
}