package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.registry.BlockEntities;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

//import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class CuttingBoardBE extends AshiharaMachineBE
{
    private ItemStack content = ItemStack.EMPTY;
    private final RecipeManager.CachedCheck<RecipeWrapper, CuttingBoardRecipe> quickCheck;

    public CuttingBoardBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.CUTTING_BOARD_BE.get(), pos, state);
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
            ParticleHelper.spawnItemStackDestruction(this.level, this.content, new Vec3(this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.7d, this.worldPosition.getZ() + 0.5d), 10);
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
        this.content = ItemStack.parse(registries, nbt.getCompound("Content")).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries)
    {
        super.saveAdditional(compound, registries);
        if (!this.content.isEmpty()) compound.put("Content", this.content.save(registries));
    }
}
