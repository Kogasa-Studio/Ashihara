package kogasastudio.ashihara.interaction.loot;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class AddTableModifier extends LootModifier
{
    private final ResourceLocation lootTable;

    public AddTableModifier(ILootCondition[] conditionsIn, ResourceLocation lootTableIn)
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
            table.recursiveGenerate(context, LootTable.capStackSizes(generatedLoot::add));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddTableModifier>
    {
        @Override
        public AddTableModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
        {
            ResourceLocation lootTable = new ResourceLocation(JSONUtils.getString(object, "lootTable"));
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
