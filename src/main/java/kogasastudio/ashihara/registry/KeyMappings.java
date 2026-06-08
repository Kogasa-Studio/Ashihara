package kogasastudio.ashihara.registry;

import com.mojang.blaze3d.platform.InputConstants;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyMappings
{
    public static final KeyMapping.Category IN_WORLD_TOOLTIP = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Ashihara.MODID, "key.category.in_world_tooltip"));
    public static final KeyMapping SHOW_IN_WORLD_TOOLTIP = new KeyMapping("key.show_in_world_tooltip", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, IN_WORLD_TOOLTIP);
    public static final KeyMapping.Category ASHIHARA_COMMON = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Ashihara.MODID, "main"));
    public static final KeyMapping EATING_MODE_KEY = new KeyMapping(
    "key.ashihara.eating_mode", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, InputConstants.KEY_C, ASHIHARA_COMMON);

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.registerCategory(IN_WORLD_TOOLTIP);
        event.register(SHOW_IN_WORLD_TOOLTIP);
    }
}
