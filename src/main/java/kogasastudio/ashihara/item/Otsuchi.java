package kogasastudio.ashihara.item;

import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.helper.PlayerAnimationHelper;
import kogasastudio.ashihara.registry.PlayerAnimations;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Otsuchi extends Item implements IHeavyUse, IHasHoldAnim
{
    private final ItemAttributeModifiers attributeModifiers;

    public Otsuchi(ToolMaterial tier, int dmgIn, double spdIn, Properties properties)
    {
        super(properties);
        float attackDamage = (float) dmgIn + (float) Math.pow(tier.attackDamageBonus(), 2);
        List<ItemAttributeModifiers.Entry> entries = new ArrayList<>();
        entries.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND));
        //entries.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, spdIn, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND));
        this.attributeModifiers = new ItemAttributeModifiers(entries);
    }

    public Otsuchi(ToolMaterial tier, int dmgIn, double spdIn)
    {
        this(tier, dmgIn, spdIn, new Properties());
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
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MortarBE)
        {
            prepareUse(context);
            return InteractionResult.FAIL;
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void actuallyUseBounded(ItemStack itemstack, Player player, BlockPos p, AtomicBoolean cir)
    {
        BlockEntity block = player.level().getBlockEntity(p);
        if (block instanceof MortarBE)
        {
            player.level().getBlockState(p).useItemOn(itemstack, player.level(), player, player.getUsedItemHand(), new BlockHitResult(p.getCenter(), player.getNearestViewDirection(), p, true));
            cir.set(false);
        }
    }

    @Override
    public int getDelayValue(boolean isUse)
    {
        return 21;
    }

    @Override
    public String getHoldAnim()
    {
        return PlayerAnimations.OTSUCHI_HOLD_ANIM;
    }
}
