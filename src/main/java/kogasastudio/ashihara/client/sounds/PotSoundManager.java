package kogasastudio.ashihara.client.sounds;

import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

public class PotSoundManager
{
    private static final Map<BlockPos, BoilSoundInstance> SOUNDS = new HashMap<>();

    public static void tick(PotBlockEntity be)
    {
        BlockPos pos = be.getBlockPos();

        if (be.isRemoved() || !be.isCooking())
        {
            BoilSoundInstance sound = SOUNDS.remove(pos);
            if (sound != null)
            {
                Minecraft.getInstance().getSoundManager().stop(sound);
            }
            return;
        }

        if (!SOUNDS.containsKey(pos))
        {
            BoilSoundInstance sound = new BoilSoundInstance(be);
            SOUNDS.put(pos, sound);
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    public static void clear()
    {
        SOUNDS.values().forEach(s -> Minecraft.getInstance().getSoundManager().stop(s));
        SOUNDS.clear();
    }
}
