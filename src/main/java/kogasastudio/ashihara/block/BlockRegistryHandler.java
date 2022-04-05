package kogasastudio.ashihara.block;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.trees.CherryBlossomTree;
import kogasastudio.ashihara.block.trees.RedMapleTree;
import kogasastudio.ashihara.block.woodcrafts.*;
import kogasastudio.ashihara.client.particles.GenericParticleType;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.utils.WoodTypes;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistryHandler {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ashihara.MODID);

    public static final RegistryObject<Block> BLOCK_WATER_FIELD = BLOCKS.register("water_field", BlockWaterField::new);
    public static final RegistryObject<Block> BLOCK_RICE_CROP = BLOCKS.register("rice_crop", BlockRiceCrop::new);
    public static final RegistryObject<Block> CHERRY_BLOSSOM = BLOCKS.register("cherry_blossom", () -> new AbstractFallingLeavesBlock(5, false) {@Override protected Block getFallenBlock() {return FALLEN_SAKURA.get();}@Override protected GenericParticleType getParticle() {return ParticleRegistryHandler.SAKURA.get();}});
    public static final RegistryObject<Block> MAPLE_LEAVES_RED = BLOCKS.register("maple_leaves_red", () -> new AbstractFallingLeavesBlock() {@Override protected Block getFallenBlock() {return FALLEN_MAPLE_LEAVES_RED.get();}@Override protected GenericParticleType getParticle() {return ParticleRegistryHandler.MAPLE_LEAF.get();}});
    public static final RegistryObject<Block> BLOCK_JINJA_LANTERN = BLOCKS.register("jinja_lantern", BlockJinjaLantern::new);
    public static final RegistryObject<Block> BLOCK_DIRT_DEPRESSION = BLOCKS.register("dirt_depression", BlockDirtDepression::new);
    public static final RegistryObject<Block> BLOCK_TETSUSENCHI = BLOCKS.register("tetsusenchi", BlockTetsusenchi::new);
    public static final RegistryObject<Block> CHERRY_SAPLING = BLOCKS.register("cherry_sapling", () -> new SaplingBlock(new CherryBlossomTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> RED_MAPLE_SAPLING = BLOCKS.register("red_maple_sapling", () -> new SaplingBlock(new RedMapleTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> BLOCK_MORTAR = BLOCKS.register("mortar", BlockMortar::new);
    public static final RegistryObject<Block> FALLEN_SAKURA = BLOCKS.register("fallen_sakura", AbstractFallenLeavesBlock::new);
    public static final RegistryObject<Block> FALLEN_MAPLE_LEAVES_RED = BLOCKS.register("fallen_maple_leaves_red", AbstractFallenLeavesBlock::new);
    public static final RegistryObject<Block> BLOCK_IMMATURE_RICE = BLOCKS.register("immature_rice", BlockImmatureRiceCrop::new);
    public static final RegistryObject<Block> POTTED_CHERRY_SAPLING = BLOCKS.register("potted_cherry_sapling", () -> new FlowerPotBlock(BlockRegistryHandler.CHERRY_SAPLING.get(), AbstractBlock.Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance().notSolid()));
    public static final RegistryObject<Block> POTTED_RED_MAPLE_SAPLING = BLOCKS.register("potted_red_maple_sapling", () -> new FlowerPotBlock(BlockRegistryHandler.RED_MAPLE_SAPLING.get(), AbstractBlock.Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance().notSolid()));
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

    //作物
    public static final RegistryObject<Block> SOY_BEANS = BLOCKS.register("soy_beans", () -> new AbstractCropAge7Pickable(7, 3) {@Override protected IItemProvider getSeedsItem() {return ItemRegistryHandler.SOY_BEAN.get();}});
    public static final RegistryObject<Block> SWEET_POTATOES = BLOCKS.register("sweet_potatoes", () -> new AbstractCropAge7() {@Override protected IItemProvider getSeedsItem() {return ItemRegistryHandler.SWEET_POTATO.get();}});
    public static final RegistryObject<Block> CUCUMBERS = BLOCKS.register("cucumbers", () -> new BlockCucumberCrop() {@Override protected IItemProvider getSeedsItem() {return ItemRegistryHandler.CUCUMBER.get();}});

    //木制品
    public static final RegistryObject<Block> CHERRY_LOG = BLOCKS.register("cherry_log", () -> new StrippableLogBlock() {@Override public Block getStrippedBlock() {return STRIPPED_CHERRY_LOG.get();}});
    public static final RegistryObject<Block> STRIPPED_CHERRY_LOG = BLOCKS.register("stripped_cherry_log", SimpleLogBlock::new);
    public static final RegistryObject<Block> CHERRY_WOOD = BLOCKS.register("cherry_wood", SimpleWoodBlock::new);
    public static final RegistryObject<Block> CHERRY_PLANKS = BLOCKS.register("cherry_planks", SimplePlanksBlock::new);
    public static final RegistryObject<Block> CHERRY_STAIRS = BLOCKS.register("cherry_stairs", SimpleStairsBlock::new);
    public static final RegistryObject<Block> CHERRY_SLAB = BLOCKS.register("cherry_slab", SimpleSlabBlock::new);
    public static final RegistryObject<Block> CHERRY_FENCE = BLOCKS.register("cherry_fence", SimpleFenceBlock::new);
    public static final RegistryObject<Block> CHERRY_FENCE_GATE = BLOCKS.register("cherry_fence_gate", SimpleFenceGateBlock::new);
    public static final RegistryObject<Block> CHERRY_BUTTON = BLOCKS.register("cherry_button", SimpleButtonBlock::new);

    public static final RegistryObject<Block> STRIPPED_RED_LOG = BLOCKS.register("stripped_red_log", SimpleLogBlock::new);
    public static final RegistryObject<Block> RED_PLANKS = BLOCKS.register("red_planks", SimplePlanksBlock::new);
    public static final RegistryObject<Block> RED_STAIRS = BLOCKS.register("red_stairs", SimpleStairsBlock::new);
    public static final RegistryObject<Block> RED_SLAB = BLOCKS.register("red_slab", SimpleSlabBlock::new);
    public static final RegistryObject<Block> RED_FENCE = BLOCKS.register("red_fence", SimpleFenceBlock::new);
    public static final RegistryObject<Block> RED_FENCE_GATE = BLOCKS.register("red_fence_gate", SimpleFenceGateBlock::new);

    public static final RegistryObject<Block> MAPLE_LOG = BLOCKS.register("maple_log", () -> new StrippableLogBlock() {@Override public Block getStrippedBlock() {return STRIPPED_MAPLE_LOG.get();}});
    public static final RegistryObject<Block> STRIPPED_MAPLE_LOG = BLOCKS.register("stripped_maple_log", SimpleLogBlock::new);
    public static final RegistryObject<Block> MAPLE_WOOD = BLOCKS.register("maple_wood", SimpleWoodBlock::new);
    public static final RegistryObject<Block> MAPLE_PLANKS = BLOCKS.register("maple_planks", SimplePlanksBlock::new);
    public static final RegistryObject<Block> MAPLE_STAIRS = BLOCKS.register("maple_stairs", SimpleStairsBlock::new);
    public static final RegistryObject<Block> MAPLE_SLAB = BLOCKS.register("maple_slab", SimpleSlabBlock::new);
    public static final RegistryObject<Block> MAPLE_FENCE = BLOCKS.register("maple_fence", SimpleFenceBlock::new);
    public static final RegistryObject<Block> MAPLE_FENCE_GATE = BLOCKS.register("maple_fence_gate", SimpleFenceGateBlock::new);
    public static final RegistryObject<Block> MAPLE_BUTTON = BLOCKS.register("maple_button", SimpleButtonBlock::new);

    public static final RegistryObject<Block> RED_ADVANCED_FENCE = BLOCKS.register("advanced_red_fence", () -> new BlockAdvancedFence(WoodTypes.RED));
    public static final RegistryObject<Block> GOLD_FENCE_DECORATION = BLOCKS.register("gold_fence_decoration", BlockFenceDecoration::new);
    public static final RegistryObject<Block> RED_FENCE_EXPANSION = BLOCKS.register("red_fence_expansion", () -> new BlockFenceExpansion(WoodTypes.RED));

    public static final RegistryObject<FlowingFluidBlock> SOY_MILK_BLOCK = BLOCKS.register("soy_milk", () ->
        new FlowingFluidBlock(FluidRegistryHandler.SOY_MILK, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static final RegistryObject<FlowingFluidBlock> OIL_BLOCK = BLOCKS.register("oil", () ->
        new FlowingFluidBlock(FluidRegistryHandler.OIL, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
}
