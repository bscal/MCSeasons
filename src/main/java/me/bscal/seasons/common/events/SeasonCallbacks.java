package me.bscal.seasons.common.events;

import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface SeasonCallbacks
{

    Event<DayChangedCallback> ON_NEW_DAY = EventFactory.createArrayBacked(DayChangedCallback.class,
            (listeners) -> (daysProgressed, currentDay) -> {
                for (DayChangedCallback listener : listeners) {
                    ActionResult result = listener.onDayChanged(daysProgressed, currentDay);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    interface DayChangedCallback
    {
        ActionResult onDayChanged(int daysProgressed, int currentDay);
    }

    Event<SeasonChangedCallback> ON_SEASON_CHANGED = EventFactory.createArrayBacked(SeasonChangedCallback.class,
            (listeners) -> (newSeason, seasonWorld) -> {
                for (SeasonChangedCallback listener : listeners) {
                    ActionResult result = listener.onSeasonChanged(newSeason, seasonWorld);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    interface SeasonChangedCallback
    {
        ActionResult onSeasonChanged(Season newSeason, SeasonWorld world);
    }


}
