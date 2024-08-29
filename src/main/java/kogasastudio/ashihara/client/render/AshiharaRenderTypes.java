package kogasastudio.ashihara.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import kogasastudio.ashihara.registry.Shaders;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class AshiharaRenderTypes
{
    public static final RenderType CHUNK_ENTITY_SOLID = RenderType.create
    (
        "chunk_entity_solid",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        4194304,
        false,
        true,
        RenderType.CompositeState.builder()
        .setLightmapState(LIGHTMAP)
        .setShaderState(new ShaderStateShard(() -> Shaders.CHUNK_ENTITY_SOLID))
        .setTextureState(BLOCK_SHEET_MIPPED/*new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/block/candle_java.png"), false, false)*/)
        .createCompositeState(true)
    );

    public static final RenderType CHUNK_ENTITY_CUTOUT_MIPPED = RenderType.create
    (
        "chunk_cutout_mipped",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        4194304,
        true,
        false,
        RenderType.CompositeState.builder()
        .setLightmapState(LIGHTMAP)
        .setShaderState(new ShaderStateShard(() -> Shaders.CHUNK_ENTITY_CUTOUT_TRANSLUCENT))
        .setTextureState(BLOCK_SHEET_MIPPED)
        .createCompositeState(true)
    );

    public static final RenderType CHUNK_ENTITY_TRANSLUCENT = RenderType.create
    (
        "chunk_cutout_translucent",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        786432,
        true,
        true,
        RenderType.CompositeState.builder()
        .setLightmapState(LIGHTMAP)
        .setShaderState(new ShaderStateShard(() -> Shaders.CHUNK_ENTITY_CUTOUT_TRANSLUCENT))
        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
        .setTextureState(BLOCK_SHEET_MIPPED)
        .setOutputState(TRANSLUCENT_TARGET)
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
        AFTER_ENTITIES.add(CHUNK_ENTITY_SOLID);

        AFTER_BLOCK_ENTITIES.add(CHUNK_ENTITY_CUTOUT_MIPPED);

        AFTER_PARTICLES.add(CHUNK_ENTITY_TRANSLUCENT);

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
