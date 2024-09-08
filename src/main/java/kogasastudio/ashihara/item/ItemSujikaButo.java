package kogasastudio.ashihara.item;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemSujikaButo extends ArmorItem
{
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/armors/sujikabuto.png");

    public ItemSujikaButo()
    {
        super(ArmorMaterials.NETHERITE, Type.HELMET, new Properties());
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel)
    {
        return TEXTURE;
    }
}
