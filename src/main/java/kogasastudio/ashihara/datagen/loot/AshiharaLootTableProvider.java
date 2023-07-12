package kogasastudio.ashihara.datagen.loot;

import kogasastudio.ashihara.datagen.loot.tables.AshiharaBlockLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;

public class AshiharaLootTableProvider extends LootTableProvider
{
    public AshiharaLootTableProvider(PackOutput output)
    {
        super
        (
                output,
                Collections.emptySet(),
                List.of
                (
                        new SubProviderEntry(AshiharaBlockLootTables::new, LootContextParamSets.BLOCK)
                )
        );
    }
}
