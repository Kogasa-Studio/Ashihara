package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.*;
import kogasastudio.ashihara.block.CandleBlock;
import kogasastudio.ashihara.block.LanternBlock;
import kogasastudio.ashihara.block.building.*;
import kogasastudio.ashihara.block.blockentity.*;
import kogasastudio.ashihara.block.WoodenBasinBlock;
import kogasastudio.ashihara.block.FermentationVatBlock;
import kogasastudio.ashihara.block.LargeFermentationVatBlock;
import kogasastudio.ashihara.block.trees.TreeGrowers;
import kogasastudio.ashihara.block.woodcraft.*;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import kogasastudio.ashihara.utils.WallTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

@SuppressWarnings("all")
public class Blocks
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Ashihara.MODID);

    public static ResourceKey<Block> getResourceKey(String name)
    {
        return ResourceKey.create(BLOCKS.getRegistryKey(), Identifier.fromNamespaceAndPath(Ashihara.MODID, name));
    }

    public static final DeferredBlock<Block> WATER_FIELD = BLOCKS.registerBlock("water_field", PaddyFieldBlock::new, properties -> properties.mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL));
    public static final DeferredBlock<Block> RICE_CROP = BLOCKS.registerBlock("rice_crop", RiceCropBlock::new, properties -> properties.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.CROP));
    public static final DeferredBlock<Block> DIRT_DEPRESSION = BLOCKS.registerBlock("dirt_depression", DirtDepressionBlock::new, properties -> properties.mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL).noOcclusion());
    public static final DeferredBlock<Block> TETSUSENCHI = BLOCKS.registerBlock("tetsusenchi", TetsusenchiBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<Block> RICE_DRYING_STICKS = BLOCKS.registerBlock("rice_drying_sticks", RiceDryingSticksBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2).sound(SoundType.WOOD).noOcclusion().lightLevel(i -> 1));
    public static final DeferredBlock<Block> CHERRY_SAPLING = BLOCKS.registerBlock("cherry_sapling", properties -> new SaplingBlock(TreeGrowers.CHERRY_BLOSSOM, properties), properties -> properties.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.GRASS));
    public static final DeferredBlock<Block> POTTED_CHERRY_SAPLING = BLOCKS.registerBlock("potted_cherry_sapling", properties -> new FlowerPotBlock(Blocks.CHERRY_SAPLING.get(), properties), properties -> properties.instabreak().noOcclusion());
    public static final DeferredBlock<Block> RED_MAPLE_SAPLING = BLOCKS.registerBlock("red_maple_sapling", properties -> new SaplingBlock(TreeGrowers.RED_MAPLE, properties), properties -> properties.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.GRASS));
    public static final DeferredBlock<Block> POTTED_RED_MAPLE_SAPLING = BLOCKS.registerBlock("potted_red_maple_sapling", properties -> new FlowerPotBlock(Blocks.RED_MAPLE_SAPLING.get(), properties), properties -> properties.instabreak().noOcclusion());
    public static final DeferredBlock<Block> MORTAR = BLOCKS.registerBlock("mortar", MortarBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(3.0F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<Block> POT = BLOCKS.registerBlock("pot", PotBlock::new, properties -> properties.noOcclusion().mapColor(MapColor.TERRACOTTA_CYAN).strength(0.5F).sound(SoundType.LANTERN));
    public static final DeferredBlock<Block> IMMATURE_RICE = BLOCKS.registerBlock("immature_rice", ImmatureRiceCropBlock::new, properties -> properties.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.CROP));
    public static final DeferredBlock<Block> CHRYSANTHEMUM = BLOCKS.registerBlock("chrysanthemum", ChrysanthemumBushBlock::new, properties -> properties.mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XYZ));
    public static final DeferredBlock<Block> WILD_RICE = BLOCKS.registerBlock("wild_rice", properties -> new ChrysanthemumBushBlock(properties)
    {
        @Override
        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
        {
            return box(2,0,2,14,16,14);
        }
    }, properties -> properties.mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XYZ));
    public static final DeferredBlock<Block> REED = BLOCKS.registerBlock("reed", ReedBlock::new, properties -> properties.mapColor(MapColor.PLANT).offsetType(BlockBehaviour.OffsetType.XZ).noCollision().instabreak().sound(SoundType.GRASS).noOcclusion());
    public static final DeferredBlock<Block> SHORTER_REED = BLOCKS.registerBlock("shorter_reed", ShorterReedBlock::new, properties -> properties.mapColor(MapColor.PLANT).offsetType(BlockBehaviour.OffsetType.XZ).noCollision().instabreak().sound(SoundType.GRASS).noOcclusion());
    public static final DeferredBlock<Block> MILL = BLOCKS.registerBlock("mill", MillBlock::new, properties -> properties.mapColor(MapColor.STONE).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> DIRT_COOKSTOVE = BLOCKS.registerBlock("dirt_cookstove", DirtCookStoveBlock::new, properties -> properties.noOcclusion().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.DRIPSTONE_BLOCK).lightLevel(state -> state.getValue(DirtCookStoveBlock.BURNING) ? 15 : 0));
    public static final DeferredBlock<Block> HYDRANGEA_BUSH = BLOCKS.registerBlock("hydrangea_bush", properties -> new HydrangeaBushBlock(properties, false), properties -> properties.mapColor(MapColor.PLANT).strength(0.05F).sound(SoundType.GRASS).noOcclusion().randomTicks().lightLevel((state) -> 1));
    public static final DeferredBlock<Block> PAIL = BLOCKS.registerBlock("pail", PailBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(3.0F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<Block> TEA_TREE = BLOCKS.registerBlock("tea_tree", TeaTreeBlock::new, properties -> properties.mapColor(MapColor.PLANT).noCollision().randomTicks().strength(0.2F).sound(SoundType.SWEET_BERRY_BUSH));
    public static final DeferredBlock<Block> MEAL_TABLE = BLOCKS.registerBlock("meal_table", MealTableBlock::new, properties -> properties.mapColor(MapColor.WOOD).sound(SoundType.BAMBOO).strength(0.5f));
    public static final DeferredBlock<Block> CUTTING_BOARD = BLOCKS.registerBlock("cutting_board", CuttingBoardBlock::new, properties -> properties.mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(0.4F));
    public static final DeferredBlock<Block> CHERRY_VINES = BLOCKS.registerBlock("cherry_vines", CherryVinesBlock::new, properties -> properties.mapColor(DyeColor.PINK).strength(0.05f).sound(SoundType.PINK_PETALS).noOcclusion().noCollision().forceSolidOn());
    public static final DeferredBlock<Block> FALLEN_SAKURA = BLOCKS.registerBlock("fallen_sakura", AbstractFallenLeavesBlock::new, properties -> properties.mapColor(MapColor.COLOR_PINK).strength(0.1F).sound(SoundType.CHERRY_SAPLING).noOcclusion().noCollision());
    //建筑
    public static final DeferredBlock<Block> CHERRY_BLOSSOM = BLOCKS.registerBlock("cherry_blossom", properties -> new AbstractFallingLeavesBlock
    (
        5, false,
        properties
    )
    {
        @Override
        protected Block getFallenBlock()
        {
            return FALLEN_SAKURA.get();
        }

        @Override
        protected SimpleParticleType getParticle()
        {
            return ParticleRegistryHandler.SAKURA.get();
        }

        @Override
        protected List<ItemStack> getBonusResource()
        {
            ArrayList<ItemStack> list = new ArrayList<>();
            list.add(new ItemStack(Items.SAKURA.get(), Ashihara.RANDOM.nextInt(1, 2)));
            list.add(new ItemStack(Items.SAKURA_PETAL.get(), Ashihara.RANDOM.nextInt(1, 3)));
            return list;
        }
    }, properties -> properties.mapColor(MapColor.COLOR_PINK).strength(0.05F).randomTicks().sound(SoundType.CHERRY_SAPLING).noOcclusion());
    public static final DeferredBlock<Block> FALLEN_MAPLE_LEAVES_RED = BLOCKS.registerBlock("fallen_maple_leaves_red", AbstractFallenLeavesBlock::new, properties -> properties.mapColor(MapColor.TERRACOTTA_RED).strength(0.1F).sound(SoundType.GRASS).noOcclusion().noCollision());
    public static final DeferredBlock<Block> MAPLE_LEAVES_RED = BLOCKS.registerBlock("maple_leaves_red", properties -> new AbstractFallingLeavesBlock
    (
        properties
    )
    {
        @Override
        protected Block getFallenBlock()
        {
            return FALLEN_MAPLE_LEAVES_RED.get();
        }

        @Override
        protected SimpleParticleType getParticle()
        {
            return ParticleRegistryHandler.MAPLE_LEAF.get();
        }
    }, properties -> properties.mapColor(MapColor.TERRACOTTA_RED).strength(0.05F).randomTicks().sound(SoundType.GRASS).noOcclusion());

    //灯具
    public static final DeferredBlock<Block> JINJA_LANTERN = BLOCKS.registerBlock("jinja_lantern", JinjaLanternBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> STONE_LANTERN = BLOCKS.registerBlock("stone_lantern", StoneLanternBlock::new, properties -> properties.mapColor(MapColor.STONE).strength(4.0F).sound(SoundType.STONE).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> BONBURI_LAMP = BLOCKS.registerBlock("bonburi_lamp", properties -> new DoubleLanternBlock.AxisAlignedVariant
            (
                            properties,
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
    }, properties -> properties.mapColor(MapColor.METAL).strength(1F).sound(SoundType.LANTERN).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> CANDLESTICK = BLOCKS.registerBlock("candlestick", properties -> new DoubleLanternBlock
            (
                    properties,
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
    }, properties -> properties.mapColor(MapColor.METAL).strength(1F).sound(SoundType.LANTERN).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> OIL_PLATE_STICK = BLOCKS.registerBlock("oil_plate_stick", properties -> new DoubleLanternBlock.FourFacingVariant
            (
                    properties,
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
    }, properties -> properties.mapColor(MapColor.METAL).strength(1F).sound(SoundType.LANTERN).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> LANTERN_LONG_WHITE = BLOCKS.registerBlock("lantern_long_white", MarkableHangingLanternBlock::new, properties -> properties.mapColor(MapColor.WOOL).strength(1.0F).sound(SoundType.BAMBOO_SAPLING).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> LANTERN_LONG_RED = BLOCKS.registerBlock("lantern_long_red", MarkableHangingLanternBlock::new, properties -> properties.mapColor(MapColor.WOOL).strength(1.0F).sound(SoundType.BAMBOO_SAPLING).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> HOUSE_LIKE_HANGING_LANTERN = BLOCKS.registerBlock("house_like_hanging_lantern", properties -> new kogasastudio.ashihara.block.LanternBlock.HangingLanternBlock
            (
                    properties,
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
    }, properties -> properties.mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.LANTERN).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> HEXAGONAL_HANGING_LANTERN = BLOCKS.registerBlock("hexagonal_hanging_lantern", properties -> new LanternBlock.HangingLanternBlock
            (
                    properties,
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
    }, properties -> properties.mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.LANTERN).lightLevel(getLightValueLit(15)));
    public static final DeferredBlock<Block> CANDLE = BLOCKS.registerBlock("candle", CandleBlock::new, properties -> properties.mapColor(MapColor.SNOW).strength(0.05F).sound(SoundType.SNOW).lightLevel(getLightValueLit(15)).noOcclusion());
    public static final DeferredBlock<Block> TATAMI = BLOCKS.registerBlock("tatami", TatamiBlock::new, properties -> properties.mapColor(MapColor.COLOR_YELLOW).strength(0.3F).sound(SoundType.BAMBOO_SAPLING));
    public static final DeferredBlock<Block> RED_THIN_BEAM = BLOCKS.registerBlock("red_thin_beam", properties -> new AbstractBeamBlock(properties)
    {
        @Override
        public AshiharaWoodTypes getType()
        {
            return AshiharaWoodTypes.RED;
        }

        @Override
        public Item getBeam()
        {
            return Items.RED_THIN_BEAM.get();
        }
    }, properties -> properties.mapColor(MapColor.WOOD).strength(0.3F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<BaseMultiBuiltBlock> BAMBOO_BONES_COMPONENT = BLOCKS.registerBlock("bamboo_bones_component", BaseMultiBuiltBlock.ComponentMaterial.BAMBOO_BONES::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.BAMBOO_BONES.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.BAMBOO_BONES.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.BAMBOO_BONES.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> RAMMED_SOIL_COMPONENT = BLOCKS.registerBlock("rammed_soil_component", BaseMultiBuiltBlock.ComponentMaterial.RAMMED_SOIL::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.RAMMED_SOIL.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.RAMMED_SOIL.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.RAMMED_SOIL.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> WHITE_SOIL_COMPONENT = BLOCKS.registerBlock("white_soil_component", BaseMultiBuiltBlock.ComponentMaterial.WHITE_SOIL::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.WHITE_SOIL.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.WHITE_SOIL.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.WHITE_SOIL.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> WHITE_WOOD_COMPONENT = BLOCKS.registerBlock("white_wood_component", BaseMultiBuiltBlock.ComponentMaterial.WHITE_WOOD::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.WHITE_WOOD.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.WHITE_WOOD.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.WHITE_WOOD.getStrength()));

    public static final DeferredBlock<BaseMultiBuiltBlock> GREEN_WOOD_COMPONENT = BLOCKS.registerBlock("green_wood_component", BaseMultiBuiltBlock.ComponentMaterial.GREEN_WOOD::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.GREEN_WOOD.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.GREEN_WOOD.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.GREEN_WOOD.getStrength()));

    public static final DeferredBlock<BaseMultiBuiltBlock> MULTI_BUILT_BLOCK = BLOCKS.registerBlock("multi_built_block", BaseMultiBuiltBlock.ComponentMaterial.OAK_WOOD::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.OAK_WOOD.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.OAK_WOOD.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.OAK_WOOD.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> SPRUCE_WOOD_COMPONENT = BLOCKS.registerBlock("spruce_wood_component", BaseMultiBuiltBlock.ComponentMaterial.SPRUCE_WOOD::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.SPRUCE_WOOD.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.SPRUCE_WOOD.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.SPRUCE_WOOD.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> RED_WOOD_COMPONENT = BLOCKS.registerBlock("red_wood_component", BaseMultiBuiltBlock.ComponentMaterial.RED_WOOD::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.RED_WOOD.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.RED_WOOD.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.RED_WOOD.getStrength()));

    public static final DeferredBlock<BaseMultiBuiltBlock> GOLD_DECO_COMPONENT = BLOCKS.registerBlock("gold_deco_component", BaseMultiBuiltBlock.ComponentMaterial.GOLD_DECO::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.GOLD_DECO.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.GOLD_DECO.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.GOLD_DECO.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> GOLD_STRUCTURAL_COMPONENT = BLOCKS.registerBlock("gold_structural_component", BaseMultiBuiltBlock.ComponentMaterial.GOLD_STRUCTURAL::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.GOLD_STRUCTURAL.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.GOLD_STRUCTURAL.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.GOLD_STRUCTURAL.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> STONE_COMPONENT = BLOCKS.registerBlock("stone_component", BaseMultiBuiltBlock.ComponentMaterial.STONE::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.STONE.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.STONE.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.STONE.getStrength()));

    public static final DeferredBlock<BaseMultiBuiltBlock> CYPRESS_SKIN_COMPONENT = BLOCKS.registerBlock("cypress_skin_component", BaseMultiBuiltBlock.ComponentMaterial.CYPRESS_SKIN::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.CYPRESS_SKIN.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.CYPRESS_SKIN.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.CYPRESS_SKIN.getStrength()));
    public static final DeferredBlock<BaseMultiBuiltBlock> TERRACOTTA_TILE_COMPONENT = BLOCKS.registerBlock("terracotta_tile_component", BaseMultiBuiltBlock.ComponentMaterial.TERRACOTTA_TILE::createStandardBlock, properties -> properties.sound(BaseMultiBuiltBlock.ComponentMaterial.TERRACOTTA_TILE.getSound()).forceSolidOn().mapColor(BaseMultiBuiltBlock.ComponentMaterial.TERRACOTTA_TILE.getColor()).strength(BaseMultiBuiltBlock.ComponentMaterial.TERRACOTTA_TILE.getStrength()));

    //作物
    public static final DeferredBlock<Block> SOY_BEANS = BLOCKS.registerBlock("soy_beans", properties -> new AbstractCropAge7Pickable(7, 3, properties)
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return Items.SOY_BEAN.get();
        }
    }, properties -> properties.mapColor(MapColor.PLANT).noCollision().noOcclusion().randomTicks().instabreak().lightLevel(i -> 1).sound(SoundType.CROP));
    public static final DeferredBlock<Block> SWEET_POTATOES = BLOCKS.registerBlock("sweet_potatoes", properties -> new AbstractCropAge7(properties)
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return Items.SWEET_POTATO.get();
        }
    }, properties -> properties.mapColor(MapColor.PLANT).noCollision().noOcclusion().randomTicks().instabreak().lightLevel(i -> 1).sound(SoundType.CROP));
    public static final DeferredBlock<Block> CUCUMBERS = BLOCKS.registerBlock("cucumbers", properties -> new CucumberCropBlock(properties)
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return Items.CUCUMBER.get();
        }
    }, properties -> properties.mapColor(MapColor.PLANT).noCollision().noOcclusion().randomTicks().instabreak().lightLevel(i -> 1).sound(SoundType.CROP));
    public static final DeferredBlock<Block> TARE_CROP = BLOCKS.registerBlock("tare_crop", properties -> new AbstractCropAge7(properties)
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return Items.TARE_SEED.get();
        }
    }, properties -> properties.mapColor(MapColor.PLANT).noCollision().noOcclusion().randomTicks().instabreak().lightLevel(i -> 1).sound(SoundType.CROP));
    public static final DeferredBlock<Block> SCALLION_CROP = BLOCKS.registerBlock("scallion_crop", properties -> new AbstractCropAge7(properties)
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return Items.SCALLION.get();
        }
    }, properties -> properties.mapColor(MapColor.PLANT).noCollision().noOcclusion().randomTicks().instabreak().lightLevel(i -> 1).sound(SoundType.CROP));
    public static final DeferredBlock<Block> WASABI_CROP = BLOCKS.registerBlock("wasabi_crop", properties -> new AbstractCropAge7(properties)
    {
        @Override
        protected ItemLike getBaseSeedId()
        {
            return Items.WASABI.get();
        }
    }, properties -> properties.mapColor(MapColor.PLANT).noCollision().noOcclusion().randomTicks().instabreak().lightLevel(i -> 1).sound(SoundType.CROP));
    public static final DeferredBlock<Block> STRIPPED_CHERRY_LOG = BLOCKS.registerBlock("stripped_cherry_log", SimpleLogBlock::new, properties -> properties.mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.WOOD : MapColor.STONE).strength(2.0F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> STRIPPED_CHERRY_WOOD = BLOCKS.registerBlock("stripped_cherry_wood", SimpleWoodBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(0.2F, 0.3F).sound(SoundType.CHERRY_WOOD));
    //木制品
    public static final DeferredBlock<Block> CHERRY_LOG = BLOCKS.registerBlock("cherry_log", SimpleLogBlock::new, properties -> properties.mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.WOOD : MapColor.STONE).strength(2.0F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> CHERRY_WOOD = BLOCKS.registerBlock("cherry_wood", SimpleWoodBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(0.2F, 0.3F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> CHERRY_PLANKS = BLOCKS.registerBlock("cherry_planks", SimplePlanksBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> CHERRY_STAIRS = BLOCKS.registerBlock("cherry_stairs", SimpleStairsBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> RED_STAIRS = BLOCKS.registerBlock("red_stairs", SimpleStairsBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_STAIRS = BLOCKS.registerBlock("maple_stairs", SimpleStairsBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> CHERRY_SLAB = BLOCKS.registerBlock("cherry_slab", SimpleSlabBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> CHERRY_FENCE = BLOCKS.registerBlock("cherry_fence", SimpleFenceBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> CHERRY_FENCE_GATE = BLOCKS.registerBlock("cherry_fence_gate", SimpleFenceGateBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD));
    public static final DeferredBlock<Block> CHERRY_BUTTON = BLOCKS.registerBlock("cherry_button", SimpleButtonBlock::new, properties -> properties.noCollision().pushReaction(PushReaction.DESTROY).strength(0.5F));
    public static final DeferredBlock<Block> STRIPPED_RED_LOG = BLOCKS.registerBlock("stripped_red_log", SimpleLogBlock::new, properties -> properties.mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.WOOD : MapColor.STONE).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_PLANKS = BLOCKS.registerBlock("red_planks", SimplePlanksBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_SLAB = BLOCKS.registerBlock("red_slab", SimpleSlabBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_FENCE = BLOCKS.registerBlock("red_fence", SimpleFenceBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_FENCE_GATE = BLOCKS.registerBlock("red_fence_gate", SimpleFenceGateBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_MAPLE_LOG = BLOCKS.registerBlock("stripped_maple_log", SimpleLogBlock::new, properties -> properties.mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.WOOD : MapColor.STONE).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_LOG = BLOCKS.registerBlock("maple_log", SimpleLogBlock::new, properties -> properties.mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.WOOD : MapColor.STONE).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_WOOD = BLOCKS.registerBlock("maple_wood", SimpleWoodBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(0.2F, 0.3F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_PLANKS = BLOCKS.registerBlock("maple_planks", SimplePlanksBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_SLAB = BLOCKS.registerBlock("maple_slab", SimpleSlabBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_FENCE = BLOCKS.registerBlock("maple_fence", SimpleFenceBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_FENCE_GATE = BLOCKS.registerBlock("maple_fence_gate", SimpleFenceGateBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MAPLE_BUTTON = BLOCKS.registerBlock("maple_button", SimpleButtonBlock::new, properties -> properties.noCollision().pushReaction(PushReaction.DESTROY).strength(0.5F));


    public static final DeferredBlock<Block> CYPRESS_SKIN_BLOCK = BLOCKS.registerBlock("cypress_skin_block", SimplePlanksBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> CYPRESS_SKIN_SLAB = BLOCKS.registerBlock("cypress_skin_slab", SimpleSlabBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> CYPRESS_SKIN_STAIRS = BLOCKS.registerBlock("cypress_skin_stairs", SimpleStairsBlock::new, properties -> properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_FENCE_EXPANSION = BLOCKS.registerBlock("red_fence_expansion", properties -> new FenceExpansionBlock(properties, AshiharaWoodTypes.RED), properties -> properties.mapColor(MapColor.WOOD).strength(0.2F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_ADVANCED_FENCE = BLOCKS.registerBlock("advanced_red_fence", properties -> new AdvancedFenceBlock(properties, AshiharaWoodTypes.RED)
    {
        @Override
        protected Block getExpansion()
        {
            return RED_FENCE_EXPANSION.get();
        }
    }, properties -> properties.mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> SPRUCE_FENCE_EXPANSION = BLOCKS.registerBlock("spruce_fence_expansion", properties -> new FenceExpansionBlock(properties, AshiharaWoodTypes.SPRUCE), properties -> properties.mapColor(MapColor.WOOD).strength(0.2F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> SPRUCE_ADVANCED_FENCE = BLOCKS.registerBlock("advanced_spruce_fence", properties -> new AdvancedFenceBlock(properties, AshiharaWoodTypes.SPRUCE)
    {
        @Override
        protected Block getExpansion()
        {
            return SPRUCE_FENCE_EXPANSION.get();
        }
    }, properties -> properties.mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> GOLD_FENCE_DECORATION = BLOCKS.registerBlock("gold_fence_decoration", FenceDecorationBlock::new, properties -> properties.mapColor(MapColor.GOLD).strength(0.2F).sound(SoundType.LANTERN).noOcclusion());
    public static final DeferredBlock<Block> RED_THICK_COLUMN = BLOCKS.registerBlock("red_thick_column", properties -> new ColumnBlock(properties, AshiharaWoodTypes.RED), properties -> properties.mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_KUMIMONO = BLOCKS.registerBlock("red_kumimono", properties -> new KumimonoBlock(properties, AshiharaWoodTypes.RED), properties -> properties.mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> RED_KAWAKI = BLOCKS.registerBlock("red_kawaki", properties -> new KawakiBlock(properties, AshiharaWoodTypes.RED), properties -> properties.mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> THIN_WHITE_SOIL_WALL = BLOCKS.registerBlock("thin_white_soil_wall", properties -> new AbstractWallBlock(properties)
    {
        @Override
        public WallTypes getType()
        {
            return WallTypes.WHITE_SOIL;
        }
    }, properties -> properties.mapColor(DyeColor.WHITE).strength(1.5f).sound(SoundType.BAMBOO));
    public static final DeferredBlock<Block> STRAIGHT_BAR_WINDOW_GREEN = BLOCKS.registerBlock("straight_bar_window_green", StraightBarWindowBlock::new, properties -> properties.mapColor(DyeColor.GREEN).strength(1.5f).sound(SoundType.BAMBOO));
    public static final DeferredBlock<Block> CHARLOTTE = BLOCKS.registerBlock("charlotte", CharlotteBlock::new, properties -> properties.noOcclusion().strength(1.0F).mapColor(DyeColor.PINK).sound(SoundType.WOOL));

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Ashihara.MODID);

    public static final DeferredBlock<LiquidBlock> SOY_MILK_BLOCK = BLOCKS.registerBlock("soy_milk", properties -> new LiquidBlock(FluidRegistryHandler.SOY_MILK.get(), properties), properties -> properties.mapColor(MapColor.SNOW).noCollision().strength(100.0F).replaceable().noLootTable());
    public static final DeferredBlock<LiquidBlock> OIL_BLOCK = BLOCKS.registerBlock("oil", properties -> new LiquidBlock(FluidRegistryHandler.OIL.get(), properties), properties -> properties.mapColor(MapColor.COLOR_YELLOW).noCollision().strength(100.0F).replaceable().noLootTable());

    // 发酵容器
    public static final DeferredBlock<Block> WOODEN_BASIN = BLOCKS.registerBlock("wooden_basin", p -> new WoodenBasinBlock(p));
    public static final DeferredBlock<Block> FERMENTATION_VAT = BLOCKS.registerBlock("fermentation_vat", p -> new FermentationVatBlock(p));
    public static final DeferredBlock<Block> LARGE_FERMENTATION_VAT = BLOCKS.registerBlock("large_fermentation_vat", p -> new LargeFermentationVatBlock(p));
}