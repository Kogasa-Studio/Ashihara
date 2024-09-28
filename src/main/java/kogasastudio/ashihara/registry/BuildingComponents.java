package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.block.building.component.*;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;
import static kogasastudio.ashihara.registry.AdditionalModels.*;

public class BuildingComponents
{
    public static final Map<String, Type> TYPE_MAP = new HashMap<>();
    public static final Map<String, BuildingComponent> COMPONENTS = new HashMap<>();

    public static final BuildingComponent RED_THICK_COLUMN = register
    (
        new Column
        (
            "red_thick_column",
            Type.BAKED_MODEL,
            AdditionalModels.RED_THICK_COLUMN,
            List.of(ItemRegistryHandler.RED_THICK_COLUMN.toStack())
        )
    );
    public static final BuildingComponent RED_THICK_COLUMN_SHORT = register
    (
        new ShortColumn
        (
            "red_thick_column_short",
            Type.BAKED_MODEL,
            AdditionalModels.RED_THICK_COLUMN_SHORT,
            List.of(ItemRegistryHandler.RED_THICK_COLUMN_SHORT.toStack())
        )
    );
    public static final BuildingComponent RED_COLUMN = register
    (
        new Column
        (
            "red_column",
            Type.BAKED_MODEL,
            AdditionalModels.RED_COLUMN,
            Shapes.box(0.34375, 0, 0.34375, 0.65625, 1, 0.65625),
            List.of(ItemRegistryHandler.RED_COLUMN.toStack())
        )
    );
    public static final BuildingComponent RED_COLUMN_SHORT = register
    (
        new ShortColumn
        (
            "red_column_short",
            Type.BAKED_MODEL,
            AdditionalModels.RED_COLUMN_SHORT,
            Shapes.box(0.34375, 0, 0.34375, 0.65625, 0.5, 0.65625),
            List.of(ItemRegistryHandler.RED_COLUMN_SHORT.toStack())
        )
    );
    public static final BuildingComponent RED_CLAMP = register
    (
        new Clamp
        (
            "red_clamp",
            Type.BAKED_MODEL,
            AdditionalModels.RED_CLAMP,
            List.of(ItemRegistryHandler.RED_CLAMP.toStack())
        )
    );
    public static final BuildingComponent RED_CLAMP_JOINT = register
    (
        new ClampJoint
        (
            "red_clamp_joint",
            Type.BAKED_MODEL,
            AdditionalModels.RED_CLAMP_JOINT,
            List.of(ItemRegistryHandler.RED_CLAMP_JOINT.toStack())
        )
    );
    public static final BuildingComponent RED_BEAM = register
    (
        new Beam
        (
            "red_beam",
            Type.BAKED_MODEL,
            AdditionalModels.RED_BEAM,
            RED_BEAM_L,
            RED_BEAM_R,
            RED_BEAM_A,
            () -> RED_CLAMP,
            List.of(ItemRegistryHandler.RED_BEAM.toStack())
        )
    );
    public static final BuildingComponent RED_BEAM_CORNER = register
    (
        new BeamOblique
        (
            "red_beam",
            Type.BAKED_MODEL,
            AdditionalModels.RED_BEAM_CORNER,
            List.of(ItemRegistryHandler.RED_BEAM_CORNER.toStack())
        )
    );
    public static final BuildingComponent RED_RAFTER_STICKING_BEAM = register
    (
        new RafterStickingBeam
        (
            "red_rafter_sticking_beam",
            Type.BAKED_MODEL,
            AdditionalModels.RED_RAFTER_STICKING_BEAM,
            List.of(ItemRegistryHandler.RED_RAFTER_STICKING_BEAM.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI = register
    (
        new Hijiki
        (
            "red_hijiki",
            Type.BAKED_MODEL,
            RED_HIJIKI_L,
            RED_HIJIKI_R,
            RED_HIJIKI_A,
            List.of(ItemRegistryHandler.RED_HIJIKI.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI_SUPPORTER = register
    (
        new HijikiStandAlong
        (
            "red_hijiki_supporter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HIJIKI_SUPPORTER,
            Shapes.box(0.375, -0.03125, -0.25, 0.625, 0.25, 0.5),
            List.of(ItemRegistryHandler.RED_HIJIKI_SUPPORTER.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI_CONNECTOR = register
    (
        new HijikiStandAlong
        (
            "red_hijiki_connector",
            Type.BAKED_MODEL,
            RED_HIJIKI_A,
            List.of(ItemRegistryHandler.RED_HIJIKI_CONNECTOR.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI_LONG = register
    (
        new HijikiLong
        (
            "red_hijiki_long",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HIJIKI_LONG,
            List.of(ItemRegistryHandler.RED_HIJIKI_LONG.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI_CORNER_NORMAL = register
    (
        new HijikiCornered
        (
            "red_hijiki_corner_normal",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HIJIKI_CORNER_NORMAL,
            RED_HIJIKI_CORNER_NORMAL_LONG,
            ItemRegistryHandler.RED_HIJIKI_CORNER_NORMAL::toStack,
            List.of(ItemRegistryHandler.RED_HIJIKI_CORNER_NORMAL.toStack())//
        )
    );
    public static final BuildingComponent RED_HIJIKI_CORNER_LONG = register
    (
        new HijikiCornered
        (
            "red_hijiki_corner_long",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HIJIKI_CORNER_LONG,
            null,
            Shapes.or
            (
                Shapes.box(0.625, -0.03125, 0.625, 1.125, 0.25, 1.125),
                Shapes.box(0.375, -0.03125, 0.375, 0.875, 0.25, 0.875),
                Shapes.box(-0.125, -0.03125, -0.125, 0.375, 0.25, 0.375),
                Shapes.box(0.125, -0.03125, 0.125, 0.625, 0.25, 0.625)
            ),
            Shapes.empty(),
            ItemRegistryHandler.RED_HIJIKI_CORNER_LONG::toStack,
            List.of(ItemRegistryHandler.RED_HIJIKI_CORNER_LONG.toStack())//
        )
    );
    public static final BuildingComponent RED_TOU = register
    (
        new Tou
        (
            "red_tou",
            Type.BAKED_MODEL,
            AdditionalModels.RED_TOU,
            List.of(ItemRegistryHandler.RED_TOU.toStack())
        )
    );
    public static final BuildingComponent RED_TOU_OBLIQUE = register
    (
        new TouOblique
        (
            "red_tou_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.RED_TOU,
            List.of(ItemRegistryHandler.RED_TOU_OBLIQUE.toStack())//
        )
    );
    public static final BuildingComponent RED_ONI_TOU = register
    (
        new TouOblique
        (
            "red_oni_tou",
            Type.BAKED_MODEL,
            AdditionalModels.RED_ONI_TOU,
            List.of(ItemRegistryHandler.RED_ONI_TOU.toStack())//
        )
    );
    public static final BuildingComponent RED_BIG_TOU = register
    (
        new BigTou
        (
            "red_big_tou",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.RED_BIG_TOU.toStack())
        )
    );

    //Additional
    public static final AdditionalComponent BASE_STONE = (AdditionalComponent) register
    (
        new BaseStone
        (
            "base_stone",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.BASE_STONE.toStack())
        )
    );
    public static final AdditionalComponent WHITE_SOIL_WALL = (AdditionalComponent) register
    (
    new Wall
        (
            "white_soil_wall",
            Type.BAKED_MODEL,
            AdditionalModels.WHITE_SOIL_WALL,
            Shapes.box(0, 0, 0.40625, 1, 1, 0.59375),
            List.of(ItemRegistryHandler.BAMBOO_WALL_BONES.toStack(), ItemRegistryHandler.RAMMED_SOIL.toStack(4), ItemRegistryHandler.LIME_POWDER.toStack(4)),
            null,
            null,
            null,
            null,
            SoundType.DEEPSLATE
        )
    );
    public static final AdditionalComponent WHITE_SOIL_WALL_QUARTER = (AdditionalComponent) register
    (
        new QuarterWall
        (
            "white_soil_wall_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.WHITE_SOIL_WALL_QUARTER,
            Shapes.box(0.25, 0, 0.40625, 0.75, 0.5, 0.59375),
            List.of(ItemRegistryHandler.BAMBOO_WALL_BONES_QUARTER.toStack(), ItemRegistryHandler.RAMMED_SOIL.toStack(), ItemRegistryHandler.LIME_POWDER.toStack()),
            null,
            null,
            null,
            null,
            SoundType.DEEPSLATE
        )
    );
    public static final AdditionalComponent RAMMED_SOIL_WALL = (AdditionalComponent) register
    (
        new Wall
        (
            "rammed_soil_wall",
            Type.BAKED_MODEL,
            AdditionalModels.RAMMED_SOIL_WALL,
            Shapes.box(0, 0, 0.40625, 1, 1, 0.59375),
            List.of(ItemRegistryHandler.BAMBOO_WALL_BONES.toStack(), ItemRegistryHandler.RAMMED_SOIL.toStack(4)),
            () -> ItemRegistryHandler.LIME_POWDER.toStack(4),
            () -> WHITE_SOIL_WALL,
            Shapes.box(0, 0, 0.40625, 1, 1, 0.59375),
            AdditionalModels.WHITE_SOIL_WALL,
            SoundType.DRIPSTONE_BLOCK,
            SoundType.SOUL_SAND
        )
    );
    public static final AdditionalComponent RAMMED_SOIL_WALL_QUARTER = (AdditionalComponent) register
    (
        new QuarterWall
        (
            "rammed_soil_wall_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.RAMMED_SOIL_WALL_QUARTER,
            Shapes.box(0.25, 0, 0.40625, 0.75, 0.5, 0.59375),
            List.of(ItemRegistryHandler.BAMBOO_WALL_BONES_QUARTER.toStack(), ItemRegistryHandler.RAMMED_SOIL.toStack()),
            ItemRegistryHandler.LIME_POWDER::toStack,
            () -> WHITE_SOIL_WALL_QUARTER,
            Shapes.box(0.25, 0, 0.40625, 0.75, 0.5, 0.59375),
            AdditionalModels.WHITE_SOIL_WALL_QUARTER,
            SoundType.DRIPSTONE_BLOCK,
            SoundType.SOUL_SAND
        )
    );
    public static final AdditionalComponent BAMBOO_WALL_BONES = (AdditionalComponent) register
    (
        new Wall
        (
            "bamboo_wall_bones",
            Type.BAKED_MODEL,
            AdditionalModels.BAMBOO_WALL_BONES,
            Shapes.box(0, 0, 0.4375, 1, 1, 0.5625),
            List.of(ItemRegistryHandler.BAMBOO_WALL_BONES.toStack()),
            () -> ItemRegistryHandler.RAMMED_SOIL.toStack(4),
            () -> RAMMED_SOIL_WALL,
            Shapes.box(0, 0, 0.40625, 1, 1, 0.59375),
            AdditionalModels.RAMMED_SOIL_WALL,
            SoundType.BAMBOO,
            SoundType.DRIPSTONE_BLOCK
        )
    );
    public static final AdditionalComponent BAMBOO_WALL_BONES_QUARTER = (AdditionalComponent) register
    (
        new QuarterWall
        (
            "bamboo_wall_bones_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.BAMBOO_WALL_BONES_QUARTER,
            Shapes.box(0.25, 0, 0.4375, 0.75, 0.5, 0.5625),
            List.of(ItemRegistryHandler.BAMBOO_WALL_BONES_QUARTER.toStack()),
            ItemRegistryHandler.RAMMED_SOIL::toStack,
            () -> RAMMED_SOIL_WALL_QUARTER,
            Shapes.box(0.25, 0, 0.40625, 0.75, 0.5, 0.59375),
            AdditionalModels.RAMMED_SOIL_WALL_QUARTER,
            SoundType.BAMBOO,
            SoundType.DRIPSTONE_BLOCK
        )
    );
    public static final AdditionalComponent RED_PLANKS_WALL = (AdditionalComponent) register
    (
        new Wall
        (
            "red_planks_wall",
            Type.BAKED_MODEL,
            AdditionalModels.RED_PLANKS_WALL,
            Shapes.box(0, 0, 0.40625, 1, 1, 0.59375),
            List.of(ItemRegistryHandler.RED_PLANKS_WALL.toStack()),
            null,
            null,
            null,
            null,
            SoundType.WOOD
        )
    );
    public static final AdditionalComponent RED_PLANKS_WALL_QUARTER = (AdditionalComponent) register
    (
        new QuarterWall
        (
            "red_planks_wall_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_PLANKS_WALL_QUARTER,
            Shapes.box(0.25, 0, 0.40625, 0.75, 0.5, 0.59375),
            List.of(ItemRegistryHandler.RED_PLANKS_WALL_QUARTER.toStack()),
            null,
            null,
            null,
            null,
            SoundType.WOOD
        )
    );
    public static final BuildingComponent GREEN_STRAIGHT_BAR_WINDOW = register
    (
        new StraightBarWindow
        (
            "green_straight_bar_window",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.GREEN_STRAIGHT_BAR_WINDOW.toStack()),
            GREEN_STRAIGHT_BAR_WINDOW_ALL,
            GREEN_STRAIGHT_BAR_WINDOW_DOWN,
            GREEN_STRAIGHT_BAR_WINDOW_UP,
            AdditionalModels.GREEN_STRAIGHT_BAR_WINDOW,
            "green_straight_bar_window_quarter"
        )
    );
    public static final BuildingComponent GREEN_STRAIGHT_BAR_WINDOW_QUARTER = register
    (
        new QuarterStraightBarWindow
        (
            "green_straight_bar_window_quarter",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.GREEN_STRAIGHT_BAR_WINDOW_QUARTER.toStack()),
            GREEN_STRAIGHT_BAR_WINDOW_ALL_QUARTER,
            GREEN_STRAIGHT_BAR_WINDOW_DOWN_QUARTER,
            GREEN_STRAIGHT_BAR_WINDOW_UP_QUARTER,
            AdditionalModels.GREEN_STRAIGHT_BAR_WINDOW_QUARTER,
            "green_straight_bar_window"
        )
    );
    public static final AdditionalComponent RED_LATTICED_WINDOW = (AdditionalComponent) register
    (
        new Wall
        (
            "red_latticed_window",
            Type.BAKED_MODEL,
            AdditionalModels.RED_LATTICED_WINDOW,
            Shapes.box(0, 0, 0.4375, 1, 1, 0.5625),
            List.of(ItemRegistryHandler.RED_LATTICED_WINDOW.toStack()),
            null,
            null,
            null,
            null,
            SoundType.WOOD
        )
    );
    public static final AdditionalComponent RED_LATTICED_WINDOW_QUARTER = (AdditionalComponent) register
    (
        new QuarterWall
        (
            "red_latticed_window_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_LATTICED_WINDOW_QUARTER,
            Shapes.box(0.25, 0, 0.4375, 0.75, 0.5, 0.5625),
            List.of(ItemRegistryHandler.RED_LATTICED_WINDOW_QUARTER.toStack()),
            null,
            null,
            null,
            null,
            SoundType.WOOD
        )
    );
    public static final BuildingComponent RED_HANGING_STICKER = register
    (
        new HangingSticker
        (
            "red_hanging_sticker",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HANGING_STICKER,
            RED_HANGING_STICKER_END,
            List.of(ItemRegistryHandler.RED_HANGING_STICKER.toStack())
        )
    );
    public static final BuildingComponent RED_HANGING_STICKER_CORNER = register
    (
        new HangingStickerOblique
        (
            "red_hanging_sticker_corner",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HANGING_STICKER_CORNER,
            RED_HANGING_STICKER_END_CORNER,
            List.of(ItemRegistryHandler.RED_HANGING_STICKER_CORNER.toStack())
        )
    );
    public static final BuildingComponent RED_PLANKS_FLOOR = register
        (
        new Floor
        (
            "red_planks_floor",
            Type.BAKED_MODEL,
            AdditionalModels.RED_PLANKS_FLOOR,
            List.of(ItemRegistryHandler.RED_PLANKS_FLOOR.toStack())
        )
    );
    public static final BuildingComponent RED_PLANKS_FLOOR_QUARTER = register
    (
        new QuarterFloor
        (
            "red_planks_floor_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_PLANKS_FLOOR_QUARTER,
            List.of(ItemRegistryHandler.RED_PLANKS_FLOOR_QUARTER.toStack())
        )
    );
    public static final BuildingComponent RED_LATTICED_CEILING_WHITE = register
        (
        new Floor
        (
            "red_latticed_ceiling_white",
            Type.BAKED_MODEL,
            AdditionalModels.RED_LATTICED_CEILING_WHITE,
            Shapes.box(0, XTP(2), 0, 1, 0.25625, 1),
            List.of(ItemRegistryHandler.RED_LATTICED_CEILING_WHITE.toStack())
        )
    );
    public static final BuildingComponent RED_LATTICED_CEILING_WHITE_QUARTER = register
    (
        new QuarterFloor
        (
            "red_latticed_ceiling_white_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_LATTICED_CEILING_WHITE_QUARTER,
            Shapes.box(0.25, XTP(2), 0.25, 0.75, 0.25625, 0.75),
            List.of(ItemRegistryHandler.RED_LATTICED_CEILING_WHITE_QUARTER.toStack())
        )
    );
    public static final BuildingComponent RED_BENDED_STICKERS = register
    (
        new BendedSticker
        (
            "red_bended_stickers",
            Type.BAKED_MODEL,
            AdditionalModels.RED_BENDED_STICKERS,
            List.of(ItemRegistryHandler.RED_BENDED_STICKERS.toStack())
        )
    );
    public static final AdditionalComponent RED_FROG_LEGS_LIKED_STICKER = (AdditionalComponent) register
    (
        new Wall
        (
            "red_frog_legs_liked_sticker",
            Type.BAKED_MODEL,
            AdditionalModels.RED_FROG_LEGS_LIKED_STICKER,
            Shapes.or(Shapes.box(0.075, 0.1875, 0.375, 0.925, 0.5, 0.625), Shapes.box(-0.25, 0, 0.375, 1.25, 0.1875, 0.625)),
            List.of(ItemRegistryHandler.RED_FROG_LEGS_LIKED_STICKER.toStack()),
            null,
            null,
            null,
            null,
            SoundType.WOOD
        )
    );
    public static final BuildingComponent RED_STEEP_RAFTER = register
    (
        new SteepRafter
        (
            "red_steep_rafter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_STEEP_RAFTER,
            List.of(ItemRegistryHandler.RED_STEEP_RAFTER.toStack())
        )
    );
    public static final BuildingComponent RED_SMOOTH_RAFTER = register
    (
        new SmoothRafter
        (
            "red_smooth_rafter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_SMOOTH_RAFTER,
            List.of(ItemRegistryHandler.RED_SMOOTH_RAFTER.toStack())
        )
    );
    public static final BuildingComponent RED_RAFTER_END = register
    (
        new RafterEnd
        (
            "red_rafter_end",
            Type.BAKED_MODEL,
            AdditionalModels.RED_RAFTER_END,
            List.of(ItemRegistryHandler.RED_RAFTER_END.toStack())
        )
    );
    public static final BuildingComponent RED_RAFTER_CONNECTOR = register
    (
        new SmoothRafter
        (
            "red_rafter_connector",
            Type.BAKED_MODEL,
            AdditionalModels.RED_RAFTER_CONNECTOR,
            Shapes.box(0, 0, 0.375, 1, 0.75, 0.625),
            List.of(ItemRegistryHandler.RED_SMOOTH_RAFTER.toStack())
        )
    );
    public static final BuildingComponent SPRUCE_RAFTER_PLANKS = register
    (
        new RafterPlanks
        (
            "spruce_rafter_planks",
            Type.BAKED_MODEL,
            AdditionalModels.SPRUCE_RAFTER_PLANKS,
            List.of(ItemRegistryHandler.SPRUCE_RAFTER_PLANKS.toStack())
        )
    );
    public static final BuildingComponent WHITE_RAFTER_PLANKS = register
    (
        new RafterPlanks
        (
            "white_rafter_planks",
            Type.BAKED_MODEL,
            AdditionalModels.WHITE_RAFTER_PLANKS,
            List.of(ItemRegistryHandler.WHITE_RAFTER_PLANKS.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF = register
    (
        new OrientedFloor
        (
            "cypress_roof",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF,
            List.of(ItemRegistryHandler.CYPRESS_ROOF.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_CORNER = register
    (
        new OrientedFloor
        (
            "cypress_roof_corner",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_CORNER,
            List.of(ItemRegistryHandler.CYPRESS_ROOF_CORNER.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_EDGE = register
    (
        new OrientedFloor
        (
            "cypress_roof_edge",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_EDGE,
            Shapes.box(0, 0, 0.5, 1, 0.5, 1),
            List.of(ItemRegistryHandler.CYPRESS_ROOF_EDGE.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_EDGE_CORNER = register
    (
        new OrientedFloor
        (
            "cypress_roof_edge_corner",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_EDGE_CORNER,
            Shapes.box(0.5, 0, 0.5, 1, 0.5, 1),
            List.of(ItemRegistryHandler.CYPRESS_ROOF_EDGE_CORNER.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_HALF = register
    (
        new RafterPlanks
        (
            "cypress_roof",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_HALF,
            List.of(ItemRegistryHandler.CYPRESS_ROOF_HALF.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_QUARTER = register
    (
        new QuarterOrientedFloor
        (
            "cypress_roof_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_QUARTER,
            List.of(ItemRegistryHandler.CYPRESS_ROOF_QUARTER.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_EDGE_LAYER_BOTTOM = register
    (
        new QuarterOrientedFloor
        (
            "cypress_roof_edge_layer_bottom",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_EDGE_LAYER_BOTTOM,
            List.of(ItemRegistryHandler.CYPRESS_ROOF_EDGE_LAYER_BOTTOM.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_EDGE_LAYER_TOP = register
    (
    new QuarterOrientedFloor
    (
    "cypress_roof_edge_layer_top",
    Type.BAKED_MODEL,
    AdditionalModels.CYPRESS_ROOF_EDGE_LAYER_TOP,
    List.of(ItemRegistryHandler.CYPRESS_ROOF_EDGE_LAYER_TOP.toStack())
    )
    );
    public static final BuildingComponent CYPRESS_ROOF_QUARTER_CORNER = register
    (
        new QuarterOrientedFloor
        (
            "cypress_roof_quarter_corner",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_QUARTER_CORNER,
            List.of(ItemRegistryHandler.CYPRESS_ROOF_QUARTER_CORNER.toStack())
        )
    );
    public static final BuildingComponent CYPRESS_ROOF_TOP = register
    (
        new SmoothRafter
        (
            "cypress_roof_top",
            Type.BAKED_MODEL,
            AdditionalModels.CYPRESS_ROOF_TOP,
            Shapes.or(Shapes.box(0, 0, 0, 1, 0.5, 1), Shapes.box(0, 0.5, 0.25, 1, 1, 0.75)),
            List.of(ItemRegistryHandler.CYPRESS_ROOF_TOP.toStack())
        )
    );
    public static final BuildingComponent FLAT_TERRACOTTA_TILE_ROOF = register
    (
        new OrientedFloor
        (
            "flat_terracotta_tile_roof",
            Type.BAKED_MODEL,
            AdditionalModels.FLAT_TERRACOTTA_TILE_ROOF,
            List.of(ItemRegistryHandler.FLAT_TERRACOTTA_TILE_ROOF.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent FLAT_TERRACOTTA_TILE_ROOF_HALF = register
    (
        new RafterPlanks
        (
            "flat_terracotta_tile_roof_half",
            Type.BAKED_MODEL,
            AdditionalModels.FLAT_TERRACOTTA_TILE_ROOF_HALF,
            List.of(ItemRegistryHandler.FLAT_TERRACOTTA_TILE_ROOF_HALF.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent FLAT_TERRACOTTA_TILE_ROOF_QUARTER = register
    (
        new QuarterOrientedFloor
        (
            "flat_terracotta_tile_roof_quarter",
            Type.BAKED_MODEL,
            AdditionalModels.FLAT_TERRACOTTA_TILE_ROOF_QUARTER,
            List.of(ItemRegistryHandler.FLAT_TERRACOTTA_TILE_ROOF_QUARTER.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_1 = register
    (
        new RoundTile
        (
            "round_terracotta_tile_1_1",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_1,
            Shapes.box(0.25, 0.125, 0.125, 0.75, 0.875, 0.875),
            y -> 0,
            45,
            0,
            XTP(8),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_1.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_1_OBLIQUE = register
    (
        new RoundTile
        (
            "round_terracotta_tile_1_1_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_1_OBLIQUE,
            Shapes.or(Shapes.box(-0.375, -0.5, -0.375, 0.5, 0, 0.5), Shapes.box(0.1875, 0, 0.1875, 1, 0.5, 1)),
            y -> 0,
            35.2644f,
            -45,
            XTP(8),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_1_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_1_HALF = register
    (
        new RoundTileHalf
        (
            "round_terracotta_tile_1_1_half",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_1_HALF,
            Shapes.box(0.25, -0.0015625, 0.14644375, 0.75, 0.4984375, 0.85355625),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            45,
            0,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_1_HALF.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_2 = register
    (
        new RoundTile
        (
            "round_terracotta_tile_1_2",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_2,
            Shapes.box(0.25, 0.0625, 0.1875, 0.75, 0.6875, 0.8125),
            y -> y <= XTP(8) ? 0 : 2,
            26.5651f,
            0,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_2.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_2_OBLIQUE = register
    (
        new RoundTile
        (
            "round_terracotta_tile_1_2_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_2_OBLIQUE,
            Shapes.or(Shapes.box(-0.3125, 0, -0.3125, 0.5, 0.75, 0.5), Shapes.box(0.1875, 0, 0.1875, 1, 0.75, 1)),
            y -> y <= XTP(8) ? 0 : 2,
            19.4712f,
            -45,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_2_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_2_HALF = register
    (
        new RoundTileHalf
        (
            "round_terracotta_tile_1_2_half",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_2_HALF,
            Shapes.box(0.25, -0.0015625, 0.22049375, 0.75, 0.4984375, 0.77950625),
            y -> y <= XTP(8) ? 0 : 2,
            26.5651f,
            0,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_2_HALF.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_4 = register
    (
        new RoundTile
        (
            "round_terracotta_tile_1_4",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_4,
            Shapes.box(0.25, 0.0625, 0.21875, 0.75, 0.625, 0.78125),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            14.0362f,
            0,
            XTP(-6),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_4.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_4_OBLIQUE = register
    (
        new RoundTile
        (
            "round_terracotta_tile_1_4_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_4_OBLIQUE,
            Shapes.or(Shapes.box(-0.25, 0, -0.25, 0.5625, 0.625, 0.5625), Shapes.box(0.1875, 0, 0.1875, 1, 0.625, 1)),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            10.025f,
            -45,
            XTP(-6),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_4_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_1_4_HALF = register
    (
        new RoundTileHalf
        (
            "round_terracotta_tile_1_4_half",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_1_4_HALF,
            Shapes.box(0.25, -0.0015625, 0.24230625, 0.75, 0.4984375, 0.75769375),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            14.0362f,
            0,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_1_4_HALF.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_FLAT = register
    (
        new RoundTile
        (
            "round_terracotta_tile_flat",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_FLAT,
            Shapes.box(0.25, 0, 0, 0.75, 0.5, 1),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            0,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_FLAT.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_FLAT_OBLIQUE = register
    (
        new RoundTile
        (
            "round_terracotta_tile_flat_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_FLAT_OBLIQUE,
            Shapes.or(Shapes.box(-0.1875, 0, -0.1875, 0.625, 0.5, 0.625), Shapes.box(0.25, 0, 0.25, 1, 0.5, 1)),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            -45,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_FLAT_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_FLAT_HALF = register
    (
        new RoundTileHalf
        (
            "round_terracotta_tile_flat_half",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_FLAT_HALF,
            Shapes.box(0.25, -0.0015625, 0.25, 0.75, 0.4984375, 0.75),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            0,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_FLAT_HALF.toStack())
        )
    );
    public static final BuildingComponent TERRACOTTA_TILE_ROOF_EDGE = register
    (
        new OrientedFloor
        (
            "terracotta_tile_roof_edge",
            Type.BAKED_MODEL,
            AdditionalModels.TERRACOTTA_TILE_ROOF_EDGE,
            Shapes.box(0, 0, 0.4375, 1, 0.5625, 1),
            List.of(ItemRegistryHandler.TERRACOTTA_TILE_ROOF_EDGE.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent TERRACOTTA_TILE_ROOF_EDGE_CORNER = register
    (
        new OrientedFloor
        (
            "terracotta_tile_roof_edge_corner",
            Type.BAKED_MODEL,
            AdditionalModels.TERRACOTTA_TILE_ROOF_EDGE_CORNER,
            Shapes.box(0, 0, 0.1875, 0.8125, 0.5625, 1),
            List.of(ItemRegistryHandler.TERRACOTTA_TILE_ROOF_EDGE_CORNER.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent ROUND_TERRACOTTA_TILE_END = register
    (
        new RoundTileHalf
        (
            "round_terracotta_tile_end",
            Type.BAKED_MODEL,
            AdditionalModels.ROUND_TERRACOTTA_TILE_END,
            Shapes.box(0.21875, -0.0484375, 0.0625, 0.78125, 0.5296875, 0.75),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            0,
            XTP(-4),
            List.of(ItemRegistryHandler.ROUND_TERRACOTTA_TILE_END.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent TERRACOTTA_TILE_ROOF_TOP = register
    (
        new SmoothRafter
        (
            "terracotta_tile_roof_top",
            Type.BAKED_MODEL,
            AdditionalModels.TERRACOTTA_TILE_ROOF_TOP,
            Shapes.or
            (
                Shapes.box(0, 0, 0, 1, 0.5, 1),
                Shapes.box(0, 0.5, 0.25, 1, 1, 0.75),
                Shapes.box(0.25, 0, 0.0625, 0.75, 0.9375, 0.9375)
            ),
            List.of(ItemRegistryHandler.TERRACOTTA_TILE_ROOF_TOP.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_1_1 = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_1_1",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_1_1,
            Shapes.box(0.25, 0.125, 0.125, 0.75, 0.875, 0.875),
            y -> 0,
            45,
            0,
            XTP(0),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_1_1.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_1_1_OBLIQUE = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_1_1_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_1_1_OBLIQUE,
            Shapes.or(Shapes.box(-0.375, -0.5, -0.375, 0.5, 0, 0.5), Shapes.box(0.1875, 0, 0.1875, 1, 0.5, 1)),
            y -> 0,
            35.2644f,
            -45,
            XTP(0),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_1_1_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_1_2 = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_1_2",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_1_2,
            Shapes.box(0.25, 0.0625, 0.1875, 0.75, 0.6875, 0.8125),
            y -> y <= XTP(8) ? 0 : 2,
            26.5651f,
            0,
            XTP(4),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_1_2.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_1_2_OBLIQUE = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_1_2_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_1_2_OBLIQUE,
            Shapes.or(Shapes.box(-0.3125, 0, -0.3125, 0.5, 0.75, 0.5), Shapes.box(0.1875, 0, 0.1875, 1, 0.75, 1)),
            y -> y <= XTP(8) ? 0 : 2,
            19.4712f,
            -45,
            XTP(4),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_1_2_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_1_4 = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_1_4",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_1_4,
            Shapes.box(0.25, 0.0625, 0.21875, 0.75, 0.625, 0.78125),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            14.0362f,
            0,
            XTP(-2),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_1_4.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_1_4_OBLIQUE = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_1_4_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_1_4_OBLIQUE,
            Shapes.or(Shapes.box(-0.25, 0, -0.25, 0.5625, 0.625, 0.5625), Shapes.box(0.1875, 0, 0.1875, 1, 0.625, 1)),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            10.025f,
            -45,
            XTP(-2),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_1_4_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_FLAT = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_flat",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_FLAT,
            Shapes.box(0.25, 0, 0, 0.75, 0.5, 1),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            0,
            XTP(4),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_FLAT.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_FLAT_OBLIQUE= register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_flat_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_FLAT_OBLIQUE,
            Shapes.or(Shapes.box(-0.1875, 0, -0.1875, 0.625, 0.5, 0.625), Shapes.box(0.25, 0, 0.25, 1, 0.5, 1)),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            -45,
            XTP(4),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_FLAT_OBLIQUE.toStack())
        )
    );
    public static final BuildingComponent ROOF_TOP_TILES_PILE_START = register
    (
        new RoundTile
        (
            "roof_top_tiles_pile_start",
            Type.BAKED_MODEL,
            AdditionalModels.ROOF_TOP_TILES_PILE_START,
            Shapes.box(0.25, 0.125, 0.125, 0.75, 0.875, 0.875),
            y -> 0,
            45,
            0,
            XTP(0),
            List.of(ItemRegistryHandler.ROOF_TOP_TILES_PILE_START.toStack())
        )
    );
    public static final BuildingComponent ONI_TILE_1 = register
    (
        new RoundTile
        (
            "oni_tile_1",
            Type.BAKED_MODEL,
            AdditionalModels.ONI_TILE_1,
            Shapes.box(0, 0, 0.4375, 1, 0.5625, 1),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            0,
            XTP(4),
            List.of(ItemRegistryHandler.ONI_TILE_1.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );
    public static final BuildingComponent ONI_TILE_1_OBLIQUE= register
    (
        new RoundTile
        (
            "oni_tile_1_oblique",
            Type.BAKED_MODEL,
            AdditionalModels.ONI_TILE_1_OBLIQUE,
            Shapes.box(0, 0, 0.1875, 0.8125, 0.5625, 1),
            y -> (int) Math.clamp(Math.floor(y * 4), 0, 3),
            0,
            -45,
            XTP(4),
            List.of(ItemRegistryHandler.ONI_TILE_1_OBLIQUE.toStack())
        ).setSound(SoundType.DEEPSLATE_TILES)
    );

    static
    {
        for (Type type : Type.values())
        {
            TYPE_MAP.put(type.name(), type);
        }
    }

    public static BuildingComponent get(String id)
    {
        return COMPONENTS.getOrDefault(id, BASE_STONE);
    }

    private static BuildingComponent register(BuildingComponent component)
    {
        COMPONENTS.put(component.id, component);
        return component;
    }

    public enum Type
    {
        BAKED_MODEL("baked_model"),
        ENTITY_MODEL("entity_model"),
        ;

        public final String name;

        Type(String nameIn)
        {
            this.name = nameIn;
        }
    }
}
