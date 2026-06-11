package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.*;
import kogasastudio.ashihara.block.blockentity.FermentationSubBlockEntity;
import kogasastudio.ashihara.block.FermentationBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Ashihara.MODID);

    public static final Supplier<BlockEntityType<MarkableLanternBE>> MARKABLE_LANTERN_BE = BLOCK_ENTITIES.register("markable_lantern_blockentity",
                                                                                                                   () -> new BlockEntityType<>
            (
            MarkableLanternBE::new,
            Blocks.LANTERN_LONG_RED.get(),
            Blocks.LANTERN_LONG_WHITE.get()
            ));

    public static final Supplier<BlockEntityType<MortarBE>> MORTAR_BE = BLOCK_ENTITIES.register("mortar_be",
                                                                                                () -> new BlockEntityType<>
            (
            MortarBE::new,
            Blocks.MORTAR.get()
            ));

    public static final Supplier<BlockEntityType<PotBlockEntity>> POT_BE = BLOCK_ENTITIES.register("pot_be",
            () -> new BlockEntityType<>(PotBlockEntity::new, Blocks.POT.get()));

    public static final Supplier<BlockEntityType<DirtCookStoveBE>> DIRT_COOKSTOVE_BE = BLOCK_ENTITIES.register("dirt_cookstove_be", () -> new BlockEntityType<>(DirtCookStoveBE::new, Blocks.DIRT_COOKSTOVE.get()));

    /*public static final Supplier<BlockEntityType<MillBE>> MILL_TE = TILE_ENTITIES.register("mill_blockentity",
            () -> new BlockEntityType<
                (
                    MillBE::new,
                    Blocks.MILL.get()
                ));*/

    public static final Supplier<BlockEntityType<PailBE>> PAIL_BE = BLOCK_ENTITIES.register("pail_blockentity",
                                                                                            () -> new BlockEntityType<>
                (
                PailBE::new,
                Blocks.PAIL.get()
                ));

    public static final Supplier<BlockEntityType<CandleBE>> CANDLE_BE = BLOCK_ENTITIES.register("candle_blockentity",
                                                                                                () -> new BlockEntityType<>
                (
                CandleBE::new,
                Blocks.CANDLE.get()
                ));

    public static final Supplier<BlockEntityType<CuttingBoardBE>> CUTTING_BOARD_BE = BLOCK_ENTITIES.register("cutting_board_blockentity",
                                                                                                             () -> new BlockEntityType<>
                (
                CuttingBoardBE::new,
                Blocks.CUTTING_BOARD.get()
                ));

    public static final Supplier<BlockEntityType<CharlotteBE>> CHARLOTTE_BE = BLOCK_ENTITIES.register("charlotte_blockentity",
                                                                                                      () -> new BlockEntityType<>
                (
                CharlotteBE::new,
                Blocks.CHARLOTTE.get()
                ));

    public static final Supplier<BlockEntityType<MultiBuiltBlockEntity>> MULTI_BUILT_BLOCKENTITY = BLOCK_ENTITIES.register
    (
        "multi_built_blockentity",
        () -> new BlockEntityType<>
        (
        MultiBuiltBlockEntity::new,
        Blocks.BAMBOO_BONES_COMPONENT.get(),
        Blocks.RAMMED_SOIL_COMPONENT.get(),
        Blocks.WHITE_SOIL_COMPONENT.get(),
        Blocks.WHITE_WOOD_COMPONENT.get(),

        Blocks.GREEN_WOOD_COMPONENT.get(),
        Blocks.MULTI_BUILT_BLOCK.get(),
        Blocks.SPRUCE_WOOD_COMPONENT.get(),
        Blocks.RED_WOOD_COMPONENT.get(),

        Blocks.GOLD_DECO_COMPONENT.get(),
        Blocks.GOLD_STRUCTURAL_COMPONENT.get(),
        Blocks.STONE_COMPONENT.get(),

        Blocks.CYPRESS_SKIN_COMPONENT.get(),
        Blocks.TERRACOTTA_TILE_COMPONENT.get()
        )
    );

    /*public static final Supplier<BlockEntityType<MealTableTE>> MEAL_TABLE_TE = TILE_ENTITIES.register("meal_table_blockentity",
            () -> new BlockEntityType<
                (
                    MealTableTE::new,
                    Blocks.MEAL_TABLE.get()
                ));*/

    public static final Supplier<BlockEntityType<FermentationSubBlockEntity>> FERMENTATION_SUB_BE = BLOCK_ENTITIES.register(
        "fermentation_sub_be",
        () -> new BlockEntityType<>(FermentationSubBlockEntity::new,
            Blocks.LARGE_FERMENTATION_VAT.get()));

    public static final Supplier<BlockEntityType<FermentationBlockEntity>> FERMENTATION_BE = BLOCK_ENTITIES.register(
        "fermentation_be",
        () -> new BlockEntityType<>(FermentationBlockEntity::new,
            Blocks.WOODEN_BASIN.get(),
            Blocks.FERMENTATION_VAT.get(),
            Blocks.LARGE_FERMENTATION_VAT.get()));
}