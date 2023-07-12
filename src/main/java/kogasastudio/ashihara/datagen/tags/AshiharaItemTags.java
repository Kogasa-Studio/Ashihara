package kogasastudio.ashihara.datagen.tags;

import kogasastudio.ashihara.datagen.DataGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;

import java.util.concurrent.CompletableFuture;

/**
 * @author DustW
 **/
public class AshiharaItemTags extends VanillaItemTagsProvider
{

    public AshiharaItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, VanillaBlockTagsProvider blockTags)
    {
        super(output, future, blockTags.contentsGetter());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        super.addTags(provider);
    }

    @Override
    public String getName()
    {
        return DataGenerators.MOD_ID + "Tags";
    }
}
