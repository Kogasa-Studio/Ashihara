package kogasastudio.ashihara.block.furniture;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Implemented by FurnitureComponents that need custom data persisted
 * per ComponentStateDefinition instance (not per component class).
 */
public interface ICustomData
{
    /** Write the custom data of a specific definition instance. */
    void serializeCustom(ValueOutput output, Object customData);

    /** Read and reconstruct custom data. Returns the deserialized object. */
    Object deserializeCustom(ValueInput input);
}