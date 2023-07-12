package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.client.render.ister.PailISTER;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

public class ItemBlockPail extends BlockItem
{
    public ItemBlockPail()
    {
        super(BlockRegistryHandler.PAIL.get(), new Properties());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions()
        {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return new PailISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                                     Minecraft.getInstance().getEntityModels());
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        MutableComponent component = Component.translatable("tooltip.ashihara.pail_empty_message");
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null && !nbt.isEmpty() && !nbt.getCompound("bucket").getString("FluidName").equals("minecraft:empty"))
        {
            CompoundTag bucket = nbt.getCompound("bucket");
            ResourceLocation rl = new ResourceLocation(bucket.getString("FluidName"));
            String nameSpace = rl.getNamespace();
            String fluidName = rl.getPath();
            String data = "block." + nameSpace + "." + fluidName;
            String name = I18n.get(data);
            component = Component.translatable("tooltip.ashihara.fluid_existence");
            component.append(name);
            component.append("§b: §a" + bucket.getInt("Amount") + " §6mB §7/ §64000mB");
        }

        tooltip.add(Component.translatable("tooltip.ashihara.pail_display"));
        tooltip.add(component);
    }
}
