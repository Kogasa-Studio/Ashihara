package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.client.render.ister.PailISTER;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.List;
import java.util.function.Consumer;

import static kogasastudio.ashihara.Ashihara.ASHIHARA;

public class ItemBlockPail extends BlockItem {
    public ItemBlockPail() {
        super(BlockRegistryHandler.PAIL.get(), new Properties().tab(ASHIHARA));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new PailISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                        Minecraft.getInstance().getEntityModels());
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        TranslatableComponent component = new TranslatableComponent(new TranslatableComponent("tooltip.ashihara.pail_empty_message").getString());
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null && !nbt.isEmpty() && !nbt.getCompound("bucket").getString("FluidName").equals("minecraft:empty")) {
            CompoundTag bucket = nbt.getCompound("bucket");
            ResourceLocation rl = new ResourceLocation(bucket.getString("FluidName"));
            String nameSpace = rl.getNamespace();
            String fluidName = rl.getPath();
            String data = "block." + nameSpace + "." + fluidName;
            String name = I18n.get(data);
            component = new TranslatableComponent(new TranslatableComponent("tooltip.ashihara.pail_fluid_existence").getString());
            component.append(name);
            component.append("§b: §a" + bucket.getInt("Amount") + " §6mB §7/ §64000mB");
        }

        tooltip.add(new TranslatableComponent("tooltip.ashihara.pail_display"));
        tooltip.add(component);
    }
}
