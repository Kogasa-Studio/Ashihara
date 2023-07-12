package kogasastudio.ashihara.datagen.loot.tables;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author DustW
 **/
public class AshiharaBlockLootTables extends BaseLootTableProvider
{

    public AshiharaBlockLootTables()
    {
        super();
    }

    @Override
    protected void generate()
    {
        leaves(BlockRegistryHandler.MAPLE_LEAVES_RED, BlockRegistryHandler.RED_MAPLE_SAPLING);
        leaves(BlockRegistryHandler.CHERRY_BLOSSOM, BlockRegistryHandler.CHERRY_SAPLING/*, builder ->
                builder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(applyExplosionDecay(BlockRegistryHandler.CHERRY_BLOSSOM.get(), LootItem.lootTableItem(ItemRegistryHandler.SAKURA_PETAL.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))
                                        .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F)))))
                        .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item())))*/);
    }

    void leaves(Supplier<Block> leavesBlock, Supplier<Block> saplingBlock)
    {
        lootTables.put(leavesBlock.get(), createLeavesDrops(leavesBlock.get(), saplingBlock.get(), NORMAL_LEAVES_SAPLING_CHANCES));
    }

    void leaves(Supplier<Block> leavesBlock, Supplier<Block> saplingBlock, Consumer<LootTable.Builder> builderConsumer)
    {
        LootTable.Builder leavesDrops = createLeavesDrops(leavesBlock.get(), saplingBlock.get(), NORMAL_LEAVES_SAPLING_CHANCES);
        builderConsumer.accept(leavesDrops);
        lootTables.put(leavesBlock.get(), leavesDrops);
    }
}