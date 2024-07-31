package kogasastudio.ashihara.item.foods;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class EasyFood extends Item
{
    public EasyFood(int hunger)
    {
        super(new Item.Properties().food(new FoodProperties.Builder().nutrition(hunger).saturationModifier(1).build()));
    }
}