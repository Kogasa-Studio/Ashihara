package kogasastudio.ashihara.client.render;

import cpw.mods.modlauncher.api.ITransformationService;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AshiharaAtlas
{
    //为Atlas提供RL

    public static final ResourceLocation ICON_ATLAS = new ResourceLocation(Ashihara.MODID, "textures/atlas/icons.png");
    public static final ResourceLocation ASSISTANCE_ATLAS = new ResourceLocation(Ashihara.MODID, "textures/atlas/assistance.png");

    private static final Map<ResourceLocation, ResourceLocation> ASHIHARA_ATLASES = Map.of
            (
                    ICON_ATLAS, new ResourceLocation(Ashihara.MODID, "icons"),
                    ASSISTANCE_ATLAS, new ResourceLocation(Ashihara.MODID, "assistants")
            );
    public final AtlasSet atlasSet = new AtlasSet(ASHIHARA_ATLASES, Minecraft.getInstance().textureManager);

    public static final List<ResourceLocation> ALL_ICON = new ArrayList<>();
    public static final Map<String, ResourceLocation> ALL_ASSISTANCE = new HashMap<>();

    //遍历资源包目录来收集Atlas的组成部分（小贴图）
    //纹理路径省略开头的 textures/ 和结尾的 .png ，mc会自动加上

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onAtlasGenerate(ModelEvent.ModifyBakingResult event)
    {
        ALL_ICON.clear();
        ALL_ASSISTANCE.clear();

        ALL_ICON.add(new ResourceLocation(Ashihara.MODID, "textures/icons/hanataki.png"));
        ALL_ICON.add(new ResourceLocation(Ashihara.MODID, "textures/icons/tomoe.png"));
        ALL_ICON.add(new ResourceLocation(Ashihara.MODID, "textures/icons/yamasakura.png"));
        ALL_ICON.add(new ResourceLocation(Ashihara.MODID, "textures/icons/otakobe.png"));
        ALL_ICON.add(new ResourceLocation(Ashihara.MODID, "textures/icons/reinakakobo.png"));

        ALL_ASSISTANCE.put("cereals_level", new ResourceLocation(Ashihara.MODID, "textures/assistants/cereals_level.png"));
        ALL_ASSISTANCE.put("processed_level", new ResourceLocation(Ashihara.MODID, "textures/assistants/processed_level.png"));
        //用at拿到一个原版字段，用来将我们的小贴图丢进Atlas并把Atlas丢进游戏
        //纹章
        /*Map<ResourceLocation, Resource> icons = Minecraft.getInstance().getResourceManager().listResources("textures/icons/", s -> s.getPath().endsWith(".png"));

        //辅助
        Map<ResourceLocation, Resource> assistants = Minecraft.getInstance().getResourceManager().listResources("textures/assistants/", s -> s.getPath().endsWith(".png"));
        //添加纹章类贴图
        for (ResourceLocation location : icons.keySet())
        {
            if (location.getNamespace().equals(Ashihara.MODID))
            {
                ResourceLocation trans = trans(location);
//                miscMaterials.add(new Material(ICON_ATLAS, trans));
                ALL_ICON.add(trans);
            }
        }
        //添加辅助类贴图
        for (ResourceLocation location : assistants.keySet())
        {
            if (location.getNamespace().equals(Ashihara.MODID))
            {
                ResourceLocation trans = trans(location);
//                miscMaterials.add(new Material(ASSISTANCE_ATLAS, trans));
                ALL_ASSISTANCE.add(trans);
            }
        }*/
    }

    public Function<ResourceLocation, TextureAtlasSprite> getTextureAtlas(ResourceLocation key)
    {
        return this.atlasSet.getAtlas(key)::getSprite;
    }

    /**
     * 用来省略开头和结尾
     */
    public static ResourceLocation trans(ResourceLocation location)
    {
        return new ResourceLocation(location.getNamespace(), location.getPath().substring(9, location.getPath().length() - 4));
    }
}
