package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.client.render.ister.PailISTER;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

import net.minecraft.item.Item.Properties;

public class ItemBlockPail extends BlockItem
{
    public ItemBlockPail() {super(BlockRegistryHandler.PAIL.get(), new Properties().tab(ASHIHARA).setISTER(() -> PailISTER::new));}

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        TranslationTextComponent component = new TranslationTextComponent(new TranslationTextComponent("tooltip.ashihara.pail_empty_message").getString());
        CompoundNBT nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null && !nbt.isEmpty() && !nbt.getCompound("bucket").getString("FluidName").equals("minecraft:empty"))
        {
            CompoundNBT bucket = nbt.getCompound("bucket");
            ResourceLocation rl = new ResourceLocation(bucket.getString("FluidName"));
            String nameSpace = rl.getNamespace();
            String fluidName = rl.getPath();
            String data = "block." + nameSpace + "." + fluidName;
            String name = I18n.get(data);
            component = new TranslationTextComponent(new TranslationTextComponent("tooltip.ashihara.pail_fluid_existence").getString());
            component.append(name);
            component.append("§b: §a" + bucket.getInt("Amount") + " §6mB §7/ §64000mB");
        }

        tooltip.add(new TranslationTextComponent("tooltip.ashihara.pail_display"));
        tooltip.add(component);
    }
}
