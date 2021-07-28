package kogasastudio.ashihara.block;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.woodcrafts.*;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistryHandler
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ashihara.MODID);

    public static final RegistryObject<Block> BLOCK_WATER_FIELD = BLOCKS.register("water_field", BlockWaterField::new);
    public static final RegistryObject<Block> BLOCK_RICE_CROP = BLOCKS.register("rice_crop", BlockRiceCrop::new);
    public static final RegistryObject<Block> CHERRY_LOG = BLOCKS.register("cherry_log", BlockCherryLog::new);
    public static final RegistryObject<Block> CHERRY_BLOSSOM = BLOCKS.register("cherry_blossom", BlockCherryBlossom::new);
    public static final RegistryObject<Block> BLOCK_JINJA_LANTERN = BLOCKS.register("jinja_lantern", BlockJinjaLantern::new);
    public static final RegistryObject<Block> BLOCK_DIRT_DEPRESSION = BLOCKS.register("dirt_depression", BlockDirtDepression::new);
    public static final RegistryObject<Block> BLOCK_TETSUSENCHI = BLOCKS.register("tetsusenchi", BlockTetsusenchi::new);
    public static final RegistryObject<Block> CHERRY_SAPLING = BLOCKS.register("cherry_sapling", BlockCherrySapling::new);
    public static final RegistryObject<Block> BLOCK_MORTAR = BLOCKS.register("mortar", BlockMortar::new);
    public static final RegistryObject<Block> FALLEN_SAKURA = BLOCKS.register("fallen_sakura", BlockFallenSakura::new);
    public static final RegistryObject<Block> BLOCK_IMMATURE_RICE = BLOCKS.register("immature_rice", BlockImmatureRiceCrop::new);
    public static final RegistryObject<Block> CHERRY_PLANKS = BLOCKS.register("cherry_planks", BlockCherryPlanks::new);
    public static final RegistryObject<Block> CHERRY_STAIRS = BLOCKS.register("cherry_stairs", BlockCherryStairs::new);
    public static final RegistryObject<Block> CHERRY_FENCE = BLOCKS.register("cherry_fence", BlockCherryFence::new);
    public static final RegistryObject<Block> CHERRY_FENCE_GATE = BLOCKS.register("cherry_fence_gate", BlockCherryFenceGate::new);
    public static final RegistryObject<Block> POTTED_CHERRY_SAPLING = BLOCKS.register("potted_cherry_sapling", BlockPottedCherrySapling::new);
    public static final RegistryObject<Block> CHERRY_BUTTON = BLOCKS.register("cherry_button", BlockCherryButton::new);
    public static final RegistryObject<Block> CHERRY_SLAB = BLOCKS.register("cherry_slab", BlockCherrySlab::new);
    public static final RegistryObject<Block> CHERRY_WOOD = BLOCKS.register("cherry_wood", BlockCherryWood::new);
    public static final RegistryObject<Block> LANTERN_LONG_WHITE = BLOCKS.register("lantern_long_white", BlockHangingLanternLong::new);
    public static final RegistryObject<Block> LANTERN_LONG_RED = BLOCKS.register("lantern_long_red", BlockHangingLanternLong::new);
    public static final RegistryObject<Block> CHRYSANTHEMUM = BLOCKS.register("chrysanthemum", BlockChrysanthemumBush::new);
    public static final RegistryObject<Block> BLOCK_REED = BLOCKS.register("reed", BlockReed::new);
}
