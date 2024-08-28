package kogasastudio.ashihara.compat.sodium;

import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

public class AshiharaMaterials
{
    public static final Map<RenderType, Material> MATERIALS = new HashMap<>();

    static
    {
        for (RenderType type : AshiharaRenderTypes.AFTER_SKY)
        {
            MATERIALS.put(type, new Material(AshiharaTerrainRenderPasses.ASHIHARA_PASSES.get(type), AlphaCutoffParameter.ZERO, true));
        }
        for (RenderType type : AshiharaRenderTypes.AFTER_ENTITIES)
        {
            MATERIALS.put(type, new Material(AshiharaTerrainRenderPasses.ASHIHARA_PASSES.get(type), AlphaCutoffParameter.ZERO, true));
        }
        for (RenderType type : AshiharaRenderTypes.AFTER_BLOCK_ENTITIES)
        {
            MATERIALS.put(type, new Material(AshiharaTerrainRenderPasses.ASHIHARA_PASSES.get(type), AlphaCutoffParameter.ONE_TENTH, false));
        }
        for (RenderType type : AshiharaRenderTypes.AFTER_PARTICLES)
        {
            MATERIALS.put(type, new Material(AshiharaTerrainRenderPasses.ASHIHARA_PASSES.get(type), AlphaCutoffParameter.ZERO, true));
        }
        for (RenderType type : AshiharaRenderTypes.AFTER_WEATHER)
        {
            MATERIALS.put(type, new Material(AshiharaTerrainRenderPasses.ASHIHARA_PASSES.get(type), AlphaCutoffParameter.ONE_TENTH, true));
        }
    }
}
