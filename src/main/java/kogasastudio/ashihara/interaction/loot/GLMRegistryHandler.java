package kogasastudio.ashihara.interaction.loot;

import kogasastudio.ashihara.Ashihara;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class GLMRegistryHandler
{
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, Ashihara.MODID);

    public static final RegistryObject<GlobalLootModifierSerializer<AddTableModifier>> ADD_TABLE_MODIFIER = MODIFIERS.register("add_loot_table", AddTableModifier.Serializer::new);
}
