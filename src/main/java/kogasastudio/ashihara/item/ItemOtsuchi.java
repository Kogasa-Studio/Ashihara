package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.helper.MathHelper;
import kogasastudio.ashihara.helper.PlayerAnimationHelper;
import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.registry.PlayerAnimations;
import kogasastudio.ashihara.utils.PrePostSwingHandler;
import kogasastudio.ashihara.utils.mixin.PlayerHandleAttackTickResetProvider;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ItemOtsuchi extends TieredItem implements IHasPreSwing, IBoundedAttack, IHasHoldAnim
{
    private final ItemAttributeModifiers attributeModifiers;

    public ItemOtsuchi(Tier tier, int dmgIn, double spdIn)
    {
        super(tier, new Properties());
        float attackDamage = (float) dmgIn + (float) Math.pow(tier.getAttackDamageBonus(), 2);
        List<ItemAttributeModifiers.Entry> entries = new ArrayList<>();
        entries.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND));
        //entries.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, spdIn, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND));
        this.attributeModifiers = new ItemAttributeModifiers(entries, true);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
        return this.attributeModifiers;
    }

    @Override
    public AABB getBoundingBox(Entity entity)
    {
        Vec3 entityPos = entity.position();
        double xTargetOffset = entityPos.x() + Math.sin(Math.toRadians(entity.getYRot())) * -1.5;
        double zTargetOffset = entityPos.z() + Math.cos(Math.toRadians(entity.getYRot())) * 1.5;
        return new AABB(xTargetOffset - 0.75, entityPos.y(), zTargetOffset - 0.75, xTargetOffset + 0.75, entityPos.y() + 2.5, zTargetOffset + 0.75);
    }

    @Override
    public void prepareAttack(ItemStack stack, Player player, Entity target)
    {
        InteractionHand hand = player.getMainHandItem().is(stack.getItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        player.setData(DataComponentTypes.PRE_SWING_REMAINING, new PrePostSwingHandler(stack, hand, 21));
        PlayerAnimationHelper.pushPlayerAnimation(player, PlayerAnimations.OTSUCHI_SMASH_ANIM);
    }

    @Override
    public void actuallyAttack(ItemStack stack, Player player)
    {
        AABB aabb = this.getBoundingBox(player);
        for (double i = aabb.minX; i < aabb.maxX; i += 0.02)
        {
            double offset = Ashihara.RANDOM.nextIntBetweenInclusive(-90, 90) / 1000d;
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), i + offset, aabb.minY + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), i + offset, aabb.maxY + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), i + offset, aabb.minY + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), i + offset, aabb.maxY + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
        }
        for (double i = aabb.minY; i < aabb.maxY; i += 0.02)
        {
            double offset = Ashihara.RANDOM.nextIntBetweenInclusive(-90, 90) / 1000d;
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.minX + offset, i + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.maxX + offset, i + offset, aabb.minZ + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.minX + offset, i + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.maxX + offset, i + offset, aabb.maxZ + offset, 0.0D, 0.0D, 0.0D);
        }
        for (double i = aabb.minZ; i < aabb.maxZ; i += 0.02)
        {
            double offset = Ashihara.RANDOM.nextIntBetweenInclusive(-90, 90) / 1000d;
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.minX + offset, aabb.minY + offset, i + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.maxX + offset, aabb.minY + offset, i + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.minX + offset, aabb.maxY + offset, i + offset, 0.0D, 0.0D, 0.0D);
            player.level().addParticle(new DustParticleOptions(new Vector3f(0.3f, 0.5f, 0.8f), 1f), aabb.maxX + offset, aabb.maxY + offset, i + offset, 0.0D, 0.0D, 0.0D);
        }
        List<LivingEntity> entities = player.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        entities.forEach(player::attack);
        ((PlayerHandleAttackTickResetProvider) player).ashihara1_21$resetAttackStrengthForBoundedWeapon();
        stack.hurtAndBreak(1, player, stack.getEquipmentSlot());
    }

    @Override
    public String getHoldAnim()
    {
        return PlayerAnimations.OTSUCHI_HOLD_ANIM;
    }
}
