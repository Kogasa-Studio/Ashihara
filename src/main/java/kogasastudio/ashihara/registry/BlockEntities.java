package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.blockentity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Ashihara.MODID);

    public static final Supplier<BlockEntityType<MarkableLanternBE>> MARKABLE_LANTERN_BE = BLOCK_ENTITIES.register("markable_lantern_blockentity",
                                                                                                                   () -> BlockEntityType.Builder.of
            (
            MarkableLanternBE::new,
            BlockRegistryHandler.LANTERN_LONG_RED.get(),
            BlockRegistryHandler.LANTERN_LONG_WHITE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<MortarBE>> MORTAR_BE = BLOCK_ENTITIES.register("mortar_be",
                                                                                                () -> BlockEntityType.Builder.of
            (
            MortarBE::new,
            BlockRegistryHandler.MORTAR.get()
            ).build(null));

    /*public static final Supplier<BlockEntityType<MillBE>> MILL_TE = TILE_ENTITIES.register("mill_blockentity",
            () -> BlockEntityType.Builder.of
                (
                    MillBE::new,
                    BlockRegistryHandler.MILL.get()
                ).build(null));*/

    public static final Supplier<BlockEntityType<PailBE>> PAIL_BE = BLOCK_ENTITIES.register("pail_blockentity",
                                                                                            () -> BlockEntityType.Builder.of
                (
                PailBE::new,
                BlockRegistryHandler.PAIL.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CandleBE>> CANDLE_BE = BLOCK_ENTITIES.register("candle_blockentity",
                                                                                                () -> BlockEntityType.Builder.of
                (
                CandleBE::new,
                BlockRegistryHandler.CANDLE.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CuttingBoardBE>> CUTTING_BOARD_BE = BLOCK_ENTITIES.register("cutting_board_blockentity",
                                                                                                             () -> BlockEntityType.Builder.of
                (
                CuttingBoardBE::new,
                BlockRegistryHandler.CUTTING_BOARD.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CharlotteBE>> CHARLOTTE_BE = BLOCK_ENTITIES.register("charlotte_blockentity",
                                                                                                      () -> BlockEntityType.Builder.of
                (
                CharlotteBE::new,
                BlockRegistryHandler.CHARLOTTE.get()
                ).build(null));

    public static final Supplier<BlockEntityType<MultiBuiltBlockEntity>> MULTI_BUILT_BLOCKENTITY = BLOCK_ENTITIES.register
    (
        "multi_built_blockentity",
        () -> BlockEntityType.Builder.of
        (
            MultiBuiltBlockEntity::new,
            BlockRegistryHandler.BAMBOO_BONES_COMPONENT.get(),
            BlockRegistryHandler.RAMMED_SOIL_COMPONENT.get(),
            BlockRegistryHandler.WHITE_SOIL_COMPONENT.get(),
            BlockRegistryHandler.WHITE_WOOD_COMPONENT.get(),

            BlockRegistryHandler.GREEN_WOOD_COMPONENT.get(),
            BlockRegistryHandler.MULTI_BUILT_BLOCK.get(),
            BlockRegistryHandler.SPRUCE_WOOD_COMPONENT.get(),
            BlockRegistryHandler.RED_WOOD_COMPONENT.get(),

            BlockRegistryHandler.GOLD_DECO_COMPONENT.get(),
            BlockRegistryHandler.GOLD_STRUCTURAL_COMPONENT.get(),
            BlockRegistryHandler.STONE_COMPONENT.get(),

            BlockRegistryHandler.CYPRESS_SKIN_COMPONENT.get(),
            BlockRegistryHandler.TERRACOTTA_TILE_COMPONENT.get()
        ).build(null)
    );

    /*public static final Supplier<BlockEntityType<MealTableTE>> MEAL_TABLE_TE = TILE_ENTITIES.register("meal_table_blockentity",
            () -> BlockEntityType.Builder.of
                (
                    MealTableTE::new,
                    BlockRegistryHandler.MEAL_TABLE.get()
                ).build(null));*/
}
