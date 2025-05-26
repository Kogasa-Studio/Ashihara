package kogasastudio.ashihara;

import kogasastudio.ashihara.registry.Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CreativeModeTabsRegistryHandler
{
    public static final String BUILDING_BLOCKS_TAB_NAME = "group_ash_building_blocks";
    public static final String MATERIALS_TAB_NAME = "group_ash_materials";
    public static final String ASHIHARA_MAIN_TAB_NAME = "group_ashihara";

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ashihara.MODID);

    public static final Supplier<CreativeModeTab> BUILDING_BLOCKS =
            TABS.register
                    (
                            BUILDING_BLOCKS_TAB_NAME,
                            () -> CreativeModeTab.builder()
                                    .title(Component.translatable("itemGroup.group_ash_building_blocks"))
                                    .icon(() -> Items.RED_ADVANCED_FENCE.get().getDefaultInstance())
                                    .displayItems(((itemDisplayParameters, output) ->
                                    {
                                        //樱木
                                        output.accept(Items.CHERRY_LOG.get());
                                        output.accept(Items.CHERRY_WOOD.get());
                                        output.accept(Items.STRIPPED_CHERRY_LOG.get());
                                        output.accept(Items.CHERRY_BLOSSOM.get());
                                        output.accept(Items.FALLEN_SAKURA.get());
                                        output.accept(Items.CHERRY_VINES.get());
                                        output.accept(Items.CHERRY_STAIRS.get());
                                        output.accept(Items.CHERRY_SLAB.get());
                                        output.accept(Items.CHERRY_FENCE.get());
                                        output.accept(Items.CHERRY_FENCE_GATE.get());
                                        output.accept(Items.CHERRY_BUTTON.get());
                                        output.accept(Items.CHERRY_LOG.get());
                                        //枫木
                                        output.accept(Items.MAPLE_LOG.get());
                                        output.accept(Items.MAPLE_WOOD.get());
                                        output.accept(Items.STRIPPED_MAPLE_LOG.get());
                                        output.accept(Items.MAPLE_LEAVES_RED.get());
                                        output.accept(Items.FALLEN_MAPLE_LEAVES_RED.get());
                                        output.accept(Items.MAPLE_STAIRS.get());
                                        output.accept(Items.MAPLE_SLAB.get());
                                        output.accept(Items.MAPLE_FENCE.get());
                                        output.accept(Items.MAPLE_FENCE_GATE.get());
                                        output.accept(Items.MAPLE_BUTTON.get());
                                        output.accept(Items.MAPLE_LOG.get());
                                        //朱木
                                        output.accept(Items.STRIPPED_RED_LOG.get());
                                        output.accept(Items.RED_PLANKS.get());
                                        output.accept(Items.RED_STAIRS.get());
                                        output.accept(Items.RED_SLAB.get());
                                        output.accept(Items.RED_FENCE.get());
                                        output.accept(Items.RED_FENCE_GATE.get());
                                        //桧皮
                                        output.accept(Items.CYPRESS_SKIN_BLOCK.get());
                                        output.accept(Items.CYPRESS_SKIN_SLAB.get());
                                        output.accept(Items.CYPRESS_SKIN_STAIRS.get());

                                        output.accept(Items.RED_ADVANCED_FENCE.get());
                                        output.accept(Items.SPRUCE_ADVANCED_FENCE.get());
                                        output.accept(Items.RED_THICK_COLUMN.get());
                                        output.accept(Items.RED_KUMINONO.get());
                                        output.accept(Items.RED_KAWAKI.get());
                                        output.accept(Items.RED_THIN_BEAM.get());
                                        output.accept(Items.THIN_WHITE_SOIL_WALL.get());
                                        output.accept(Items.STRAIGHT_BAR_WINDOW_GREEN.get());
                                        output.accept(Items.LANTERN_LONG_WHITE.get());
                                        output.accept(Items.LANTERN_LONG_RED.get());
                                        output.accept(Items.HOUSE_LIKE_HANGING_LANTERN.get());
                                        output.accept(Items.HEXAGONAL_HANGING_LANTERN.get());
                                        output.accept(Items.JINJA_LANTERN.get());
                                        output.accept(Items.STONE_LANTERN.get());
                                        output.accept(Items.BONBURI_LAMP.get());
                                        output.accept(Items.CANDLESTICK.get());
                                        output.accept(Items.OIL_PLATE_STICK.get());
                                        output.accept(Items.TATAMI.get());
                                        output.accept(Items.MULTI_RED_THICK_COLUMN.get());
                                        output.accept(Items.RED_THICK_COLUMN_SHORT.get());
                                        output.accept(Items.RED_COLUMN.get());
                                        output.accept(Items.RED_COLUMN_SHORT.get());
                                        output.accept(Items.RED_BEAM.get());
                                        output.accept(Items.RED_BEAM_CORNER.get());
                                        output.accept(Items.RED_CLAMP.get());
                                        output.accept(Items.RED_CLAMP_JOINT.get());
                                        output.accept(Items.RED_RAFTER_STICKING_BEAM.get());
                                        output.accept(Items.RED_HIJIKI.get());
                                        output.accept(Items.RED_HIJIKI_SUPPORTER.get());
                                        output.accept(Items.RED_HIJIKI_CONNECTOR.get());
                                        output.accept(Items.RED_HIJIKI_LONG.get());
                                        output.accept(Items.RED_HIJIKI_CORNER_NORMAL.get());
                                        output.accept(Items.RED_HIJIKI_CORNER_LONG.get());
                                        output.accept(Items.RED_HANGING_STICKER.get());
                                        output.accept(Items.RED_HANGING_STICKER_CORNER.get());
                                        output.accept(Items.RED_BIG_TOU.get());
                                        output.accept(Items.RED_TOU.get());
                                        output.accept(Items.RED_TOU_OBLIQUE.get());
                                        output.accept(Items.RED_ONI_TOU.get());
                                        output.accept(Items.BASE_STONE.get());
                                        output.accept(Items.WHITE_SOIL_WALL.get());
                                        output.accept(Items.RAMMED_SOIL_WALL.get());
                                        output.accept(Items.BAMBOO_WALL_BONES.get());
                                        output.accept(Items.RED_PLANKS_WALL.get());
                                        output.accept(Items.WHITE_SOIL_WALL_QUARTER.get());
                                        output.accept(Items.RAMMED_SOIL_WALL_QUARTER.get());
                                        output.accept(Items.BAMBOO_WALL_BONES_QUARTER.get());
                                        output.accept(Items.RED_PLANKS_WALL_QUARTER.get());
                                        output.accept(Items.RED_ROOF_EDGE_PLANKS.get());
                                        output.accept(Items.GOLD_ROOF_EDGE_PLANKS_DECORATION.get());
                                        output.accept(Items.RED_HANGING_FISH_PIG_EYE.get());
                                        output.accept(Items.GREEN_STRAIGHT_BAR_WINDOW.get());
                                        output.accept(Items.GREEN_STRAIGHT_BAR_WINDOW_QUARTER.get());
                                        output.accept(Items.RED_LATTICED_WINDOW.get());
                                        output.accept(Items.RED_LATTICED_WINDOW_QUARTER.get());
                                        output.accept(Items.RED_PLANKS_FLOOR.get());
                                        output.accept(Items.RED_PLANKS_FLOOR_QUARTER.get());
                                        output.accept(Items.RED_LATTICED_CEILING_WHITE.get());
                                        output.accept(Items.RED_LATTICED_CEILING_WHITE_QUARTER.get());
                                        output.accept(Items.RED_BENDED_STICKERS.get());
                                        output.accept(Items.RED_FROG_LEGS_LIKED_STICKER.get());
                                        output.accept(Items.RED_STEEP_RAFTER.get());
                                        output.accept(Items.RED_SMOOTH_RAFTER.get());
                                        output.accept(Items.RED_RAFTER_END.get());
                                        output.accept(Items.RED_RAFTER_CONNECTOR.get());
                                        output.accept(Items.RED_THIN_RAFTER.get());
                                        output.accept(Items.RED_THIN_RAFTER_CONNECTOR.get());
                                        output.accept(Items.SPRUCE_RAFTER_PLANKS.get());
                                        output.accept(Items.SPRUCE_RAFTER_PLANKS_QUARTER.get());
                                        output.accept(Items.WHITE_RAFTER_PLANKS.get());
                                        output.accept(Items.WHITE_RAFTER_PLANKS_QUARTER.get());
                                        output.accept(Items.CYPRESS_ROOF.get());
                                        output.accept(Items.CYPRESS_ROOF_CORNER.get());
                                        output.accept(Items.CYPRESS_ROOF_QUARTER.get());
                                        output.accept(Items.CYPRESS_ROOF_QUARTER_CORNER.get());
                                        output.accept(Items.CYPRESS_ROOF_EDGE.get());
                                        output.accept(Items.CYPRESS_ROOF_EDGE_CORNER.get());
                                        output.accept(Items.CYPRESS_ROOF_EDGE_LAYER_BOTTOM.get());
                                        output.accept(Items.CYPRESS_ROOF_EDGE_LAYER_TOP.get());
                                        output.accept(Items.CYPRESS_ROOF_HALF.get());
                                        output.accept(Items.CYPRESS_ROOF_TOP.get());
                                        output.accept(Items.FLAT_TERRACOTTA_TILE_ROOF.get());
                                        output.accept(Items.FLAT_TERRACOTTA_TILE_ROOF_HALF.get());
                                        output.accept(Items.FLAT_TERRACOTTA_TILE_ROOF_QUARTER.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_1.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_2.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_4.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_FLAT.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_1_HALF.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_2_HALF.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_4_HALF.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_FLAT_HALF.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_1_OBLIQUE.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_2_OBLIQUE.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_1_4_OBLIQUE.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_FLAT_OBLIQUE.get());
                                        output.accept(Items.TERRACOTTA_TILE_ROOF_EDGE.get());
                                        output.accept(Items.TERRACOTTA_TILE_ROOF_EDGE_CORNER.get());
                                        output.accept(Items.ROUND_TERRACOTTA_TILE_END.get());
                                        output.accept(Items.TERRACOTTA_TILE_ROOF_TOP.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_1_1.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_1_1_OBLIQUE.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_1_2.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_1_2_OBLIQUE.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_1_4.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_1_4_OBLIQUE.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_FLAT.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_FLAT_OBLIQUE.get());
                                        output.accept(Items.ROOF_TOP_TILES_PILE_START.get());
                                        output.accept(Items.ONI_TILE_1.get());
                                        output.accept(Items.ONI_TILE_1_OBLIQUE.get());
                                        output.accept(Items.GOLD_PIN_FIN.get());
                                        output.accept(Items.DIRT_DEPRESSION.get());
                                        output.accept(Items.WATER_FIELD.get());
                                    })).build()
                    );
    public static final Supplier<CreativeModeTab> MATERIALS =
            TABS.register
                    (
                            MATERIALS_TAB_NAME,
                            () -> CreativeModeTab.builder()
                                    .title(Component.translatable("itemGroup.group_ash_materials"))
                                    .icon(() -> Items.SAKURA.get().getDefaultInstance())
                                    .displayItems(((itemDisplayParameters, output) ->
                                    {
                                        output.accept(Items.SAKURA.get());
                                        output.accept(Items.SAKURA_PETAL.get());
                                        output.accept(Items.DRIED_BAMBOO.get());
                                        output.accept(Items.BAMBOO_MATERIAL.get());
                                        output.accept(Items.BAMBOO_STRIPS.get());
                                        output.accept(Items.BAMBOO_STICK.get());
                                        output.accept(Items.LIME_POWDER.get());
                                        output.accept(Items.SALT.get());
                                        output.accept(Items.COAL_POWDER.get());
                                        output.accept(Items.RICE_POWDER.get());
                                        output.accept(Items.FLOUR.get());
                                        output.accept(Items.BEAN_POWDER.get());
                                        output.accept(Items.MACHA_POWDER.get());
                                        output.accept(Items.STONE_SHATTER.get());
                                        output.accept(Items.IRON_ORE_SHATTER.get());
                                        output.accept(Items.GOLD_ORE_SHATTER.get());
                                        output.accept(Items.TEA_LEAF.get());
                                        output.accept(Items.DRIED_TEA_LEAF.get());
                                        output.accept(Items.TEA_FLOWER.get());
                                        output.accept(Items.TAMAGO.get());
                                        output.accept(Items.DIRT_BALL.get());
                                        output.accept(Items.RAMMED_SOIL.get());
                                        output.accept(Items.OIL_BUCKET.get());
                                        output.accept(Items.SOY_MILK_BUCKET.get());
                                    })).build()
                    );
    public static final Supplier<CreativeModeTab> ASHIHARA =
            TABS.register
                    (
                            ASHIHARA_MAIN_TAB_NAME,
                            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.group_ashihara"))
                                    .icon(() -> Items.ASHIHARA_ICON.get().getDefaultInstance())
                                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                                    .withTabsAfter(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, MATERIALS_TAB_NAME), ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, BUILDING_BLOCKS_TAB_NAME))
                                    .displayItems(((itemDisplayParameters, output) ->
                                    {
                                        output.accept(Items.GUIDEBOOK.get());
                                        output.accept(Items.WILD_RICE.get());
                                        output.accept(Items.RICE_SEEDLING.get());
                                        output.accept(Items.PADDY.get());
                                        output.accept(Items.RICE_CROP.get());
                                        output.accept(Items.DRIED_RICE_CROP.get());
                                        output.accept(Items.STRAW.get());
                                        output.accept(Items.PADDY_PILE.get());
                                        output.accept(Items.BROWN_RICE.get());
                                        output.accept(Items.RICE.get());
                                        output.accept(Items.COOKED_RICE.get());
                                        output.accept(Items.ONIGIRI.get());
                                        output.accept(Items.DIRT_BALL_DON.get());
                                        output.accept(Items.SAKURAMOCHI.get());
                                        output.accept(Items.CHRYSANTHEMUM.get());
                                        output.accept(Items.CHRYSANTHEMUM_FLOWER.get());
                                        output.accept(Items.MOCHI.get());
                                        output.accept(Items.DAIFUKU.get());
                                        output.accept(Items.DAIFUKU_KUSA.get());
                                        output.accept(Items.DAIFUKU_SAKURA.get());
                                        output.accept(Items.DANGO.get());
                                        output.accept(Items.DANGO_BEAN.get());
                                        output.accept(Items.DANGO_HANAMIE.get());
                                        output.accept(Items.DANGO_MITARASHI.get());
                                        output.accept(Items.SUSHI_BASIC.get());
                                        output.accept(Items.SUSHI_SAKURA.get());
                                        output.accept(Items.SUSHI_RAW_FISH.get());
                                        output.accept(Items.SUSHI_TAMAGO.get());
                                        output.accept(Items.CHERRY_SAPLING.get());
                                        output.accept(Items.RED_MAPLE_SAPLING.get());
                                        output.accept(Items.CUCUMBER.get());
                                        output.accept(Items.TOMATO.get());
                                        output.accept(Items.SOY_BEAN.get());
                                        output.accept(Items.TOFU.get());
                                        output.accept(Items.COTTON.get());
                                        output.accept(Items.SWEET_POTATO.get());
                                        output.accept(Items.ROASTED_SWEET_POTATO.get());
                                        output.accept(Items.REED.get());
                                        output.accept(Items.SHORTER_REED.get());
                                        output.accept(Items.TEA_SEED.get());
                                        output.accept(Items.PESTLE.get());
                                        output.accept(Items.DIRT_COOKSTOVE.get());
                                        output.accept(Items.MILL.get());
                                        output.accept(Items.MORTAR.get());
                                        output.accept(Items.TETSUSENCHI.get());
                                        output.accept(Items.RICE_DRYING_STICKS.get());
                                        output.accept(Items.CUTTING_BOARD.get());
                                        output.accept(Items.PAIL.get());
                                        output.accept(Items.CANDLE.get());
                                        output.accept(Items.MEAL_TABLE.get());
                                        output.accept(Items.WOOD_OTSUCHI.get());
                                        output.accept(Items.IRON_OTSUCHI.get());
                                        output.accept(Items.DIAMOND_OTSUCHI.get());
                                        output.accept(Items.WOODEN_HAMMER.get());
                                        output.accept(Items.CHISEL.get());
                                        output.accept(Items.TACHI.get());
                                        output.accept(Items.SUJIKABUTO.get());
                                    })).build()
                    );
}
