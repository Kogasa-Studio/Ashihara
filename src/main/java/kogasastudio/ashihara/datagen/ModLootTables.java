package kogasastudio.ashihara.datagen;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author DustW
 **/
public class ModLootTables extends BaseLootTableProvider {

    public ModLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        leaves(BlockRegistryHandler.MAPLE_LEAVES_RED, BlockRegistryHandler.RED_MAPLE_SAPLING);
        leaves(BlockRegistryHandler.CHERRY_BLOSSOM, BlockRegistryHandler.CHERRY_SAPLING, builder -> {
            builder.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                            .add(applyExplosionDecay(BlockRegistryHandler.CHERRY_BLOSSOM.get(), LootItem.lootTableItem(ItemRegistryHandler.SAKURA_PETAL.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))
                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F)))))
                    .when(HAS_NO_SHEARS_OR_SILK_TOUCH));
        });
    }

    void leaves(Supplier<Block> leavesBlock, Supplier<Block> saplingBlock) {
        lootTables.put(leavesBlock.get(), createLeavesDrops(leavesBlock.get(), saplingBlock.get(), NORMAL_LEAVES_SAPLING_CHANCES));
    }

    void leaves(Supplier<Block> leavesBlock, Supplier<Block> saplingBlock, Consumer<LootTable.Builder> builderConsumer) {
        LootTable.Builder leavesDrops = createLeavesDrops(leavesBlock.get(), saplingBlock.get(), NORMAL_LEAVES_SAPLING_CHANCES);
        builderConsumer.accept(leavesDrops);
        lootTables.put(leavesBlock.get(), leavesDrops);
    }
}