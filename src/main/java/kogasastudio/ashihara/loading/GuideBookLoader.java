package kogasastudio.ashihara.loading;

import com.google.gson.Gson;
import kogasastudio.ashihara.item.GuideBook;
import kogasastudio.ashihara.utils.json.JsonUtils;
import kogasastudio.ashihara.utils.json.serializer.GuideBookPageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class GuideBookLoader
{
    public static CompletableFuture<Void> reloadGuideBookPages(Executor backgroundExecutor, ResourceManager resourceManager, BiConsumer<ResourceLocation, GuideBook.Page> elementConsumer)
    {
        Minecraft mc = Minecraft.getInstance();
        String currentLanguage = mc.getLanguageManager().getSelected();
        return CompletableFuture.allOf
        (
            ReloadableResources.loadResources
            (
                backgroundExecutor, resourceManager, "guidebook/" + currentLanguage, (resourceLocation ->
                {
                    Gson gson = JsonUtils.INSTANCE.normal;
                    return GuideBookPageSerializer.deserialize(JsonUtils.loadJsonFromFile(gson, resourceLocation, resourceManager));
                }), elementConsumer
            ),
            ReloadableResources.loadResources
            (
                backgroundExecutor, resourceManager, "guidebook/zh_cn", (resourceLocation ->
                {
                    Gson gson = JsonUtils.INSTANCE.normal;
                    return GuideBookPageSerializer.deserialize(JsonUtils.loadJsonFromFile(gson, resourceLocation, resourceManager));
                }), elementConsumer
            )
        );
    }
}
