package kogasastudio.ashihara.item.food;
import kogasastudio.ashihara.datacomponent.PickleType;
import kogasastudio.ashihara.registry.DataComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class PickledFoodItem extends Item
{
    public PickledFoodItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        PickleType type = stack.get(DataComponentTypes.PICKLE_TYPE.get());
        if (type != null)
        {
            MutableComponent prefix = Component.translatable("tooltip.ashihara.pickle_type." + type.getSerializedName());
            prefix.setStyle(Style.EMPTY.withColor(type.getColor()));
            return prefix.append(super.getName(stack));
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag)
    {
        PickleType type = stack.get(DataComponentTypes.PICKLE_TYPE.get());
        if (type != null)
        {
            MobEffectInstance effect = type.getEffect();
            MutableComponent name = Component.translatable(effect.getDescriptionId());
            int seconds = effect.getDuration() / 20;
            int min = seconds / 60;
            int sec = seconds % 60;
            String minSec;
            if (sec < 10) minSec = " (" + min + ":0" + sec + ")";
            else minSec = " (" + min + ":" + sec + ")";
            builder.accept(name.append(Component.literal(minSec)).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, display, builder, flag);
    }
}
