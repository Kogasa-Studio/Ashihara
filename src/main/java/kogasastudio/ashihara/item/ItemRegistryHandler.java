package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.item.block.ItemBlockCandle;
import kogasastudio.ashihara.item.block.ItemBlockPail;
import kogasastudio.ashihara.item.foods.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;
import static kogasastudio.ashihara.Ashihara.MATERIALS;

public class ItemRegistryHandler
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ashihara.MODID);

    //特殊物品
    public static final RegistryObject<Item> ASHIHARA_ICON = ITEMS.register("ashihara_icon", AshiharaIcon::new);

    //以下为物品
    public static final RegistryObject<Item> KOISHI = ITEMS.register("koishi", ItemKoishi::new);
    public static final RegistryObject<Item> MINATO_AQUA = ITEMS.register("aqua", ItemMinatoAqua::new);
    public static final RegistryObject<Item> RICE_SEEDLING = ITEMS.register("rice_seedling", ItemRiceSeedling::new);
    public static final RegistryObject<Item> RICE_CROP = ITEMS.register("rice_crop_item", ItemRiceCrop::new);
    public static final RegistryObject<Item> UNTHRESHED_RICE = ITEMS.register("unthreshed_rice", ItemUnthreshedRice::new);
    public static final RegistryObject<Item> STRAW = ITEMS.register("straw", ItemStraw::new);
    public static final RegistryObject<Item> RICE = ITEMS.register("rice", ItemRice::new);
    public static final RegistryObject<Item> COOKED_RICE = ITEMS.register("cooked_rice", ItemCookedRice::new);
    public static final RegistryObject<Item> DIRT_BALL_DON = ITEMS.register("dirt_ball_don", ItemDirtBallDon::new);
    public static final RegistryObject<Item> SAKURAMOCHI = ITEMS.register("sakuramochi", ItemSakuramochi::new);
    public static final RegistryObject<Item> PESTLE = ITEMS.register("pestle", ItemPestle::new);
    public static final RegistryObject<Item> SUJIKABUTO = ITEMS.register("sujikabuto", ItemSujikaButo::new);
    public static final RegistryObject<Item> SUSHI_BASIC = ITEMS.register("sushi_basic", () -> new EasyFood(5));
    public static final RegistryObject<Item> SUSHI_RAW_FISH = ITEMS.register("sushi_raw_fish", () -> new EasyFood(6));
    public static final RegistryObject<Item> SUSHI_SAKURA = ITEMS.register("sushi_sakura", () -> new EasyFood(7));
    public static final RegistryObject<Item> SUSHI_TAMAGO = ITEMS.register("sushi_tamago", () -> new EasyFood(6));
    public static final RegistryObject<Item> ONIGIRI = ITEMS.register("onigiri", () -> new EasyFood(6));
    public static final RegistryObject<Item> TAMAGO = ITEMS.register("tamago", () -> new EasyFood(1));
    public static final RegistryObject<Item> CUCUMBER = ITEMS.register("cucumber", () -> new EasyFood(2));
    public static final RegistryObject<Item> TOMATO = ITEMS.register("tomato", () -> new EasyFood(2));
    public static final RegistryObject<Item> SWEET_POTATO = ITEMS.register("sweet_potato", () -> new EasyFood(2));

    //工具
    public static final RegistryObject<Item> WOOD_OTSUCHI = ITEMS.register("wood_otsuchi", () -> new ItemOtsuchi(ItemTier.WOOD, 16, -3.4d));
    public static final RegistryObject<Item> IRON_OTSUCHI = ITEMS.register("iron_otsuchi", () -> new ItemOtsuchi(ItemTier.IRON, 16, -3.5d));
    public static final RegistryObject<Item> DIAMOND_OTSUCHI = ITEMS.register("diamond_otsuchi", () -> new ItemOtsuchi(ItemTier.DIAMOND, 16, -3.55d));

    //材料
    public static final RegistryObject<Item> SAKURA = ITEMS.register("sakura", ItemSakura::new);
    public static final RegistryObject<Item> SAKURA_PETAL = ITEMS.register("sakura_petal", ItemSakuraPetal::new);
    public static final RegistryObject<Item> DIRT_BALL = ITEMS.register("dirt_ball", ItemDirtBall::new);
    public static final RegistryObject<Item> CHRYSANTHEMUM_FLOWER = ITEMS.register("chrysanthemum_flower", ItemChrysanthemumFlower::new);
    public static final RegistryObject<Item> IRON_ORE_SHATTER = ITEMS.register("iron_ore_shatter", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> STONE_SHATTER = ITEMS.register("stone_shatter", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> FLOUR = ITEMS.register("flour", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> SOY_BEAN = ITEMS.register("soy_bean", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> COTTON = ITEMS.register("cotton", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> TEA_LEAF = ITEMS.register("tea_leaf", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> DRIED_TEA_LEAF = ITEMS.register("dried_tea_leaf", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> MACHA_POWDER = ITEMS.register("macha_powder", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> TEA_FLOWER = ITEMS.register("tea_flower", () -> new Item(new Item.Properties().group(MATERIALS)));
    public static final RegistryObject<Item> TEA_SEED = ITEMS.register("tea_seeds", () -> new BlockNamedItem(BlockRegistryHandler.TEA_TREE.get(), (new Item.Properties()).group(MATERIALS)));

    //以下为方块
    //TODO: public static final RegistryObject<Item>  = ITEMS.register("", () -> new BlockItem(BlockRegistryHandler. .get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_DIRT_DEPRESSION = ITEMS.register("dirt_depression", () -> new BlockItem(BlockRegistryHandler.BLOCK_DIRT_DEPRESSION.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> ITEM_WATER_FIELD = ITEMS.register("water_field", () -> new BlockItem(BlockRegistryHandler.BLOCK_WATER_FIELD.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> ITEM_CHERRY_BLOSSOM = ITEMS.register("cherry_blossom", () -> new BlockItem(BlockRegistryHandler.CHERRY_BLOSSOM.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_FALLEN_SAKURA = ITEMS.register("fallen_sakura", () -> new BlockItem(BlockRegistryHandler.FALLEN_SAKURA.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_CHERRY_SAPLING = ITEMS.register("cherry_sapling", () -> new BlockItem(BlockRegistryHandler.CHERRY_SAPLING.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_TETSUSENCHI = ITEMS.register("tetsusenchi", () -> new BlockItem(BlockRegistryHandler.BLOCK_TETSUSENCHI.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_MORTAR = ITEMS.register("mortar", () -> new BlockItem(BlockRegistryHandler.BLOCK_MORTAR.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> TATAMI = ITEMS.register("tatami", () -> new BlockItem(BlockRegistryHandler.TATAMI.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_JINJA_LANTERN = ITEMS.register("jinja_lantern", () -> new BlockItem(BlockRegistryHandler.BLOCK_JINJA_LANTERN.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> LANTERN_LONG_WHITE = ITEMS.register("lantern_long_white", () -> new BlockItem(BlockRegistryHandler.LANTERN_LONG_WHITE.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> LANTERN_LONG_RED = ITEMS.register("lantern_long_red", () -> new BlockItem(BlockRegistryHandler.LANTERN_LONG_RED.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHRYSANTHEMUM = ITEMS.register("chrysanthemum", () -> new BlockItem(BlockRegistryHandler.CHRYSANTHEMUM.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> REED = ITEMS.register("reed", () -> new BlockItem(BlockRegistryHandler.BLOCK_REED.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> SHORTER_REED = ITEMS.register("shorter_reed", () -> new BlockItem(BlockRegistryHandler.BLOCK_SHORTER_REED.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> MILL = ITEMS.register("mill", () -> new BlockItem(BlockRegistryHandler.MILL.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> STONE_LANTERN = ITEMS.register("stone_lantern", () -> new BlockItem(BlockRegistryHandler.STONE_LANTERN.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> HYDRANGEA_BUSH = ITEMS.register("hydrangea_bush", () -> new BlockItem(BlockRegistryHandler.HYDRANGEA_BUSH.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> TEA_TREE = ITEMS.register("tea_tree", () -> new BlockItem(BlockRegistryHandler.TEA_TREE.get(), new Item.Properties()));
    public static final RegistryObject<Item> PAIL = ITEMS.register("pail", ItemBlockPail::new);
    public static final RegistryObject<Item> CANDLE = ITEMS.register("candle", ItemBlockCandle::new);

    //木制品
    public static final RegistryObject<Item> CHERRY_LOG = ITEMS.register("cherry_log", () -> new BlockItem(BlockRegistryHandler.CHERRY_LOG.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHERRY_WOOD = ITEMS.register("cherry_wood", () -> new BlockItem(BlockRegistryHandler.CHERRY_WOOD.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHERRY_PLANKS = ITEMS.register("cherry_planks", () -> new BlockItem(BlockRegistryHandler.CHERRY_PLANKS.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHERRY_STAIRS = ITEMS.register("cherry_stairs", () -> new BlockItem(BlockRegistryHandler.CHERRY_STAIRS.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHERRY_SLAB = ITEMS.register("cherry_slab", () -> new BlockItem(BlockRegistryHandler.CHERRY_SLAB.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHERRY_FENCE = ITEMS.register("cherry_fence", () -> new BlockItem(BlockRegistryHandler.CHERRY_FENCE.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHERRY_FENCE_GATE = ITEMS.register("cherry_fence_gate", () -> new BlockItem(BlockRegistryHandler.CHERRY_FENCE_GATE.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> CHERRY_BUTTON = ITEMS.register("cherry_button", () -> new BlockItem(BlockRegistryHandler.CHERRY_BUTTON.get(), new Item.Properties().group(ASHIHARA)));

    public static final RegistryObject<Item> STRIPPED_RED_LOG = ITEMS.register("stripped_red_log", () -> new BlockItem(BlockRegistryHandler.STRIPPED_RED_LOG.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> RED_PLANKS = ITEMS.register("red_planks", () -> new BlockItem(BlockRegistryHandler.RED_PLANKS.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> RED_STAIRS = ITEMS.register("red_stairs", () -> new BlockItem(BlockRegistryHandler.RED_STAIRS.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> RED_SLAB = ITEMS.register("red_slab", () -> new BlockItem(BlockRegistryHandler.RED_SLAB.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> RED_FENCE = ITEMS.register("red_fence", () -> new BlockItem(BlockRegistryHandler.RED_FENCE.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> RED_FENCE_GATE = ITEMS.register("red_fence_gate", () -> new BlockItem(BlockRegistryHandler.RED_FENCE_GATE.get(), new Item.Properties().group(ASHIHARA)));

    public static final RegistryObject<Item> RED_ADVANCED_FENCE = ITEMS.register("advanced_red_fence", () -> new BlockItem(BlockRegistryHandler.RED_ADVANCED_FENCE.get(), new Item.Properties().group(ASHIHARA)));

    //桶
    public static final RegistryObject<Item> SOY_MILK_BUCKET = ITEMS.register("soy_milk_bucket", () -> new BucketItem(FluidRegistryHandler.SOY_MILK, new Item.Properties().group(ItemGroup.MISC)));
}
