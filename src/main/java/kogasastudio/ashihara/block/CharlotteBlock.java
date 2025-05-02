package kogasastudio.ashihara.block;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.tileentities.CharlotteTE;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.utils.json.JsonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CharlotteBlock extends Block implements EntityBlock
{
    public CharlotteBlock()
    {
        super
        (
            BlockBehaviour.Properties.of()
            .noOcclusion()
            .strength(1.0F)
            .mapColor(DyeColor.PINK)
            .sound(SoundType.WOOL)
        );
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {return 1.0F;}

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());}

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.is(ItemRegistryHandler.RICE.asItem()) && level.isClientSide())
        {
            CharlotteTE te = (CharlotteTE) level.getBlockEntity(pos);
            if (te != null)
            {
                if (te.toolTipController == null) te.init(player);
                if (stack.getCount() == 1)
                {
                    te.switchRender(player);
                }
                if (stack.getCount() > 1)
                {
                    te.reScale(stack.getCount() * 8, stack.getCount() * 4, player);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        if (stack.is(ItemRegistryHandler.CHISEL.asItem()) && level.isClientSide())
        {
            final DynamicOps<JsonElement> dynamicOps = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, level.registryAccess()), ICondition.IContext.EMPTY);
            MortarRecipe recipe = new MortarRecipe
            (
                ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "chick"),
                NonNullList.of(SizedIngredient.of(ItemStack.EMPTY.getItem(), 1), SizedIngredient.of(ItemTags.WOLF_FOOD, 4)),
                NonNullList.of(ItemStack.EMPTY, ItemRegistryHandler.RICE.toStack(), ItemRegistryHandler.RICE.toStack(7)),
                new FluidStack(Fluids.WATER.getSource(), 1000),
                7,
                new ConcurrentLinkedQueue<>(List.of(MortarTE.MortarToolType.PESTLE, MortarTE.MortarToolType.HAND, MortarTE.MortarToolType.OTSUCHI))
            );
            JsonElement element = MortarRecipe.CODEC.encodeStart(dynamicOps, recipe).getOrThrow(msg -> new RuntimeException("Failed to encode %s: %s".formatted("test/test_recipe.json", msg)));
            try
            {
                JsonUtils.writeToJson(JsonUtils.INSTANCE.pretty, Path.of("test/test_recipe.json"), element.getAsJsonObject());
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CharlotteTE(pos, state);
    }
}
