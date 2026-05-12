package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.Blocks;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;

import java.util.function.Consumer;

public class PailBlockItem extends BlockItem
{
    public PailBlockItem(Properties properties)
    {
        super(Blocks.PAIL.get(), properties.useBlockDescriptionPrefix());
    }

    public PailBlockItem()
    {
        this(new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, pContext, display, tooltip, flagIn);
        MutableComponent component = Component.translatable("tooltip.ashihara.pail_empty_message");
        CompoundTag nbt = ((TypedEntityData<?>) stack.getComponents().getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)).copyTagWithoutId();
        if (!nbt.isEmpty() && !nbt.getCompoundOrEmpty("bucket").getStringOr("FluidName", "").equals("minecraft:empty"))
        {
            CompoundTag bucket = nbt.getCompoundOrEmpty("bucket");
            Identifier rl = Identifier.fromNamespaceAndPath(Ashihara.MODID, bucket.getStringOr("FluidName", ""));
            String nameSpace = rl.getNamespace();
            String fluidName = rl.getPath();
            String data = "block." + nameSpace + "." + fluidName;
            String name = I18n.get(data);
            component = Component.translatable("tooltip.ashihara.fluid_existence");
            component.append(name);
            component.append("§b: §a" + bucket.getInt("Amount") + " §6mB §7/ §64000mB");
        }

        tooltip.accept(Component.translatable("tooltip.ashihara.pail_display"));
        tooltip.accept(component);
    }
}
