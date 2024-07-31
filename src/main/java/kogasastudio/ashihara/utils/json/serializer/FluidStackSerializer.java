package kogasastudio.ashihara.utils.json.serializer;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

import java.lang.reflect.Type;

/**
 * @author DustW
 **/
public class FluidStackSerializer implements BaseSerializer<FluidStack>
{
    public static FluidStack deserialize(JsonElement json)
    {
        var obj = json.getAsJsonObject();
        var fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(obj.get("fluid").getAsString()));

        if (fluid == null)
        {
            throw new JsonParseException("Fluid not found: " + obj.get("fluid").getAsString());
        }

        var amount = obj.get("amount").getAsInt();
        return new FluidStack(fluid, amount);
    }

    @Override
    public FluidStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return deserialize(json);
    }

    @Override
    public JsonElement serialize(FluidStack src, Type typeOfSrc, JsonSerializationContext context)
    {
        var result = new JsonObject();
        result.addProperty("fluid", src.toString());
        result.addProperty("amount", src.getAmount());

        return result;
    }
}
