package kogasastudio.ashihara.block;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.building.AbstractBeamBlock;
import kogasastudio.ashihara.block.building.ColumnBlock;
import kogasastudio.ashihara.block.building.AbstractWallBlock;
import kogasastudio.ashihara.block.building.StraightBarWindowBlock;
import kogasastudio.ashihara.block.trees.CherryBlossomTreeGrower;
import kogasastudio.ashihara.block.trees.RedMapleTreeGrower;
import kogasastudio.ashihara.block.woodcrafts.*;
import kogasastudio.ashihara.client.particles.GenericParticleType;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import kogasastudio.ashihara.utils.WallTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

public class BlockRegistryHandler
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ashihara.MODID);

    public static final RegistryObject<Block> WATER_FIELD = BLOCKS.register("water_field", PaddyFieldBlock::new);
    public static final RegistryObject<Block> RICE_CROP = BLOCKS.register("rice_crop", RiceCropBlock::new);
    public static final RegistryObject<Block> DIRT_DEPRESSION = BLOCKS.register("dirt_depression", DirtDepressionBlock::new);
    public static final RegistryObject<Block> TETSUSENCHI = BLOCKS.register("tetsusenchi", TetsusenchiBlock::new);
    public static final RegistryObject<Block> CHERRY_SAPLING = BLOCKS.register("cherry_sapling", () -> new SaplingBlock(new CherryBlossomTreeGrower(), BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> POTTED_CHERRY_SAPLING = BLOCKS.register("potted_cherry_sapling", () -> new FlowerPotBlock(BlockRegistryHandler.CHERRY_SAPLING.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion()));
    public static final RegistryObject<Block> RED_MAPLE_SAPLING = BLOCKS.register("red_maple_sapling", () -> new SaplingBlock(new RedMapleTreeGrower(), BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> POTTED_RED_MAPLE_SAPLING = BLOCKS.register("potted_red_maple_sapling", () -> new FlowerPotBlock(BlockRegistryHandler.RED_MAPLE_SAPLING.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion()));
    public static final RegistryObject<Block> MORTAR = BLOCKS.register("mortar", MortarBlock::new);
    public static final RegistryObject<Block> IMMATURE_RICE = BLOCKS.register("immature_rice", ImmatureRiceCropBlock::new);
    public static final RegistryObject<Block> CHRYSANTHEMUM = BLOCKS.register("chrysanthemum", ChrysanthemumBushBlock::new);
    public static final RegistryObject<Block> REED = BLOCKS.register("reed", ReedBlock::new);
    public static final RegistryObject<Block> SHORTER_REED = BLOCKS.register("shorter_reed", ShorterReedBlock::new);
    public static final RegistryObject<Block> MILL = BLOCKS.register("mill", MillBlock::new);
    public static final RegistryObject<Block> HYDRANGEA_BUSH = BLOCKS.register("hydrangea_bush", () -> new HydrangeaBushBlock(false));
    public static final RegistryObject<Block> PAIL = BLOCKS.register("pail", PailBlock::new);
    public static final RegistryObject<Block> TEA_TREE = BLOCKS.register("tea_tree", TeaTreeBlock::new);
    public static final RegistryObject<Block> MEAL_TABLE = BLOCKS.register("meal_table", MealTableBlock::new);
    public static final RegistryObject<Block> CUTTING_BOARD = BLOCKS.register("cutting_board", CuttingBoardBlock::new);
    public static final RegistryObject<Block> CHERRY_BLOSSOM_VINES = BLOCKS.register("cherry_blossom_vines", CherryBlossomVinesBlock::new);
    public static final RegistryObject<Block> FALLEN_SAKURA = BLOCKS.register("fallen_sakura", AbstractFallenLeavesBlock::new);
    //建筑
    public static final RegistryObject<Block> CHERRY_BLOSSOM = BLOCKS.register("cherry_blossom", () -> new AbstractFallingLeavesBlock(5, false)
    {
        @Override
        protected Block getFallenBlock()
        {
            return FALLEN_SAKURA.get();
        }

        @Override
        protected GenericParticleType getParticle()
        {
            return ParticleRegistryHandler.SAKURA.get();
        }

        @Override
        protected List<ItemStack> getBonusResource()
        {
            ArrayList<ItemStack> list = new ArrayList<>();
            list.add(new ItemStack(ItemRegistryHandler.SAKURA.get(), Ashihara.RANDOM.nextInt(1,2)));
            list.add(new ItemStack(ItemRegistryHandler.SAKURA_PETAL.get(), Ashihara.RANDOM.nextInt(1,3)));
            return list;
        }
    });
    public static final RegistryObject<Block> FALLEN_MAPLE_LEAVES_RED = BLOCKS.register("fallen_maple_leaves_red", AbstractFallenLeavesBlock::new);
    public static final RegistryObject<Block> MAPLE_LEAVES_RED = BLOCKS.register("maple_leaves_red", () -> new AbstractFallingLeavesBlock()
    {
        @Override
        protected Block getFallenBlock()
        {
            return FALLEN_MAPLE_LEAVES_RED.get();
        }

        @Override
        protected GenericParticleType getParticle()
        {
            return ParticleRegistryHandler.MAPLE_LEAF.get();
        }
    });

    //灯具
    public static final RegistryObject<Block> JINJA_LANTERN = BLOCKS.register("jinja_lantern", JinjaLanternBlock::new);
    public static final RegistryObject<Block> STONE_LANTERN = BLOCKS.register("stone_lantern", StoneLanternBlock::new);
    public static final RegistryObject<Block> BONBURI_LAMP = BLOCKS.register("bonburi_lamp", () -> new DoubleLanternBlock.AxisAlignedVariant
            (
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(1F)
                            .sound(SoundType.LANTERN)
                            .lightLevel(getLightValueLit(15)),
                            0.5d, 7.5d / 16d, 0.5d
            )
    {
        @Override
        public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
        {
            VoxelShape upper = Block.box(3,0,3,13,15,13);

            VoxelShape lower1 = Block.box(5,0,5,11,2.25,11);
            VoxelShape lower2 = Block.box(7,2.25,7,9,2.75,9);
            VoxelShape lower3 = Block.box(7.25,2.75,7.25,8.75,3.75,8.75);
            VoxelShape lower4 = Block.box(7.5,3.75,7.5,8.5,16,8.5);

            VoxelShape lower = Shapes.or(lower1, lower2, lower3, lower4);

            return state.getValue(HALF).equals(DoubleBlockHalf.UPPER) ? upper : lower;
        }
    });
    public static final RegistryObject<Block> CANDLESTICK = BLOCKS.register("candlestick", () -> new DoubleLanternBlock
            (
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(1F)
                            .sound(SoundType.LANTERN)
                            .lightLevel(getLightValueLit(15)),
                    0.5d, 9d / 16d, 0.5d
            )
    {
        @Override
        public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
        {
            VoxelShape upper1 = Block.box(4.5,0,4.5,11.5,1.5,11.5);
            VoxelShape upper2 = Block.box(7.4,1.5,7.4,8.6,6,8.6);

            VoxelShape lower1 = Block.box(5,0,5,11,2.25,11);
            VoxelShape lower2 = Block.box(7,2.25,7,9,2.75,9);
            VoxelShape lower3 = Block.box(7.25,2.75,7.25,8.75,3.75,8.75);
            VoxelShape lower4 = Block.box(7.5,3.75,7.5,8.5,16,8.5);

            VoxelShape lower = Shapes.or(lower1, lower2, lower3, lower4);
            VoxelShape upper = Shapes.or(upper1, upper2);

            return state.getValue(HALF).equals(DoubleBlockHalf.UPPER) ? upper : lower;
        }
    });
    public static final RegistryObject<Block> OIL_PLATE_STICK = BLOCKS.register("oil_plate_stick", () -> new DoubleLanternBlock.FourFacingVariant
            (
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(1F)
                            .sound(SoundType.LANTERN)
                            .lightLevel(getLightValueLit(15)),
                    12d, 9d / 16d, 0.5d
            )
    {

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
        {
            VoxelShape upper = Block.box(4.5,0,4.5,11.5,4.5,11.5);

            VoxelShape lower1 = Block.box(5,0,5,11,2.25,11);
            VoxelShape lower2 = Block.box(7,2.25,7,9,2.75,9);
            VoxelShape lower3 = Block.box(7.25,2.75,7.25,8.75,3.75,8.75);
            VoxelShape lower4 = Block.box(7.5,3.75,7.5,8.5,16,8.5);

            VoxelShape lower = Shapes.or(lower1, lower2, lower3, lower4);

            return state.getValue(HALF).equals(DoubleBlockHalf.UPPER) ? upper : lower;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
        {
            if (stateIn.getValue(HALF).equals(DoubleBlockHalf.LOWER)) return;
            if (stateIn.getValue(LIT))
            {
                double xOffset;
                double zOffset;

                switch (stateIn.getValue(FACING))
                {
                    case SOUTH ->
                    {
                        xOffset = 4d;
                        zOffset = 8d;
                    }
                    case EAST ->
                    {
                        xOffset = 8d;
                        zOffset = 12d;
                    }
                    case WEST ->
                    {
                        xOffset = 8d;
                        zOffset = 4d;
                    }
                    default ->
                    {
                        xOffset = 12d;
                        zOffset = 8d;
                    }
                }

                double x = (double) pos.getX() + xOffset / 16d;
                double y = (double) pos.getY() + 7.5d / 16d;
                double z = (double) pos.getZ() + zOffset / 16d;
                worldIn.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    });
    public static final RegistryObject<Block> LANTERN_LONG_WHITE = BLOCKS.register("lantern_long_white", MarkableHangingLanternBlock::new);
    public static final RegistryObject<Block> LANTERN_LONG_RED = BLOCKS.register("lantern_long_red", MarkableHangingLanternBlock::new);
    public static final RegistryObject<Block> HOUSE_LIKE_HANGING_LANTERN = BLOCKS.register("house_like_hanging_lantern", () -> new LanternBlock.HangingLanternBlock
            (
                    BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(getLightValueLit(15)),
                    0.5d, 0.6875d, 0.5d
            )
    {
        @Override
        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
        {
            VoxelShape shape1 = Block.box(3,4.5,3,13,10,13);
            VoxelShape shape2 = Block.box(0,10,0,16,15,16);
            return Shapes.or(shape1, shape2);
        }
    });
    public static final RegistryObject<Block> HEXAGONAL_HANGING_LANTERN = BLOCKS.register("hexagonal_hanging_lantern", () -> new LanternBlock.HangingLanternBlock
            (
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(2.0F)
                            .sound(SoundType.LANTERN)
                            .lightLevel(getLightValueLit(15)),
                    0.5d, 0.375d, 0.5d
            )
    {
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            builder.add(BlockStateProperties.HORIZONTAL_AXIS);
            super.createBlockStateDefinition(builder);
        }

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context)
        {
            return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, context.getHorizontalDirection().getAxis());
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
        {
            VoxelShape shape1 = Block.box(1,-3.5,1,15,12.5,15);
            VoxelShape shape2 = Block.box(-1.5,12.5,-1.5,17.5,16,17.5);
            return Shapes.or(shape1, shape2);
        }
    });
    public static final RegistryObject<Block> CANDLE = BLOCKS.register("candle", CandleBlock::new);
    public static final RegistryObject<Block> TATAMI = BLOCKS.register("tatami", TatamiBlock::new);
    public static final RegistryObject<Block> RED_BEAM = BLOCKS.register("red_beam", () -> new AbstractBeamBlock()
    {
        @Override
        public AshiharaWoodTypes getType()
        {
            return AshiharaWoodTypes.RED;
        }

        @Override
        public Item getBeam()
        {
            return ItemRegistryHandler.RED_BEAM.get();
        }
    });

    //作物
    public static final RegistryObject<Block> SOY_BEANS = BLOCKS.register("soy_beans", () -> new AbstractCropAge7Pickable(7, 3)
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return ItemRegistryHandler.SOY_BEAN.get();
        }
    });
    public static final RegistryObject<Block> SWEET_POTATOES = BLOCKS.register("sweet_potatoes", () -> new AbstractCropAge7()
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return ItemRegistryHandler.SWEET_POTATO.get();
        }
    });
    public static final RegistryObject<Block> CUCUMBERS = BLOCKS.register("cucumbers", () -> new CucumberCropBlock()
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return ItemRegistryHandler.CUCUMBER.get();
        }
    });
    public static final RegistryObject<Block> STRIPPED_CHERRY_LOG = BLOCKS.register("stripped_cherry_log", SimpleLogBlock::new);
    //木制品
    public static final RegistryObject<Block> CHERRY_LOG = BLOCKS.register("cherry_log", () -> new StrippableLogBlock()
    {
        @Override
        public Block getStrippedBlock()
        {
            return STRIPPED_CHERRY_LOG.get();
        }
    });
    public static final RegistryObject<Block> CHERRY_WOOD = BLOCKS.register("cherry_wood", SimpleWoodBlock::new);
    public static final RegistryObject<Block> CHERRY_PLANKS = BLOCKS.register("cherry_planks", SimplePlanksBlock::new);
    public static final RegistryObject<Block> CHERRY_STAIRS = BLOCKS.register("cherry_stairs", SimpleStairsBlock::new);
    public static final RegistryObject<Block> RED_STAIRS = BLOCKS.register("red_stairs", SimpleStairsBlock::new);
    public static final RegistryObject<Block> MAPLE_STAIRS = BLOCKS.register("maple_stairs", SimpleStairsBlock::new);
    public static final RegistryObject<Block> CHERRY_SLAB = BLOCKS.register("cherry_slab", SimpleSlabBlock::new);
    public static final RegistryObject<Block> CHERRY_FENCE = BLOCKS.register("cherry_fence", SimpleFenceBlock::new);
    public static final RegistryObject<Block> CHERRY_FENCE_GATE = BLOCKS.register("cherry_fence_gate", SimpleFenceGateBlock::new);
    public static final RegistryObject<Block> CHERRY_BUTTON = BLOCKS.register("cherry_button", SimpleButtonBlock::new);
    public static final RegistryObject<Block> STRIPPED_RED_LOG = BLOCKS.register("stripped_red_log", SimpleLogBlock::new);
    public static final RegistryObject<Block> RED_PLANKS = BLOCKS.register("red_planks", SimplePlanksBlock::new);
    public static final RegistryObject<Block> RED_SLAB = BLOCKS.register("red_slab", SimpleSlabBlock::new);
    public static final RegistryObject<Block> RED_FENCE = BLOCKS.register("red_fence", SimpleFenceBlock::new);
    public static final RegistryObject<Block> RED_FENCE_GATE = BLOCKS.register("red_fence_gate", SimpleFenceGateBlock::new);
    public static final RegistryObject<Block> STRIPPED_MAPLE_LOG = BLOCKS.register("stripped_maple_log", SimpleLogBlock::new);
    public static final RegistryObject<Block> MAPLE_LOG = BLOCKS.register("maple_log", () -> new StrippableLogBlock()
    {
        @Override
        public Block getStrippedBlock()
        {
            return STRIPPED_MAPLE_LOG.get();
        }
    });
    public static final RegistryObject<Block> MAPLE_WOOD = BLOCKS.register("maple_wood", SimpleWoodBlock::new);
    public static final RegistryObject<Block> MAPLE_PLANKS = BLOCKS.register("maple_planks", SimplePlanksBlock::new);
    public static final RegistryObject<Block> MAPLE_SLAB = BLOCKS.register("maple_slab", SimpleSlabBlock::new);
    public static final RegistryObject<Block> MAPLE_FENCE = BLOCKS.register("maple_fence", SimpleFenceBlock::new);
    public static final RegistryObject<Block> MAPLE_FENCE_GATE = BLOCKS.register("maple_fence_gate", SimpleFenceGateBlock::new);
    public static final RegistryObject<Block> MAPLE_BUTTON = BLOCKS.register("maple_button", SimpleButtonBlock::new);


    public static final RegistryObject<Block> CYPRESS_SKIN_BLOCK = BLOCKS.register("cypress_skin_block", SimplePlanksBlock::new);
    public static final RegistryObject<Block> CYPRESS_SKIN_SLAB = BLOCKS.register("cypress_skin_slab", SimpleSlabBlock::new);
    public static final RegistryObject<Block> CYPRESS_SKIN_STAIRS = BLOCKS.register("cypress_skin_stairs", SimpleStairsBlock::new);
    public static final RegistryObject<Block> RED_FENCE_EXPANSION = BLOCKS.register("red_fence_expansion", () -> new FenceExpansionBlock(AshiharaWoodTypes.RED));
    public static final RegistryObject<Block> RED_ADVANCED_FENCE = BLOCKS.register("advanced_red_fence", () -> new AdvancedFenceBlock(AshiharaWoodTypes.RED)
    {
        @Override
        protected Block getExpansion()
        {
            return RED_FENCE_EXPANSION.get();
        }
    });
    public static final RegistryObject<Block> SPRUCE_FENCE_EXPANSION = BLOCKS.register("spruce_fence_expansion", () -> new FenceExpansionBlock(AshiharaWoodTypes.SPRUCE));
    public static final RegistryObject<Block> SPRUCE_ADVANCED_FENCE = BLOCKS.register("advanced_spruce_fence", () -> new AdvancedFenceBlock(AshiharaWoodTypes.SPRUCE)
    {
        @Override
        protected Block getExpansion()
        {
            return SPRUCE_FENCE_EXPANSION.get();
        }
    });
    public static final RegistryObject<Block> GOLD_FENCE_DECORATION = BLOCKS.register("gold_fence_decoration", FenceDecorationBlock::new);
    public static final RegistryObject<Block> RED_THICK_COLUMN = BLOCKS.register("red_thick_column", () -> new ColumnBlock(AshiharaWoodTypes.RED));
    public static final RegistryObject<Block> RED_KUMIMONO = BLOCKS.register("red_kumimono", () -> new KumimonoBlock(AshiharaWoodTypes.RED));
    public static final RegistryObject<Block> RED_KAWAKI = BLOCKS.register("red_kawaki", () -> new KawakiBlock(AshiharaWoodTypes.RED));
    public static final RegistryObject<Block> THIN_WHITE_SOIL_WALL = BLOCKS.register("thin_white_soil_wall", () -> new AbstractWallBlock()
    {
        @Override
        public WallTypes getType()
        {
            return WallTypes.WHITE_SOIL;
        }
    });
    public static final RegistryObject<Block> STRAIGHT_BAR_WINDOW_GREEN = BLOCKS.register("straight_bar_window_green", () -> new StraightBarWindowBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).strength(1.5f).sound(SoundType.BAMBOO)));

    public static final RegistryObject<LiquidBlock> SOY_MILK_BLOCK = BLOCKS.register("soy_milk", () ->
            new LiquidBlock(FluidRegistryHandler.SOY_MILK, Block.Properties.of().mapColor(MapColor.SNOW).noCollission().strength(100.0F).replaceable().noLootTable()));
    public static final RegistryObject<LiquidBlock> OIL_BLOCK = BLOCKS.register("oil", () ->
            new LiquidBlock(FluidRegistryHandler.OIL, Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().strength(100.0F).replaceable().noLootTable()));
}
