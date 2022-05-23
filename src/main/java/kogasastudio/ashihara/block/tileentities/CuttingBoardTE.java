package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.interaction.recipe.CuttingBoardRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;
import java.util.Random;

//import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class CuttingBoardTE extends AshiharaMachineTE
{
    private ItemStack content = ItemStack.EMPTY;

    public CuttingBoardTE(BlockPos pos, BlockState state) {
        super(TERegistryHandler.CUTTING_BOARD_TE.get(), pos, state);
    }

    public ItemStack getContent() {return this.content.copy();}

    public Optional<CuttingBoardRecipe> tryMatchRecipe(RecipeWrapper wrapper)
    {
        if(this.level == null) return Optional.empty();

        return this.level.getRecipeManager().getRecipeFor(CuttingBoardRecipe.TYPE, wrapper, this.level);
    }

    public void cut(CuttingBoardRecipe recipe)
    {
        if (this.level == null) return;
        SoundEvent event = SoundEvents.AXE_STRIP;
        this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (this.level.isClientSide())
        {
            Random random = this.level.getRandom();
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
            }
            else
            {
                ItemStackHandler inv = new ItemStackHandler(NonNullList.withSize(1, this.content));
                Optional<CuttingBoardRecipe> recipe = tryMatchRecipe(new RecipeWrapper(inv));
//                LOGGER_MAIN.info
//                (
//                    "\n{\n    inv: " + inv.serializeNBT()
//                    + ";\n    recipe: " + (recipe.isPresent() ? recipe.get().getInfo() : "not provided")
//                    + ";\n    tool: " + (recipe.isPresent() ? recipe.get().getTool().getName() : "not provided")
//                    + ";\n    tool_matches: " + (recipe.isPresent() ? recipe.get().getTool().toolMatches(stack) : "not provided")
//                    + ";\n}"
//                );
                if (recipe.isPresent() && recipe.get().getTool().toolMatches(stack))
                {
                    this.cut(recipe.get());
                    if (!playerIn.isCreative()) stack.hurtAndBreak(1, playerIn, player -> player.broadcastBreakEvent(handIn));
                    return true;
                }
            }
        }
        else
        {
            this.content = stack.split(Math.min(stack.getCount(), 4));
            worldIn.playSound(playerIn, posIn, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.content = ItemStack.of(nbt.getCompound("content"));
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.put("content", this.content.save(new CompoundTag()));
        super.saveAdditional(compound);
    }
}
