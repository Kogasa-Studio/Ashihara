package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.SujikabutoModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemSujikaButo extends ArmorItem
{
    public ItemSujikaButo()
    {
        super(ArmorMaterial.NETHERITE, EquipmentSlotType.HEAD, new Properties().group(ASHIHARA));
    }
    private static final ResourceLocation TEXTURE = new ResourceLocation(Ashihara.MODID, "textures/armors/sujikabuto.png");

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default)
    {
        return (A) new SujikabutoModel(0.8F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type)
    {
        return TEXTURE.toString();
    }
}
