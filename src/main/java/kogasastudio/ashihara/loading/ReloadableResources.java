package kogasastudio.ashihara.loading;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.item.GuideBook;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

@EventBusSubscriber
public class ReloadableResources
{
    private static Map<Identifier, GuideBook.Page> GUIDEBOOK_PAGES;
    private static Map<Integer, GuideBook.Page> GUIDEBOOK_PAGES_REORDERED;
    private static final Identifier GUIDEBOOK_CONTENTS = Identifier.fromNamespaceAndPath(Ashihara.MODID, "guidebook_contents");

    @SubscribeEvent
    public static void register(AddClientReloadListenersEvent event)
    {
        event.addListener(GUIDEBOOK_CONTENTS, ReloadableResources::reload);
    }

    public static CompletableFuture<Void> reload(PreparableReloadListener.SharedState currentReload,
                                                 Executor taskExecutor,
                                                 PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                 Executor reloadExecutor)
    {
        Map<Identifier, GuideBook.Page> pages = new Object2ObjectOpenHashMap<>();
        Map<Integer, GuideBook.Page> pagesReordered = new Object2ObjectOpenHashMap<>();
        CompletableFuture<Void> futures = CompletableFuture.allOf
        (
        GuideBookLoader.reloadGuideBookPages(reloadExecutor, currentReload.resourceManager(), (resourceLocation, pagesRead) ->
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
                } else if (!resourceLocation.toString().contains("zh_cn"))
                {
                    Ashihara.LOGGER_MAIN.warn("Loading pages for same page number {}: {} and {}. Remaining the former.", page.getPageNumber(), page, pagesReordered.get(page.getPageNumber()));
                }
            }
        })
        );
        return futures.thenCompose(preparationBarrier::wait).thenAcceptAsync
        ((unused ->
        {
            GUIDEBOOK_PAGES = pages;
            GUIDEBOOK_PAGES_REORDERED = pagesReordered;
        }));
    }

    public static <T> CompletableFuture<Void> loadResources(Executor executor, ResourceManager resourceManager, String type, Function<Identifier, T> loader, BiConsumer<Identifier, T> map)
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
             Map<Identifier, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();

             for (Identifier resource : resources.keySet())
             {
                 tasks.put(resource, CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));
             }
             return tasks;
         }, executor
        ).thenAcceptAsync
        (tasks ->
         {
             for (Map.Entry<Identifier, CompletableFuture<T>> entry : tasks.entrySet())
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
