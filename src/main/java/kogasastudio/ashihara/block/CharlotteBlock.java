package kogasastudio.ashihara.block;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.CharlotteBE;
import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.interaction.HeatLevel;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.interaction.recipes.PotRecipe;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.utils.json.JsonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CharlotteBlock extends Block implements EntityBlock
{
    public CharlotteBlock(BlockBehaviour.Properties properties)
    {
        super(properties);
    }

    public CharlotteBlock()
    {
        this
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
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.is(Items.RICE.asItem()) && level.isClientSide())
        {
            CharlotteBE te = (CharlotteBE) level.getBlockEntity(pos);
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
                return InteractionResult.SUCCESS;
            }
        }
        if (stack.is(Items.CUCUMBER.asItem()) && level.isClientSide())
        {
            final DynamicOps<JsonElement> dynamicOps = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, level.registryAccess()), ICondition.IContext.EMPTY);
            if (stack.count() == 1)
            {
                MortarRecipe recipe = new MortarRecipe
                (
                    Identifier.fromNamespaceAndPath(Ashihara.MODID, "chick"),
                    NonNullList.of(SizedIngredient.of(net.minecraft.world.item.Items.ACACIA_BOAT, 1), new SizedIngredient(Ingredient.of(level.registryAccess().getOrThrow(ItemTags.WOLF_FOOD)), 4), SizedIngredient.of(Items.KOISHI, 1)),
                    NonNullList.of(ItemStackTemplate.fromNonEmptyStack(net.minecraft.world.item.Items.ACACIA_BOAT.getDefaultInstance()), ItemStackTemplate.fromNonEmptyStack(Items.RICE.toStack()), ItemStackTemplate.fromNonEmptyStack(Items.RICE.toStack(7))),
                    Optional.of(new FluidStackTemplate(Fluids.WATER.getSource(), 1000)),
                    0,
                    new ConcurrentLinkedQueue<>(List.of(MortarBE.MortarToolType.PESTLE, MortarBE.MortarToolType.HAND, MortarBE.MortarToolType.OTSUCHI))
                );
                JsonElement element = MortarRecipe.MAP_CODEC.codec().encodeStart(dynamicOps, recipe).getOrThrow(msg -> new RuntimeException("Failed to encode %s: %s".formatted("test/test_recipe.json", msg)));
                try
                {
                    JsonUtils.writeToJson(JsonUtils.INSTANCE.pretty, Path.of("test/test_recipe.json"), element.getAsJsonObject());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            if (stack.getCount() == 2)
            {
                PotRecipe potRecipe = new PotRecipe
                (
                    Identifier.fromNamespaceAndPath(Ashihara.MODID, "test"),
                    NonNullList.of(SizedIngredient.of(Items.RICE, 1), SizedIngredient.of(net.minecraft.world.item.Items.COBBLESTONE, 1)),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(new FluidStackTemplate(Fluids.LAVA.getSource(), 100)),
                    Optional.of(HeatLevel.HIGH),
                    100,
                    0);
                JsonElement element = PotRecipe.MAP_CODEC.codec().encodeStart(dynamicOps, potRecipe).getOrThrow(msg -> new RuntimeException("Failed to encode %s: %s".formatted("test/test_pot_recipe.json", msg)));
                try
                {
                    JsonUtils.writeToJson(JsonUtils.INSTANCE.pretty, Path.of("test/test_pot_recipe.json"), element.getAsJsonObject());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CharlotteBE(pos, state);
    }
}
