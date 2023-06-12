package kogasastudio.ashihara.datagen;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * @author DustW
 **/
public class TutBlockTags extends BlockTagsProvider
{

    public TutBlockTags(DataGenerator generator, ExistingFileHelper helper)
    {
        super(generator, DataGenerators.MOD_ID, helper);
    }

    @Override
    protected void addTags()
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