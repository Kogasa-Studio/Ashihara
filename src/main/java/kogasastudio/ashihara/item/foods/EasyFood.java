package kogasastudio.ashihara.item.foods;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import static kogasastudio.ashihara.Ashihara.MATERIALS;

public class EasyFood extends Item
{
    public EasyFood(int hunger)
    {
        super
        (
            new Item.Properties().tab(MATERIALS)
            .food(new FoodProperties.Builder().nutrition(hunger).build())
        );
    }
}