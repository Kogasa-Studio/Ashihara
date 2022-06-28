package kogasastudio.ashihara.datagen.recipes;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import kogasastudio.ashihara.utils.json.JsonUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author DustW
 **/
public class CuttingBoardRecipes extends AvarusRecipes {
    @Override
    protected void addRecipes() {
        addRecipe(new CuttingBoardRecipe(
                new ResourceLocation(Ashihara.MODID, "cut_dried_bamboo"),
                Ingredient.of(ItemRegistryHandler.DRIED_BAMBOO.get()),
                NonNullList.of(ItemStack.EMPTY, new ItemStack(ItemRegistryHandler.BAMBOO_MATERIAL.get(), 4)),
                CuttingBoardToolType.KNIFE));
    }

    protected void addRecipe(CuttingBoardRecipe recipe) {
        recipe.type = RecipeSerializers.CUTTING_BOARD.get().getRegistryName().toString();
        addRecipe(recipe.getId(), JsonUtils.INSTANCE.pretty.toJson(recipe), "cutting");
    }
}
