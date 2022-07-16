package kogasastudio.ashihara.client.render;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AshiharaAtlas {
    //为Atlas提供RL

    public static final ResourceLocation ICON_ATLAS = new ResourceLocation(Ashihara.MODID, "textures/atlas/icons.png");
    public static final ResourceLocation ASSISTANCE_ATLAS = new ResourceLocation(Ashihara.MODID, "textures/atlas/assistance.png");

    public static final List<ResourceLocation> ALL_ICON = new ArrayList<>();
    public static final List<ResourceLocation> ALL_ASSISTANCE = new ArrayList<>();

    //遍历资源包目录来收集Atlas的组成部分（小贴图）
    //纹理路径省略开头的 textures/ 和结尾的 .png ，mc会自动加上

    @SubscribeEvent
    public static void onAtlasGenerate(ModelRegistryEvent event) {
        ALL_ICON.clear();
        ALL_ASSISTANCE.clear();

        //用at拿到一个原版字段，用来将我们的小贴图丢进Atlas并把Atlas丢进游戏
        Set<Material> miscMaterials = ModelBakery.UNREFERENCED_TEXTURES;

        //纹章
        ArrayList<ResourceLocation> icons = new ArrayList<>
                (Minecraft.getInstance().getResourceManager().listResources("textures/icons/", s -> s.endsWith(".png")));

        //辅助
        ArrayList<ResourceLocation> assistants = new ArrayList<>
                (Minecraft.getInstance().getResourceManager().listResources("textures/assistants/", s -> s.endsWith(".png")));
        //添加纹章类贴图
        for (ResourceLocation location : icons) {
            if (location.getNamespace().equals(Ashihara.MODID)) {
                ResourceLocation trans = trans(location);
                miscMaterials.add(new Material(ICON_ATLAS, trans));
                ALL_ICON.add(trans);
            }
        }
        //添加辅助类贴图
        for (ResourceLocation location : assistants) {
            if (location.getNamespace().equals(Ashihara.MODID)) {
                ResourceLocation trans = trans(location);
                miscMaterials.add(new Material(ASSISTANCE_ATLAS, trans));
                ALL_ASSISTANCE.add(trans);
            }
        }
    }

    /** 用来省略开头和结尾 */
    public static ResourceLocation trans(ResourceLocation location) {
        return new ResourceLocation(location.getNamespace(), location.getPath().substring(9, location.getPath().length() - 4));
    }
}
