package me.bscal.seasons.common.seasons;

import me.bscal.seasons.Seasons;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SeasonClockItem extends Item
{

    public static final Item SEASON_CLOCK = new SeasonClockItem();

    public SeasonClockItem()
    {
        super(new Item.Settings());

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (!world.isClient)
        {
            var timer = SeasonTimer.get();
            var season = timer.CurrentSeason;
            var daysPerSeason = Seasons.GetDaysPerSeason();
            var daysIntoSeason = timer.DayInSeason;
            var text = Text.translatable("seasons.text.season_info", season, daysIntoSeason, daysPerSeason);
            user.sendMessage(text);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public static void register()
    {
        Registry.register(Registries.ITEM, new Identifier(Seasons.MOD_ID, "season_clock"), SEASON_CLOCK);
        ModelPredicateProviderRegistry.register(SEASON_CLOCK, new Identifier(Seasons.MOD_ID, "time"), (itemStack, clientWorld, livingEntity, seed) ->
        {
            if (livingEntity == null) return 0.0f;
            return SeasonTimer.get().getProgressInYear();
        });
    }
}
