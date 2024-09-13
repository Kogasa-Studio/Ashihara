package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.item.block.*;
import kogasastudio.ashihara.item.foods.EasyFood;
import kogasastudio.ashihara.item.foods.ItemDirtBallDon;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@SuppressWarnings("unused")
public class ItemRegistryHandler
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Ashihara.MODID);

    //特殊物品
    public static final DeferredItem<Item> ASHIHARA_ICON = ITEMS.register("ashihara_icon", () -> new Item(new Item.Properties()));

    //以下为物品
    public static final DeferredItem<Item> KOISHI = ITEMS.register("koishi", ItemKoishi::new);
    public static final DeferredItem<Item> MINATO_AQUA = ITEMS.register("aqua", ItemMinatoAqua::new);
    public static final DeferredItem<Item> RICE_SEEDLING = ITEMS.register("rice_seedling", ItemRiceSeedling::new);
    public static final DeferredItem<Item> RICE_CROP = ITEMS.register("rice_crop_item", AshiharaItem::new);
    public static final DeferredItem<Item> DRIED_RICE_CROP = ITEMS.register("dried_rice_crop", AshiharaItem::new);
    public static final DeferredItem<Item> PADDY = ITEMS.register("paddy", ItemUnthreshedRice::new);
    public static final DeferredItem<Item> STRAW = ITEMS.register("straw", AshiharaItem::new);
    public static final DeferredItem<Item> RICE = ITEMS.register("rice", AshiharaItem::new);
    public static final DeferredItem<Item> PESTLE = ITEMS.register("pestle", () -> new Item(new Item.Properties().durability(256)));
    public static final DeferredItem<Item> SUJIKABUTO = ITEMS.register("sujikabuto", ItemSujikaButo::new);

    //食物
    public static final DeferredItem<Item> SAKURAMOCHI = ITEMS.register("sakuramochi", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(1).effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 2), 1.0F).build())));
    public static final DeferredItem<Item> COOKED_RICE = ITEMS.register("cooked_rice", () -> new EasyFood(5));
    public static final DeferredItem<Item> DIRT_BALL_DON = ITEMS.register("dirt_ball_don", ItemDirtBallDon::new);
    public static final DeferredItem<Item> SUSHI_BASIC = ITEMS.register("sushi_basic", () -> new EasyFood(5));
    public static final DeferredItem<Item> SUSHI_RAW_FISH = ITEMS.register("sushi_raw_fish", () -> new EasyFood(6));
    public static final DeferredItem<Item> SUSHI_SAKURA = ITEMS.register("sushi_sakura", () -> new EasyFood(7));
    public static final DeferredItem<Item> SUSHI_TAMAGO = ITEMS.register("sushi_tamago", () -> new EasyFood(6));
    public static final DeferredItem<Item> ONIGIRI = ITEMS.register("onigiri", () -> new EasyFood(6));
    public static final DeferredItem<Item> TAMAGO = ITEMS.register("tamago", () -> new EasyFood(1));
    public static final DeferredItem<Item> CUCUMBER = ITEMS.register("cucumber", () -> new ItemNameBlockItem(BlockRegistryHandler.CUCUMBERS.get(), new Item.Properties().food(new FoodProperties.Builder().nutrition(2).build())));
    public static final DeferredItem<Item> TOMATO = ITEMS.register("tomato", () -> new EasyFood(2));
    public static final DeferredItem<Item> SWEET_POTATO = ITEMS.register("sweet_potato", () -> new ItemNameBlockItem(BlockRegistryHandler.SWEET_POTATOES.get(), new Item.Properties().food(new FoodProperties.Builder().nutrition(2).build())));
    public static final DeferredItem<Item> ROASTED_SWEET_POTATO = ITEMS.register("roasted_sweet_potato", () -> new EasyFood(5));
    public static final DeferredItem<Item> TOFU = ITEMS.register("tofu", () -> new EasyFood(4));
    public static final DeferredItem<Item> MOCHI = ITEMS.register("mochi", () -> new EasyFood(3));
    public static final DeferredItem<Item> DAIFUKU = ITEMS.register("daifuku", () -> new EasyFood(5));
    public static final DeferredItem<Item> DAIFUKU_SAKURA = ITEMS.register("daifuku_sakura", () -> new EasyFood(5));
    public static final DeferredItem<Item> DAIFUKU_KUSA = ITEMS.register("daifuku_kusa", () -> new EasyFood(5));
    public static final DeferredItem<Item> DANGO = ITEMS.register("dango", () -> new EasyFood(6));
    public static final DeferredItem<Item> DANGO_HANAMIE = ITEMS.register("dango_hanamie", () -> new EasyFood(6));
    public static final DeferredItem<Item> DANGO_BEAN = ITEMS.register("dango_bean", () -> new EasyFood(6));
    public static final DeferredItem<Item> DANGO_MITARASHI = ITEMS.register("dango_mitarashi", () -> new EasyFood(6));

    //工具
    public static final DeferredItem<Item> TACHI = ITEMS.register("tachi", () -> new SwordItem(Tiers.DIAMOND, new Item.Properties(), new Tool(List.of(Tool.Rule.overrideSpeed(List.of(Blocks.COBWEB), 15f)), 4f, 15)));

    public static final DeferredItem<Item> WOOD_OTSUCHI = ITEMS.register("wood_otsuchi", () -> new ItemOtsuchi(Tiers.WOOD, 16, -3.4d));
    public static final DeferredItem<Item> IRON_OTSUCHI = ITEMS.register("iron_otsuchi", () -> new ItemOtsuchi(Tiers.IRON, 16, -3.5d));
    public static final DeferredItem<Item> DIAMOND_OTSUCHI = ITEMS.register("diamond_otsuchi", () -> new ItemOtsuchi(Tiers.DIAMOND, 16, -3.55d));
    public static final DeferredItem<Item> WOODEN_HAMMER = ITEMS.register("wooden_hammer", AshiharaItem::new);
    public static final DeferredItem<Item> CHISEL = ITEMS.register("chisel", AshiharaItem::new);

    //材料
    public static final DeferredItem<Item> SAKURA = ITEMS.register("sakura", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 1), 1.0F).build())));
    public static final DeferredItem<Item> SAKURA_PETAL = ITEMS.register("sakura_petal", AshiharaItem::new);
    public static final DeferredItem<Item> DIRT_BALL = ITEMS.register("dirt_ball", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 400, 2), 1.0F).build())));
    public static final DeferredItem<Item> CHRYSANTHEMUM_FLOWER = ITEMS.register("chrysanthemum_flower", AshiharaItem::new);
    public static final DeferredItem<Item> IRON_ORE_SHATTER = ITEMS.register("iron_ore_shatter", AshiharaItem::new);
    public static final DeferredItem<Item> GOLD_ORE_SHATTER = ITEMS.register("gold_ore_shatter", AshiharaItem::new);
    public static final DeferredItem<Item> STONE_SHATTER = ITEMS.register("stone_shatter", AshiharaItem::new);
    public static final DeferredItem<Item> COAL_POWDER = ITEMS.register("coal_powder", AshiharaItem::new);
    public static final DeferredItem<Item> SALT = ITEMS.register("salt", AshiharaItem::new);
    public static final DeferredItem<Item> FLOUR = ITEMS.register("flour", AshiharaItem::new);
    public static final DeferredItem<Item> RICE_POWDER = ITEMS.register("rice_powder", AshiharaItem::new);
    public static final DeferredItem<Item> BEAN_POWDER = ITEMS.register("bean_powder", AshiharaItem::new);
    public static final DeferredItem<Item> SOY_BEAN = ITEMS.register("soy_bean", () -> new ItemNameBlockItem(BlockRegistryHandler.SOY_BEANS.get(), new Item.Properties()));
    public static final DeferredItem<Item> COTTON = ITEMS.register("cotton", AshiharaItem::new);
    public static final DeferredItem<Item> TEA_LEAF = ITEMS.register("tea_leaf", AshiharaItem::new);
    public static final DeferredItem<Item> DRIED_TEA_LEAF = ITEMS.register("dried_tea_leaf", AshiharaItem::new);
    public static final DeferredItem<Item> MACHA_POWDER = ITEMS.register("macha_powder", AshiharaItem::new);
    public static final DeferredItem<Item> TEA_FLOWER = ITEMS.register("tea_flower", AshiharaItem::new);
    public static final DeferredItem<Item> TEA_SEED = ITEMS.register("tea_seeds", () -> new ItemNameBlockItem(BlockRegistryHandler.TEA_TREE.get(), (new Item.Properties())));
    public static final DeferredItem<Item> DRIED_BAMBOO = ITEMS.register("dried_bamboo", AshiharaItem::new);
    public static final DeferredItem<Item> BAMBOO_MATERIAL = ITEMS.register("bamboo_material", AshiharaItem::new);
    public static final DeferredItem<Item> BAMBOO_STICK = ITEMS.register("bamboo_stick", AshiharaItem::new);
    public static final DeferredItem<Item> BAMBOO_STRIPS = ITEMS.register("bamboo_strips", AshiharaItem::new);

    //以下为方块
    //TODO: public static final DeferredItem<Item>  = ITEMS.register("", () -> new BlockItem(BlockRegistryHandler. .get(), new Item.Properties().group(ASHIHARA)));
    public static final DeferredItem<Item> DIRT_DEPRESSION = ITEMS.register("dirt_depression", () -> new BlockItem(BlockRegistryHandler.DIRT_DEPRESSION.get(), new Item.Properties()));
    public static final DeferredItem<Item> WATER_FIELD = ITEMS.register("water_field", () -> new BlockItem(BlockRegistryHandler.WATER_FIELD.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_SAPLING = ITEMS.register("cherry_sapling", () -> new BlockItem(BlockRegistryHandler.CHERRY_SAPLING.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_MAPLE_SAPLING = ITEMS.register("red_maple_sapling", () -> new BlockItem(BlockRegistryHandler.RED_MAPLE_SAPLING.get(), new Item.Properties()));
    public static final DeferredItem<Item> TETSUSENCHI = ITEMS.register("tetsusenchi", () -> new BlockItem(BlockRegistryHandler.TETSUSENCHI.get(), new Item.Properties()));
    public static final DeferredItem<Item> RICE_DRYING_STICKS = ITEMS.register("rice_drying_sticks", () -> new BlockItem(BlockRegistryHandler.RICE_DRYING_STICKS.get(), new Item.Properties()));
    public static final DeferredItem<Item> MORTAR = ITEMS.register("mortar", () -> new BlockItem(BlockRegistryHandler.MORTAR.get(), new Item.Properties()));
    public static final DeferredItem<Item> MILL = ITEMS.register("mill", () -> new BlockItem(BlockRegistryHandler.MILL.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHRYSANTHEMUM = ITEMS.register("chrysanthemum", () -> new BlockItem(BlockRegistryHandler.CHRYSANTHEMUM.get(), new Item.Properties()));
    public static final DeferredItem<Item> WILD_RICE = ITEMS.register("wild_rice", () -> new BlockItem(BlockRegistryHandler.WILD_RICE.get(), new Item.Properties()));
    public static final DeferredItem<Item> REED = ITEMS.register("reed", () -> new BlockItem(BlockRegistryHandler.REED.get(), new Item.Properties()));
    public static final DeferredItem<Item> SHORTER_REED = ITEMS.register("shorter_reed", () -> new BlockItem(BlockRegistryHandler.SHORTER_REED.get(), new Item.Properties()));
    public static final DeferredItem<Item> HYDRANGEA_BUSH = ITEMS.register("hydrangea_bush", () -> new BlockItem(BlockRegistryHandler.HYDRANGEA_BUSH.get(), new Item.Properties()));
    public static final DeferredItem<Item> PAIL = ITEMS.register("pail", ItemBlockPail::new);
    public static final DeferredItem<Item> MEAL_TABLE = ITEMS.register("meal_table", () -> new BlockItem(BlockRegistryHandler.MEAL_TABLE.get(), new Item.Properties()));
    public static final DeferredItem<Item> CUTTING_BOARD = ITEMS.register("cutting_board", () -> new BlockItem(BlockRegistryHandler.CUTTING_BOARD.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHARLOTTE = ITEMS.register("charlotte", () -> new BlockItem(BlockRegistryHandler.CHARLOTTE.get(), new Item.Properties()));

    //建筑方块
    public static final DeferredItem<Item> CHERRY_BLOSSOM = ITEMS.register("cherry_blossom", () -> new BlockItem(BlockRegistryHandler.CHERRY_BLOSSOM.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_LEAVES_RED = ITEMS.register("maple_leaves_red", () -> new BlockItem(BlockRegistryHandler.MAPLE_LEAVES_RED.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_VINES = ITEMS.register("cherry_vines", () -> new BlockItem(BlockRegistryHandler.CHERRY_VINES.get(), new Item.Properties()));
    public static final DeferredItem<Item> FALLEN_SAKURA = ITEMS.register("fallen_sakura", () -> new BlockItem(BlockRegistryHandler.FALLEN_SAKURA.get(), new Item.Properties()));
    public static final DeferredItem<Item> FALLEN_MAPLE_LEAVES_RED = ITEMS.register("fallen_maple_leaves_red", () -> new BlockItem(BlockRegistryHandler.FALLEN_MAPLE_LEAVES_RED.get(), new Item.Properties()));
    public static final DeferredItem<Item> TATAMI = ITEMS.register("tatami", () -> new BlockItem(BlockRegistryHandler.TATAMI.get(), new Item.Properties()));

    public static final DeferredItem<Item> RED_ADVANCED_FENCE = ITEMS.register("advanced_red_fence", () -> new BlockItem(BlockRegistryHandler.RED_ADVANCED_FENCE.get(), new Item.Properties()));
    public static final DeferredItem<Item> SPRUCE_ADVANCED_FENCE = ITEMS.register("advanced_spruce_fence", () -> new BlockItem(BlockRegistryHandler.SPRUCE_ADVANCED_FENCE.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_THICK_COLUMN = ITEMS.register("red_thick_column", () -> new BlockItem(BlockRegistryHandler.RED_THICK_COLUMN.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_THIN_BEAM = ITEMS.register("red_thin_beam", ItemBlockBeam::new);
    public static final DeferredItem<Item> RED_KUMINONO = ITEMS.register("red_kumimono", () -> new BlockItem(BlockRegistryHandler.RED_KUMIMONO.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_KAWAKI = ITEMS.register("red_kawaki", () -> new BlockItem(BlockRegistryHandler.RED_KAWAKI.get(), new Item.Properties()));
    public static final DeferredItem<Item> THIN_WHITE_SOIL_WALL = ITEMS.register("thin_white_soil_wall", () -> new BlockItem(BlockRegistryHandler.THIN_WHITE_SOIL_WALL.get(), new Item.Properties()));
    public static final DeferredItem<Item> STRAIGHT_BAR_WINDOW_GREEN = ITEMS.register("straight_bar_window_green", () -> new BlockItem(BlockRegistryHandler.STRAIGHT_BAR_WINDOW_GREEN.get(), new Item.Properties()));

    public static final DeferredItem<Item> MULTI_RED_THICK_COLUMN = ITEMS.register("multi_red_thick_column", () -> new BuildingComponentItem(() -> BuildingComponents.RED_THICK_COLUMN));
    public static final DeferredItem<Item> RED_BEAM = ITEMS.register("red_beam", () -> new BuildingComponentItem(() -> BuildingComponents.RED_BEAM));
    public static final DeferredItem<Item> RED_HIJIKI = ITEMS.register("red_hijiki", () -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI));
    public static final DeferredItem<Item> RED_HIJIKI_SUPPORTER = ITEMS.register("red_hijiki_supporter", () -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_SUPPORTER));
    public static final DeferredItem<Item> RED_HIJIKI_CONNECTOR = ITEMS.register("red_hijiki_connector", () -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_CONNECTOR));
    public static final DeferredItem<Item> RED_HIJIKI_LONG = ITEMS.register("red_hijiki_long", () -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_LONG));
    public static final DeferredItem<Item> RED_TOU = ITEMS.register("red_tou", () -> new BuildingComponentItem(() -> BuildingComponents.RED_TOU));
    public static final DeferredItem<Item> RED_BIG_TOU = ITEMS.register("red_big_tou", () -> new BuildingComponentItem(() -> BuildingComponents.RED_BIG_TOU));
    public static final DeferredItem<Item> BASE_STONE = ITEMS.register("base_stone", () -> new BuildingComponentItem(() -> BuildingComponents.BASE_STONE));

    //灯具
    public static final DeferredItem<Item> LANTERN_LONG_WHITE = ITEMS.register("lantern_long_white", () -> new BlockItem(BlockRegistryHandler.LANTERN_LONG_WHITE.get(), new Item.Properties()));
    public static final DeferredItem<Item> LANTERN_LONG_RED = ITEMS.register("lantern_long_red", () -> new BlockItem(BlockRegistryHandler.LANTERN_LONG_RED.get(), new Item.Properties()));
    public static final DeferredItem<Item> HOUSE_LIKE_HANGING_LANTERN = ITEMS.register("house_like_hanging_lantern", () -> new BlockItem(BlockRegistryHandler.HOUSE_LIKE_HANGING_LANTERN.get(), new Item.Properties()));
    public static final DeferredItem<Item> HEXAGONAL_HANGING_LANTERN = ITEMS.register("hexagonal_hanging_lantern", () -> new BlockItem(BlockRegistryHandler.HEXAGONAL_HANGING_LANTERN.get(), new Item.Properties()));
    public static final DeferredItem<Item> JINJA_LANTERN = ITEMS.register("jinja_lantern", () -> new ItemBlockDouble(BlockRegistryHandler.JINJA_LANTERN.get(), new Item.Properties()));
    public static final DeferredItem<Item> STONE_LANTERN = ITEMS.register("stone_lantern", () -> new ItemBlockDouble(BlockRegistryHandler.STONE_LANTERN.get(), new Item.Properties()));
    public static final DeferredItem<Item> BONBURI_LAMP = ITEMS.register("bonburi_lamp", () -> new ItemBlockDouble(BlockRegistryHandler.BONBURI_LAMP.get(), new Item.Properties()));
    public static final DeferredItem<Item> CANDLESTICK = ITEMS.register("candlestick", () -> new ItemBlockDouble(BlockRegistryHandler.CANDLESTICK.get(), new Item.Properties()));
    public static final DeferredItem<Item> OIL_PLATE_STICK = ITEMS.register("oil_plate_stick", () -> new ItemBlockDouble(BlockRegistryHandler.OIL_PLATE_STICK.get(), new Item.Properties()));
    public static final DeferredItem<Item> CANDLE = ITEMS.register("candle", ItemBlockCandle::new);

    //木制品
    public static final DeferredItem<Item> CHERRY_LOG = ITEMS.register("cherry_log", () -> new BlockItem(BlockRegistryHandler.CHERRY_LOG.get(), new Item.Properties()));
    public static final DeferredItem<Item> STRIPPED_CHERRY_LOG = ITEMS.register("stripped_cherry_log", () -> new BlockItem(BlockRegistryHandler.STRIPPED_CHERRY_LOG.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_WOOD = ITEMS.register("cherry_wood", () -> new BlockItem(BlockRegistryHandler.CHERRY_WOOD.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_PLANKS = ITEMS.register("cherry_planks", () -> new BlockItem(BlockRegistryHandler.CHERRY_PLANKS.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_STAIRS = ITEMS.register("cherry_stairs", () -> new BlockItem(BlockRegistryHandler.CHERRY_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_SLAB = ITEMS.register("cherry_slab", () -> new BlockItem(BlockRegistryHandler.CHERRY_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_FENCE = ITEMS.register("cherry_fence", () -> new BlockItem(BlockRegistryHandler.CHERRY_FENCE.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_FENCE_GATE = ITEMS.register("cherry_fence_gate", () -> new BlockItem(BlockRegistryHandler.CHERRY_FENCE_GATE.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_BUTTON = ITEMS.register("cherry_button", () -> new BlockItem(BlockRegistryHandler.CHERRY_BUTTON.get(), new Item.Properties()));

    public static final DeferredItem<Item> STRIPPED_RED_LOG = ITEMS.register("stripped_red_log", () -> new BlockItem(BlockRegistryHandler.STRIPPED_RED_LOG.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_PLANKS = ITEMS.register("red_planks", () -> new BlockItem(BlockRegistryHandler.RED_PLANKS.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_STAIRS = ITEMS.register("red_stairs", () -> new BlockItem(BlockRegistryHandler.RED_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_SLAB = ITEMS.register("red_slab", () -> new BlockItem(BlockRegistryHandler.RED_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_FENCE = ITEMS.register("red_fence", () -> new BlockItem(BlockRegistryHandler.RED_FENCE.get(), new Item.Properties()));
    public static final DeferredItem<Item> RED_FENCE_GATE = ITEMS.register("red_fence_gate", () -> new BlockItem(BlockRegistryHandler.RED_FENCE_GATE.get(), new Item.Properties()));

    public static final DeferredItem<Item> MAPLE_LOG = ITEMS.register("maple_log", () -> new BlockItem(BlockRegistryHandler.MAPLE_LOG.get(), new Item.Properties()));
    public static final DeferredItem<Item> STRIPPED_MAPLE_LOG = ITEMS.register("stripped_maple_log", () -> new BlockItem(BlockRegistryHandler.STRIPPED_MAPLE_LOG.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_WOOD = ITEMS.register("maple_wood", () -> new BlockItem(BlockRegistryHandler.MAPLE_WOOD.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_PLANKS = ITEMS.register("maple_planks", () -> new BlockItem(BlockRegistryHandler.MAPLE_PLANKS.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_STAIRS = ITEMS.register("maple_stairs", () -> new BlockItem(BlockRegistryHandler.MAPLE_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_SLAB = ITEMS.register("maple_slab", () -> new BlockItem(BlockRegistryHandler.MAPLE_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_FENCE = ITEMS.register("maple_fence", () -> new BlockItem(BlockRegistryHandler.MAPLE_FENCE.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_FENCE_GATE = ITEMS.register("maple_fence_gate", () -> new BlockItem(BlockRegistryHandler.MAPLE_FENCE_GATE.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAPLE_BUTTON = ITEMS.register("maple_button", () -> new BlockItem(BlockRegistryHandler.MAPLE_BUTTON.get(), new Item.Properties()));

    public static final DeferredItem<Item> CYPRESS_SKIN_BLOCK = ITEMS.register("cypress_skin_block", () -> new BlockItem(BlockRegistryHandler.CYPRESS_SKIN_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> CYPRESS_SKIN_SLAB = ITEMS.register("cypress_skin_slab", () -> new BlockItem(BlockRegistryHandler.CYPRESS_SKIN_SLAB.get(), new Item.Properties()));

    public static final DeferredItem<Item> CYPRESS_SKIN_STAIRS = ITEMS.register("cypress_skin_stairs", () -> new BlockItem(BlockRegistryHandler.CYPRESS_SKIN_STAIRS.get(), new Item.Properties()));


    //桶
    public static final DeferredItem<Item> SOY_MILK_BUCKET = ITEMS.register("soy_milk_bucket", () -> new BucketItem(FluidRegistryHandler.SOY_MILK.get(), new Item.Properties()));
    public static final DeferredItem<Item> OIL_BUCKET = ITEMS.register("oil_bucket", () -> new BucketItem(FluidRegistryHandler.OIL.get(), new Item.Properties()));

    private static class AshiharaItem extends Item
    {
        public AshiharaItem()
        {
            super(new Properties());
        }
    }
}
