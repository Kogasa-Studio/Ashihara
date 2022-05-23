package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.SujikabutoModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemSujikaButo extends ArmorItem
{
    public ItemSujikaButo()
    {
        super(ArmorMaterial.NETHERITE, EquipmentSlot.HEAD, new Properties().tab(ASHIHARA));
    }
    private static final ResourceLocation TEXTURE = new ResourceLocation(Ashihara.MODID, "textures/armors/sujikabuto.png");

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default)
    {
        return (A) new SujikabutoModel(0.8F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
    {
        return TEXTURE.toString();
    }
}
