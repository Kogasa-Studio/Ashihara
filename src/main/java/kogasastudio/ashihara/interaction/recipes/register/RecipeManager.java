package kogasastudio.ashihara.interaction.recipes.register;

import net.neoforged.bus.api.IEventBus;

/**
 * @author DustW
 **/
public class RecipeManager {
    public static void register(IEventBus bus) {
        RecipeTypes.register(bus);
        RecipeSerializers.register(bus);
    }
}
