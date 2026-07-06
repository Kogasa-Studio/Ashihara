package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AdditionalModels
{
    public static final String STANDALONE_VARIANT = "standalone";
    private static final List<BuildingComponentModelResourceLocation> models = new ArrayList<>();

    public static List<BuildingComponentModelResourceLocation> getModels()
    {
        return models;
    }

    public static BuildingComponentModelResourceLocation get(Identifier id)
    {
        return models.stream().filter(rl -> rl.id().equals(id)).findFirst().orElse(null);
    }

    //Structural
    public static final BuildingComponentModelResourceLocation RED_THICK_COLUMN = register("block/building_blocks/column/column_thick_red");
    public static final BuildingComponentModelResourceLocation RED_THICK_COLUMN_SHORT = register("block/components/red_thick_column_short");
    public static final BuildingComponentModelResourceLocation RED_COLUMN = register("block/components/red_column");
    public static final BuildingComponentModelResourceLocation RED_COLUMN_SHORT = register("block/components/red_column_short");

    public static final BuildingComponentModelResourceLocation RED_BEAM = register("block/components/red_beam");
    public static final BuildingComponentModelResourceLocation RED_BEAM_L = register("block/components/red_beam_l");
    public static final BuildingComponentModelResourceLocation RED_BEAM_R = register("block/components/red_beam_r");
    public static final BuildingComponentModelResourceLocation RED_BEAM_A = register("block/components/red_beam_a");
    public static final BuildingComponentModelResourceLocation RED_BEAM_CORNER = register("block/components/red_beam_corner");
    public static final BuildingComponentModelResourceLocation RED_BEAM_CORNER_PAINTED_YELLOW = register("block/components/red_beam_corner_painted_yellow");

    public static final BuildingComponentModelResourceLocation RED_CLAMP = register("block/components/red_clamp");
    public static final BuildingComponentModelResourceLocation RED_CLAMP_JOINT = register("block/components/red_clamp_joint");

    public static final BuildingComponentModelResourceLocation RED_HIJIKI_L = register("block/components/red_hijiki_l");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_R = register("block/components/red_hijiki_r");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_A = register("block/components/red_hijiki_a");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_SUPPORTER = register("block/components/red_hijiki_supporter");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_LONG = register("block/components/red_hijiki_long");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_CORNER_NORMAL = register("block/components/red_hijiki_cornered_normal");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_CORNER_NORMAL_LONG = register("block/components/red_hijiki_cornered_normal_long");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_CORNER_LONG = register("block/components/red_hijiki_cornered_long");

    public static final BuildingComponentModelResourceLocation RED_RAFTER_STICKING_BEAM = register("block/components/red_rafter_sticking_beam");

    public static final BuildingComponentModelResourceLocation RED_TOU = register("block/components/red_tou");
    public static final BuildingComponentModelResourceLocation RED_BIG_TOU = register("block/components/red_big_tou");
    public static final BuildingComponentModelResourceLocation RED_ONI_TOU = register("block/components/red_tou_oni");

    //Additional
    public static final BuildingComponentModelResourceLocation BASE_STONE_0 = register("block/components/base_stone_0");
    public static final BuildingComponentModelResourceLocation BASE_STONE_1 = register("block/components/base_stone_1");
    public static final BuildingComponentModelResourceLocation BASE_STONE_2 = register("block/components/base_stone_2");

    public static final BuildingComponentModelResourceLocation BAMBOO_WALL_BONES = register("block/components/bamboo_wall_bones");
    public static final BuildingComponentModelResourceLocation BAMBOO_WALL_BONES_QUARTER = register("block/components/bamboo_wall_bones_quarter");
    public static final BuildingComponentModelResourceLocation RAMMED_SOIL_WALL = register("block/components/rammed_soil_wall");
    public static final BuildingComponentModelResourceLocation RAMMED_SOIL_WALL_QUARTER = register("block/components/rammed_soil_wall_quarter");
    public static final BuildingComponentModelResourceLocation WHITE_SOIL_WALL = register("block/components/white_soil_wall");
    public static final BuildingComponentModelResourceLocation WHITE_SOIL_WALL_QUARTER = register("block/components/white_soil_wall_quarter");
    public static final BuildingComponentModelResourceLocation RED_PLANKS_WALL = register("block/components/red_planks_wall");
    public static final BuildingComponentModelResourceLocation RED_PLANKS_WALL_QUARTER = register("block/components/red_planks_wall_quarter");

    public static final BuildingComponentModelResourceLocation RED_HANGING_FISH_PIG_EYE = register("block/components/red_hanging_fish_pig_eye");
    public static final BuildingComponentModelResourceLocation RED_HANGING_FISH_TURNIP = register("block/components/red_hanging_fish_turnip");
    public static final BuildingComponentModelResourceLocation GOLD_PIN_FIN = register("block/components/gold_pin_fin");
    public static final BuildingComponentModelResourceLocation GOLD_ROOF_EDGE_PLANKS_DECORATION = register("block/components/gold_roof_edge_planks_decoration");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_LEFT = register("block/components/red_roof_edge_planks_left");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_1_1_LEFT = register("block/components/red_roof_edge_planks_1_1_left");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_1_2_LEFT = register("block/components/red_roof_edge_planks_1_2_left");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_1_4_LEFT = register("block/components/red_roof_edge_planks_1_4_left");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_RIGHT = register("block/components/red_roof_edge_planks_right");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_1_1_RIGHT = register("block/components/red_roof_edge_planks_1_1_right");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_1_2_RIGHT = register("block/components/red_roof_edge_planks_1_2_right");
    public static final BuildingComponentModelResourceLocation RED_ROOF_EDGE_PLANKS_1_4_RIGHT = register("block/components/red_roof_edge_planks_1_4_right");

    public static final BuildingComponentModelResourceLocation RED_SACRED_FENCE_PILLAR = register("block/components/red_sacred_fence_pillar");
    public static final BuildingComponentModelResourceLocation RED_SACRED_FENCE_HALF = register("block/components/red_sacred_fence_half");
    public static final BuildingComponentModelResourceLocation RED_SACRED_FENCE = register("block/components/red_sacred_fence");
    public static final BuildingComponentModelResourceLocation SPRUCE_SACRED_FENCE_PILLAR = register("block/components/spruce_sacred_fence_pillar");
    public static final BuildingComponentModelResourceLocation SPRUCE_SACRED_FENCE_HALF = register("block/components/spruce_sacred_fence_half");
    public static final BuildingComponentModelResourceLocation STONE_SACRED_FENCE = register("block/components/stone_sacred_fence");
    public static final BuildingComponentModelResourceLocation STONE_SACRED_FENCE_HALF = register("block/components/stone_sacred_fence_half");
    public static final BuildingComponentModelResourceLocation STONE_SACRED_FENCE_PILLAR = register("block/components/stone_sacred_fence_pillar");
    public static final BuildingComponentModelResourceLocation SPRUCE_SACRED_FENCE = register("block/components/spruce_sacred_fence");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW = register("block/components/green_straight_bar_window");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW_QUARTER = register("block/components/green_straight_bar_window_quarter");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW_ALL = register("block/components/green_straight_bar_window_all");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW_ALL_QUARTER = register("block/components/green_straight_bar_window_all_quarter");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW_UP = register("block/components/green_straight_bar_window_up");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW_UP_QUARTER = register("block/components/green_straight_bar_window_up_quarter");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW_DOWN = register("block/components/green_straight_bar_window_down");
    public static final BuildingComponentModelResourceLocation GREEN_STRAIGHT_BAR_WINDOW_DOWN_QUARTER = register("block/components/green_straight_bar_window_down_quarter");

    public static final BuildingComponentModelResourceLocation RED_LATTICED_WINDOW = register("block/components/red_latticed_window");
    public static final BuildingComponentModelResourceLocation RED_LATTICED_WINDOW_QUARTER = register("block/components/red_latticed_window_quarter");

    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER = register("block/components/red_hanging_sticker");
    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER_END = register("block/components/red_hanging_sticker_end");
    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER_CORNER = register("block/components/red_hanging_sticker_corner");
    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER_END_CORNER = register("block/components/red_hanging_sticker_end_corner");

    public static final BuildingComponentModelResourceLocation RED_PLANKS_FLOOR = register("block/components/red_planks_floor");
    public static final BuildingComponentModelResourceLocation RED_PLANKS_FLOOR_QUARTER = register("block/components/red_planks_floor_quarter");
    public static final BuildingComponentModelResourceLocation RED_LATTICED_CEILING_WHITE = register("block/components/red_latticed_ceiling_white");
    public static final BuildingComponentModelResourceLocation RED_LATTICED_CEILING_WHITE_QUARTER = register("block/components/red_latticed_ceiling_white_quarter");

    public static final BuildingComponentModelResourceLocation RED_BENDED_STICKERS = register("block/components/red_bended_stickers");
    public static final BuildingComponentModelResourceLocation RED_FROG_LEGS_LIKED_STICKER = register("block/components/red_frog_legs_liked_sticker");

    public static final BuildingComponentModelResourceLocation RED_STEEP_RAFTER = register("block/components/red_steep_rafter");
    public static final BuildingComponentModelResourceLocation RED_SMOOTH_RAFTER = register("block/components/red_smooth_rafter");
    public static final BuildingComponentModelResourceLocation RED_RAFTER_END = register("block/components/red_rafter_end");
    public static final BuildingComponentModelResourceLocation RED_DOUBLE_RAFTER = register("block/components/red_thin_rafter_doubled");
    public static final BuildingComponentModelResourceLocation RED_DOUBLE_RAFTER_1_1 = register("block/components/red_thin_rafter_doubled_1_1");
    public static final BuildingComponentModelResourceLocation RED_DOUBLE_RAFTER_1_2 = register("block/components/red_thin_rafter_doubled_1_2");
    public static final BuildingComponentModelResourceLocation RED_LONG_DOUBLE_RAFTER = register("block/components/red_thin_rafter_long_doubled");
    public static final BuildingComponentModelResourceLocation RED_LONG_DOUBLE_RAFTER_1_4 = register("block/components/red_thin_rafter_long_doubled_1_4");
    public static final BuildingComponentModelResourceLocation RED_RAFTER_CONNECTOR = register("block/components/red_rafter_connector");

    public static final BuildingComponentModelResourceLocation RED_THIN_RAFTER = register("block/components/red_thin_rafter");
    public static final BuildingComponentModelResourceLocation RED_THIN_RAFTER_1_1 = register("block/components/red_thin_rafter_1_1");
    public static final BuildingComponentModelResourceLocation RED_THIN_RAFTER_1_2 = register("block/components/red_thin_rafter_1_2");
    public static final BuildingComponentModelResourceLocation RED_THIN_RAFTER_1_4 = register("block/components/red_thin_rafter_long_1_4");
    public static final BuildingComponentModelResourceLocation RED_THIN_RAFTER_PAINTED_YELLOW = register("block/components/red_thin_rafter_painted_yellow");
    public static final BuildingComponentModelResourceLocation RED_THIN_RAFTER_CONNECTOR = register("block/components/red_thin_rafter_connector");

    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS = register("block/components/spruce_rafter_planks");
    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_QUARTER = register("block/components/spruce_rafter_planks_quarter");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS = register("block/components/white_rafter_planks");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_QUARTER = register("block/components/white_rafter_planks_quarter");

    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_1_1 = register("block/components/spruce_rafter_planks_1_1");
    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_1_2 = register("block/components/spruce_rafter_planks_1_2");
    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_1_4 = register("block/components/spruce_rafter_planks_1_4");
    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_WIDE = register("block/components/spruce_rafter_planks_wide");
    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_WIDE_1_4 = register("block/components/spruce_rafter_planks_wide_1_4");
    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_QUARTER_1_2 = register("block/components/spruce_rafter_planks_quarter_1_2");
    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS_QUARTER_1_1 = register("block/components/spruce_rafter_planks_quarter_1_1");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_1_1 = register("block/components/white_rafter_planks_1_1");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_1_2 = register("block/components/white_rafter_planks_1_2");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_1_4 = register("block/components/white_rafter_planks_1_4");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_WIDE = register("block/components/white_rafter_planks_wide");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_WIDE_1_4 = register("block/components/white_rafter_planks_wide_1_4");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_QUARTER_1_2 = register("block/components/white_rafter_planks_quarter_1_2");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS_QUARTER_1_1 = register("block/components/white_rafter_planks_quarter_1_1");

    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF = register("block/components/cypress_roof");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_1_4 = register("block/components/cypress_roof_1_4");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_CORNER = register("block/components/cypress_roof_corner");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE = register("block/components/cypress_roof_edge");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_RIGHT = register("block/components/cypress_roof_edge_right");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_LEFT = register("block/components/cypress_roof_edge_left");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_RIGHT_1_4 = register("block/components/cypress_roof_edge_right_1_4");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_LEFT_1_4 = register("block/components/cypress_roof_edge_left_1_4");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_RIGHT_1_2 = register("block/components/cypress_roof_edge_right_1_2");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_LEFT_1_2 = register("block/components/cypress_roof_edge_left_1_2");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_RIGHT_1_1 = register("block/components/cypress_roof_edge_right_1_1");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_LEFT_1_1 = register("block/components/cypress_roof_edge_left_1_1");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_CORNER = register("block/components/cypress_roof_edge_corner");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_LAYER_BOTTOM = register("block/components/cypress_roof_edge_layer_bottom");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_LAYER_TOP = register("block/components/cypress_roof_edge_layer_top");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_HALF = register("block/components/cypress_roof_half");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_HALF_1_4 = register("block/components/cypress_roof_half_1_4");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_HALF_1_2 = register("block/components/cypress_roof_half_1_2");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_HALF_1_1 = register("block/components/cypress_roof_half_1_1");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_QUARTER = register("block/components/cypress_roof_quarter");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_QUARTER_1_2 = register("block/components/cypress_roof_quarter_1_2");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_QUARTER_1_1 = register("block/components/cypress_roof_quarter_1_1");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_QUARTER_CORNER = register("block/components/cypress_roof_quarter_corner");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_TOP = register("block/components/cypress_roof_top");

    public static final BuildingComponentModelResourceLocation FLAT_TERRACOTTA_TILE_ROOF = register("block/components/flat_terracotta_tile_roof");
    public static final BuildingComponentModelResourceLocation FLAT_TERRACOTTA_TILE_ROOF_HALF = register("block/components/flat_terracotta_tile_roof_half");
    public static final BuildingComponentModelResourceLocation FLAT_TERRACOTTA_TILE_ROOF_QUARTER = register("block/components/flat_terracotta_tile_roof_quarter");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_1 = register("block/components/round_terracotta_tile_1_1");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_2 = register("block/components/round_terracotta_tile_1_2");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_4 = register("block/components/round_terracotta_tile_1_4");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_FLAT = register("block/components/round_terracotta_tile_flat");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_1_OBLIQUE = register("block/components/round_terracotta_tile_1_1_oblique");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_2_OBLIQUE = register("block/components/round_terracotta_tile_1_2_oblique");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_4_OBLIQUE = register("block/components/round_terracotta_tile_1_4_oblique");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_FLAT_OBLIQUE = register("block/components/round_terracotta_tile_flat_oblique");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_1_HALF = register("block/components/round_terracotta_tile_1_1_half");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_2_HALF = register("block/components/round_terracotta_tile_1_2_half");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_1_4_HALF = register("block/components/round_terracotta_tile_1_4_half");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_FLAT_HALF = register("block/components/round_terracotta_tile_flat_half");
    public static final BuildingComponentModelResourceLocation TERRACOTTA_TILE_ROOF_EDGE = register("block/components/terracotta_tile_roof_edge");
    public static final BuildingComponentModelResourceLocation TERRACOTTA_TILE_ROOF_EDGE_CORNER = register("block/components/terracotta_tile_roof_edge_corner");
    public static final BuildingComponentModelResourceLocation ROUND_TERRACOTTA_TILE_END = register("block/components/round_terracotta_tile_end");
    public static final BuildingComponentModelResourceLocation TERRACOTTA_TILE_ROOF_TOP = register("block/components/terracotta_tile_roof_top");

    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_1_1 = register("block/components/roof_top_tiles_pile_1_1");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_1_1_OBLIQUE = register("block/components/roof_top_tiles_pile_1_1_oblique");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_1_2 = register("block/components/roof_top_tiles_pile_1_2");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_1_2_OBLIQUE = register("block/components/roof_top_tiles_pile_1_2_oblique");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_1_4 = register("block/components/roof_top_tiles_pile_1_4");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_1_4_OBLIQUE = register("block/components/roof_top_tiles_pile_1_4_oblique");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_FLAT = register("block/components/roof_top_tiles_pile_flat");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_FLAT_OBLIQUE = register("block/components/roof_top_tiles_pile_flat_oblique");
    public static final BuildingComponentModelResourceLocation ROOF_TOP_TILES_PILE_START = register("block/components/roof_top_tiles_pile_start");
    public static final BuildingComponentModelResourceLocation ONI_TILE_1 = register("block/components/oni_tile_1");
    public static final BuildingComponentModelResourceLocation ONI_TILE_1_OBLIQUE = register("block/components/oni_tile_1_oblique");

    // Furniture
    public static final BuildingComponentModelResourceLocation WOODEN_BOWL_MID = register("block/furnitures/bowl_mid");
    public static final BuildingComponentModelResourceLocation WOODEN_BOWL_BIG = register("block/furnitures/bowl_big");
    public static final BuildingComponentModelResourceLocation WOODEN_DISH_SMALL = register("block/furnitures/dish_small");
    public static final BuildingComponentModelResourceLocation WOODEN_DISH_MID = register("block/furnitures/dish_mid");
    public static final BuildingComponentModelResourceLocation WOODEN_DISH_BIG = register("block/furnitures/dish_big");
    public static final BuildingComponentModelResourceLocation CURTAIN_TABLE = register("block/furnitures/kicho");
    public static final BuildingComponentModelResourceLocation BAMBOO_CURTAIN_HEAD = register("block/furnitures/bamboo_curtain_head");
    public static final BuildingComponentModelResourceLocation BAMBOO_CURTAIN_HEAD_ROLLED = register("block/furnitures/bamboo_curtain_head_rolled_up");
    public static final BuildingComponentModelResourceLocation BAMBOO_CURTAIN_BODY = register("block/furnitures/bamboo_curtain_body");
    public static final BuildingComponentModelResourceLocation BAMBOO_CURTAIN_TAIL = register("block/furnitures/bamboo_curtain_tail");
    public static final BuildingComponentModelResourceLocation BAMBOO_CURTAIN_TAIL_ROLLED = register("block/furnitures/bamboo_curtain_tail_rolled_up");

    private static BuildingComponentModelResourceLocation register(String path)
    {
        Identifier id = Identifier.fromNamespaceAndPath(Ashihara.MODID, path);
        BuildingComponentModelResourceLocation rl = new BuildingComponentModelResourceLocation(id, STANDALONE_VARIANT);
        models.add(rl);
        return rl;
    }
}
