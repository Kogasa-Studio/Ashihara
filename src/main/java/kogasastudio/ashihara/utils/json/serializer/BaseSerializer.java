package kogasastudio.ashihara.utils.json.serializer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author DustW
 **/
public interface BaseSerializer<T> extends JsonSerializer<T>, JsonDeserializer<T>
{
    static <E> TypeToken<List<E>> listOf(final Type arg)
    {
        return new TypeToken<List<E>>() {}.where(new TypeParameter<E>() {}, (TypeToken<E>) TypeToken.of(arg));
    }
}
