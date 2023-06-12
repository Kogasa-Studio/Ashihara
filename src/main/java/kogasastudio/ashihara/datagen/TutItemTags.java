package kogasastudio.ashihara.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * @author DustW
 **/
public class TutItemTags extends ItemTagsProvider
{

    public TutItemTags(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper)
    {
        super(generator, blockTags, DataGenerators.MOD_ID, helper);
    }

    @Override
    protected void addTags()
    {
    }

    @Override
    public String getName()
    {
        return "Tutorial Tags";
    }
}
