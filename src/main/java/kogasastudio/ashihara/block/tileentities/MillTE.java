package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.interaction.recipe.MillRecipe;
import kogasastudio.ashihara.inventory.container.MillContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;
import static kogasastudio.ashihara.interaction.recipe.MillRecipes.getRecipes;

public class MillTE extends AshiharaMachineTE implements ITickableTileEntity, INamedContainerProvider
{
    public MillTE() {super(TERegistryHandler.MILL_TE.get());}

    public Inventory input = new Inventory(4);
    public Inventory output = new Inventory(4);
    public byte round; //现在已经转的圈数
    public byte roundTotal; //完成这个配方需要转的总圈数, 由所选配方决定
    public int roundProgress; //现在正在转的这一圈的进度
    public int roundTicks; //每转一圈所需要的时间, 由所选配方决定
    public boolean isWorking;
    private MillRecipe recipe;
    public IIntArray millData = new IIntArray()
    {
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;

        public int get(int index)
        {
            switch (index)
            {
                case 0:return a;
                case 1:return b;
                case 2:return c;
                case 3:return d;
                default:return 0;
            }
        }

        public void set(int index, int value)
        {
            switch (index)
            {
                case 0:a = value;break;
                case 1:b = value;break;
                case 2:c = value;break;
                case 3:d = value;
            }
        }

        public int size () {return 4;}
    };

    private boolean tryMatchRecipe(MillRecipe recipeIn)
    {
        if(world.isRemote) return false;
        return recipeIn.tryApply(input);
    }

    private void applyRecipe(MillRecipe recipeIn)
    {
        round = 0;
        roundProgress = 0;
        roundTotal = recipeIn.round;
        roundTicks = recipeIn.roundTicks;
        recipe = recipeIn;
        isWorking = true;
        markDirty();
        LOGGER_MAIN.info("recipe " + recipeIn.toString() + " applied");
    }

    //结束时调用, 将te重置并生成产出物
    private void finishReciping(MillRecipe recipeIn)
    {
        round = 0;roundTotal = 0;roundProgress = 0;roundTicks = 0;isWorking = false;recipe = null;
        millData.set(0, 0);
        millData.set(1, 0);
        millData.set(2, 0);
        millData.set(3, 0);
        if (recipeIn != null)
        {
            for (ItemStack stack : recipeIn.getPeeledOutput())
            {
                output.addItem(stack);
            }
        }
        markDirty();
    }

    //获取磨石的旋转角度（角度制）
    public int getMillStoneRotation()
    {
        return this.roundTicks != 0 ? 360 * this.roundProgress / this.roundTicks : 0;
    }

    public Inventory getInput() {return input;}
    public Inventory getOutput() {return output;}

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        round = nbt.getByte("round");
        roundTotal = nbt.getByte("roundTotal");
        roundProgress = nbt.getInt("roundProgress");
        roundTicks = nbt.getInt("roundTicks");
        input.read(nbt.getList("input", 10));
        output.read(nbt.getList("output", 10));
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putByte("round", round);
        compound.putByte("roundTotal", roundTotal);
        compound.putInt("roundProgress", roundProgress);
        compound.putInt("roundTicks", roundTicks);
        compound.put("input", input.write());
        compound.put("output", output.write());
        return super.write(compound);
    }

    @Override
    public void tick()
    {
        if (!this.world.isRemote())
        {
            if (!input.isEmpty())
            {
                millData.set(0, round);
                millData.set(1, roundTotal);
                millData.set(2, roundProgress);
                millData.set(3, roundTicks);
                boolean matchAny = false;
                for (MillRecipe recipeIn : getRecipes())
                {
                    if (tryMatchRecipe(recipeIn)) {if (!(recipe == recipeIn)) {applyRecipe(recipeIn);}matchAny = true;break;}
                }
                if (!matchAny) {finishReciping(null);}
                if (isWorking)
                {
                    if (round == roundTotal) {finishReciping(recipe);}
                    else
                    {
                        if (roundProgress == roundTicks) {roundProgress = 0; round += 1;}
                        else {roundProgress += 1;}
                        markDirty();
                    }
                }
            }
            else if (isWorking) finishReciping(null);
            this.sync();
        }
    }

    @Override
    public ITextComponent getDisplayName() {return new TranslationTextComponent("gui." + Ashihara.MODID + ".mill");}

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
    {
        return new MillContainer(p_createMenu_1_, p_createMenu_2_, this.world, this.pos, this.millData);
    }

    private void sync()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("round", this.round);
        nbt.putByte("roundTotal", this.roundTotal);
        nbt.putInt("roundProgress", this.roundProgress);
        nbt.putInt("roundTicks", this.roundTicks);
        nbt.putBoolean("isWorking", this.isWorking);
        SUpdateTileEntityPacket p = new SUpdateTileEntityPacket(this.pos, 1, nbt);
        ((ServerWorld)this.world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(this.pos), false)
        .forEach(k -> k.connection.sendPacket(p));
    }
}
