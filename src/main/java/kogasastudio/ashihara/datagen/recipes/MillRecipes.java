package kogasastudio.ashihara.datagen.recipes;

import com.google.common.collect.ImmutableMap;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.interaction.recipes.MillRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

/**
 * @author DustW
 **/
public class MillRecipes extends ModGenRecipes {
    @Override
    protected void addRecipes() {
        addRecipe("mill_bean_powder",
                ImmutableMap.of(
                        Ingredient.of(ItemRegistryHandler.SOY_BEAN.get()), (byte) 1
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.BEAN_POWDER.get(), 2)
                },
                null, null,
                (byte) 3, 50, 20);

        addRecipe("mill_coal_powder",
                ImmutableMap.of(
                        Ingredient.of(Items.COAL), (byte) 1
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.COAL_POWDER.get(), 2)
                },
                null, null,
                (byte) 3, 50, 20);

        addRecipe("mill_flour",
                ImmutableMap.of(
                        Ingredient.of(Items.WHEAT), (byte) 2
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.FLOUR.get(), 2)
                },
                null, null,
                (byte) 3, 100, 20);

        addRecipe("mill_glowstone_dust",
                ImmutableMap.of(
                        Ingredient.of(Items.GLOWSTONE), (byte) 2
                ),
                new ItemStack[] {
                        new ItemStack(Items.GLOWSTONE_DUST, 8)
                },
                null, null,
                (byte) 5, 50, 20);

        addRecipe("mill_gold_ore_milling",
                ImmutableMap.of(
                        Ingredient.of(Items.RAW_GOLD), (byte) 1
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.GOLD_ORE_SHATTER.get(), 2),
                        new ItemStack(ItemRegistryHandler.STONE_SHATTER.get(), 2)
                },
                null, null,
                (byte) 5, 75, 20);

        addRecipe("mill_iron_ore_milling",
                ImmutableMap.of(
                        Ingredient.of(Items.RAW_IRON), (byte) 1
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.IRON_ORE_SHATTER.get(), 2),
                        new ItemStack(ItemRegistryHandler.STONE_SHATTER.get(), 2)
                },
                null, null,
                (byte) 5, 100, 20);

        addRecipe("mill_macha_powder",
                ImmutableMap.of(
                        Ingredient.of(ItemRegistryHandler.DRIED_TEA_LEAF.get()), (byte) 3
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.MACHA_POWDER.get(), 2)
                },
                null, null,
                (byte) 3, 30, 20);

        addRecipe("mill_rice",
                ImmutableMap.of(
                        Ingredient.of(ItemRegistryHandler.PADDY.get()), (byte) 1
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.RICE.get(), 2)
                },
                null, null,
                (byte) 3, 100, 20);

        addRecipe("mill_rice_powder",
                ImmutableMap.of(
                        Ingredient.of(ItemRegistryHandler.RICE.get()), (byte) 1
                ),
                new ItemStack[] {
                        new ItemStack(ItemRegistryHandler.RICE_POWDER.get(), 1)
                },
                null, null,
                (byte) 3, 30, 20);

        addRecipe("mill_soy_milk",
                ImmutableMap.of(
                        Ingredient.of(ItemRegistryHandler.BEAN_POWDER.get()), (byte) 2
                ),
                new ItemStack[] {},
                new FluidStack(Fluids.WATER, 100),
                new FluidStack(FluidRegistryHandler.SOY_MILK.get(), 100),
                (byte) 1, 20, 20);
    }

    void addRecipe(String name, Map<Ingredient, Byte> inputCostsIn, ItemStack[] outputs,
                   FluidStack inFluid, FluidStack outFluid,
                   byte roundIn, int roundTicksIn, float expIn) {
        MillRecipe recipe = new MillRecipe(new ResourceLocation(Ashihara.MODID, name),
                "test_group", inputCostsIn, NonNullList.of(ItemStack.EMPTY, outputs),
                inFluid, outFluid,  roundIn, roundTicksIn, expIn);
        recipe.type = RecipeSerializers.MILL.get().getRegistryName().toString();
        addRecipe(recipe.getId(),baseRecipe(recipe), "mill");
    }
}
