package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.helper.PlayerAnimationHelper;
import kogasastudio.ashihara.item.*;
import kogasastudio.ashihara.item.block.FurnitureComponentItem;
import kogasastudio.ashihara.item.block.ContainerComponentItem;
import kogasastudio.ashihara.item.armor.HagoromoItem;
import kogasastudio.ashihara.item.block.*;
import kogasastudio.ashihara.item.food.EasyFood;
import kogasastudio.ashihara.item.food.DirtBallDon;
import kogasastudio.ashihara.item.food.PickledFoodItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class Items
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Ashihara.MODID);

    //闁绘顫夐悾鈺呮偋閳轰焦鎯?
    public static final DeferredItem<? extends Item> ASHIHARA_ICON = ITEMS.registerSimpleItem("ashihara_icon");
    public static final DeferredItem<? extends Item> GUIDEBOOK = ITEMS.registerItem("guidebook", GuideBook::new);

    //濞寸姰鍎扮粭鍛▔閾忕懓鈷栭柛?
    public static final DeferredItem<? extends Item> KOISHI = ITEMS.registerItem("koishi", Koishi::new);
    public static final DeferredItem<? extends Item> MINATO_AQUA = ITEMS.registerItem("aqua", MinatoAqua::new);
    public static final DeferredItem<? extends Item> RICE_SEEDLING = ITEMS.registerItem("rice_seedling", RiceSeedling::new);
    public static final DeferredItem<? extends Item> RICE_CROP = ITEMS.registerItem("rice_crop_item", AshiharaItem::new);
    public static final DeferredItem<? extends Item> DRIED_RICE_CROP = ITEMS.registerItem("dried_rice_crop", AshiharaItem::new);
    public static final DeferredItem<? extends Item> PADDY = ITEMS.registerItem("paddy", Paddy::new);
    public static final DeferredItem<? extends Item> STRAW = ITEMS.registerItem("straw", AshiharaItem::new);
    public static final DeferredItem<? extends Item> PADDY_PILE = ITEMS.registerItem("paddy_pile", AshiharaItem::new);
    public static final DeferredItem<? extends Item> RICE = ITEMS.registerItem("rice", AshiharaItem::new);
    public static final DeferredItem<? extends Item> BROWN_RICE = ITEMS.registerItem("brown_rice", AshiharaItem::new);
    public static final DeferredItem<? extends Item> PESTLE = ITEMS.registerSimpleItem("pestle", properties -> properties.durability(512));
    public static final DeferredItem<? extends Item> POT_LID = ITEMS.registerItem("pot_lid", AshiharaItem::new);
    public static final DeferredItem<? extends Item> SUJIKABUTO = ITEMS.registerItem("sujikabuto", AshiharaItem::new);

    //濡炲鍠撴晶?
    public static final DeferredItem<? extends Item> SAKURAMOCHI = ITEMS.registerSimpleItem("sakuramochi", properties -> properties.food(new FoodProperties.Builder().nutrition(4).saturationModifier(1).build(), Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 2), 1.0F)).build()));
    public static final DeferredItem<? extends Item> COOKED_RICE = ITEMS.registerItem("cooked_rice", properties -> new EasyFood(5, properties));
    public static final DeferredItem<? extends Item> COOKED_BROWN_RICE = ITEMS.registerItem("cooked_brown_rice", properties -> new EasyFood(5, properties));
    public static final DeferredItem<? extends Item> DIRT_BALL_DON = ITEMS.registerItem("dirt_ball_don", DirtBallDon::new);
    public static final DeferredItem<? extends Item> SUSHI_BASIC = ITEMS.registerItem("sushi_basic", properties -> new EasyFood(5, properties));
    public static final DeferredItem<? extends Item> SUSHI_RAW_FISH = ITEMS.registerItem("sushi_raw_fish", properties -> new EasyFood(6, properties));
    public static final DeferredItem<? extends Item> SUSHI_SAKURA = ITEMS.registerItem("sushi_sakura", properties -> new EasyFood(7, properties));
    public static final DeferredItem<? extends Item> SUSHI_TAMAGO = ITEMS.registerItem("sushi_tamago", properties -> new EasyFood(6, properties));
    public static final DeferredItem<? extends Item> ONIGIRI = ITEMS.registerItem("onigiri", properties -> new EasyFood(6, properties));
    public static final DeferredItem<? extends Item> TAMAGO = ITEMS.registerItem("tamago", properties -> new EasyFood(1, properties));
    public static final DeferredItem<? extends Item> CUCUMBER = ITEMS.registerItem("cucumber", properties -> new BlockItem(Blocks.CUCUMBERS.get(), properties.food(new FoodProperties.Builder().nutrition(2).build()).useItemDescriptionPrefix())
    {
        @Override
        public InteractionResult use(Level level, Player player, InteractionHand usedHand)
        {
            if (usedHand.equals(InteractionHand.OFF_HAND) && player.getMainHandItem().is(KOISHI.asItem()))
            {
                PlayerAnimationHelper.pushPlayerAnimation(player, PlayerAnimations.TEST_TEKOKI_ANIM);
                return InteractionResult.SUCCESS;
            }
            return super.use(level, player, usedHand);
        }
    });
    public static final DeferredItem<? extends Item> CUCUMBER_SLICE = ITEMS.registerSimpleItem("cucumber_slice", properties -> properties.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.4f).build()));
    public static final DeferredItem<? extends Item> PICKLED_CUCUMBER_SLICE = ITEMS.registerItem("pickled_cucumber_slice", properties -> new PickledFoodItem(properties.food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.8f).build(), Consumables.DEFAULT_FOOD)));
    public static final DeferredItem<? extends Item> TOMATO = ITEMS.registerItem("tomato", properties -> new EasyFood(2, properties));
    public static final DeferredItem<? extends Item> SWEET_POTATO = ITEMS.registerItem("sweet_potato", properties -> new BlockItem(Blocks.SWEET_POTATOES.get(), properties.food(new FoodProperties.Builder().nutrition(2).build()).useItemDescriptionPrefix()));
    public static final DeferredItem<? extends Item> SCALLION = ITEMS.registerItem("scallion", properties -> new BlockItem(Blocks.SCALLION_CROP.get(), properties.food(new FoodProperties.Builder().nutrition(2).build()).useItemDescriptionPrefix()));
    public static final DeferredItem<? extends Item> WASABI = ITEMS.registerItem("wasabi", properties -> new BlockItem(Blocks.WASABI_CROP.get(), properties.food(new FoodProperties.Builder().nutrition(2).build()).useItemDescriptionPrefix()));
    public static final DeferredItem<? extends Item> TARE_SEED = ITEMS.registerItem("tare_seed", properties -> new BlockItem(Blocks.TARE_CROP.get(), properties.useItemDescriptionPrefix()));
    public static final DeferredItem<? extends Item> ROASTED_SWEET_POTATO = ITEMS.registerItem("roasted_sweet_potato", properties -> new EasyFood(5, properties));
    public static final DeferredItem<? extends Item> TOFU = ITEMS.registerItem("tofu", properties -> new EasyFood(4, properties));
    public static final DeferredItem<? extends Item> MOCHI = ITEMS.registerItem("mochi", properties -> new EasyFood(3, properties));
    public static final DeferredItem<? extends Item> DAIFUKU = ITEMS.registerItem("daifuku", properties -> new EasyFood(5, properties));
    public static final DeferredItem<? extends Item> DAIFUKU_SAKURA = ITEMS.registerItem("daifuku_sakura", properties -> new EasyFood(5, properties));
    public static final DeferredItem<? extends Item> DAIFUKU_KUSA = ITEMS.registerItem("daifuku_kusa", properties -> new EasyFood(5, properties));
    public static final DeferredItem<? extends Item> DANGO = ITEMS.registerItem("dango", properties -> new EasyFood(6, properties));
    public static final DeferredItem<? extends Item> DANGO_HANAMIE = ITEMS.registerItem("dango_hanamie", properties -> new EasyFood(6, properties));
    public static final DeferredItem<? extends Item> DANGO_BEAN = ITEMS.registerItem("dango_bean", properties -> new EasyFood(6, properties));
    public static final DeferredItem<? extends Item> DANGO_MITARASHI = ITEMS.registerItem("dango_mitarashi", properties -> new EasyFood(6, properties));

    //鐎规悶鍎遍崣?
    public static final DeferredItem<? extends Item> TACHI = ITEMS.registerSimpleItem("tachi", properties -> properties.sword(ToolMaterial.DIAMOND, 15, 4));

    public static final DeferredItem<? extends Item> WOOD_OTSUCHI = ITEMS.registerItem("wood_otsuchi", properties -> new Otsuchi(ToolMaterial.WOOD, 16, -3.4d, properties));
    public static final DeferredItem<? extends Item> IRON_OTSUCHI = ITEMS.registerItem("iron_otsuchi", properties -> new Otsuchi(ToolMaterial.IRON, 16, -3.5d, properties));
    public static final DeferredItem<? extends Item> DIAMOND_OTSUCHI = ITEMS.registerItem("diamond_otsuchi", properties -> new Otsuchi(ToolMaterial.DIAMOND, 16, -3.55d, properties));
    public static final DeferredItem<? extends Item> IRON_WIDE_HOE = ITEMS.registerItem("iron_wide_hoe", properties -> new WideHoeItem(ToolMaterial.IRON, -0.5F, -1.0F, properties.durability(375)));
    public static final DeferredItem<? extends Item> WOODEN_HAMMER = ITEMS.registerItem("wooden_hammer", AshiharaItem::new);
    public static final DeferredItem<? extends Item> CHISEL = ITEMS.registerItem("chisel", AshiharaItem::new);
   public static final DeferredItem<? extends Item> CHOPSTICKS = ITEMS.registerItem("bamboo_chopsticks", properties -> new ChopsticksItem(properties.stacksTo(1)));

    //闁哄鍔栭弸?
    public static final DeferredItem<? extends Item> SAKURA = ITEMS.registerSimpleItem("sakura", properties -> properties.food(new FoodProperties.Builder().nutrition(1).build(), Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 1), 1.0F)).build()));
    public static final DeferredItem<? extends Item> SAKURA_PETAL = ITEMS.registerItem("sakura_petal", AshiharaItem::new);
    public static final DeferredItem<? extends Item> DIRT_BALL = ITEMS.registerSimpleItem("dirt_ball", properties -> properties.food(new FoodProperties.Builder().nutrition(2).build(), Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 400, 2), 1.0F)).build()));
    public static final DeferredItem<? extends Item> CHRYSANTHEMUM_FLOWER = ITEMS.registerItem("chrysanthemum_flower", AshiharaItem::new);
    public static final DeferredItem<? extends Item> IRON_ORE_SHATTER = ITEMS.registerItem("iron_ore_shatter", AshiharaItem::new);
    public static final DeferredItem<? extends Item> GOLD_ORE_SHATTER = ITEMS.registerItem("gold_ore_shatter", AshiharaItem::new);
    public static final DeferredItem<? extends Item> STONE_SHATTER = ITEMS.registerItem("stone_shatter", AshiharaItem::new);
    public static final DeferredItem<? extends Item> COAL_POWDER = ITEMS.registerItem("coal_powder", AshiharaItem::new);
    public static final DeferredItem<? extends Item> COARSE_SALT = ITEMS.registerItem("coarse_salt", AshiharaItem::new);
    public static final DeferredItem<? extends Item> SALT = ITEMS.registerItem("salt", AshiharaItem::new);
    public static final DeferredItem<? extends Item> BRAN = ITEMS.registerItem("bran", AshiharaItem::new);
    public static final DeferredItem<? extends Item> KOJI = ITEMS.registerItem("koji", properties -> new EasyFood(4, properties));
    public static final DeferredItem<? extends Item> FLOUR = ITEMS.registerItem("flour", AshiharaItem::new);
    public static final DeferredItem<? extends Item> RICE_POWDER = ITEMS.registerItem("rice_powder", AshiharaItem::new);
    public static final DeferredItem<? extends Item> BEAN_POWDER = ITEMS.registerItem("bean_powder", AshiharaItem::new);
    public static final DeferredItem<? extends Item> SOY_BEAN = ITEMS.registerItem("soy_bean", properties -> new BlockItem(Blocks.SOY_BEANS.get(), properties.useItemDescriptionPrefix()));
    public static final DeferredItem<? extends Item> MILLET = ITEMS.registerItem("millet", properties -> new BlockItem(Blocks.MILLETS.get(), properties.useItemDescriptionPrefix()));
    public static final DeferredItem<? extends Item> COOKED_MILLET = ITEMS.registerSimpleItem("cooked_millet", properties -> properties.food(new FoodProperties.Builder().nutrition(5).saturationModifier(1f).build(), Consumables.DEFAULT_FOOD));
    public static final DeferredItem<? extends Item> WHITE_RADISH = ITEMS.registerItem("white_radish", properties -> new BlockItem(Blocks.WHITE_RADISHES.get(), properties.food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build(), Consumables.DEFAULT_FOOD)));
    public static final DeferredItem<? extends Item> SOAKED_SOY_BEAN = ITEMS.registerItem("soaked_soy_bean", properties -> new EasyFood(1, properties));
    public static final DeferredItem<? extends Item> BOILED_SOY_BEAN = ITEMS.registerItem("boiled_soy_bean", properties -> new EasyFood(2, properties));
    public static final DeferredItem<? extends Item> COTTON = ITEMS.registerItem("cotton", AshiharaItem::new);
    public static final DeferredItem<? extends Item> TEA_LEAF = ITEMS.registerItem("tea_leaf", AshiharaItem::new);
    public static final DeferredItem<? extends Item> DRIED_TEA_LEAF = ITEMS.registerItem("dried_tea_leaf", AshiharaItem::new);
    public static final DeferredItem<? extends Item> MACHA_POWDER = ITEMS.registerItem("macha_powder", AshiharaItem::new);
    public static final DeferredItem<? extends Item> TEA_FLOWER = ITEMS.registerItem("tea_flower", AshiharaItem::new);
    public static final DeferredItem<? extends Item> TEA_SEED = ITEMS.registerSimpleBlockItem("tea_seeds", Blocks.TEA_TREE);
    public static final DeferredItem<? extends Item> DRIED_BAMBOO = ITEMS.registerItem("dried_bamboo", AshiharaItem::new);
    public static final DeferredItem<? extends Item> BAMBOO_MATERIAL = ITEMS.registerItem("bamboo_material", AshiharaItem::new);
    public static final DeferredItem<? extends Item> BAMBOO_STICK = ITEMS.registerItem("bamboo_stick", AshiharaItem::new);
    public static final DeferredItem<? extends Item> BAMBOO_STRIPS = ITEMS.registerItem("bamboo_strips", AshiharaItem::new);
    public static final DeferredItem<? extends Item> RAMMED_SOIL = ITEMS.registerItem("rammed_soil", AshiharaItem::new);
    public static final DeferredItem<? extends Item> LIME_POWDER = ITEMS.registerItem("lime_powder", AshiharaItem::new);

    //濞寸姰鍎扮粭鍛▔閻戞ɑ鐓欓柛?
    //TODO: public static final DeferredItem<? extends Item>  = ITEMS.register("", () -> new BlockItem(Blocks. .get(), new Item.Properties().group(ASHIHARA)));
    public static final DeferredItem<? extends Item> DIRT_DEPRESSION = ITEMS.registerSimpleBlockItem("dirt_depression", Blocks.DIRT_DEPRESSION);
    public static final DeferredItem<? extends Item> WATER_FIELD = ITEMS.registerSimpleBlockItem("water_field", Blocks.WATER_FIELD);
    public static final DeferredItem<? extends Item> SALT_FIELD = ITEMS.registerSimpleBlockItem("salt_field", Blocks.SALT_FIELD);
    public static final DeferredItem<? extends Item> CHERRY_SAPLING = ITEMS.registerSimpleBlockItem("cherry_sapling", Blocks.CHERRY_SAPLING);
    public static final DeferredItem<? extends Item> RED_MAPLE_SAPLING = ITEMS.registerSimpleBlockItem("red_maple_sapling", Blocks.RED_MAPLE_SAPLING);
    public static final DeferredItem<? extends Item> TETSUSENCHI = ITEMS.registerSimpleBlockItem("tetsusenchi", Blocks.TETSUSENCHI);
    public static final DeferredItem<? extends Item> RICE_DRYING_STICKS = ITEMS.registerSimpleBlockItem("rice_drying_sticks", Blocks.RICE_DRYING_STICKS);
    public static final DeferredItem<? extends Item> MORTAR = ITEMS.registerSimpleBlockItem("mortar", Blocks.MORTAR);
    public static final DeferredItem<? extends Item> POT = ITEMS.registerSimpleBlockItem("pot", Blocks.POT);
    public static final DeferredItem<? extends Item> MILL = ITEMS.registerSimpleBlockItem("mill", Blocks.MILL);
    public static final DeferredItem<? extends Item> DIRT_COOKSTOVE = ITEMS.registerSimpleBlockItem("dirt_cookstove", Blocks.DIRT_COOKSTOVE);
    public static final DeferredItem<? extends Item> CHRYSANTHEMUM = ITEMS.registerSimpleBlockItem("chrysanthemum", Blocks.CHRYSANTHEMUM);
    public static final DeferredItem<? extends Item> WILD_RICE = ITEMS.registerSimpleBlockItem("wild_rice", Blocks.WILD_RICE);
    public static final DeferredItem<? extends Item> REED = ITEMS.registerSimpleBlockItem("reed", Blocks.REED);
    public static final DeferredItem<? extends Item> SHORTER_REED = ITEMS.registerSimpleBlockItem("shorter_reed", Blocks.SHORTER_REED);
    public static final DeferredItem<? extends Item> HYDRANGEA_BUSH = ITEMS.registerSimpleBlockItem("hydrangea_bush", Blocks.HYDRANGEA_BUSH);
    //public static final DeferredItem<? extends Item> PAIL = ITEMS.registerItem("pail", PailBlockItem::new, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<? extends Item> MEAL_TABLE = ITEMS.registerSimpleBlockItem("meal_table", Blocks.MEAL_TABLE);
    public static final DeferredItem<? extends Item> CUTTING_BOARD = ITEMS.registerSimpleBlockItem("cutting_board", Blocks.CUTTING_BOARD);
    public static final DeferredItem<? extends Item> CHARLOTTE = ITEMS.registerSimpleBlockItem("charlotte", Blocks.CHARLOTTE);

    //鐎点倛娅ｉ悺姘跺棘閻熺増鍋?
    public static final DeferredItem<? extends Item> CHERRY_BLOSSOM = ITEMS.registerSimpleBlockItem("cherry_blossom", Blocks.CHERRY_BLOSSOM);
    public static final DeferredItem<? extends Item> MAPLE_LEAVES_RED = ITEMS.registerSimpleBlockItem("maple_leaves_red", Blocks.MAPLE_LEAVES_RED);
    public static final DeferredItem<? extends Item> CHERRY_VINES = ITEMS.registerSimpleBlockItem("cherry_vines", Blocks.CHERRY_VINES);
    public static final DeferredItem<? extends Item> FALLEN_SAKURA = ITEMS.registerSimpleBlockItem("fallen_sakura", Blocks.FALLEN_SAKURA);
    public static final DeferredItem<? extends Item> FALLEN_MAPLE_LEAVES_RED = ITEMS.registerSimpleBlockItem("fallen_maple_leaves_red", Blocks.FALLEN_MAPLE_LEAVES_RED);
    public static final DeferredItem<? extends Item> TATAMI = ITEMS.registerSimpleBlockItem("tatami", Blocks.TATAMI);

    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE = ITEMS.registerSimpleBlockItem("advanced_red_fence", Blocks.RED_ADVANCED_FENCE);
    public static final DeferredItem<? extends Item> SPRUCE_ADVANCED_FENCE = ITEMS.registerSimpleBlockItem("advanced_spruce_fence", Blocks.SPRUCE_ADVANCED_FENCE);
    public static final DeferredItem<? extends Item> RED_THICK_COLUMN = ITEMS.registerSimpleBlockItem("red_thick_column", Blocks.RED_THICK_COLUMN);
    public static final DeferredItem<? extends Item> RED_THIN_BEAM = ITEMS.registerItem("red_thin_beam", BeamBlockItem::new, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_KUMINONO = ITEMS.registerSimpleBlockItem("red_kumimono", Blocks.RED_KUMIMONO);
    public static final DeferredItem<? extends Item> RED_KAWAKI = ITEMS.registerSimpleBlockItem("red_kawaki", Blocks.RED_KAWAKI);
    public static final DeferredItem<? extends Item> THIN_WHITE_SOIL_WALL = ITEMS.registerSimpleBlockItem("thin_white_soil_wall", Blocks.THIN_WHITE_SOIL_WALL);
    public static final DeferredItem<? extends Item> STRAIGHT_BAR_WINDOW_GREEN = ITEMS.registerSimpleBlockItem("straight_bar_window_green", Blocks.STRAIGHT_BAR_WINDOW_GREEN);

    public static final DeferredItem<? extends Item> MULTI_RED_THICK_COLUMN = ITEMS.registerItem("multi_red_thick_column", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_THICK_COLUMN, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_THICK_COLUMN_SHORT = ITEMS.registerItem("red_thick_column_short", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_THICK_COLUMN_SHORT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_COLUMN = ITEMS.registerItem("red_column", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_COLUMN, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_COLUMN_SHORT = ITEMS.registerItem("red_column_short", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_COLUMN_SHORT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_BEAM = ITEMS.registerItem("red_beam", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_BEAM, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_BEAM_CORNER = ITEMS.registerItem("red_beam_corner", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_BEAM_CORNER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_CLAMP = ITEMS.registerItem("red_clamp", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_CLAMP, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_CLAMP_JOINT = ITEMS.registerItem("red_clamp_joint", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_CLAMP_JOINT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_RAFTER_STICKING_BEAM = ITEMS.registerItem("red_rafter_sticking_beam", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_RAFTER_STICKING_BEAM, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HIJIKI = ITEMS.registerItem("red_hijiki", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HIJIKI_SUPPORTER = ITEMS.registerItem("red_hijiki_supporter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_SUPPORTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HIJIKI_CONNECTOR = ITEMS.registerItem("red_hijiki_connector", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_CONNECTOR, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HIJIKI_LONG = ITEMS.registerItem("red_hijiki_long", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_LONG, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HIJIKI_CORNER_NORMAL = ITEMS.registerItem("red_hijiki_corner_normal", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_CORNER_NORMAL, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HIJIKI_CORNER_LONG = ITEMS.registerItem("red_hijiki_corner_long", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HIJIKI_CORNER_LONG, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HANGING_STICKER = ITEMS.registerItem("red_hanging_sticker", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HANGING_STICKER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HANGING_STICKER_CORNER = ITEMS.registerItem("red_hanging_sticker_corner", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HANGING_STICKER_CORNER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_TOU = ITEMS.registerItem("red_tou", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_TOU, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_TOU_OBLIQUE = ITEMS.registerItem("red_tou_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_TOU_OBLIQUE, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ONI_TOU = ITEMS.registerItem("red_oni_tou", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ONI_TOU, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_BIG_TOU = ITEMS.registerItem("red_big_tou", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_BIG_TOU, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> BASE_STONE = ITEMS.registerItem("base_stone", properties -> new BuildingComponentItem(() -> BuildingComponents.BASE_STONE, Blocks.STONE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_SOIL_WALL = ITEMS.registerItem("white_soil_wall", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_SOIL_WALL, Blocks.WHITE_SOIL_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_SOIL_WALL_QUARTER = ITEMS.registerItem("white_soil_wall_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_SOIL_WALL_QUARTER, Blocks.WHITE_SOIL_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RAMMED_SOIL_WALL = ITEMS.registerItem("rammed_soil_wall", properties -> new BuildingComponentItem(() -> BuildingComponents.RAMMED_SOIL_WALL, Blocks.RAMMED_SOIL_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RAMMED_SOIL_WALL_QUARTER = ITEMS.registerItem("rammed_soil_wall_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.RAMMED_SOIL_WALL_QUARTER, Blocks.RAMMED_SOIL_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> BAMBOO_WALL_BONES = ITEMS.registerItem("bamboo_wall_bones", properties -> new BuildingComponentItem(() -> BuildingComponents.BAMBOO_WALL_BONES, Blocks.BAMBOO_BONES_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> BAMBOO_WALL_BONES_QUARTER = ITEMS.registerItem("bamboo_wall_bones_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.BAMBOO_WALL_BONES_QUARTER, Blocks.BAMBOO_BONES_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_PLANKS_WALL = ITEMS.registerItem("red_planks_wall", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_PLANKS_WALL, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_PLANKS_WALL_QUARTER = ITEMS.registerItem("red_planks_wall_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_PLANKS_WALL_QUARTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_LEFT = ITEMS.registerItem("red_roof_edge_planks_left", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_LEFT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_RIGHT = ITEMS.registerItem("red_roof_edge_planks_right", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_RIGHT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_1_4_LEFT = ITEMS.registerItem("red_roof_edge_planks_1_4_left", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_1_4_LEFT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_1_2_LEFT = ITEMS.registerItem("red_roof_edge_planks_1_2_left", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_1_2_LEFT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_1_1_LEFT = ITEMS.registerItem("red_roof_edge_planks_1_1_left", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_1_1_LEFT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_1_4_RIGHT = ITEMS.registerItem("red_roof_edge_planks_1_4_right", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_1_4_RIGHT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_1_2_RIGHT = ITEMS.registerItem("red_roof_edge_planks_1_2_right", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_1_2_RIGHT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ROOF_EDGE_PLANKS_1_1_RIGHT = ITEMS.registerItem("red_roof_edge_planks_1_1_right", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ROOF_EDGE_PLANKS_1_1_RIGHT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> GOLD_ROOF_EDGE_PLANKS_DECORATION = ITEMS.registerItem("gold_roof_edge_planks_decoration", properties -> new BuildingComponentItem(() -> BuildingComponents.GOLD_ROOF_EDGE_PLANKS_DECORATION, Blocks.GOLD_STRUCTURAL_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HANGING_FISH_PIG_EYE = ITEMS.registerItem("red_hanging_fish_pig_eye", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HANGING_FISH_PIG_EYE, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_HANGING_FISH_TURNIP = ITEMS.registerItem("red_hanging_fish_turnip", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_HANGING_FISH_TURNIP, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> GREEN_STRAIGHT_BAR_WINDOW = ITEMS.registerItem("green_straight_bar_window", properties -> new BuildingComponentItem(() -> BuildingComponents.GREEN_STRAIGHT_BAR_WINDOW, Blocks.GREEN_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> GREEN_STRAIGHT_BAR_WINDOW_QUARTER = ITEMS.registerItem("green_straight_bar_window_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.GREEN_STRAIGHT_BAR_WINDOW_QUARTER, Blocks.GREEN_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_LATTICED_WINDOW = ITEMS.registerItem("red_latticed_window", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_LATTICED_WINDOW, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_LATTICED_WINDOW_QUARTER = ITEMS.registerItem("red_latticed_window_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_LATTICED_WINDOW_QUARTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_PLANKS_FLOOR = ITEMS.registerItem("red_planks_floor", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_PLANKS_FLOOR, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_PLANKS_FLOOR_QUARTER = ITEMS.registerItem("red_planks_floor_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_PLANKS_FLOOR_QUARTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_LATTICED_CEILING_WHITE = ITEMS.registerItem("red_latticed_ceiling_white", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_LATTICED_CEILING_WHITE, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_LATTICED_CEILING_WHITE_QUARTER = ITEMS.registerItem("red_latticed_ceiling_white_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_LATTICED_CEILING_WHITE_QUARTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_BENDED_STICKERS = ITEMS.registerItem("red_bended_stickers", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_BENDED_STICKERS, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_FROG_LEGS_LIKED_STICKER = ITEMS.registerItem("red_frog_legs_liked_sticker", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_FROG_LEGS_LIKED_STICKER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_STEEP_RAFTER = ITEMS.registerItem("red_steep_rafter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_STEEP_RAFTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_SMOOTH_RAFTER = ITEMS.registerItem("red_smooth_rafter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_SMOOTH_RAFTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_RAFTER_END = ITEMS.registerItem("red_rafter_end", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_RAFTER_END, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_RAFTER_CONNECTOR = ITEMS.registerItem("red_rafter_connector", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_RAFTER_CONNECTOR, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_THIN_RAFTER = ITEMS.registerItem("red_thin_rafter", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_THIN_RAFTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_THIN_RAFTER_1_1 = ITEMS.registerItem("red_thin_rafter_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_THIN_RAFTER_1_1, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_THIN_RAFTER_1_2 = ITEMS.registerItem("red_thin_rafter_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_THIN_RAFTER_1_2, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_DOUBLE_RAFTER = ITEMS.registerItem("red_thin_rafter_doubled", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_DOUBLE_RAFTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_DOUBLE_RAFTER_1_1 = ITEMS.registerItem("red_thin_rafter_doubled_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_DOUBLE_RAFTER_1_1, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_DOUBLE_RAFTER_1_2 = ITEMS.registerItem("red_thin_rafter_doubled_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_DOUBLE_RAFTER_1_2, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_LONG_DOUBLE_RAFTER = ITEMS.registerItem("red_thin_rafter_long_doubled", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_LONG_DOUBLE_RAFTER, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_LONG_DOUBLE_RAFTER_1_4 = ITEMS.registerItem("red_thin_rafter_long_doubled_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_LONG_DOUBLE_RAFTER_1_4, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_THIN_RAFTER_CONNECTOR = ITEMS.registerItem("red_thin_rafter_connector", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_THIN_RAFTER_CONNECTOR, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS = ITEMS.registerItem("spruce_rafter_planks", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_QUARTER = ITEMS.registerItem("spruce_rafter_planks_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_QUARTER, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS = ITEMS.registerItem("white_rafter_planks", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_QUARTER = ITEMS.registerItem("white_rafter_planks_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_QUARTER, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_1_1 = ITEMS.registerItem("spruce_rafter_planks_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_1_1, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_1_2 = ITEMS.registerItem("spruce_rafter_planks_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_1_2, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_WIDE = ITEMS.registerItem("spruce_rafter_planks_wide", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_WIDE, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_WIDE_1_4 = ITEMS.registerItem("spruce_rafter_planks_wide_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_WIDE_1_4, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_QUARTER_1_2 = ITEMS.registerItem("spruce_rafter_planks_quarter_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_QUARTER_1_2, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_QUARTER_1_1 = ITEMS.registerItem("spruce_rafter_planks_quarter_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_QUARTER_1_1, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_1_1 = ITEMS.registerItem("white_rafter_planks_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_1_1, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_1_2 = ITEMS.registerItem("white_rafter_planks_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_1_2, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_WIDE = ITEMS.registerItem("white_rafter_planks_wide", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_WIDE, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_WIDE_1_4 = ITEMS.registerItem("white_rafter_planks_wide_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_WIDE_1_4, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_QUARTER_1_2 = ITEMS.registerItem("white_rafter_planks_quarter_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_QUARTER_1_2, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_QUARTER_1_1 = ITEMS.registerItem("white_rafter_planks_quarter_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_QUARTER_1_1, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF = ITEMS.registerItem("cypress_roof", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_1_4 = ITEMS.registerItem("cypress_roof_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_1_4, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_THIN_RAFTER_1_4 = ITEMS.registerItem("red_thin_rafter_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_THIN_RAFTER_1_4, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_RAFTER_PLANKS_1_4 = ITEMS.registerItem("spruce_rafter_planks_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_RAFTER_PLANKS_1_4, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_RAFTER_PLANKS_1_4 = ITEMS.registerItem("white_rafter_planks_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.WHITE_RAFTER_PLANKS_1_4, Blocks.WHITE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_HALF_1_4 = ITEMS.registerItem("cypress_roof_half_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_HALF_1_4, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_CORNER = ITEMS.registerItem("cypress_roof_corner", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_CORNER, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE = ITEMS.registerItem("cypress_roof_edge", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_RIGHT = ITEMS.registerItem("cypress_roof_edge_right", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_RIGHT, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_LEFT = ITEMS.registerItem("cypress_roof_edge_left", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_LEFT, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_RIGHT_1_4 = ITEMS.registerItem("cypress_roof_edge_right_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_RIGHT_1_4, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_LEFT_1_4 = ITEMS.registerItem("cypress_roof_edge_left_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_LEFT_1_4, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_RIGHT_1_2 = ITEMS.registerItem("cypress_roof_edge_right_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_RIGHT_1_2, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_LEFT_1_2 = ITEMS.registerItem("cypress_roof_edge_left_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_LEFT_1_2, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_RIGHT_1_1 = ITEMS.registerItem("cypress_roof_edge_right_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_RIGHT_1_1, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_LEFT_1_1 = ITEMS.registerItem("cypress_roof_edge_left_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_LEFT_1_1, Blocks.CYPRESS_SKIN_COMPONENT, properties), Item.Properties::useItemDescriptionPrefix);
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_CORNER = ITEMS.registerItem("cypress_roof_edge_corner", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_CORNER, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_SACRED_FENCE_PILLAR = ITEMS.registerItem("red_sacred_fence_pillar", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_SACRED_FENCE_PILLAR, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_SACRED_FENCE_HALF = ITEMS.registerItem("red_sacred_fence_half", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_SACRED_FENCE_HALF, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_SACRED_FENCE = ITEMS.registerItem("red_sacred_fence", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_SACRED_FENCE, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_SACRED_FENCE_PILLAR = ITEMS.registerItem("spruce_sacred_fence_pillar", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_SACRED_FENCE_PILLAR, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_SACRED_FENCE_HALF = ITEMS.registerItem("spruce_sacred_fence_half", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_SACRED_FENCE_HALF, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> STONE_SACRED_FENCE = ITEMS.registerItem("stone_sacred_fence", properties -> new BuildingComponentItem(() -> BuildingComponents.STONE_SACRED_FENCE, Blocks.STONE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> STONE_SACRED_FENCE_HALF = ITEMS.registerItem("stone_sacred_fence_half", properties -> new BuildingComponentItem(() -> BuildingComponents.STONE_SACRED_FENCE_HALF, Blocks.STONE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> STONE_SACRED_FENCE_PILLAR = ITEMS.registerItem("stone_sacred_fence_pillar", properties -> new BuildingComponentItem(() -> BuildingComponents.STONE_SACRED_FENCE_PILLAR, Blocks.STONE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> SPRUCE_SACRED_FENCE = ITEMS.registerItem("spruce_sacred_fence", properties -> new BuildingComponentItem(() -> BuildingComponents.SPRUCE_SACRED_FENCE, Blocks.SPRUCE_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_1_1 = ITEMS.registerItem("red_advanced_fence_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_1_1, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_1_2 = ITEMS.registerItem("red_advanced_fence_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_1_2, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_1_4 = ITEMS.registerItem("red_advanced_fence_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_1_4, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_FULL = ITEMS.registerItem("red_advanced_fence_full", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_FULL, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_HALF = ITEMS.registerItem("red_advanced_fence_half", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_HALF, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_TAIL = ITEMS.registerItem("red_advanced_fence_tail", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_TAIL, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_PILLAR = ITEMS.registerItem("red_advanced_fence_pillar", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_PILLAR, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_PILLAR_SHORT = ITEMS.registerItem("red_advanced_fence_pillar_short", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_PILLAR_SHORT, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> RED_ADVANCED_FENCE_PILLAR_THICK = ITEMS.registerItem("red_advanced_fence_pillar_thick", properties -> new BuildingComponentItem(() -> BuildingComponents.RED_ADVANCED_FENCE_PILLAR_THICK, Blocks.RED_WOOD_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_HALF = ITEMS.registerItem("cypress_roof_half", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_HALF, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_HALF_1_2 = ITEMS.registerItem("cypress_roof_half_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_HALF_1_2, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_HALF_1_1 = ITEMS.registerItem("cypress_roof_half_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_HALF_1_1, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_QUARTER = ITEMS.registerItem("cypress_roof_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_QUARTER, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_QUARTER_1_2 = ITEMS.registerItem("cypress_roof_quarter_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_QUARTER_1_2, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_QUARTER_1_1 = ITEMS.registerItem("cypress_roof_quarter_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_QUARTER_1_1, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_LAYER_BOTTOM = ITEMS.registerItem("cypress_roof_edge_layer_bottom", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_LAYER_BOTTOM, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_EDGE_LAYER_TOP = ITEMS.registerItem("cypress_roof_edge_layer_top", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_EDGE_LAYER_TOP, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_QUARTER_CORNER = ITEMS.registerItem("cypress_roof_quarter_corner", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_QUARTER_CORNER, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CYPRESS_ROOF_TOP = ITEMS.registerItem("cypress_roof_top", properties -> new BuildingComponentItem(() -> BuildingComponents.CYPRESS_ROOF_TOP, Blocks.CYPRESS_SKIN_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> FLAT_TERRACOTTA_TILE_ROOF = ITEMS.registerItem("flat_terracotta_tile_roof", properties -> new BuildingComponentItem(() -> BuildingComponents.FLAT_TERRACOTTA_TILE_ROOF, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> FLAT_TERRACOTTA_TILE_ROOF_HALF = ITEMS.registerItem("flat_terracotta_tile_roof_half", properties -> new BuildingComponentItem(() -> BuildingComponents.FLAT_TERRACOTTA_TILE_ROOF_HALF, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> FLAT_TERRACOTTA_TILE_ROOF_QUARTER = ITEMS.registerItem("flat_terracotta_tile_roof_quarter", properties -> new BuildingComponentItem(() -> BuildingComponents.FLAT_TERRACOTTA_TILE_ROOF_QUARTER, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_1 = ITEMS.registerItem("round_terracotta_tile_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_1, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_1_HALF = ITEMS.registerItem("round_terracotta_tile_1_1_half", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_1_HALF, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_1_OBLIQUE = ITEMS.registerItem("round_terracotta_tile_1_1_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_1_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_2 = ITEMS.registerItem("round_terracotta_tile_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_2, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_2_HALF = ITEMS.registerItem("round_terracotta_tile_1_2_half", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_2_HALF, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_2_OBLIQUE = ITEMS.registerItem("round_terracotta_tile_1_2_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_2_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_4 = ITEMS.registerItem("round_terracotta_tile_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_4, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_4_HALF = ITEMS.registerItem("round_terracotta_tile_1_4_half", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_4_HALF, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_1_4_OBLIQUE = ITEMS.registerItem("round_terracotta_tile_1_4_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_1_4_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_FLAT = ITEMS.registerItem("round_terracotta_tile_flat", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_FLAT, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_FLAT_HALF = ITEMS.registerItem("round_terracotta_tile_flat_half", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_FLAT_HALF, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_FLAT_OBLIQUE = ITEMS.registerItem("round_terracotta_tile_flat_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_FLAT_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> TERRACOTTA_TILE_ROOF_EDGE = ITEMS.registerItem("terracotta_tile_roof_edge", properties -> new BuildingComponentItem(() -> BuildingComponents.TERRACOTTA_TILE_ROOF_EDGE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> TERRACOTTA_TILE_ROOF_EDGE_CORNER = ITEMS.registerItem("terracotta_tile_roof_edge_corner", properties -> new BuildingComponentItem(() -> BuildingComponents.TERRACOTTA_TILE_ROOF_EDGE_CORNER, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROUND_TERRACOTTA_TILE_END = ITEMS.registerItem("round_terracotta_tile_end", properties -> new BuildingComponentItem(() -> BuildingComponents.ROUND_TERRACOTTA_TILE_END, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> TERRACOTTA_TILE_ROOF_TOP = ITEMS.registerItem("terracotta_tile_roof_top", properties -> new BuildingComponentItem(() -> BuildingComponents.TERRACOTTA_TILE_ROOF_TOP, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_1_1 = ITEMS.registerItem("roof_top_tiles_pile_1_1", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_1_1, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_1_1_OBLIQUE = ITEMS.registerItem("roof_top_tiles_pile_1_1_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_1_1_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_1_2 = ITEMS.registerItem("roof_top_tiles_pile_1_2", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_1_2, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_1_2_OBLIQUE = ITEMS.registerItem("roof_top_tiles_pile_1_2_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_1_2_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_1_4 = ITEMS.registerItem("roof_top_tiles_pile_1_4", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_1_4, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_1_4_OBLIQUE = ITEMS.registerItem("roof_top_tiles_pile_1_4_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_1_4_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_FLAT = ITEMS.registerItem("roof_top_tiles_pile_flat", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_FLAT, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_FLAT_OBLIQUE = ITEMS.registerItem("roof_top_tiles_pile_flat_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_FLAT_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ROOF_TOP_TILES_PILE_START = ITEMS.registerItem("roof_top_tiles_pile_start", properties -> new BuildingComponentItem(() -> BuildingComponents.ROOF_TOP_TILES_PILE_START, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ONI_TILE_1 = ITEMS.registerItem("oni_tile_1", properties -> new BuildingComponentItem(() -> BuildingComponents.ONI_TILE_1, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> ONI_TILE_1_OBLIQUE = ITEMS.registerItem("oni_tile_1_oblique", properties -> new BuildingComponentItem(() -> BuildingComponents.ONI_TILE_1_OBLIQUE, Blocks.TERRACOTTA_TILE_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> GOLD_PIN_FIN = ITEMS.registerItem("gold_pin_fin", properties -> new BuildingComponentItem(() -> BuildingComponents.GOLD_PIN_FIN, Blocks.GOLD_DECO_COMPONENT, properties), properties -> properties.useItemDescriptionPrefix());

    //瀹跺叿
    // 鍙戦叺瀹瑰櫒
    public static final DeferredItem<? extends BlockItem> WOODEN_BASIN = ITEMS.registerSimpleBlockItem("wooden_basin", Blocks.WOODEN_BASIN);
    public static final DeferredItem<? extends BlockItem> FERMENTATION_VAT = ITEMS.registerSimpleBlockItem("fermentation_vat", Blocks.FERMENTATION_VAT);
    public static final DeferredItem<? extends BlockItem> LARGE_FERMENTATION_VAT = ITEMS.registerSimpleBlockItem("large_fermentation_vat", Blocks.LARGE_FERMENTATION_VAT);

    public static final DeferredItem<? extends Item> WOODEN_BOWL_MID = ITEMS.registerItem("wooden_bowl_mid", properties -> new ContainerComponentItem(() -> FurnitureComponents.WOODEN_BOWL_MID, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix().component(kogasastudio.ashihara.registry.DataComponentTypes.FLUID_CONTENT.get(), net.neoforged.neoforge.fluids.SimpleFluidContent.EMPTY));
    public static final DeferredItem<? extends Item> WOODEN_BOWL_BIG = ITEMS.registerItem("wooden_bowl_big", properties -> new ContainerComponentItem(() -> FurnitureComponents.WOODEN_BOWL_BIG, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WOODEN_DISH_SMALL = ITEMS.registerItem("wooden_dish_small", properties -> new ContainerComponentItem(() -> FurnitureComponents.WOODEN_DISH_SMALL, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WOODEN_DISH_MID = ITEMS.registerItem("wooden_dish_mid", properties -> new ContainerComponentItem(() -> FurnitureComponents.WOODEN_DISH_MID, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WOODEN_DISH_BIG = ITEMS.registerItem("wooden_dish_big", properties -> new ContainerComponentItem(() -> FurnitureComponents.WOODEN_DISH_BIG, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_WOOD_8_LEG_ALTAR_TABLE = ITEMS.registerItem("white_wood_8_leg_altar_table", properties -> new FurnitureComponentItem(() -> FurnitureComponents.WHITE_WOOD_8_LEG_ALTAR_TABLE, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_WOOD_8_LEG_ALTAR_TABLE_MINI_SHORT = ITEMS.registerItem("white_wood_8_leg_altar_table_mini_short", properties -> new FurnitureComponentItem(() -> FurnitureComponents.WHITE_WOOD_8_LEG_ALTAR_TABLE_MINI_SHORT, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_WOOD_8_LEG_ALTAR_TABLE_TALL = ITEMS.registerItem("white_wood_8_leg_altar_table_tall", properties -> new FurnitureComponentItem(() -> FurnitureComponents.WHITE_WOOD_8_LEG_ALTAR_TABLE_TALL, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_WOOD_8_LEG_ALTAR_TABLE_THIN = ITEMS.registerItem("white_wood_8_leg_altar_table_thin", properties -> new FurnitureComponentItem(() -> FurnitureComponents.WHITE_WOOD_8_LEG_ALTAR_TABLE_THIN, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_WOOD_8_LEG_ALTAR_TABLE_THIN_SHORT = ITEMS.registerItem("white_wood_8_leg_altar_table_thin_short", properties -> new FurnitureComponentItem(() -> FurnitureComponents.WHITE_WOOD_8_LEG_ALTAR_TABLE_THIN_SHORT, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> WHITE_WOOD_8_LEG_ALTAR_TABLE_THIN_TALL = ITEMS.registerItem("white_wood_8_leg_altar_table_thin_tall", properties -> new FurnitureComponentItem(() -> FurnitureComponents.WHITE_WOOD_8_LEG_ALTAR_TABLE_THIN_TALL, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> BLACK_MEAL_TRAY = ITEMS.registerItem("black_meal_tray", properties -> new FurnitureComponentItem(() -> FurnitureComponents.BLACK_MEAL_TRAY, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> BLACK_CAT_FEET_MEAL_TABLE = ITEMS.registerItem("black_cat_feet_meal_table", properties -> new FurnitureComponentItem(() -> FurnitureComponents.BLACK_CAT_FEET_MEAL_TABLE, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> HAGOROMO = ITEMS.registerItem("hagoromo", properties -> new HagoromoItem(ArmorMaterials.LEATHER, EquipmentSlot.CHEST, properties.stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredItem<? extends Item> BAMBOO_CURTAIN = ITEMS.registerItem("bamboo_curtain", properties -> new FurnitureComponentItem(() -> FurnitureComponents.BAMBOO_CURTAIN, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());
    public static final DeferredItem<? extends Item> CURTAIN_TABLE = ITEMS.registerItem("curtain_table", properties -> new FurnitureComponentItem(() -> FurnitureComponents.CURTAIN_TABLE, Blocks.MULTI_BUILT_BLOCK, properties), properties -> properties.useItemDescriptionPrefix());

    //闁诲浚鍨伴崣?
    public static final DeferredItem<? extends Item> LANTERN_LONG_WHITE = ITEMS.registerSimpleBlockItem("lantern_long_white", Blocks.LANTERN_LONG_WHITE);
    public static final DeferredItem<? extends Item> LANTERN_LONG_RED = ITEMS.registerSimpleBlockItem("lantern_long_red", Blocks.LANTERN_LONG_RED);
    public static final DeferredItem<? extends Item> HOUSE_LIKE_HANGING_LANTERN = ITEMS.registerSimpleBlockItem("house_like_hanging_lantern", Blocks.HOUSE_LIKE_HANGING_LANTERN);
    public static final DeferredItem<? extends Item> HEXAGONAL_HANGING_LANTERN = ITEMS.registerSimpleBlockItem("hexagonal_hanging_lantern", Blocks.HEXAGONAL_HANGING_LANTERN);
    public static final DeferredItem<? extends Item> JINJA_LANTERN = ITEMS.registerItem("jinja_lantern", properties -> new DoubleBlockItem(Blocks.JINJA_LANTERN.get(), properties), properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<? extends Item> STONE_LANTERN = ITEMS.registerItem("stone_lantern", properties -> new DoubleBlockItem(Blocks.STONE_LANTERN.get(), properties), properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<? extends Item> BONBURI_LAMP = ITEMS.registerItem("bonburi_lamp", properties -> new DoubleBlockItem(Blocks.BONBURI_LAMP.get(), properties), properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<? extends Item> CANDLESTICK = ITEMS.registerItem("candlestick", properties -> new DoubleBlockItem(Blocks.CANDLESTICK.get(), properties), properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<? extends Item> OIL_PLATE_STICK = ITEMS.registerItem("oil_plate_stick", properties -> new DoubleBlockItem(Blocks.OIL_PLATE_STICK.get(), properties), properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<? extends Item> CANDLE = ITEMS.registerItem("candle", CandleBlockItem::new, properties -> properties.useBlockDescriptionPrefix());

    //闁哄牄鍔岄崺妤呭传?
    public static final DeferredItem<? extends Item> CHERRY_LOG = ITEMS.registerSimpleBlockItem("cherry_log", Blocks.CHERRY_LOG);
    public static final DeferredItem<? extends Item> STRIPPED_CHERRY_LOG = ITEMS.registerSimpleBlockItem("stripped_cherry_log", Blocks.STRIPPED_CHERRY_LOG);
    public static final DeferredItem<? extends Item> STRIPPED_CHERRY_WOOD = ITEMS.registerSimpleBlockItem("stripped_cherry_wood", Blocks.STRIPPED_CHERRY_WOOD);
    public static final DeferredItem<? extends Item> CHERRY_WOOD = ITEMS.registerSimpleBlockItem("cherry_wood", Blocks.CHERRY_WOOD);
    public static final DeferredItem<? extends Item> CHERRY_PLANKS = ITEMS.registerSimpleBlockItem("cherry_planks", Blocks.CHERRY_PLANKS);
    public static final DeferredItem<? extends Item> CHERRY_STAIRS = ITEMS.registerSimpleBlockItem("cherry_stairs", Blocks.CHERRY_STAIRS);
    public static final DeferredItem<? extends Item> CHERRY_SLAB = ITEMS.registerSimpleBlockItem("cherry_slab", Blocks.CHERRY_SLAB);
    public static final DeferredItem<? extends Item> CHERRY_FENCE = ITEMS.registerSimpleBlockItem("cherry_fence", Blocks.CHERRY_FENCE);
    public static final DeferredItem<? extends Item> CHERRY_FENCE_GATE = ITEMS.registerSimpleBlockItem("cherry_fence_gate", Blocks.CHERRY_FENCE_GATE);
    public static final DeferredItem<? extends Item> CHERRY_BUTTON = ITEMS.registerSimpleBlockItem("cherry_button", Blocks.CHERRY_BUTTON);

    public static final DeferredItem<? extends Item> STRIPPED_RED_LOG = ITEMS.registerSimpleBlockItem("stripped_red_log", Blocks.STRIPPED_RED_LOG);
    public static final DeferredItem<? extends Item> RED_PLANKS = ITEMS.registerSimpleBlockItem("red_planks", Blocks.RED_PLANKS);
    public static final DeferredItem<? extends Item> RED_STAIRS = ITEMS.registerSimpleBlockItem("red_stairs", Blocks.RED_STAIRS);
    public static final DeferredItem<? extends Item> RED_SLAB = ITEMS.registerSimpleBlockItem("red_slab", Blocks.RED_SLAB);
    public static final DeferredItem<? extends Item> RED_FENCE = ITEMS.registerSimpleBlockItem("red_fence", Blocks.RED_FENCE);
    public static final DeferredItem<? extends Item> RED_FENCE_GATE = ITEMS.registerSimpleBlockItem("red_fence_gate", Blocks.RED_FENCE_GATE);

    public static final DeferredItem<? extends Item> MAPLE_LOG = ITEMS.registerSimpleBlockItem("maple_log", Blocks.MAPLE_LOG);
    public static final DeferredItem<? extends Item> STRIPPED_MAPLE_LOG = ITEMS.registerSimpleBlockItem("stripped_maple_log", Blocks.STRIPPED_MAPLE_LOG);
    public static final DeferredItem<? extends Item> MAPLE_WOOD = ITEMS.registerSimpleBlockItem("maple_wood", Blocks.MAPLE_WOOD);
    public static final DeferredItem<? extends Item> MAPLE_PLANKS = ITEMS.registerSimpleBlockItem("maple_planks", Blocks.MAPLE_PLANKS);
    public static final DeferredItem<? extends Item> MAPLE_STAIRS = ITEMS.registerSimpleBlockItem("maple_stairs", Blocks.MAPLE_STAIRS);
    public static final DeferredItem<? extends Item> MAPLE_SLAB = ITEMS.registerSimpleBlockItem("maple_slab", Blocks.MAPLE_SLAB);
    public static final DeferredItem<? extends Item> MAPLE_FENCE = ITEMS.registerSimpleBlockItem("maple_fence", Blocks.MAPLE_FENCE);
    public static final DeferredItem<? extends Item> MAPLE_FENCE_GATE = ITEMS.registerSimpleBlockItem("maple_fence_gate", Blocks.MAPLE_FENCE_GATE);
    public static final DeferredItem<? extends Item> MAPLE_BUTTON = ITEMS.registerSimpleBlockItem("maple_button", Blocks.MAPLE_BUTTON);

    public static final DeferredItem<? extends Item> CYPRESS_SKIN_BLOCK = ITEMS.registerSimpleBlockItem("cypress_skin_block", Blocks.CYPRESS_SKIN_BLOCK);
    public static final DeferredItem<? extends Item> CYPRESS_SKIN_SLAB = ITEMS.registerSimpleBlockItem("cypress_skin_slab", Blocks.CYPRESS_SKIN_SLAB);

    public static final DeferredItem<? extends Item> CYPRESS_SKIN_STAIRS = ITEMS.registerSimpleBlockItem("cypress_skin_stairs", Blocks.CYPRESS_SKIN_STAIRS);


    //婵?
    public static final DeferredItem<? extends Item> SOY_MILK_BUCKET = ITEMS.registerItem("soy_milk_bucket", properties -> new BucketItem(FluidRegistryHandler.SOY_MILK.get(), properties));
    public static final DeferredItem<? extends Item> OIL_BUCKET = ITEMS.registerItem("oil_bucket", properties -> new BucketItem(FluidRegistryHandler.OIL.get(), properties));
    public static final DeferredItem<? extends Item> RICE_PORRIDGE_BUCKET = ITEMS.registerItem("rice_porridge_bucket", properties -> new BucketItem(FluidRegistryHandler.RICE_PORRIDGE.get(), properties.food(new FoodProperties.Builder().nutrition(5).saturationModifier(1.0f).build(), Consumables.defaultDrink().sound(SoundEvents.HONEY_DRINK).onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 1), 1.0F)).build())));

    private static class AshiharaItem extends Item
    {
        public AshiharaItem(Properties properties)
        {
            super(properties);
        }

        public AshiharaItem()
        {
            this(new Properties());
        }
    }
}


