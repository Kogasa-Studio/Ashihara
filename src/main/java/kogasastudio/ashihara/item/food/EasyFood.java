package kogasastudio.ashihara.item.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;

public class EasyFood extends Item
{
    public EasyFood(int hunger, Item.Properties properties)
    {
        super(properties.food(new FoodProperties.Builder().nutrition(hunger).saturationModifier(1).build(), Consumables.DEFAULT_FOOD));
    }

    public EasyFood(int hunger)
    {
        this(hunger, new Item.Properties());
    }
}