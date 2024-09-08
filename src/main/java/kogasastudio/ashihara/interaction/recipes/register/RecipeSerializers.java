package kogasastudio.ashihara.interaction.recipes.register;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.mill.MillRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * @author DustW
 **/
public class RecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Ashihara.MODID);

    public static final Supplier<RecipeSerializer<?>> CUTTING_BOARD = SERIALIZER.register("cutting", CuttingBoardRecipe.CuttingBoardRecipeSerializer::new);

    //public static final Supplier<RecipeSerializer<?>> MORTAR = SERIALIZER.register("mortar", MortarRecipe.MortarRecipeSerializer::new);

    public static final Supplier<RecipeSerializer<?>> MILL = SERIALIZER.register("mill", MillRecipeSerializer::new);

    static void register(IEventBus bus) {
        SERIALIZER.register(bus);
    }
}
