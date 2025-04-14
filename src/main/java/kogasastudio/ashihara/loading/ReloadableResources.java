package kogasastudio.ashihara.loading;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.item.GuideBook;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ReloadableResources
{
    private static Map<ResourceLocation, GuideBook.Page> GUIDEBOOK_PAGES;
    private static Map<Integer, GuideBook.Page> GUIDEBOOK_PAGES_REORDERED;

    public static void register()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getResourceManager() instanceof ReloadableResourceManager resourceManager)
        {
            resourceManager.registerReloadListener(ReloadableResources::reload);
        }
    }

    public static CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        Map<ResourceLocation, GuideBook.Page> pages = new Object2ObjectOpenHashMap<>();
        Map<Integer, GuideBook.Page> pagesReordered = new Object2ObjectOpenHashMap<>();
        CompletableFuture<Void> futures = CompletableFuture.allOf
        (
            GuideBookLoader.reloadGuideBookPages(backgroundExecutor, resourceManager, (resourceLocation, pagesRead) ->
            {
                for (GuideBook.Page page : pagesRead.values())
                {
                    if (!pagesReordered.containsKey(page.getPageNumber()))
                    {
                        Minecraft mc = Minecraft.getInstance();
                        String current = mc.getLanguageManager().getSelected();
                        if (resourceLocation.toString().contains(current) || (resourceLocation.toString().contains("zh_cn")))
                        {
                            pagesReordered.put(page.getPageNumber(), page);
                        }
                    }
                    else if (!resourceLocation.toString().contains("zh_cn"))
                    {
                        Ashihara.LOGGER_MAIN.warn("Loading pages for same page number {}: {} and {}. Remaining the former.", page.getPageNumber(), page, pagesReordered.get(page.getPageNumber()));
                    }
                }
            })
        );
        return futures.thenCompose(stage::wait).thenAcceptAsync
        ((unused ->
        {
            GUIDEBOOK_PAGES = pages;
            GUIDEBOOK_PAGES_REORDERED = pagesReordered;
        }));
    }

    public static <T> CompletableFuture<Void> loadResources(Executor executor, ResourceManager resourceManager, String type, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map)
    {
        return CompletableFuture.supplyAsync
        (() -> resourceManager.listResources
            (
                type, fileName ->
                fileName.toString().endsWith(".json")
            ), executor
        ).thenApplyAsync
        (resources ->
            {
                Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();

                for (ResourceLocation resource : resources.keySet())
                {
                    tasks.put(resource, CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));
                }
                return tasks;
            }, executor
        ).thenAcceptAsync
        (tasks ->
            {
                for (Map.Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet())
                {
                    map.accept(entry.getKey(), entry.getValue().join());
                }
            }
        );
    }

    public static Map<Integer, GuideBook.Page> getGuidebookPagesReordered()
    {
        return GUIDEBOOK_PAGES_REORDERED;
    }
}
