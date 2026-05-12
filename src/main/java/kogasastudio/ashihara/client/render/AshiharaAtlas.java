package kogasastudio.ashihara.client.render;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.*;
import java.util.function.Function;

@EventBusSubscriber
public class AshiharaAtlas
{
    //为Atlas提供RL

    public static final Identifier ICON_ATLAS = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/atlas/icons.png");
    public static final Identifier ASSISTANCE_ATLAS = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/atlas/assistance.png");

    private static final Map<Identifier, Identifier> ASHIHARA_ATLASES = Map.of
            (
                    ICON_ATLAS, Identifier.fromNamespaceAndPath(Ashihara.MODID, "icons"),
                    ASSISTANCE_ATLAS, Identifier.fromNamespaceAndPath(Ashihara.MODID, "assistants")
            );

    public static final List<Identifier> ALL_ICON = new ArrayList<>();
    public static final Map<String, Identifier> ALL_ASSISTANCE = new HashMap<>();

    //遍历资源包目录来收集Atlas的组成部分（小贴图）
    //纹理路径省略开头的 textures/ 和结尾的 .png ，mc会自动加上

    @SubscribeEvent
    public static void onAtlasGenerate(ModelEvent.ModifyBakingResult event)
    {
        ALL_ICON.clear();
        ALL_ASSISTANCE.clear();

        ALL_ICON.add(Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/icons/hanataki.png"));
        ALL_ICON.add(Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/icons/tomoe.png"));
        ALL_ICON.add(Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/icons/yamasakura.png"));
        ALL_ICON.add(Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/icons/otakobe.png"));
        ALL_ICON.add(Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/icons/reinakakobo.png"));

        ALL_ASSISTANCE.put("cereals_level", Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/assistants/cereals_level.png"));
        ALL_ASSISTANCE.put("processed_level", Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/assistants/processed_level.png"));
    }
    /**
     * 用来省略开头和结尾
     */
    public static Identifier trans(Identifier location)
    {
        return Identifier.fromNamespaceAndPath(location.getNamespace(), location.getPath().substring(9, location.getPath().length() - 4));
    }
}
