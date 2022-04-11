package kogasastudio.ashihara.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AshiharaAtlas
{
    //为Atlas提供RL
    public static final ResourceLocation ICON_ATLAS = new ResourceLocation("ashihara:textures/atlas/icons.png");
    public static final ResourceLocation ASSISTANCE_ATLAS = new ResourceLocation("ashihara:textures/atlas/assistance.png");
    //遍历资源包目录来收集Atlas的组成部分（小贴图）
    //纹理路径省略开头的 textures/ 和结尾的 .png ，mc会自动加上

    @SubscribeEvent
    public static void onAtlasGenerate(ModelRegistryEvent event)
    {
        //用反射拿到一个原版字段，用来将我们的小贴图丢进Atlas并把Atlas丢进游戏
        Set<RenderMaterial> miscMaterials = ObfuscationReflectionHelper.getPrivateValue(ModelBakery.class, null, "field_177602_b");
        //纹章
        ArrayList<ResourceLocation> icons = new ArrayList<>
        (Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/icons/", s -> s.endsWith(".png")));
        //辅助
        ArrayList<ResourceLocation> assistants = new ArrayList<>
        (Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/assistants/", s -> s.endsWith(".png")));
        if (miscMaterials != null)
        {
            //添加纹章类贴图
            for (ResourceLocation location : icons)
            {
                //用来省略开头和结尾
                String path = location.getPath().substring(9, location.getPath().length() - 4);
                miscMaterials.add(new RenderMaterial(ICON_ATLAS, new ResourceLocation(location.getNamespace(), path)));
            }
            //添加辅助类贴图
            for (ResourceLocation location : assistants)
            {
                //用来省略开头和结尾
                String path = location.getPath().substring(9, location.getPath().length() - 4);
                miscMaterials.add(new RenderMaterial(ASSISTANCE_ATLAS, new ResourceLocation(location.getNamespace(), path)));
            }
        }
    }
}
