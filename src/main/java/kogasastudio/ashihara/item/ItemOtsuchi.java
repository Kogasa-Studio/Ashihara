package kogasastudio.ashihara.item;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;

public class ItemOtsuchi extends TieredItem
{
    private final ItemAttributeModifiers attributeModifiers;

    public ItemOtsuchi(Tier tier, int dmgIn, double spdIn)
    {
        super(tier, new Properties());
        float attackDamage = (float) dmgIn + (float) Math.pow(tier.getAttackDamageBonus(), 2);
        List<ItemAttributeModifiers.Entry> entries = new ArrayList<>();
        entries.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND));
        entries.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, spdIn, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND));
        this.attributeModifiers = new ItemAttributeModifiers(entries, true);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        stack.hurtAndBreak(3, attacker, stack.getEquipmentSlot());
        return true;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
        return this.attributeModifiers;
    }
}
