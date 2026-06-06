package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.registry.DataAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Utility for furniture grid snapping.
 * <p>
 * Grid levels represent subdivisions within a 16×16×16 block:
 * <ul>
 *   <li>GRID_NONE    — free placement, no snap</li>
 *   <li>GRID_16PX    — 1-block snap (16 px)</li>
 *   <li>GRID_8PX     — 1/2-block snap (8 px)</li>
 *   <li>GRID_4PX     — 1/4-block snap (4 px)</li>
 *   <li>GRID_2PX     — 1/8-block snap (2 px)</li>
 *   <li>GRID_1PX     — 1/16-block snap (1 px)</li>
 *   <li>GRID_HALF_PX — 1/32-block snap (1/2 px)</li>
 * </ul>
 */
public final class GridSnapHelper
{
    public static final int GRID_NONE = -1;
    public static final int GRID_16PX = 0;
    public static final int GRID_8PX  = 1;
    public static final int GRID_4PX  = 2;
    public static final int GRID_2PX  = 3;
    public static final int GRID_1PX  = 4;
    public static final int GRID_HALF_PX = 5;

    public static final int GRID_COUNT = 6;
    public static final int DEFAULT = GRID_1PX;

    private static final int[] GRID_ORDER =
    {
        GRID_16PX, GRID_8PX, GRID_4PX, GRID_2PX, GRID_1PX, GRID_HALF_PX, GRID_NONE,
    };

    private static final float[] STEP_SIZES = {1.0F, 0.5F, 0.25F, 0.125F, 0.0625F, 0.03125F};

    private static final String[] DISPLAY_LABELS = {"1", "1/2", "1/4", "1/8", "1/16", "1/32"};

    private GridSnapHelper() {}

    public static float getStepSize(int gridLevel)
    {
        if (gridLevel < 0 || gridLevel >= STEP_SIZES.length) return 0F;
        return STEP_SIZES[gridLevel];
    }

    /**
     * Snap a world-space click location to the grid relative to the block origin.
     */
    public static Vec3 snapClickLocation(Vec3 clickLocation, BlockPos blockPos, int gridLevel)
    {
        if (gridLevel == GRID_NONE) return clickLocation;
        float step = getStepSize(gridLevel);
        if (step <= 0F) return clickLocation;

        double bx = blockPos.getX();
        double by = blockPos.getY();
        double bz = blockPos.getZ();

        double rx = clickLocation.x() - bx;
        double ry = clickLocation.y() - by;
        double rz = clickLocation.z() - bz;

        rx = Math.round(rx / step) * step;
        //ry = Math.round(ry / step) * step;
        rz = Math.round(rz / step) * step;

        rx = Math.clamp(rx, 0.0, 1.0);
        //ry = Math.clamp(ry, 0.0, 1.0);
        rz = Math.clamp(rz, 0.0, 1.0);

        return new Vec3(bx + rx, by + ry, bz + rz);
    }

    public static int getGridStep(Player player)
    {
        if (player == null) return DEFAULT;
        return player.getData(DataAttachmentTypes.GRID_SNAP_STEP.get());
    }

    public static void setGridStep(Player player, int level)
    {
        if (player == null) return;
        player.setData(DataAttachmentTypes.GRID_SNAP_STEP.get(), level);
    }

    public static int cycleNext(int current)
    {
        for (int i = 0; i < GRID_ORDER.length; i++)
        {
            if (GRID_ORDER[i] == current)
                return GRID_ORDER[(i + 1) % GRID_ORDER.length];
        }
        return GRID_ORDER[0];
    }

    public static int cyclePrev(int current)
    {
        for (int i = 0; i < GRID_ORDER.length; i++)
        {
            if (GRID_ORDER[i] == current)
                return GRID_ORDER[(i - 1 + GRID_ORDER.length) % GRID_ORDER.length];
        }
        return GRID_ORDER[GRID_ORDER.length - 1];
    }

    public static String getDisplayLabelPrefix(int gridLevel)
    {
        if (gridLevel == GRID_NONE) return "1/∞";
        if (gridLevel < 0 || gridLevel >= DISPLAY_LABELS.length) return "?";
        return DISPLAY_LABELS[gridLevel];
    }
}
