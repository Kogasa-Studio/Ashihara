package kogasastudio.ashihara.block;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistryHandler
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ashihara.MODID);

    public static final RegistryObject<Block> BLOCK_WATER_FIELD = BLOCKS.register("water_field", BlockWaterField::new);
    public static final RegistryObject<Block> BLOCK_RICE_CROP = BLOCKS.register("rice_crop", BlockRiceCrop::new);
    public static final RegistryObject<Block> CHERRY_LOG = BLOCKS.register("cherry_log", BlockCherryLog::new);
    public static final RegistryObject<Block> CHERRY_BLOSSOM = BLOCKS.register("cherry_blossom", BlockCherryBlossom::new);
    public static final RegistryObject<Block> BLOCK_JINJA_LANTERN = BLOCKS.register("jinja_lantern", BlockJinjaLantern::new);
    public static final RegistryObject<Block> BLOCK_DIRT_DEPRESSION = BLOCKS.register("dirt_depression", BlockDirtDepression::new);
    public static final RegistryObject<Block> BLOCK_TETSUSENCHI = BLOCKS.register("tetsusenchi", BlockTetsusenchi::new);
}
