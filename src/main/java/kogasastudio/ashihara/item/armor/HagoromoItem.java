package kogasastudio.ashihara.item.armor;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import kogasastudio.ashihara.client.render.geo.HagoromoRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class HagoromoItem extends Item implements GeoItem
{
    public static final String ANIM_IDLE = "idle";
    public static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("ashihara", "armor/hagoromo");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HagoromoItem(ArmorMaterial material, EquipmentSlot slot, Properties properties)
    {
        super(properties
            .component(DataComponents.EQUIPPABLE, Equippable.builder(slot).setAsset(material.assetId()).setEquipSound(material.equipSound()).build())
            .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
            .component(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(Identifier.fromNamespaceAndPath("ashihara", "hagoromo_speed"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.CHEST)
                .add(Attributes.SCALE, new AttributeModifier(Identifier.fromNamespaceAndPath("ashihara", "hagoromo_scale"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.CHEST)
                .build())
        );
    }

    @Override
    public boolean isFoil(ItemStack itemStack)
    {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag)
    {
        builder.accept(Component.translatable("tooltip.ashihara.hagoromo.1").withStyle(Style.EMPTY.withItalic(true).withColor(0x62185d)));
        builder.accept(Component.translatable("tooltip.ashihara.hagoromo.2").withStyle(Style.EMPTY.withItalic(true).withColor(0x62185d)));
        builder.accept(Component.translatable("tooltip.ashihara.hagoromo.3").withStyle(Style.EMPTY.withColor(0x46d787)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar)
    {
        registrar.add
        (
            new AnimationController<>("idle", 0, state ->
            {
                state.setAnimation(RawAnimation.begin().thenLoop(ANIM_IDLE));
                return PlayState.CONTINUE;
            })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer)
    {
        consumer.accept(new GeoRenderProvider()
        {
            private HagoromoRenderer renderer;

            @Override
            public @NotNull HagoromoRenderer getGeoArmorRenderer(ItemStack stack, EquipmentSlot slot)
            {
                if (this.renderer == null)
                    this.renderer = new HagoromoRenderer();
                return this.renderer;
            }
        });
    }
}