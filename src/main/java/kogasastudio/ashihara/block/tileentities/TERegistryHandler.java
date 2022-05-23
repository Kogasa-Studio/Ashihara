package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TERegistryHandler
{
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Ashihara.MODID);

    public static final RegistryObject<BlockEntityType<MarkableLanternTE>> MARKABLE_LANTERN_TE = TILE_ENTITIES.register("markable_lantern_tileentity",
    () -> BlockEntityType.Builder.of
    (
        MarkableLanternTE::new,
        BlockRegistryHandler.LANTERN_LONG_RED.get(),
        BlockRegistryHandler.LANTERN_LONG_WHITE.get()
    ).build(null));

    public static final RegistryObject<BlockEntityType<MortarTE>> MORTAR_TE = TILE_ENTITIES.register("mortar_tileentity",
    () -> BlockEntityType.Builder.of
    (
        MortarTE::new,
        BlockRegistryHandler.MORTAR.get()
    ).build(null));

    public static final RegistryObject<BlockEntityType<MillTE>> MILL_TE = TILE_ENTITIES.register("mill_tileentity",
    () -> BlockEntityType.Builder.of
    (
        MillTE::new,
        BlockRegistryHandler.MILL.get()
    ).build(null));

    public static final RegistryObject<BlockEntityType<PailTE>> PAIL_TE = TILE_ENTITIES.register("pail_tileentity",
    () -> BlockEntityType.Builder.of
    (
        PailTE::new,
        BlockRegistryHandler.PAIL.get()
    ).build(null));

    public static final RegistryObject<BlockEntityType<CandleTE>> CANDLE_TE = TILE_ENTITIES.register("candle_tileentity",
    () -> BlockEntityType.Builder.of
    (
        CandleTE::new,
        BlockRegistryHandler.CANDLE.get()
    ).build(null));

    public static final RegistryObject<BlockEntityType<CuttingBoardTE>> CUTTING_BOARD_TE = TILE_ENTITIES.register("cutting_board_tileentity",
    () -> BlockEntityType.Builder.of
    (
        CuttingBoardTE::new,
        BlockRegistryHandler.CUTTING_BOARD.get()
    ).build(null));

    public static final RegistryObject<BlockEntityType<MealTableTE>> MEAL_TABLE_TE = TILE_ENTITIES.register("meal_table_tileentity",
    () -> BlockEntityType.Builder.of
    (
        MealTableTE::new,
        BlockRegistryHandler.MEAL_TABLE.get()
    ).build(null));
}
