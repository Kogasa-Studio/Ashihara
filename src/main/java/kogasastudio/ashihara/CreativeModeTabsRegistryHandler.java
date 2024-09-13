package kogasastudio.ashihara;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

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
                                    .icon(() -> ItemRegistryHandler.RED_ADVANCED_FENCE.get().getDefaultInstance())
                                    .displayItems(((itemDisplayParameters, output) ->
                                    {
                                        //樱木
                                        output.accept(ItemRegistryHandler.CHERRY_LOG.get());
                                        output.accept(ItemRegistryHandler.CHERRY_WOOD.get());
                                        output.accept(ItemRegistryHandler.STRIPPED_CHERRY_LOG.get());
                                        output.accept(ItemRegistryHandler.CHERRY_BLOSSOM.get());
                                        output.accept(ItemRegistryHandler.FALLEN_SAKURA.get());
                                        output.accept(ItemRegistryHandler.CHERRY_VINES.get());
                                        output.accept(ItemRegistryHandler.CHERRY_STAIRS.get());
                                        output.accept(ItemRegistryHandler.CHERRY_SLAB.get());
                                        output.accept(ItemRegistryHandler.CHERRY_FENCE.get());
                                        output.accept(ItemRegistryHandler.CHERRY_FENCE_GATE.get());
                                        output.accept(ItemRegistryHandler.CHERRY_BUTTON.get());
                                        output.accept(ItemRegistryHandler.CHERRY_LOG.get());
                                        //枫木
                                        output.accept(ItemRegistryHandler.MAPLE_LOG.get());
                                        output.accept(ItemRegistryHandler.MAPLE_WOOD.get());
                                        output.accept(ItemRegistryHandler.STRIPPED_MAPLE_LOG.get());
                                        output.accept(ItemRegistryHandler.MAPLE_LEAVES_RED.get());
                                        output.accept(ItemRegistryHandler.FALLEN_MAPLE_LEAVES_RED.get());
                                        output.accept(ItemRegistryHandler.MAPLE_STAIRS.get());
                                        output.accept(ItemRegistryHandler.MAPLE_SLAB.get());
                                        output.accept(ItemRegistryHandler.MAPLE_FENCE.get());
                                        output.accept(ItemRegistryHandler.MAPLE_FENCE_GATE.get());
                                        output.accept(ItemRegistryHandler.MAPLE_BUTTON.get());
                                        output.accept(ItemRegistryHandler.MAPLE_LOG.get());
                                        //朱木
                                        output.accept(ItemRegistryHandler.STRIPPED_RED_LOG.get());
                                        output.accept(ItemRegistryHandler.RED_PLANKS.get());
                                        output.accept(ItemRegistryHandler.RED_STAIRS.get());
                                        output.accept(ItemRegistryHandler.RED_SLAB.get());
                                        output.accept(ItemRegistryHandler.RED_FENCE.get());
                                        output.accept(ItemRegistryHandler.RED_FENCE_GATE.get());
                                        //桧皮
                                        output.accept(ItemRegistryHandler.CYPRESS_SKIN_BLOCK.get());
                                        output.accept(ItemRegistryHandler.CYPRESS_SKIN_SLAB.get());
                                        output.accept(ItemRegistryHandler.CYPRESS_SKIN_STAIRS.get());

                                        output.accept(ItemRegistryHandler.RED_ADVANCED_FENCE.get());
                                        output.accept(ItemRegistryHandler.SPRUCE_ADVANCED_FENCE.get());
                                        output.accept(ItemRegistryHandler.RED_THICK_COLUMN.get());
                                        output.accept(ItemRegistryHandler.RED_KUMINONO.get());
                                        output.accept(ItemRegistryHandler.RED_KAWAKI.get());
                                        output.accept(ItemRegistryHandler.RED_THIN_BEAM.get());
                                        output.accept(ItemRegistryHandler.THIN_WHITE_SOIL_WALL.get());
                                        output.accept(ItemRegistryHandler.STRAIGHT_BAR_WINDOW_GREEN.get());
                                        output.accept(ItemRegistryHandler.LANTERN_LONG_WHITE.get());
                                        output.accept(ItemRegistryHandler.LANTERN_LONG_RED.get());
                                        output.accept(ItemRegistryHandler.HOUSE_LIKE_HANGING_LANTERN.get());
                                        output.accept(ItemRegistryHandler.HEXAGONAL_HANGING_LANTERN.get());
                                        output.accept(ItemRegistryHandler.JINJA_LANTERN.get());
                                        output.accept(ItemRegistryHandler.STONE_LANTERN.get());
                                        output.accept(ItemRegistryHandler.BONBURI_LAMP.get());
                                        output.accept(ItemRegistryHandler.CANDLESTICK.get());
                                        output.accept(ItemRegistryHandler.OIL_PLATE_STICK.get());
                                        output.accept(ItemRegistryHandler.TATAMI.get());
                                        output.accept(ItemRegistryHandler.MULTI_RED_THICK_COLUMN.get());
                                        output.accept(ItemRegistryHandler.RED_BEAM.get());
                                        output.accept(ItemRegistryHandler.RED_HIJIKI.get());
                                        output.accept(ItemRegistryHandler.RED_HIJIKI_SUPPORTER.get());
                                        output.accept(ItemRegistryHandler.RED_HIJIKI_CONNECTOR.get());
                                        output.accept(ItemRegistryHandler.RED_HIJIKI_LONG.get());
                                        output.accept(ItemRegistryHandler.RED_BIG_TOU.get());
                                        output.accept(ItemRegistryHandler.RED_TOU.get());
                                        output.accept(ItemRegistryHandler.BASE_STONE.get());
                                        output.accept(ItemRegistryHandler.WHITE_SOIL_WALL.get());
                                        output.accept(ItemRegistryHandler.RAMMED_SOIL_WALL.get());
                                        output.accept(ItemRegistryHandler.BAMBOO_WALL_BONES.get());
                                        output.accept(ItemRegistryHandler.DIRT_DEPRESSION.get());
                                        output.accept(ItemRegistryHandler.WATER_FIELD.get());
                                    })).build()
                    );
    public static final Supplier<CreativeModeTab> MATERIALS =
            TABS.register
                    (
                            MATERIALS_TAB_NAME,
                            () -> CreativeModeTab.builder()
                                    .title(Component.translatable("itemGroup.group_ash_materials"))
                                    .icon(() -> ItemRegistryHandler.SAKURA.get().getDefaultInstance())
                                    .displayItems(((itemDisplayParameters, output) ->
                                    {
                                        output.accept(ItemRegistryHandler.SAKURA.get());
                                        output.accept(ItemRegistryHandler.SAKURA_PETAL.get());
                                        output.accept(ItemRegistryHandler.DRIED_BAMBOO.get());
                                        output.accept(ItemRegistryHandler.BAMBOO_MATERIAL.get());
                                        output.accept(ItemRegistryHandler.BAMBOO_STRIPS.get());
                                        output.accept(ItemRegistryHandler.BAMBOO_STICK.get());
                                        output.accept(ItemRegistryHandler.LIME_POWDER.get());
                                        output.accept(ItemRegistryHandler.SALT.get());
                                        output.accept(ItemRegistryHandler.COAL_POWDER.get());
                                        output.accept(ItemRegistryHandler.RICE_POWDER.get());
                                        output.accept(ItemRegistryHandler.FLOUR.get());
                                        output.accept(ItemRegistryHandler.BEAN_POWDER.get());
                                        output.accept(ItemRegistryHandler.MACHA_POWDER.get());
                                        output.accept(ItemRegistryHandler.STONE_SHATTER.get());
                                        output.accept(ItemRegistryHandler.IRON_ORE_SHATTER.get());
                                        output.accept(ItemRegistryHandler.GOLD_ORE_SHATTER.get());
                                        output.accept(ItemRegistryHandler.TEA_LEAF.get());
                                        output.accept(ItemRegistryHandler.DRIED_TEA_LEAF.get());
                                        output.accept(ItemRegistryHandler.TEA_FLOWER.get());
                                        output.accept(ItemRegistryHandler.TAMAGO.get());
                                        output.accept(ItemRegistryHandler.DIRT_BALL.get());
                                        output.accept(ItemRegistryHandler.RAMMED_SOIL.get());
                                        output.accept(ItemRegistryHandler.OIL_BUCKET.get());
                                        output.accept(ItemRegistryHandler.SOY_MILK_BUCKET.get());
                                    })).build()
                    );
    public static final Supplier<CreativeModeTab> ASHIHARA =
            TABS.register
                    (
                            ASHIHARA_MAIN_TAB_NAME,
                            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.group_ashihara"))
                                    .icon(() -> ItemRegistryHandler.ASHIHARA_ICON.get().getDefaultInstance())
                                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                                    .withTabsAfter(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, MATERIALS_TAB_NAME), ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, BUILDING_BLOCKS_TAB_NAME))
                                    .displayItems(((itemDisplayParameters, output) ->
                                    {
                                        output.accept(ItemRegistryHandler.WILD_RICE.get());
                                        output.accept(ItemRegistryHandler.RICE_SEEDLING.get());
                                        output.accept(ItemRegistryHandler.PADDY.get());
                                        output.accept(ItemRegistryHandler.RICE_CROP.get());
                                        output.accept(ItemRegistryHandler.DRIED_RICE_CROP.get());
                                        output.accept(ItemRegistryHandler.STRAW.get());
                                        output.accept(ItemRegistryHandler.RICE.get());
                                        output.accept(ItemRegistryHandler.COOKED_RICE.get());
                                        output.accept(ItemRegistryHandler.ONIGIRI.get());
                                        output.accept(ItemRegistryHandler.DIRT_BALL_DON.get());
                                        output.accept(ItemRegistryHandler.SAKURAMOCHI.get());
                                        output.accept(ItemRegistryHandler.CHRYSANTHEMUM.get());
                                        output.accept(ItemRegistryHandler.CHRYSANTHEMUM_FLOWER.get());
                                        output.accept(ItemRegistryHandler.MOCHI.get());
                                        output.accept(ItemRegistryHandler.DAIFUKU.get());
                                        output.accept(ItemRegistryHandler.DAIFUKU_KUSA.get());
                                        output.accept(ItemRegistryHandler.DAIFUKU_SAKURA.get());
                                        output.accept(ItemRegistryHandler.DANGO.get());
                                        output.accept(ItemRegistryHandler.DANGO_BEAN.get());
                                        output.accept(ItemRegistryHandler.DANGO_HANAMIE.get());
                                        output.accept(ItemRegistryHandler.DANGO_MITARASHI.get());
                                        output.accept(ItemRegistryHandler.SUSHI_BASIC.get());
                                        output.accept(ItemRegistryHandler.SUSHI_SAKURA.get());
                                        output.accept(ItemRegistryHandler.SUSHI_RAW_FISH.get());
                                        output.accept(ItemRegistryHandler.SUSHI_TAMAGO.get());
                                        output.accept(ItemRegistryHandler.CHERRY_SAPLING.get());
                                        output.accept(ItemRegistryHandler.RED_MAPLE_SAPLING.get());
                                        output.accept(ItemRegistryHandler.CUCUMBER.get());
                                        output.accept(ItemRegistryHandler.TOMATO.get());
                                        output.accept(ItemRegistryHandler.SOY_BEAN.get());
                                        output.accept(ItemRegistryHandler.TOFU.get());
                                        output.accept(ItemRegistryHandler.COTTON.get());
                                        output.accept(ItemRegistryHandler.SWEET_POTATO.get());
                                        output.accept(ItemRegistryHandler.ROASTED_SWEET_POTATO.get());
                                        output.accept(ItemRegistryHandler.REED.get());
                                        output.accept(ItemRegistryHandler.SHORTER_REED.get());
                                        output.accept(ItemRegistryHandler.TEA_SEED.get());
                                        output.accept(ItemRegistryHandler.PESTLE.get());
                                        output.accept(ItemRegistryHandler.MILL.get());
                                        output.accept(ItemRegistryHandler.MORTAR.get());
                                        output.accept(ItemRegistryHandler.TETSUSENCHI.get());
                                        output.accept(ItemRegistryHandler.RICE_DRYING_STICKS.get());
                                        output.accept(ItemRegistryHandler.CUTTING_BOARD.get());
                                        output.accept(ItemRegistryHandler.PAIL.get());
                                        output.accept(ItemRegistryHandler.CANDLE.get());
                                        output.accept(ItemRegistryHandler.MEAL_TABLE.get());
                                        output.accept(ItemRegistryHandler.WOOD_OTSUCHI.get());
                                        output.accept(ItemRegistryHandler.IRON_OTSUCHI.get());
                                        output.accept(ItemRegistryHandler.DIAMOND_OTSUCHI.get());
                                        output.accept(ItemRegistryHandler.WOODEN_HAMMER.get());
                                        output.accept(ItemRegistryHandler.CHISEL.get());
                                        output.accept(ItemRegistryHandler.TACHI.get());
                                        output.accept(ItemRegistryHandler.SUJIKABUTO.get());
                                    })).build()
                    );
}
