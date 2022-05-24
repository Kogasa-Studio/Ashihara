package kogasastudio.ashihara.utils.json.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.NonNullList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author DustW
 **/
public class NonNullListSerializer implements BaseSerializer<NonNullList<?>> {

    @Override
    public NonNullList<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Type[] typeArguments = ((ParameterizedType) typeOfT).getActualTypeArguments();
        final Type parametrizedType = BaseSerializer.listOf(typeArguments[0]).getType();
        final List<?> list = context.deserialize(json, parametrizedType);
        return NonNullList.of(null, list.toArray());
    }

    @Override
    public JsonElement serialize(NonNullList<?> src, Type typeOfSrc, JsonSerializationContext context) {
        final Type[] typeArguments = ((ParameterizedType) typeOfSrc).getActualTypeArguments();
        final Type parametrizedType = BaseSerializer.listOf(typeArguments[0]).getType();
        return context.serialize(src, parametrizedType);
    }
}
