package kogasastudio.ashihara.utils.json.serializer;

import com.google.gson.*;
import kogasastudio.ashihara.item.GuideBook;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public class GuideBookPageSerializer implements BaseSerializer<GuideBook.Page>
{
    public static GuideBook.Page deserialize(JsonElement json)
    {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonArray textFieldsRaw = jsonObject.get("textFields").getAsJsonArray();
        JsonArray illustrationsRaw = jsonObject.get("illustrations").getAsJsonArray();

        GuideBook.Page.TextField[] textFields = new GuideBook.Page.TextField[textFieldsRaw.size()];
        GuideBook.Page.Illustration[] illustrations = new GuideBook.Page.Illustration[illustrationsRaw.size()];

        for (int i = 0; i < textFieldsRaw.size(); i++)
        {
            JsonElement element = textFieldsRaw.get(i);
            textFields[i] = GuideBookTextFieldSerializer.deserialize(element);
        }

        for (int i = 0; i < illustrationsRaw.size(); i++)
        {
            JsonElement element = illustrationsRaw.get(i);
            illustrations[i] = GuideBookIllustrationSerializer.deserialize(element);
        }

        return new GuideBook.Page(jsonObject.get("pageNumber").getAsInt(), jsonObject.get("isTextColumned").getAsBoolean(), textFields, illustrations);
    }

    @Override
    public GuideBook.Page deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return deserialize(json);
    }

    public static JsonElement serialize(GuideBook.Page src)
    {
        JsonObject json = new JsonObject();
        json.addProperty("pageNumber", src.getPageNumber());
        json.addProperty("isTextColumned", src.isTextColumned());
        JsonArray fieldsRaw = new JsonArray();
        JsonArray illustrationsRaw = new JsonArray();
        for (GuideBook.Page.TextField tf : src.getTextFields()) {fieldsRaw.add(GuideBookTextFieldSerializer.serialize(tf));}
        for (GuideBook.Page.Illustration illustration : src.getIllustrations()) {fieldsRaw.add(GuideBookIllustrationSerializer.serialize(illustration));}
        json.add("textFields", fieldsRaw);
        json.add("illustrations", illustrationsRaw);
        return json;
    }

    @Override
    public JsonElement serialize(GuideBook.Page src, Type typeOfSrc, JsonSerializationContext context)
    {
        return serialize(src);
    }

    public static class GuideBookTextFieldSerializer implements BaseSerializer<GuideBook.Page.TextField>
    {
        public static GuideBook.Page.TextField deserialize(JsonElement json)
        {
            JsonObject jsonObject = json.getAsJsonObject();
            return new GuideBook.Page.TextField(jsonObject.get("x").getAsFloat(), jsonObject.get("y").getAsFloat(), jsonObject.get("width").getAsInt(), jsonObject.get("height").getAsInt(), jsonObject.get("text").getAsString());
        }

        @Override
        public GuideBook.Page.TextField deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return deserialize(json, typeOfT, context);
        }

        public static JsonElement serialize(GuideBook.Page.TextField src)
        {
            JsonObject json = new JsonObject();
            json.addProperty("x", src.x());
            json.addProperty("y", src.y());
            json.addProperty("width", src.widthInFullWidthChar());
            json.addProperty("height", src.heightInFullWidthChar());
            json.addProperty("text", src.text());
            return json;
        }

        @Override
        public JsonElement serialize(GuideBook.Page.TextField src, Type typeOfSrc, JsonSerializationContext context)
        {
            return serialize(src);
        }
    }

    public static class GuideBookIllustrationSerializer implements BaseSerializer<GuideBook.Page.Illustration>
    {
        public static GuideBook.Page.Illustration deserialize(JsonElement json)
        {
            JsonObject jsonObject = json.getAsJsonObject();
            return new GuideBook.Page.Illustration(jsonObject.get("x").getAsFloat(), jsonObject.get("y").getAsFloat(), jsonObject.get("width").getAsInt(), jsonObject.get("height").getAsInt(), ResourceLocation.parse(jsonObject.get("pic").getAsString()));
        }

        @Override
        public GuideBook.Page.Illustration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return deserialize(json);
        }

        public static JsonElement serialize(GuideBook.Page.Illustration src)
        {
            JsonObject json = new JsonObject();
            json.addProperty("x", src.x());
            json.addProperty("y", src.y());
            json.addProperty("width", src.width());
            json.addProperty("height", src.height());
            json.addProperty("text", src.pic().toString());
            return json;
        }

        @Override
        public JsonElement serialize(GuideBook.Page.Illustration src, Type typeOfSrc, JsonSerializationContext context)
        {
            return serialize(src);
        }
    }
}
