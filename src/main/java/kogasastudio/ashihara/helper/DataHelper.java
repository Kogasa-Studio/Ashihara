package kogasastudio.ashihara.helper;

import net.minecraft.core.NonNullList;

import java.util.List;

/**
 * @author DustW
 **/
public class DataHelper
{
    /**
     * 创建目标List的Nonnull版复制品，同样可担当将List转换为NonnullList的功能
     * @param list 输入的list
     * @return 输出的NonNullList
     */
    public static <T> NonNullList<T> copyAndCast(List<T> list)
    {
        NonNullList<T> result = NonNullList.create();

        for (T t : list) if (t != null) result.add(t);

        return result;
    }
}
