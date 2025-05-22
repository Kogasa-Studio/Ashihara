package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
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
            Blocks.LANTERN_LONG_RED.get(),
            Blocks.LANTERN_LONG_WHITE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<MortarBE>> MORTAR_BE = BLOCK_ENTITIES.register("mortar_be",
                                                                                                () -> BlockEntityType.Builder.of
            (
            MortarBE::new,
            Blocks.MORTAR.get()
            ).build(null));

    /*public static final Supplier<BlockEntityType<MillBE>> MILL_TE = TILE_ENTITIES.register("mill_blockentity",
            () -> BlockEntityType.Builder.of
                (
                    MillBE::new,
                    Blocks.MILL.get()
                ).build(null));*/

    public static final Supplier<BlockEntityType<PailBE>> PAIL_BE = BLOCK_ENTITIES.register("pail_blockentity",
                                                                                            () -> BlockEntityType.Builder.of
                (
                PailBE::new,
                Blocks.PAIL.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CandleBE>> CANDLE_BE = BLOCK_ENTITIES.register("candle_blockentity",
                                                                                                () -> BlockEntityType.Builder.of
                (
                CandleBE::new,
                Blocks.CANDLE.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CuttingBoardBE>> CUTTING_BOARD_BE = BLOCK_ENTITIES.register("cutting_board_blockentity",
                                                                                                             () -> BlockEntityType.Builder.of
                (
                CuttingBoardBE::new,
                Blocks.CUTTING_BOARD.get()
                ).build(null));

    public static final Supplier<BlockEntityType<CharlotteBE>> CHARLOTTE_BE = BLOCK_ENTITIES.register("charlotte_blockentity",
                                                                                                      () -> BlockEntityType.Builder.of
                (
                CharlotteBE::new,
                Blocks.CHARLOTTE.get()
                ).build(null));

    public static final Supplier<BlockEntityType<MultiBuiltBlockEntity>> MULTI_BUILT_BLOCKENTITY = BLOCK_ENTITIES.register
    (
        "multi_built_blockentity",
        () -> BlockEntityType.Builder.of
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
        ).build(null)
    );

    /*public static final Supplier<BlockEntityType<MealTableTE>> MEAL_TABLE_TE = TILE_ENTITIES.register("meal_table_blockentity",
            () -> BlockEntityType.Builder.of
                (
                    MealTableTE::new,
                    Blocks.MEAL_TABLE.get()
                ).build(null));*/
}
