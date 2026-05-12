package kogasastudio.ashihara.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import kogasastudio.ashihara.utils.json.serializer.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * @author DustW
 **/
public enum JsonUtils
{
    /**
     * 最 佳 单 例
     */
    INSTANCE;
    public final Gson normal;
    public final Gson pretty;
    public final Gson noExpose;

    JsonUtils()
    {
        GsonBuilder builder = new GsonBuilder()
        // 关闭 html 转义
        .disableHtmlEscaping()
        // 开启复杂 Map 的序列化
        .enableComplexMapKeySerialization()
        // 注册自定义类型的序列化/反序列化器
        //.registerTypeAdapter(Ingredient.class, new IngredientSerializer())
        //.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
        .registerTypeAdapter(NonNullList.class, new NonNullListSerializer())
        .registerTypeAdapter(FluidStack.class, new FluidStackSerializer())
        .registerTypeAdapter(CuttingBoardToolType.class, new CuttingBoardToolTypeSerializer())
        .registerTypeAdapter(GuideBookPageSerializer.class, new GuideBookPageSerializer())
        .registerTypeAdapter(GuideBookPageSerializer.GuideBookTextFieldSerializer.class, new GuideBookPageSerializer.GuideBookTextFieldSerializer())
        .registerTypeAdapter(GuideBookPageSerializer.GuideBookIllustrationSerializer.class, new GuideBookPageSerializer.GuideBookIllustrationSerializer());

        // 无视 @Expose 注解的 Gson 实例
        noExpose = builder.create();

        // 要求 *全部*字段都有 @Expose 注解的 Gson 实例
        builder.excludeFieldsWithoutExposeAnnotation();
        normal = builder.create();

        // 输出的字符串漂亮一点的 Gson 实例 -> 输出到 json 文件（例如合成表）的，好看
        builder.setPrettyPrinting();
        pretty = builder.create();
    }

    public static JsonObject loadJsonFromFile(Gson gson, Identifier location, ResourceManager manager)
    {
        return GsonHelper.fromJson(gson, getFileContents(location, manager), JsonObject.class);
    }

    /**
     * Read a text-based file into memory in the form of a single string
     *
     * @param location The resource path of the file
     * @param manager The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
     */

    public static String getFileContents(Identifier location, ResourceManager manager)
    {
        try (InputStream inputStream = manager.getResourceOrThrow(location).open())
        {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        }
        catch (Exception e)
        {
            Ashihara.LOGGER_MAIN.error("Couldn't load {}", location, e);

            throw new RuntimeException(new FileNotFoundException(location.toString()));
        }
    }

    public static void writeToJson(Gson gson, Path path, JsonObject json) throws IOException
    {
        Path gameDirectory = Minecraft.getInstance().gameDirectory.toPath();
        File file = new File(gameDirectory.toFile(), path.toString());
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (FileWriter fileWriter = new FileWriter(file))
        {
            gson.toJson(json, fileWriter);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
