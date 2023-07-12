package kogasastudio.ashihara.interaction.recipes.register;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.MillRecipe;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.interaction.recipes.base.BaseRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * todo 注册新的 RecipeType 在这里注册
 *   因为锁了注册表，所以不能直接 new 了
 *
 * @author DustW
 **/
public class RecipeTypes
{
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Ashihara.MODID);

    public static final RegistryObject<RecipeType<CuttingBoardRecipe>> CUTTING_BOARD = register("cutting_board");
    public static final RegistryObject<RecipeType<MortarRecipe>> MORTAR = register("mortar");
    public static final RegistryObject<RecipeType<MillRecipe>> MILL = register("mill");

    private static <TYPE extends BaseRecipe> RegistryObject<RecipeType<TYPE>> register(String name)
    {
        return TYPES.register(name, () -> new RecipeType<>()
        {
            @Override
            public String toString()
            {
                return new ResourceLocation(Ashihara.MODID, name).toString();
            }
        });
    }

    static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }
}
