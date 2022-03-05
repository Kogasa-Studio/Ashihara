package kogasastudio.ashihara.block;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.woodcrafts.*;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class BlockRegistryHandler
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ashihara.MODID);

    public static final RegistryObject<Block> BLOCK_WATER_FIELD = BLOCKS.register("water_field", BlockWaterField::new);
    public static final RegistryObject<Block> BLOCK_RICE_CROP = BLOCKS.register("rice_crop", BlockRiceCrop::new);
    public static final RegistryObject<Block> CHERRY_BLOSSOM = BLOCKS.register("cherry_blossom", BlockCherryBlossom::new);
    public static final RegistryObject<Block> BLOCK_JINJA_LANTERN = BLOCKS.register("jinja_lantern", BlockJinjaLantern::new);
    public static final RegistryObject<Block> BLOCK_DIRT_DEPRESSION = BLOCKS.register("dirt_depression", BlockDirtDepression::new);
    public static final RegistryObject<Block> BLOCK_TETSUSENCHI = BLOCKS.register("tetsusenchi", BlockTetsusenchi::new);
    public static final RegistryObject<Block> CHERRY_SAPLING = BLOCKS.register("cherry_sapling", BlockCherrySapling::new);
    public static final RegistryObject<Block> BLOCK_MORTAR = BLOCKS.register("mortar", BlockMortar::new);
    public static final RegistryObject<Block> FALLEN_SAKURA = BLOCKS.register("fallen_sakura", BlockFallenSakura::new);
    public static final RegistryObject<Block> BLOCK_IMMATURE_RICE = BLOCKS.register("immature_rice", BlockImmatureRiceCrop::new);
    public static final RegistryObject<Block> POTTED_CHERRY_SAPLING = BLOCKS.register("potted_cherry_sapling", BlockPottedCherrySapling::new);
    public static final RegistryObject<Block> TATAMI = BLOCKS.register("tatami", BlockTatami::new);
    public static final RegistryObject<Block> LANTERN_LONG_WHITE = BLOCKS.register("lantern_long_white", BlockHangingLanternLong::new);
    public static final RegistryObject<Block> LANTERN_LONG_RED = BLOCKS.register("lantern_long_red", BlockHangingLanternLong::new);
    public static final RegistryObject<Block> CHRYSANTHEMUM = BLOCKS.register("chrysanthemum", BlockChrysanthemumBush::new);
    public static final RegistryObject<Block> BLOCK_REED = BLOCKS.register("reed", BlockReed::new);
    public static final RegistryObject<Block> BLOCK_SHORTER_REED = BLOCKS.register("shorter_reed", BlockShorterReed::new);
    public static final RegistryObject<Block> MILL = BLOCKS.register("mill", BlockMill::new);
    public static final RegistryObject<Block> STONE_LANTERN = BLOCKS.register("stone_lantern", BlockStoneLantern::new);
    public static final RegistryObject<Block> HYDRANGEA_BUSH = BLOCKS.register("hydrangea_bush", () -> new BlockHydrangeaBush(false));
    public static final RegistryObject<Block> PAIL = BLOCKS.register("pail", BlockPail::new);
    public static final RegistryObject<Block> CANDLE = BLOCKS.register("candle", BlockCandle::new);
    public static final RegistryObject<Block> TEA_TREE = BLOCKS.register("tea_tree", BlockTeaTree::new);

    //木制品
    public static final Map<WoodCraftType, RegistryObject<Block>> CHERRY_WOODCRAFTS = registerAllWoodCraftsFor("cherry");

    public static final RegistryObject<Block> CHERRY_LOG = BLOCKS.register("cherry_log", SimpleLogBlock::new);
    public static final RegistryObject<Block> CHERRY_WOOD = BLOCKS.register("cherry_wood", SimpleWoodBlock::new);
    public static final RegistryObject<Block> CHERRY_BUTTON = BLOCKS.register("cherry_button", SimpleButtonBlock::new);


    public static final Map<WoodCraftType, RegistryObject<Block>> RED_WOODCRAFTS = registerAllWoodCraftsFor("red");

    public static final RegistryObject<Block> STRIPPED_RED_LOG = BLOCKS.register("stripped_red_log", SimpleLogBlock::new);

    //流体
    public static final RegistryObject<FlowingFluidBlock> SOY_MILK_BLOCK = BLOCKS.register("soy_milk", () ->
        new FlowingFluidBlock(FluidRegistryHandler.SOY_MILK, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));

    private static Map<WoodCraftType, RegistryObject<Block>> registerAllWoodCraftsFor(String name)
    {
        Map<WoodCraftType, RegistryObject<Block>> map = new HashMap<>();

        map.put(WoodCraftType.PLANKS, BLOCKS.register(name + "_planks", SimpleLogBlock::new));
        map.put(WoodCraftType.STAIRS, BLOCKS.register(name + "_stairs", SimpleLogBlock::new));
        map.put(WoodCraftType.SLAB, BLOCKS.register(name + "_slab", SimpleLogBlock::new));
        map.put(WoodCraftType.FENCE, BLOCKS.register(name + "_fence", SimpleLogBlock::new));
        map.put(WoodCraftType.FENCE_GATE, BLOCKS.register(name + "fence_gate", SimpleLogBlock::new));

        return map;
    }

    public enum WoodCraftType
    {
        PLANKS,
        STAIRS,
        SLAB,
        FENCE,
        FENCE_GATE
    }
}
