package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.registry.ConsumeEffectTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.ArrayList;
import java.util.List;

public final class BowlFoodHelper
{
    private static final float BOOST = 1.25f;

    private BowlFoodHelper() {}

    /** Apply food to bowl: boost FOOD x1.25, set CONSUMABLE (EAT), set USE_REMAINDER. */
    public static void applyFood(ItemStack bowl, ItemStack food)
    {
        FoodProperties src = food.get(DataComponents.FOOD);
        if (src == null) return;

        FoodProperties boosted = new FoodProperties(
            Math.round(src.nutrition() * BOOST),
            src.saturation() * BOOST,
            true);

        Consumable srcConsumable = food.get(DataComponents.CONSUMABLE);
        List<ConsumeEffect> effects = new ArrayList<>();
        if (srcConsumable != null)
        {
            for (ConsumeEffect e : srcConsumable.onConsumeEffects())
                effects.add(boostEffect(e));
        }

        Consumable.Builder cb = Consumable.builder()
            .consumeSeconds(1.6f)
            .animation(ItemUseAnimation.EAT)
            .sound(SoundEvents.GENERIC_EAT)
            .hasConsumeParticles(true);
        for (ConsumeEffect e : effects) cb = cb.onConsume(e);
        Consumable cons = cb.build();

        bowl.set(DataComponents.FOOD, boosted);
        bowl.set(DataComponents.CONSUMABLE, cons);
        bowl.set(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(bowl.getItem())));
    }

    /** Apply fluid to bowl. Inherits FOOD/CONSUMABLE from bucket item if registered; otherwise keyword effects. */
    public static void applyFluid(ItemStack bowl, FluidStack fluid)
    {
        bowl.set(DataComponents.FOOD, buildFluidFood(fluid));
        bowl.set(DataComponents.CONSUMABLE, buildFluidConsumable(fluid));
        bowl.set(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(bowl.getItem())));
    }

    /** Remove consumable-related components from bowl. */
    public static void clear(ItemStack bowl)
    {
        bowl.remove(DataComponents.FOOD);
        bowl.remove(DataComponents.CONSUMABLE);
        bowl.remove(DataComponents.USE_REMAINDER);
    }

    /** Boost a ConsumeEffect duration by BOOST. Only handles ApplyStatusEffectsConsumeEffect; others pass through. */
    private static ConsumeEffect boostEffect(ConsumeEffect e)
    {
        if (e instanceof ApplyStatusEffectsConsumeEffect se)
        {
            List<MobEffectInstance> boosted = new ArrayList<>();
            for (MobEffectInstance mei : se.effects())
                boosted.add(new MobEffectInstance(mei.getEffect(), (int) (mei.getDuration() * BOOST), mei.getAmplifier(), mei.isAmbient(), mei.isVisible(), mei.showIcon()));
            return new ApplyStatusEffectsConsumeEffect(boosted, se.probability());
        }
        return e;
    }

    /** Check fluid stack itself first, then its bucket item. */
    @Nullable
    private static FoodProperties getFluidFood(FluidStack fluid)
    {
        if (fluid.has(DataComponents.FOOD))
            return fluid.get(DataComponents.FOOD);
        ItemStack bucket = fluid.getFluidType().getBucket(fluid);
        if (bucket.isEmpty()) return null;
        return bucket.has(DataComponents.FOOD) ? bucket.get(DataComponents.FOOD) : null;
    }

    /** Check fluid stack itself first, then its bucket item. */
    @Nullable
    private static Consumable getFluidConsumable(FluidStack fluid)
    {
        if (fluid.has(DataComponents.CONSUMABLE))
            return fluid.get(DataComponents.CONSUMABLE);
        ItemStack bucket = fluid.getFluidType().getBucket(fluid);
        if (bucket.isEmpty()) return null;
        return bucket.has(DataComponents.CONSUMABLE) ? bucket.get(DataComponents.CONSUMABLE) : null;
    }

    private static boolean isFoodFluid(String id)
    {
        return id.contains("soup") || id.contains("broth") || id.contains("congee")
            || id.contains("stew") || id.contains("porridge") || id.contains("cream")
            || id.contains("juice") || id.contains("tea") || id.contains("coffee")
            || id.contains("honey");
    }

    /** Build FOOD, CONSUMABLE, USE_REMAINDER from fluid and set them on an ItemResource. */
    public static ItemResource applyFluidToItemResource(
    ItemResource resource, FluidStack fluid, int newAmount)
    {
        ItemResource result = resource;
        if (newAmount <= 0)
        {
            result = result.without(DataComponents.FOOD);
            result = result.without(DataComponents.CONSUMABLE);
            result = result.without(DataComponents.USE_REMAINDER);
        }
        else
        {
            result = result.with(DataComponents.FOOD, buildFluidFood(fluid));
            result = result.with(DataComponents.CONSUMABLE, buildFluidConsumable(fluid));
            result = result.with(DataComponents.USE_REMAINDER,
                new UseRemainder(new ItemStackTemplate(resource.getItem())));
        }
        return result;
    }

    private static FoodProperties buildFluidFood(FluidStack fluid)
    {
        FoodProperties fluidFood = getFluidFood(fluid);
        if (fluidFood != null)
            return new FoodProperties(Math.round(fluidFood.nutrition() * BOOST), fluidFood.saturation() * BOOST, true);

        String id = BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getPath();
        if (isFoodFluid(id))
            return new FoodProperties(Math.round(6 * BOOST), 0.6f * BOOST, true);
        return new FoodProperties(0, 0, true);
    }

    private static Consumable buildFluidConsumable(FluidStack fluid)
    {
        Consumable fluidCons = getFluidConsumable(fluid);
        if (fluidCons != null)
        {
            List<ConsumeEffect> boosted = new ArrayList<>();
            for (ConsumeEffect e : fluidCons.onConsumeEffects())
                boosted.add(boostEffect(e));
            Consumable.Builder cb = Consumable.builder()
                .consumeSeconds(fluidCons.consumeSeconds())
                .animation(fluidCons.animation())
                .sound(fluidCons.sound())
                .hasConsumeParticles(fluidCons.hasConsumeParticles());
            for (ConsumeEffect e : boosted) cb = cb.onConsume(e);
            return cb.build();
        }

        String id = BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getPath();
        List<ConsumeEffect> effects = new ArrayList<>();
        if (!isFoodFluid(id))
        {
            if ((id.contains("molten") || id.contains("lava") || id.contains("magma") || id.contains("plasma") || id.contains("superheat")) && !id.contains("chocolate"))
                effects.add(new ConsumeEffectTypes.SetFire(4));
            if (id.contains("toxic") || id.contains("waste") || id.contains("poison") || id.contains("mercury") || id.contains("fuel"))
                effects.add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 200, 1)));
            if (id.contains("acid") || id.contains("chlorine") || id.contains("fluorine") || id.contains("corrosive") || id.contains("hydroxide") || id.contains("ammonia") || id.contains("hydrogen_chloride"))
                effects.add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 3)));
            if (id.contains("nuclear") || id.contains("radio") || id.contains("uranium") || id.contains("plutonium") || id.contains("thorium") || id.contains("tritium") || id.contains("cyanide"))
                effects.add(new ApplyStatusEffectsConsumeEffect(List.of(new MobEffectInstance(MobEffects.WITHER, 500, 3), new MobEffectInstance(MobEffects.NAUSEA, 900, 1))));
            if (id.contains("poop") || id.contains("blood") || id.contains("rotten"))
                effects.add(new ApplyStatusEffectsConsumeEffect(List.of(new MobEffectInstance(MobEffects.NAUSEA, 100, 1), new MobEffectInstance(MobEffects.HUNGER, 200, 2))));
            if (id.contains("soup") || id.contains("heal"))
                effects.add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2)));
            if (id.contains("milk"))
                effects.add(new ClearAllStatusEffectsConsumeEffect());
        }
        Consumable.Builder cb = Consumable.builder()
            .consumeSeconds(1.6f)
            .animation(ItemUseAnimation.DRINK)
            .sound(SoundEvents.HONEY_DRINK)
            .hasConsumeParticles(false);
        for (ConsumeEffect e : effects) cb = cb.onConsume(e);
        return cb.build();
    }
}