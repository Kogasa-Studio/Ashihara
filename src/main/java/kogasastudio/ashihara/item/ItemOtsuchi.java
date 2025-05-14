package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.helper.PlayerAnimationHelper;
import kogasastudio.ashihara.registry.PlayerAnimations;
import kogasastudio.ashihara.utils.mixin.PlayerHandleAttackTickResetProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

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
    public void playPrePostSwingAnim(Player player)
    {
        PlayerAnimationHelper.pushPlayerAnimation(player, PlayerAnimations.OTSUCHI_SMASH_ANIM);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MortarTE)
        {
            prepareUse(context);
            return InteractionResult.FAIL;
        }
        return super.onItemUseFirst(stack, context);
    }
    @Override
    public void actuallyUse(ItemStack itemstack, Player player)
    {
        AABB aabb = this.getBoundingBox(player);
        BlockPos blockPosStart = new BlockPos((int) Math.floor(aabb.minX), (int) Math.floor(aabb.minY), (int) Math.floor(aabb.minZ));
        BlockPos blockPosEnd = new BlockPos((int) Math.ceil(aabb.maxX), (int) Math.ceil(aabb.maxY), (int) Math.ceil(aabb.maxZ));
        BlockPos.betweenClosed(blockPosStart, blockPosEnd).forEach
        (
            p ->
            {
                BlockEntity block = player.level().getBlockEntity(p);
                if (block instanceof MortarTE)
                player.level().getBlockState(p).useItemOn(itemstack, player.level(), player, player.getUsedItemHand(), new BlockHitResult(p.getCenter(), player.getNearestViewDirection(), p, true));
            }
        );
    }

    @Override
    public int getDelayValue(boolean isUse)
    {
        return 21;
    }

    @Override
    public void actuallyAttack(ItemStack stack, Player player)
    {
        AABB aabb = this.getBoundingBox(player);
        ParticleHelper.drawAABB(aabb, player.level());
        List<LivingEntity> entities = player.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        entities.forEach(player::attack);
        ((PlayerHandleAttackTickResetProvider) player).ashihara1_21$resetAttackStrengthForBoundedWeapon();
        player.swing(InteractionHand.MAIN_HAND);
        stack.hurtAndBreak(1, player, stack.getEquipmentSlot());
    }

    @Override
    public String getHoldAnim()
    {
        return PlayerAnimations.OTSUCHI_HOLD_ANIM;
    }
}
