package kogasastudio.ashihara.datagen;

import net.minecraft.data.DataGenerator;

/**
 * @author DustW
 **/
public class TutLootTables extends BaseLootTableProvider {

    public TutLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        //createSimpleTable("red_dirt", AvarusBlocks.RED_DIRT.get());
    }
}