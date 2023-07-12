package kogasastudio.ashihara.datagen.tags;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.datagen.DataGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

/**
 * @author DustW
 **/
public class AshiharaBlockTags extends VanillaBlockTagsProvider
{

    public AshiharaBlockTags(DataGenerator generator, CompletableFuture<HolderLookup.Provider> future)
    {
        super(generator.getPackOutput(), future);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // todo 就用类似这样的方式给 block 添加 tag
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(BlockRegistryHandler.WATER_FIELD.get());
        tag(BlockTags.NEEDS_IRON_TOOL)
                //                    挖掘等级 2 是铁吗？
                .add(BlockRegistryHandler.WATER_FIELD.get());
    }

    @Override
    public String getName()
    {
        return DataGenerators.MOD_ID + " Tags";
    }
}