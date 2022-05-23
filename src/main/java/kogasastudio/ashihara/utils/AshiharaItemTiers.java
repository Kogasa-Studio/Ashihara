package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public enum AshiharaItemTiers implements IItemTier
{
    STEEL(4, 1200, 7.0F, 5.0F, 10, 1.5f,
            () -> Ingredient.of(ItemRegistryHandler.RICE.get())),

    TAMA(2, 105, 4.0F, 1.0F, 20, 10.0f,
                  () -> Ingredient.of(ItemRegistryHandler.RICE.get()));

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final float akatsuibility;
    private final LazyValue<Ingredient> repairMaterial;

    AshiharaItemTiers(int levelIn, int durIn, float effIn, float dmgIn, int ectIn, float akaIn, Supplier<Ingredient> mtrIn)
    {
        this.harvestLevel = levelIn;
        this.maxUses = durIn;
        this.efficiency = effIn;
        this.attackDamage = dmgIn;
        this.enchantability = ectIn;
        this.akatsuibility = akaIn;
        this.repairMaterial = new LazyValue<>(mtrIn);
    }

    @Override
    public int getUses() {return 0;}

    @Override
    public float getSpeed() {return 0;}

    @Override
    public float getAttackDamageBonus() {return 0;}

    @Override
    public int getLevel() {return 0;}

    @Override
    public int getEnchantmentValue() {return 0;}

    @Override
    public Ingredient getRepairIngredient() {return null;}
}
