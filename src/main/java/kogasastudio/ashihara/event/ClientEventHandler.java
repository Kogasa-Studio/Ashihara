package kogasastudio.ashihara.event;

import kogasastudio.ashihara.helper.RecipeHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onRecipesReceived(RecipesReceivedEvent event)
    {
        var map = event.getRecipeMap();
        for (var type : event.getRecipeTypes())
        {
            Collection<RecipeHolder<?>> recipes = map.byType((net.minecraft.world.item.crafting.RecipeType) type);
            RecipeHelper.cacheClientRecipes(type, new ArrayList<>(recipes));
        }
    }
}
