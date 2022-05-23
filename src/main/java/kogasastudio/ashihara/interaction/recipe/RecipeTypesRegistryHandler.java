package kogasastudio.ashihara.interaction.recipe;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeTypesRegistryHandler
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Ashihara.MODID);

    public static final RegistryObject<RecipeSerializer<MillRecipe>> MILL = RECIPES.register("mill", MillRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<MortarRecipe>> MORTAR = RECIPES.register("mortar", MortarRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<CuttingBoardRecipe>> CUTTING_BOARD = RECIPES.register("cutting", CuttingBoardRecipe.Serializer::new);
}
