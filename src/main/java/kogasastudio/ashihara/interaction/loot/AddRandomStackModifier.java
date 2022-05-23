package kogasastudio.ashihara.interaction.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;
import static net.minecraft.world.item.ItemStack.EMPTY;

/**
 * 添加一个从给定的物品列表中随机抽取指定数量项物品作为战利品表增量的LootModifier
 */
public class AddRandomStackModifier extends LootModifier {
    private final List<ItemStack> stacks;
    private final int times;

    public AddRandomStackModifier(LootItemCondition[] conditionsIn, List<ItemStack> stacksIn, int timesIn) {
        super(conditionsIn);
        this.stacks = stacksIn;
        this.times = timesIn;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        Random random = context.getRandom();
        List<Integer> usedKeys = new ArrayList<>();
        main:
        for (int i = 0; i < Math.min(this.times, this.stacks.size()); i += 1) {
            int key;
            int stacked = 0;
            do {
                if (stacked > 4) break main;
                key = random.nextInt(this.stacks.size());
                stacked += 1;
            }
            while (usedKeys.contains(key));
            generatedLoot.add(this.stacks.get(key));
            usedKeys.add(key);
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddRandomStackModifier> {
        @Override
        public AddRandomStackModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
            NonNullList<ItemStack> list = NonNullList.create();
            JsonArray stacks = GsonHelper.getAsJsonArray(object, "items", null);
            int rolls = GsonHelper.getAsInt(object, "roll", 1);
            if (stacks == null) list = NonNullList.withSize(1, EMPTY);
            else {
                for (int i = 0; i < stacks.size(); ++i) {
                    ItemStack stack = CraftingHelper.getItemStack(stacks.get(i).getAsJsonObject(), true);
                    if (!stack.isEmpty()) {
                        list.add(stack);
                    }
                }
            }
            list.forEach(stack -> LOGGER_MAIN.info("modifier applied: " + stack.toString()));
            return new AddRandomStackModifier(conditions, list, rolls);
        }

        @Override
        public JsonObject write(AddRandomStackModifier instance) {
            return new JsonObject();
        }
    }
}
