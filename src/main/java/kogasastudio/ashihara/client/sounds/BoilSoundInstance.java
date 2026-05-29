package kogasastudio.ashihara.client.sounds;

import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
import kogasastudio.ashihara.registry.SoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;

public class BoilSoundInstance extends AbstractTickableSoundInstance
{
    private final PotBlockEntity be;

    public BoilSoundInstance(PotBlockEntity be)
    {
        super(SoundEvents.BOIL.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.be = be;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        BlockPos pos = be.getBlockPos();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void tick()
    {
        if (be.isRemoved() || !be.isCooking())
        {
            this.stop();
            return;
        }
        BlockPos pos = be.getBlockPos();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public boolean canPlaySound()
    {
        return true;
    }
}
