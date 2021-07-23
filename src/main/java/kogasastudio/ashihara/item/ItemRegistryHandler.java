package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemRegistryHandler
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ashihara.MODID);

    //特殊物品
    public static final RegistryObject<Item> ASHIHARA_ICON = ITEMS.register("ashihara_icon", AshiharaIcon::new);

    //以下为物品
    public static final RegistryObject<Item> KOISHI = ITEMS.register("koishi", ItemKoishi::new);
    public static final RegistryObject<Item> MINATO_AQUA = ITEMS.register("aqua", ItemMinatoAqua::new);
    public static final RegistryObject<Item> DIRT_BALL = ITEMS.register("dirt_ball", ItemDirtBall::new);
    public static final RegistryObject<Item> RICE_SEEDLING = ITEMS.register("rice_seedling", ItemRiceSeedling::new);
    public static final RegistryObject<Item> RICE_CROP = ITEMS.register("rice_crop_item", ItemRiceCrop::new);
    public static final RegistryObject<Item> UNTHRESHED_RICE = ITEMS.register("unthreshed_rice", ItemUnthreshedRice::new);
    public static final RegistryObject<Item> STRAW = ITEMS.register("straw", ItemStraw::new);

    //以下为方块
    public static final RegistryObject<Item> ITEM_DIRT_DEPRESSION = ITEMS.register("dirt_depression", () -> new BlockItem(BlockRegistryHandler.BLOCK_DIRT_DEPRESSION.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_WATER_FIELD = ITEMS.register("water_field", () -> new BlockItem(BlockRegistryHandler.BLOCK_WATER_FIELD.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_CHERRY_LOG = ITEMS.register("cherry_log", () -> new BlockItem(BlockRegistryHandler.CHERRY_LOG.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_CHERRY_BLOSSOM = ITEMS.register("cherry_blossom", () -> new BlockItem(BlockRegistryHandler.CHERRY_BLOSSOM.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_JINJA_LANTERN = ITEMS.register("jinja_lantern", () -> new BlockItem(BlockRegistryHandler.BLOCK_JINJA_LANTERN.get(), new Item.Properties().group(ASHIHARA)));
    public static final RegistryObject<Item> ITEM_TETSUSENCHI = ITEMS.register("tetsusenchi", () -> new BlockItem(BlockRegistryHandler.BLOCK_TETSUSENCHI.get(), new Item.Properties().group(ASHIHARA)));
}
