package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.interaction.recipes.mill.MillRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RecipeTypes
{
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Ashihara.MODID);

    public static final Supplier<RecipeType<CuttingBoardRecipe>> CUTTING_BOARD = register("cutting_board");
    public static final Supplier<RecipeType<MortarRecipe>> MORTAR = register("mortar");
    public static final Supplier<RecipeType<MillRecipe>> MILL = register("mill");

    private static <TYPE extends WrappedRecipe<?, ?>> Supplier<RecipeType<TYPE>> register(String name)
    {
        return TYPES.register(name, () -> new RecipeType<>()
        {
            @Override
            public String toString()
            {
                return ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, name).toString();
            }
        });
    }

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }
}
