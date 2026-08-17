package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.ICustomData;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Off-thread reconstruction of {@link MultiBuiltBlockEntity} aggregate shapes.
 *
 * <p>All inputs ({@link ComponentStateDefinition} snapshots and component singletons)
 * are effectively immutable during the build, and {@link Shapes} carries no mutable
 * static cache, so the entire computation is safe to run on a worker thread. The only
 * mutation (the BE's shape field and list contents) happens back on the level's main
 * thread inside {@link MultiBuiltBlockEntity#commitLoadedShapes}.
 */
public final class AsyncShapeBuilder
{
    //————————MONITVM—DE—MVLTIPLICIBVS—FILIS——————
    private static final ExecutorService SHAPE_POOL = createShapePool();

    private static ExecutorService createShapePool()
    {
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors() - 1);
        ThreadFactory factory = Thread.ofPlatform().daemon().name("ashihara-shape-", 0).factory();
        return Executors.newFixedThreadPool(threads, factory);
    }
    //————————MONITVM—DE—MVLTIPLICIBVS—FILIS——————

    private AsyncShapeBuilder() {}

    /**
     * Reconstruct a single component's shape, mirroring the eager logic that
     * {@code ComponentStateDefinition.deserializeNBT} used before the async refactor.
     * Base-shape components are always rebuilt; {@link ICustomData} components keep
     * their serialized shape.
     */
    public static VoxelShape computeComponentShape(ComponentStateDefinition def)
    {
        if (def.component().getBaseShape() != null && !(def.component() instanceof ICustomData))
        {
            return def.component().rebuildShape(def.inBlockPos(), def.rotationX(), def.rotationY(), def.rotationZ());
        }
        return def.shape();
    }

    /**
     * Union shapes via balanced binary reduction. The old linear chain
     * ({@code acc = Shapes.or(acc, next)}) reprocesses the whole accumulator each
     * iteration, giving O(n^2) voxel work; a balanced tree keeps each shape in roughly
     * O(log n) joins, dropping it to O(n log n).
     */
    public static VoxelShape unionBalanced(List<VoxelShape> shapes)
    {
        if (shapes.isEmpty()) return Shapes.empty();
        List<VoxelShape> current = new ArrayList<>(shapes);
        while (current.size() > 1)
        {
            List<VoxelShape> next = new ArrayList<>((current.size() + 1) / 2);
            for (int i = 0; i < current.size(); i += 2)
            {
                next.add(i + 1 < current.size() ? Shapes.or(current.get(i), current.get(i + 1)) : current.get(i));
            }
            current = next;
        }
        return current.getFirst();
    }

    /**
     * Build the aggregate shape off-thread from immutable snapshots taken during
     * {@code loadAdditional}, then post the result back onto the level's main thread.
     * The BE re-validates the captured epoch before committing so stale results from a
     * concurrent refresh or unload are discarded. Any failure falls back to the
     * synchronous path so loading can never break.
     */
    //————————MONITVM—DE—MVLTIPLICIBVS—FILIS——————
    public static void buildAsync
    (
        MultiBuiltBlockEntity be,
        long epoch,
        List<ComponentStateDefinition> components,
        List<ComponentStateDefinition> additional,
        List<ComponentStateDefinition> furniture
    )
    {
        SHAPE_POOL.execute(() ->
        {
            try
            {
                List<VoxelShape> compShapes = new ArrayList<>(components.size());
                for (ComponentStateDefinition def : components) compShapes.add(computeComponentShape(def));
                List<VoxelShape> addShapes = new ArrayList<>(additional.size());
                for (ComponentStateDefinition def : additional) addShapes.add(computeComponentShape(def));
                List<VoxelShape> furnShapes = new ArrayList<>(furniture.size());
                for (ComponentStateDefinition def : furniture) furnShapes.add(computeComponentShape(def));

                List<VoxelShape> all = new ArrayList<>(compShapes.size() + addShapes.size() + furnShapes.size());
                all.addAll(compShapes);
                all.addAll(addShapes);
                all.addAll(furnShapes);
                VoxelShape aggregate = unionBalanced(all);

                schedule(be.getLevel(), () -> be.commitLoadedShapes(epoch, compShapes, addShapes, furnShapes, aggregate));
            }
            catch (Throwable t)
            {
                Ashihara.LOGGER_MAIN.error("Async shape build failed for BE at {}; falling back to sync", be.getBlockPos(), t);
                schedule(be.getLevel(), () -> be.fallbackReloadShape(epoch));
            }
        });
    }

    private static void schedule(@Nullable Level level, Runnable task)
    {
        if (level == null) return;
        if (level.isClientSide()) Minecraft.getInstance().execute(task);
        else if (level instanceof ServerLevel serverLevel) serverLevel.getServer().execute(task);
    }
    //————————MONITVM—DE—MVLTIPLICIBVS—FILIS——————
}
