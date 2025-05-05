package kogasastudio.ashihara.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import kogasastudio.ashihara.client.render.PlayerAnimationProxy;
import kogasastudio.ashihara.item.IBoundedAttack;
import kogasastudio.ashihara.utils.mixin.PlayerHandleAttackTickResetProvider;
import kogasastudio.ashihara.utils.mixin.PlayerProxyProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity implements PlayerProxyProvider, PlayerHandleAttackTickResetProvider
{
    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    @Unique
    private PlayerAnimationProxy ashihara_1_21$proxy = new PlayerAnimationProxy((Player) (Object) this);


    @Override
    public PlayerAnimationProxy ashihara_1_21$getAnimationProxy()
    {
        return ashihara_1_21$proxy;
    }

    @Override
    public void ashihara1_21$resetAttackStrengthForBoundedWeapon()
    {
        super.attackStrengthTicker = 0;
    }

    @WrapOperation(method = "resetAttackStrengthTicker", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;attackStrengthTicker:I"))
    private void cancelAttackStrengthResetForBoundedWeapon(Player instance, int value, Operation<Void> original)
    {
        if (!(instance.getWeaponItem().getItem() instanceof IBoundedAttack)) original.call(instance, value);
    }
}
