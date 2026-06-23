package kogasastudio.ashihara.registry;

import com.mojang.logging.LogUtils;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public final class FoodModelRegistry
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Identifier, Map<String, List<Entry>>> LOOKUP = new HashMap<>();
    private static final Map<Identifier, Map<String, StandaloneModelKey<BlockStateModel>>> EXACT_LOOKUP = new HashMap<>();
    private static final Map<Identifier, StandaloneModelKey<BlockStateModel>> KEYS = new LinkedHashMap<>();

    private FoodModelRegistry() {}

    public record Entry(StandaloneModelKey<BlockStateModel> key, int bitesMin, int bitesMax)
    {
        public boolean matches(int chopLeft) { return chopLeft >= bitesMin && chopLeft <= bitesMax; }
        public int rangeWidth() { return bitesMax - bitesMin; }
        public boolean isExact() { return bitesMin == bitesMax; }
    }

    /** Called during ModelEvent.RegisterStandalone (client-side). Scans via ResourceManager. */
    public static void registerStandalones(ModelEvent.RegisterStandalone event)
    {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        scanResources(rm);
        for (var e : KEYS.entrySet())
            event.register(e.getValue(), SimpleUnbakedStandaloneModel.blockStateModel(e.getKey()));
    }

    
    private static void scanResources(ResourceManager rm)
    {
        String base = "models/food/item";
        var found = rm.listResources(base, id -> true);
        if (found.isEmpty()) { LOGGER.warn("FoodModelRegistry: no food models found under {}", base); return; }
        for (var entry : found.entrySet())
        {
            Identifier fullId = entry.getKey();
            // fullId = "ashihara:models/food/item/cooked_rice/bowl_mid_3.json"
            // Path relative to base: "cooked_rice/bowl_mid_3.json"
            String path = fullId.getPath();
            if (!path.startsWith(base + "/")) continue;
            String rel = path.substring(base.length() + 1);
            String[] segs = rel.split("/");

            String itemName = segs[0];
            Identifier itemId = Identifier.fromNamespaceAndPath(Ashihara.MODID, itemName);
            if (!BuiltInRegistries.ITEM.containsKey(itemId))
            {
                LOGGER.error("FoodModelRegistry: item with id {} not found in registry, skipping {}", itemId, fullId);
                continue;
            }

            if (segs.length == 3 && "exact".equals(segs[1]))
            {
                String exactCtx = segs[2];
                if (!exactCtx.endsWith(".json")) continue;
                exactCtx = exactCtx.substring(0, exactCtx.length() - 5);
                Identifier modelId = Identifier.fromNamespaceAndPath(Ashihara.MODID, "food/item/" + itemName + "/exact/" + exactCtx);
                StandaloneModelKey<BlockStateModel> key = new StandaloneModelKey<>(modelId::toDebugFileName);
                KEYS.put(modelId, key);
                EXACT_LOOKUP.computeIfAbsent(itemId, k -> new HashMap<>()).put(exactCtx, key);
                LOGGER.debug("Registered exact food model: {} -> {}", modelId, exactCtx);
                continue;
            }

            if (segs.length != 2) continue;
            String fileName = segs[1];
            if (!fileName.endsWith(".json")) continue;
            String name = fileName.substring(0, fileName.length() - 5);
            String[] parts = name.split("_");
            if (parts.length < 3) { LOGGER.warn("Skipping malformed food model: {}", fullId); continue; }
            String ctx = parts[0] + "_" + parts[1];
            int bitesMax, bitesMin;
            try
            {
                bitesMax = Integer.parseInt(parts[parts.length - 1]);
                bitesMin = parts.length >= 4 ? Integer.parseInt(parts[parts.length - 2]) : bitesMax;
            }
            catch (NumberFormatException e)
            {
                LOGGER.warn("Skipping food model with non-numeric bites: {}", fullId);
                continue;
            }
            Identifier modelId = Identifier.fromNamespaceAndPath(Ashihara.MODID, "food/item/" + itemName + "/" + name);
            StandaloneModelKey<BlockStateModel> key = new StandaloneModelKey<>(modelId::toDebugFileName);
            KEYS.put(modelId, key);
            LOOKUP.computeIfAbsent(itemId, k -> new HashMap<>()).computeIfAbsent(ctx, k -> new ArrayList<>()).add(new Entry(key, bitesMin, bitesMax));
            LOGGER.debug("Registered food model: {} -> {} bites [{},{}]", modelId, ctx, bitesMin, bitesMax);
        }
    }
   @Nullable
   public static StandaloneModelKey<BlockStateModel> lookup(Identifier itemId, String containerCtx, int chopLeft)
   {
       var ctxMap = LOOKUP.get(itemId);
       if (ctxMap == null) return null;
       var entries = ctxMap.get(containerCtx);
       if (entries == null) return null;
       Entry best = null;
       for (Entry e : entries)
       {
           if (!e.matches(chopLeft)) continue;
           if (best == null || (!best.isExact() && e.isExact()) || (best.isExact() == e.isExact() && e.rangeWidth() < best.rangeWidth()))
           {
               best = e;
           }
       }
       return best != null ? best.key() : null;
   }

   @Nullable
   public static StandaloneModelKey<BlockStateModel> lookupExact(Identifier itemId, String ctx)
   {
       var ctxMap = EXACT_LOOKUP.get(itemId);
       return ctxMap != null ? ctxMap.get(ctx) : null;
   }
}
