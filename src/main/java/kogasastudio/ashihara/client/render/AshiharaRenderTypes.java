package kogasastudio.ashihara.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.Shaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class AshiharaRenderTypes
{
    public static final RenderType CANDLE = RenderType.create
    (
        "chick",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        786432,
        false,
        true,
        RenderType.CompositeState.builder()
        .setLightmapState(LIGHTMAP)
        .setShaderState(new ShaderStateShard(() -> Shaders.CHUNK_ENTITY_SOLID))
        .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/block/candle_java.png"), false, false))
        .createCompositeState(true)
    );

    public static List<RenderType> AFTER_SKY = Lists.newArrayList();
    public static List<RenderType> AFTER_ENTITIES = Lists.newArrayList();
    public static List<RenderType> AFTER_BLOCK_ENTITIES = Lists.newArrayList();
    public static List<RenderType> AFTER_PARTICLES = Lists.newArrayList();
    public static List<RenderType> AFTER_WEATHER = Lists.newArrayList();
    public static List<RenderType> ALL = Lists.newArrayList();

    public static final Map<RenderLevelStageEvent.Stage, List<RenderType>> STAGE_TO_TYPE = new HashMap<>();

    static
    {
        AFTER_ENTITIES.add(CANDLE);

        ALL.addAll(AFTER_SKY);
        ALL.addAll(AFTER_ENTITIES);
        ALL.addAll(AFTER_BLOCK_ENTITIES);
        ALL.addAll(AFTER_PARTICLES);
        ALL.addAll(AFTER_WEATHER);

        STAGE_TO_TYPE.put(RenderLevelStageEvent.Stage.AFTER_SKY, AFTER_SKY);
        STAGE_TO_TYPE.put(RenderLevelStageEvent.Stage.AFTER_ENTITIES, AFTER_ENTITIES);
        STAGE_TO_TYPE.put(RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES, AFTER_BLOCK_ENTITIES);
        STAGE_TO_TYPE.put(RenderLevelStageEvent.Stage.AFTER_PARTICLES, AFTER_PARTICLES);
        STAGE_TO_TYPE.put(RenderLevelStageEvent.Stage.AFTER_WEATHER, AFTER_WEATHER);
    }
}
