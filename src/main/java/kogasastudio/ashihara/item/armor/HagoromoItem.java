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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
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
        super(properties.component(DataComponents.EQUIPPABLE, Equippable.builder(slot).setAsset(material.assetId()).setEquipSound(material.equipSound()).build()));
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