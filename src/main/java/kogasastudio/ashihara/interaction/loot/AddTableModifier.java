package kogasastudio.ashihara.interaction.loot;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class AddTableModifier extends LootModifier
{
    private final ResourceLocation lootTable;

    public AddTableModifier(LootItemConditions[] conditionsIn, ResourceLocation lootTableIn)
    {
        super(conditionsIn);
        this.lootTable = lootTableIn;
    }

    public boolean canModify() {return true;}

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
    {
        if (this.canModify())
        {
            LootTable table = context.getLootTable(this.lootTable);
            table.getRandomItemsRaw(context, LootTable.createStackSplitter(generatedLoot::add));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddTableModifier>
    {
        @Override
        public AddTableModifier read(ResourceLocation location, JsonObject object, LootItemConditions[] ailootcondition)
        {
            ResourceLocation lootTable = new ResourceLocation(GsonHelper.getAsString(object, "lootTable"));
            return new AddTableModifier(ailootcondition, lootTable);
        }

        @Override
        public JsonObject write(AddTableModifier instance)
        {
            JsonObject object = this.makeConditions(instance.conditions);
            object.addProperty("lootTable", instance.lootTable.toString());
            return object;
        }
    }
}
