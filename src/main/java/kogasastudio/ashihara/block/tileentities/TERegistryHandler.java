package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class TERegistryHandler
{
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Ashihara.MODID);

    public static final Supplier<BlockEntityType<MarkableLanternTE>> MARKABLE_LANTERN_TE = TILE_ENTITIES.register("markable_lantern_tileentity",
        () -> BlockEntityType.Builder.of
            (
                MarkableLanternTE::new,
                BlockRegistryHandler.LANTERN_LONG_RED.get(),
                BlockRegistryHandler.LANTERN_LONG_WHITE.get()
            ).build(null));

    /*public static final Supplier<BlockEntityType<MortarTE>> MORTAR_TE = TILE_ENTITIES.register("mortar_tileentity",
        () -> BlockEntityType.Builder.of
                (
                    MortarTE::new,
                    BlockRegistryHandler.MORTAR.get()
                ).build(null));

    public static final Supplier<BlockEntityType<MillTE>> MILL_TE = TILE_ENTITIES.register("mill_tileentity",
            () -> BlockEntityType.Builder.of
                (
                    MillTE::new,
                    BlockRegistryHandler.MILL.get()
                ).build(null));*/

    public static final Supplier<BlockEntityType<PailTE>> PAIL_TE = TILE_ENTITIES.register("pail_tileentity",
            () -> BlockEntityType.Builder.of
                (
                    PailTE::new,
                    BlockRegistryHandler.PAIL.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CandleTE>> CANDLE_TE = TILE_ENTITIES.register("candle_tileentity",
            () -> BlockEntityType.Builder.of
                (
                    CandleTE::new,
                    BlockRegistryHandler.CANDLE.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CuttingBoardTE>> CUTTING_BOARD_TE = TILE_ENTITIES.register("cutting_board_tileentity",
            () -> BlockEntityType.Builder.of
                (
                    CuttingBoardTE::new,
                    BlockRegistryHandler.CUTTING_BOARD.get()
                ).build(null));

    public static final Supplier<BlockEntityType<MultiBuiltBlockEntity>> MULTI_BUILT_BLOCKENTITY = TILE_ENTITIES.register
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

    /*public static final Supplier<BlockEntityType<MealTableTE>> MEAL_TABLE_TE = TILE_ENTITIES.register("meal_table_tileentity",
            () -> BlockEntityType.Builder.of
                (
                    MealTableTE::new,
                    BlockRegistryHandler.MEAL_TABLE.get()
                ).build(null));*/
}
