package kogasastudio.ashihara.event;

import kogasastudio.ashihara.helper.RecipeHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import kogasastudio.ashihara.network.HagoromoFlightSpeedPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void onMouseScroll(net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent event)
    {
        var mc = net.minecraft.client.Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;
        long window = mc.getWindow().handle();
        boolean ctrlDown = org.lwjgl.glfw.GLFW.glfwGetKey(window, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL) == 1
            || org.lwjgl.glfw.GLFW.glfwGetKey(window, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL) == 1;
        if (!ctrlDown) return;
        if (!player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(kogasastudio.ashihara.registry.Items.HAGOROMO)) return;
        net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new HagoromoFlightSpeedPacket(event.getScrollDeltaY()));
        event.setCanceled(true);
    }

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
