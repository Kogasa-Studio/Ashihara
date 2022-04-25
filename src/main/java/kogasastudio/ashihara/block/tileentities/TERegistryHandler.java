package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TERegistryHandler
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Ashihara.MODID);

    public static final RegistryObject<TileEntityType<MarkableLanternTE>> MARKABLE_LANTERN_TE = TILE_ENTITIES.register("markable_lantern_tileentity",
    () -> TileEntityType.Builder.create
    (
        MarkableLanternTE::new,
        BlockRegistryHandler.LANTERN_LONG_RED.get(),
        BlockRegistryHandler.LANTERN_LONG_WHITE.get()
    ).build(null));

    public static final RegistryObject<TileEntityType<MortarTE>> MORTAR_TE = TILE_ENTITIES.register("mortar_tileentity",
    () -> TileEntityType.Builder.create
    (
        MortarTE::new,
        BlockRegistryHandler.MORTAR.get()
    ).build(null));

    public static final RegistryObject<TileEntityType<MillTE>> MILL_TE = TILE_ENTITIES.register("mill_tileentity",
    () -> TileEntityType.Builder.create
    (
        MillTE::new,
        BlockRegistryHandler.MILL.get()
    ).build(null));

    public static final RegistryObject<TileEntityType<PailTE>> PAIL_TE = TILE_ENTITIES.register("pail_tileentity",
    () -> TileEntityType.Builder.create
    (
        PailTE::new,
        BlockRegistryHandler.PAIL.get()
    ).build(null));

    public static final RegistryObject<TileEntityType<CandleTE>> CANDLE_TE = TILE_ENTITIES.register("candle_tileentity",
    () -> TileEntityType.Builder.create
    (
        CandleTE::new,
        BlockRegistryHandler.CANDLE.get()
    ).build(null));

    public static final RegistryObject<TileEntityType<CuttingBoardTE>> CUTTING_BOARD_TE = TILE_ENTITIES.register("cutting_board_tileentity",
    () -> TileEntityType.Builder.create
    (
        CuttingBoardTE::new,
        BlockRegistryHandler.CUTTING_BOARD.get()
    ).build(null));

    public static final RegistryObject<TileEntityType<MealTableTE>> MEAL_TABLE_TE = TILE_ENTITIES.register("meal_table_tileentity",
    () -> TileEntityType.Builder.create
    (
        MealTableTE::new,
        BlockRegistryHandler.MEAL_TABLE.get()
    ).build(null));
}
