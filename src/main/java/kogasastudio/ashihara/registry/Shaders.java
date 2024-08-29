package kogasastudio.ashihara.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Shaders
{
    public static ShaderInstance CHUNK_ENTITY_SOLID;
    public static ShaderInstance CHUNK_ENTITY_CUTOUT_TRANSLUCENT;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException
    {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "chunk_entity_solid"), DefaultVertexFormat.BLOCK), shaderInstance -> CHUNK_ENTITY_SOLID = shaderInstance);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "chunk_entity_cutout_translucent"), DefaultVertexFormat.BLOCK), shaderInstance -> CHUNK_ENTITY_CUTOUT_TRANSLUCENT = shaderInstance);
    }
}
