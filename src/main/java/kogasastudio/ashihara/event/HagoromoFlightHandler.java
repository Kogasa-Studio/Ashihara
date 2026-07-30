package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.network.HagoromoFlightData;
import kogasastudio.ashihara.network.HagoromoFlightSyncPacket;
import kogasastudio.ashihara.registry.DataAttachmentTypes;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Ashihara.MODID)
public class HagoromoFlightHandler
{
    private static final Identifier FLIGHT_MODIFIER_ID = Identifier.fromNamespaceAndPath(Ashihara.MODID, "hagoromo_flight");
    private static final int THRESHOLD = 100;
    private static final int SLOW_FALL_DURATION = 200;
    private static final int SYNC_INTERVAL = 5;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event)
    {
        var player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer sp)) return;

        GameType mode = sp.gameMode.getGameModeForPlayer();
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean wearing = chest.is(Items.HAGOROMO);
        var data = player.getData(DataAttachmentTypes.HAGOROMO_FLIGHT);
        boolean changed = false;

        // Wearing hagoromo in any mode: activate tracking (ensures unequip cleanup)
        if (wearing && !data.hagoromoActive())
        {
            data = new HagoromoFlightData(data.hasFlight(), true, data.airborneTicks(), data.cooldownTicks());
            player.setData(DataAttachmentTypes.HAGOROMO_FLIGHT, data);
        }

        // Unequip cleanup — works in any mode
        if (!wearing && data.hagoromoActive())
        {
            removeFlight(player);
            player.getAbilities().setFlyingSpeed(0.05f);
            player.onUpdateAbilities();
            player.setData(DataAttachmentTypes.HAGOROMO_FLIGHT, new HagoromoFlightData(false, false, 0, 0));
            return;
        }

        // Only process flight timer logic in Survival/Adventure
        if (mode != GameType.SURVIVAL && mode != GameType.ADVENTURE) return;

        // --- Cooldown ticking ---
        if (data.cooldownTicks() > 0)
        {
            int cd = data.cooldownTicks() - 1;
            if (cd <= 0)
            {
                addFlight(player);
                data = new HagoromoFlightData(true, true, 0, 0);
                changed = true;
            }
            else
            {
                data = new HagoromoFlightData(false, true, 0, cd);
                changed = true;
            }
        }

        // --- Equip/cooldown-over: grant flight ---
        if (!data.hasFlight() && data.cooldownTicks() <= 0)
        {
            addFlight(player);
            data = new HagoromoFlightData(true, true, 0, 0);
            changed = true;
        }

        // --- Flying timer ---
        if (data.hasFlight() && player.getAbilities().flying)
        {
            int ticks = data.airborneTicks() + 1;
            if (ticks >= THRESHOLD)
            {
                removeFlight(player);
                player.onUpdateAbilities();
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, SLOW_FALL_DURATION, 0));
                data = new HagoromoFlightData(false, true, 0, THRESHOLD);
                changed = true;
            }
            else
            {
                data = new HagoromoFlightData(true, true, ticks, data.cooldownTicks());
                changed = true;
            }
        }
        else if (data.hasFlight())
        {
            data = new HagoromoFlightData(true, true, 0, data.cooldownTicks());
        }

        if (changed)
            player.setData(DataAttachmentTypes.HAGOROMO_FLIGHT, data);

        if (player.tickCount % SYNC_INTERVAL == 0)
            PacketDistributor.sendToPlayer(sp, new HagoromoFlightSyncPacket(data.hasFlight(), data.hagoromoActive(), data.airborneTicks(), data.cooldownTicks()));
    }

    private static void addFlight(net.minecraft.world.entity.player.Player player)
    {
        AttributeInstance attr = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (attr != null)
            attr.addTransientModifier(new AttributeModifier(FLIGHT_MODIFIER_ID, 1.0, AttributeModifier.Operation.ADD_VALUE));
        player.onUpdateAbilities();
    }

    private static void removeFlight(net.minecraft.world.entity.player.Player player)
    {
        AttributeInstance attr = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (attr != null)
            attr.removeModifier(FLIGHT_MODIFIER_ID);
        player.onUpdateAbilities();
    }
}