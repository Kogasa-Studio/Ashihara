package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

//import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class CuttingBoardTE extends AshiharaMachineTE
{
    private ItemStack content = ItemStack.EMPTY;
    private final RecipeManager.CachedCheck<RecipeWrapper, CuttingBoardRecipe> quickCheck;

    public CuttingBoardTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.CUTTING_BOARD_TE.get(), pos, state);
        this.quickCheck = RecipeManager.createCheck(RecipeTypes.CUTTING_BOARD.get());
    }

    public ItemStack getContent()
    {
        return this.content.copy();
    }

    public Optional<RecipeHolder<CuttingBoardRecipe>> tryMatchRecipe(RecipeWrapper wrapper)
    {
        if (this.level == null) return Optional.empty();

        return this.quickCheck.getRecipeFor(wrapper, this.level);
    }

    public void cut(CuttingBoardRecipe recipe)
    {
        if (this.level == null) return;
        SoundEvent event = SoundEvents.AXE_STRIP;
        this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (this.level.isClientSide())
        {
            RandomSource random = this.level.getRandom();
            ParticleOptions data = this.content.getItem() instanceof BlockItem
                    ? new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) this.content.getItem()).getBlock().defaultBlockState())
                    : new ItemParticleOption(ParticleTypes.ITEM, this.content);
            for (int i = 0; i < 10; i += 1)
            {
                this.level.addParticle
                (
                    data,
                    (double) this.worldPosition.getX() + 0.5D,
                    (double) this.worldPosition.getY() + 0.7D,
                    (double) this.worldPosition.getZ() + 0.5D,
                    ((double) random.nextFloat() - 0.5D) * 0.2D,
                    ((double) random.nextFloat() - 0.5D) * 0.2D,
                    ((double) random.nextFloat() - 0.5D) * 0.2D
                );
            }
        }
        for (int i = 0; i < this.content.getCount(); i += 1)
        {
            for (ItemStack stack : recipe.getOutput())
            {
                ItemEntity entity = new ItemEntity
                        (this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d, stack.copy());
                entity.setDefaultPickUpDelay();
                this.level.addFreshEntity(entity);
            }
        }
        this.content = ItemStack.EMPTY;
        setChanged();
    }

    public boolean handleInteraction(Player playerIn, InteractionHand handIn, Level worldIn, BlockPos posIn)
    {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!this.content.isEmpty())
        {
            if (stack.isEmpty())
            {
                playerIn.setItemInHand(handIn, this.content);
                this.content = ItemStack.EMPTY;
                worldIn.playSound(playerIn, posIn, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
                setChanged();
                return true;
            } else
            {
                Optional<RecipeHolder<CuttingBoardRecipe>> recipe = tryMatchRecipe(new RecipeWrapper(new ItemStackHandler(NonNullList.of(this.content))));
//                LOGGER_MAIN.info
//                (
//                    "\n{\n    inv: " + inv.serializeNBT()
//                    + ";\n    recipe: " + (recipe.isPresent() ? recipe.get().getInfo() : "not provided")
//                    + ";\n    tool: " + (recipe.isPresent() ? recipe.get().getTool().getName() : "not provided")
//                    + ";\n    tool_matches: " + (recipe.isPresent() ? recipe.get().getTool().toolMatches(stack) : "not provided")
//                    + ";\n}"
//                );
                if (recipe.isPresent() && recipe.get().value().getTool().toolMatches(stack))
                {
                    this.cut(recipe.get().value());
                    if (!playerIn.isCreative())
                        stack.hurtAndBreak(1, playerIn, stack.getEquipmentSlot());
                    return true;
                }
            }
        } else
        {
            this.content = stack.split(Math.min(stack.getCount(), 4));
            worldIn.playSound(playerIn, posIn, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
    {
        super.loadAdditional(nbt, registries);
        this.content = ItemStack.parseOptional(registries, nbt.getCompound("content"));
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries)
    {
        compound.put("content", this.content.save(registries));
        super.saveAdditional(compound, registries);
    }
}
