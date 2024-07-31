package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemSujikaButo extends ArmorItem
{
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/armors/sujikabuto.png");

    public ItemSujikaButo()
    {
        super(ArmorMaterials.NETHERITE, Type.HELMET, new Properties());
    }

    // todo 这个方法没了？
    //@Override
    //@OnlyIn(Dist.CLIENT)
    //@SuppressWarnings("unchecked")
    //public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
    //    return (A) new SujikabutoModel(0.8F);
    //}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new KabutoItemExtensions());
        super.initializeClient(consumer);
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel)
    {
        return TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class KabutoItemExtensions implements IClientItemExtensions
    {
        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original)
        {
            return IClientItemExtensions.super.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
        }
    }
}
