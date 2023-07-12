package kogasastudio.ashihara.datagen;

import com.google.common.hash.HashCode;
import kogasastudio.ashihara.datagen.recipes.MillRecipes;
import kogasastudio.ashihara.datagen.recipes.ModGenRecipes;
import kogasastudio.ashihara.datagen.recipes.CuttingBoardRecipes;
import kogasastudio.ashihara.datagen.recipes.MortarRecipes;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author DustW
 **/
public class TutRecipes extends RecipeProvider
{

    public TutRecipes(PackOutput packOutputIn)
    {
        super(packOutputIn);
    }

    protected List<ModGenRecipes> recipes = new ArrayList<>();

    protected void addCustomRecipes()
    {
        recipes.add(new CuttingBoardRecipes());
        recipes.add(new MillRecipes());
        recipes.add(new MortarRecipes());
    }

    /*@Override
    public CompletableFuture<?> run(CachedOutput pCache)
    {
        recipes.forEach(recipes ->
                        {
                            recipes.getRecipes().forEach((name, entry) -> save(pCache, name, entry));
                        });

        return super.run(pCache);
    }

    protected void save(CachedOutput pCache, ResourceLocation name, Map.Entry<String, String> entry)
    {
        String json = entry.getKey();
        String subPath = entry.getValue();

        Path path = this.getOutputFolder();

        saveRecipe(pCache, json, path.resolve("data/" + name.getNamespace() + "/recipes/" + subPath + "/" + name.getPath() + ".json"));
    }

    private static void saveRecipe(CachedOutput pCache, String recipe, Path pPath)
    {
        try
        {
            String s1 = SHA1.hashUnencodedChars(recipe).toString();
            if (!Objects.equals(pCache.getHash(pPath), s1) || !Files.exists(pPath))
            {
                Files.createDirectories(pPath.getParent());
                BufferedWriter bufferedwriter = Files.newBufferedWriter(pPath);

                try
                {
                    bufferedwriter.write(recipe);
                } catch (Throwable throwable1)
                {
                    try
                    {
                        bufferedwriter.close();
                    } catch (Throwable throwable)
                    {
                        throwable1.addSuppressed(throwable);
                    }

                    throw throwable1;
                }

                bufferedwriter.close();
            }

            pCache.writeIfNeeded(pPath, null, HashCode.fromInt(recipe.hashCode())).putNew(pPath, s1);
        } catch (IOException ignored) {}
    }*/

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        addCustomRecipes();
    }
}
