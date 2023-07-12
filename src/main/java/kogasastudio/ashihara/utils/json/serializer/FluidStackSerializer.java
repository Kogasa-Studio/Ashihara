package kogasastudio.ashihara.utils.json.serializer;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;

/**
 * @author DustW
 **/
public class FluidStackSerializer implements BaseSerializer<FluidStack>
{
    @Override
    public FluidStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        var obj = json.getAsJsonObject();
        var fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(obj.get("fluid").getAsString()));

        if (fluid == null)
        {
            throw new JsonParseException("Fluid not found: " + obj.get("fluid").getAsString());
        }

        var amount = obj.get("amount").getAsInt();

        if (obj.has("nbt"))
        {
            CompoundTag nbt = null;

            try
            {
                nbt = TagParser.parseTag(obj.get("nbt").getAsString());
            } catch (CommandSyntaxException e)
            {
                e.printStackTrace();
            }

            return new FluidStack(fluid, amount, nbt);
        } else
        {
            return new FluidStack(fluid, amount);
        }
    }

    @Override
    public JsonElement serialize(FluidStack src, Type typeOfSrc, JsonSerializationContext context)
    {
        var result = new JsonObject();
        result.addProperty("fluid", src.getTranslationKey());
        result.addProperty("amount", src.getAmount());

        if (src.getTag() != null)
        {
            result.addProperty("nbt", src.getTag().toString());
        }

        return result;
    }
}
