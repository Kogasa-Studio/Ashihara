package kogasastudio.ashihara.item;

import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.utils.mixin.PlayerHandleAttackTickResetProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface IHeavyUse extends IHasPreSwing, IBoundedInteract
{
    @Override
    default void actuallyUse(ItemStack stack, Player entity)
    {
        AABB aabb = this.getBoundingBox(entity);
        BlockPos blockPosStart = new BlockPos((int) Math.floor(aabb.minX), (int) Math.floor(aabb.minY), (int) Math.floor(aabb.minZ));
        BlockPos blockPosEnd = new BlockPos((int) Math.ceil(aabb.maxX), (int) Math.ceil(aabb.maxY), (int) Math.ceil(aabb.maxZ));
        AtomicBoolean cir = new AtomicBoolean(true);
        BlockPos.betweenClosed(blockPosStart, blockPosEnd).forEach
        (p -> {if (cir.get()) actuallyUseBounded(stack, entity, p, cir);});
    }

    @Override
    default void actuallyAttack(ItemStack stack, Player player)
    {
        AABB aabb = this.getBoundingBox(player);
        ParticleHelper.drawAABB(aabb, player.level());
        List<LivingEntity> entities = player.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        entities.forEach(player::attack);
        ((PlayerHandleAttackTickResetProvider) player).ashihara1_21$resetAttackStrengthForBoundedWeapon();
        player.swing(InteractionHand.MAIN_HAND);
        stack.hurtAndBreak(1, player, stack.getEquipmentSlot());
    }

    void actuallyUseBounded(ItemStack stack, Player entity, BlockPos pos, AtomicBoolean cir);
}
