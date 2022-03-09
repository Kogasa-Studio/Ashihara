package kogasastudio.ashihara.interaction.recipe;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeTypesRegistryHandler
{
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Ashihara.MODID);

    public static final RegistryObject<IRecipeSerializer<MillRecipe>> MILL = RECIPES.register("mill", MillRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<MortarRecipe>> MORTAR = RECIPES.register("mortar", MortarRecipe.Serializer::new);
}
