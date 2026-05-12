package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * @author DustW
 **/
public class RecipeSerializers
{
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Ashihara.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CuttingBoardRecipe>> CUTTING_BOARD =
        SERIALIZER.register("cutting", () -> CuttingBoardRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MortarRecipe>> MORTAR =
        SERIALIZER.register("mortar", () -> MortarRecipe.SERIALIZER);

    /*public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MillRecipe>> MILL =
        SERIALIZER.register("mill", () -> MillRecipe.SERIALIZER);*/

    public static void register(IEventBus bus)
    {
        SERIALIZER.register(bus);
    }
}
