package kogasastudio.ashihara.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class SimpleMultiBufferSource implements MultiBufferSource
{
    private final Map<RenderType, VertexConsumer> buffers = new HashMap<>();

    public SimpleMultiBufferSource of(RenderType renderType, VertexConsumer vertexConsumer)
    {
        SimpleMultiBufferSource result = new SimpleMultiBufferSource();
        result.putBuffer(vertexConsumer, renderType);
        return result;
    }

    public void putBuffer(VertexConsumer vertexConsumer, RenderType renderType)
    {
        buffers.put(renderType, vertexConsumer);
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType)
    {
        return this.buffers.get(pRenderType);
    }
}
