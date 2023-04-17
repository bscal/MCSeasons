package me.bscal.seasons.common.seasons;

import io.netty.buffer.Unpooled;
import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.Config;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class SeasonTimer extends PersistentState
{

    private static SeasonTimer Instance;

    public static final Identifier CHANNEL_NAME = new Identifier(Seasons.MOD_ID, "season_sync");
    public static final String STATE_NAME = Seasons.MOD_ID + ":season_timer";
    private static final int SIZE_OF = 24;
    private static final int TICKS_PER_SYNC = 60;

    public long TotalTicks;
    public long CurrentTicks;
    public int CurrentDay;
    public int DayInSeason;
    public Season CurrentSeason;

    private transient int m_DaysPerYear;
    private transient long m_LastSyncedTick;
    private transient boolean m_SeasonChanged;
    private transient final PacketByteBuf m_CachedBuffer;
    private transient final World m_World;

    public SeasonTimer(ClientWorld clientWorld)
    {
        assert clientWorld != null : "world parameter cannot be null";
        m_World = clientWorld;
        m_CachedBuffer = null;
        CurrentSeason = Season.Spring;
        Instance = this;
    }

    public SeasonTimer(ServerWorld serverWorld, Config.ServerSettings settings)
    {
        assert serverWorld != null : "world parameter cannot be null";
        m_World = serverWorld;
        m_CachedBuffer = new PacketByteBuf(Unpooled.buffer(SIZE_OF));
        CurrentSeason = Season.Spring;
        Instance = serverWorld.getPersistentStateManager().getOrCreate((nbt) ->
        {
            TotalTicks = nbt.getLong("TotalTicks");
            CurrentTicks = nbt.getLong("CurrentTicks");
            CurrentDay = nbt.getInt("Day");
            CurrentSeason = Season.values()[nbt.getInt("InternalSeasonId")];
            DayInSeason = nbt.getInt("DaysInCurrentSeason");
            return this;
        }, () -> this, STATE_NAME);

        m_DaysPerYear = settings.DaysPerMonth * settings.MonthsPerYear;
    }

    // Works on both client and server
    public static SeasonTimer get()
    {
        assert Instance != null : "SeasonTimer Instance must not be null";
        return Instance;
    }

    public float getProgressInYear()
    {
        assert(m_DaysPerYear > 0);
        return (float) CurrentDay / (float) m_DaysPerYear;
    }

    public SeasonDate getDate()
    {
        int days = CurrentDay % Seasons.ServerConfig.Settings.DaysPerMonth;
        int totalMonths = CurrentDay / Seasons.ServerConfig.Settings.DaysPerMonth;
        int years = totalMonths / Seasons.ServerConfig.Settings.MonthsPerYear;
        int months = totalMonths % Seasons.ServerConfig.Settings.MonthsPerYear;
        return new SeasonDate(days, months, years);
    }

    public void setSeason(Season season, int daysToRemainInNewSeason)
    {
        assert(!m_World.isClient);

        CurrentSeason = season;
        DayInSeason = daysToRemainInNewSeason;
        m_SeasonChanged = true;
        sendToClients((ServerWorld) m_World);
    }

    public void updateTime(long timeOfDay)
    {
        long diff = timeOfDay - CurrentTicks;

        int ticksPerDay = Seasons.ServerConfig.Settings.TicksPerDay;
        if (diff < 0)
            diff = ticksPerDay - Math.abs(diff);

        int days = (int) (diff / ticksPerDay);
        boolean newDay = days > 0;

        TotalTicks += diff;
        CurrentTicks = timeOfDay;

        if (newDay) nextDay(days);
        if (TotalTicks - m_LastSyncedTick > TICKS_PER_SYNC)
            sendToClients((ServerWorld) m_World);
        markDirty();
    }

    private void nextDay(int days)
    {
        assert(days > 0);

        CurrentDay += days;
        DayInSeason += days;

        int newSeasonId = CurrentSeason.ordinal();
        boolean newSeason = false;

        int daysPerSeason = Seasons.GetDaysPerSeason();
        while (DayInSeason > daysPerSeason)
        {
            DayInSeason -= daysPerSeason;
            newSeason = true;
            if (++newSeasonId > Season.MAX_SEASON_ID)
                newSeasonId = 0;
        }
        if (newSeason)
            setSeason(Season.values()[newSeasonId], DayInSeason);
    }

    // TODO maybe move this out?
    public void sendToClients(ServerWorld world)
    {
        if (world.isClient) return;

        m_CachedBuffer.clear();
        m_CachedBuffer.writeLong(TotalTicks);                   // 8
        m_CachedBuffer.writeLong(CurrentTicks);                 // 16
        m_CachedBuffer.writeInt(CurrentDay);                    // 20
        m_CachedBuffer.writeShort(DayInSeason);                 // 22
        m_CachedBuffer.writeByte(CurrentSeason.ordinal());      // 23
        m_CachedBuffer.writeBoolean(m_SeasonChanged);           // 24

        for (ServerPlayerEntity player : PlayerLookup.all(Seasons.Instance.getServer()))
            ServerPlayNetworking.send(player, CHANNEL_NAME, m_CachedBuffer);

        m_LastSyncedTick = TotalTicks;
        m_SeasonChanged = false;
    }

    // TODO this should probably be handled better ._.
    public void readFromServer(long totalTicks, long currentTicks, int day, int daysInCurrentSeason, Season internalSeason)
    {
        TotalTicks = totalTicks;
        CurrentTicks = currentTicks;
        CurrentDay = day;
        DayInSeason = daysInCurrentSeason;
        CurrentSeason = internalSeason;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putLong("TotalTicks", TotalTicks);
        nbt.putLong("CurrentTicks", CurrentTicks);
        nbt.putInt("Day", CurrentDay);
        nbt.putInt("InternalSeasonId", CurrentSeason.ordinal());
        nbt.putInt("DaysInCurrentSeason", DayInSeason);
        return nbt;
    }
}