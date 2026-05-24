package kogasastudio.ashihara.network;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.helper.RecipeHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SyncRecipesPayload(RecipeType<?> recipeType, List<RecipeHolder<?>> recipes) implements CustomPacketPayload
{
    public static final Type<SyncRecipesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Ashihara.MODID, "sync_recipes"));

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRecipesPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.RECIPE_TYPE),
        SyncRecipesPayload::recipeType,
        RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()),
        SyncRecipesPayload::recipes,
        SyncRecipesPayload::new
    );

    public static class ClientHandler
    {
        public static void handle(SyncRecipesPayload payload, IPayloadContext context)
        {
            context.enqueueWork(() ->
                RecipeHelper.cacheClientRecipes(payload.recipeType(), payload.recipes())
            );
        }
    }
}
