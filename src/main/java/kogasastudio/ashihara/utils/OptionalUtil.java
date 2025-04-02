package kogasastudio.ashihara.utils;

import java.util.Optional;
import java.util.function.Function;

public class OptionalUtil
{
    /**
     * Provides an exact value that could be defaulted for some method(s) of an Optional<T> with ifPresent() check and not needing to provide a goddamn T instance.
     * @param defaultValue The default value you wish to apply.
     * @param optional The exact Optional<T>.
     * @param function Some method of the optional, signed as foo::bar.
     * @return The default value if optional is not present, or else the return value of the function applied to it.
     * @param <C> T from Optional<T>.
     * @param <R> Type of the return value of the function.
     */
    public static <C, R> R getWithDefault(R defaultValue, Optional<C> optional, Function<C, R> function)
    {
        if (optional.isPresent()) return function.apply(optional.get());
        else return defaultValue;
    }
}
