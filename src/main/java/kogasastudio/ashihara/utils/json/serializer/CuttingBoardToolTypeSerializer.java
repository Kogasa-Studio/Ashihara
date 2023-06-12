package kogasastudio.ashihara.utils.json.serializer;

import com.google.gson.*;
import kogasastudio.ashihara.utils.CuttingBoardToolType;

import java.lang.reflect.Type;

/**
 * @author DustW
 **/
public class CuttingBoardToolTypeSerializer implements BaseSerializer<CuttingBoardToolType>
{
    @Override
    public CuttingBoardToolType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return CuttingBoardToolType.nameMatches(json.getAsString());
    }

    @Override
    public JsonElement serialize(CuttingBoardToolType src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.getName());
    }
}
