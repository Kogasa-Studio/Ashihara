package kogasastudio.ashihara.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import kogasastudio.ashihara.block.PaddyFieldBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaterFluid.class)
public class MixinWaterFluid
{
    @WrapWithCondition(method = "animateTick", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private boolean shouldPlayAmbient(Level level, double x, double y, double z, SoundEvent sound, SoundSource source, float vol, float pitch, boolean dd)
    {
        BlockPos pos = BlockPos.containing(x, y, z);
        return !(level.getBlockState(pos).getBlock() instanceof PaddyFieldBlock);
    }
}

