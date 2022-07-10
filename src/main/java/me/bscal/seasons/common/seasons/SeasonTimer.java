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


    private long m_TotalTicks;
    private long m_CurrentTicks;
    private transient long m_LastSyncedTick;
    private int m_Day;
    private Season m_InternalSeason;
    private int m_DaysInCurrentSeason;
    private int m_DaysPerSeason;
    private int m_DaysPerYear;
    private transient boolean m_SeasonChanged;
    private transient final PacketByteBuf m_CachedBuffer;
    private transient final World m_World;

    public SeasonTimer(ClientWorld clientWorld)
    {
        assert clientWorld != null : "world parameter cannot be null";
        m_World = clientWorld;
        m_CachedBuffer = null;
        m_InternalSeason = Season.Spring;
        Instance = this;
    }

    public SeasonTimer(ServerWorld serverWorld, Config.ServerSettings settings)
    {
        assert serverWorld != null : "world parameter cannot be null";
        m_World = serverWorld;
        m_CachedBuffer = new PacketByteBuf(Unpooled.buffer(SIZE_OF));
        m_InternalSeason = Season.Spring;
        Instance = serverWorld.getPersistentStateManager().getOrCreate((nbt) ->
        {
            m_TotalTicks = nbt.getLong("TotalTicks");
            m_CurrentTicks = nbt.getLong("CurrentTicks");
            m_Day = nbt.getInt("Day");
            m_InternalSeason = Season.values()[nbt.getInt("InternalSeasonId")];
            m_DaysInCurrentSeason = nbt.getInt("DaysInCurrentSeason");
            return this;
        }, () -> this, STATE_NAME);
        loadConfigValues(settings);
    }

    public void loadConfigValues(Config.ServerSettings settings)
    {
        if (!m_World.isClient)
        {
            assert settings != null : "Settings cannot be null";
            m_DaysPerSeason = settings.MonthsPerSeason * settings.DaysPerMonth;
            m_DaysPerYear = settings.DaysPerMonth * settings.MonthsPerYear;
        }
    }

    // Works on both client and server
    public static SeasonTimer get()
    {
        assert Instance != null : "SeasonTimer Instance must not be null";
        return Instance;
    }

    public Season getSeason()
    {
        return m_InternalSeason;
    }

    public long getTotalTicks()
    {
        return m_TotalTicks;
    }

    public long getCurrentTicks()
    {
        return m_CurrentTicks;
    }

    public int getInternalSeasonId()
    {
        return m_InternalSeason.ordinal();
    }

    public int getCurrentDay()
    {
        return m_Day;
    }

    public int getDaysInCurrentSeason()
    {
        return m_DaysInCurrentSeason;
    }

    public int getDaysPerSeason()
    {
        return m_DaysPerSeason;
    }

    public float getProgressInSeason()
    {
        return (float) m_DaysInCurrentSeason / m_DaysPerSeason;
    }

    public float getProgressInYear()
    {
        return (float) m_Day % (float) m_DaysPerYear / (float) m_DaysPerYear;
    }

    public SeasonDate getDate()
    {
        int days = m_Day % Seasons.ServerConfig.Settings.DaysPerMonth;
        int totalMonths = m_Day / Seasons.ServerConfig.Settings.DaysPerMonth;
        int years = totalMonths / Seasons.ServerConfig.Settings.MonthsPerYear;
        int months = totalMonths % Seasons.ServerConfig.Settings.MonthsPerYear;
        return new SeasonDate(days, months, years);
    }

    public void setSeason(Season season, int daysToRemainInNewSeason)
    {
        if (m_World.isClient) return;

        m_InternalSeason = season;
        m_DaysInCurrentSeason = daysToRemainInNewSeason;
        m_SeasonChanged = true;
        // TODO disabled until work
        //SeasonWorld seasonWorld = SeasonWorld.getOrCreate((ServerWorld) m_World);
        //seasonWorld.updateSeasonalEffects();
        //SeasonCallbacks.ON_SEASON_CHANGED.invoker().onSeasonChanged(Season.values()[m_InternalSeasonId], seasonWorld);
        sendToClients((ServerWorld) m_World);
    }

    public void addDays(int days)
    {
        if (m_World.isClient) return;

        m_TotalTicks += (long) days * Seasons.ServerConfig.Settings.TicksPerDay;
        nextDay(days);
        markDirty();
    }

    public void updateTime()
    {
        if (m_World.isClient) return;

        long timeOfDay = m_World.getTimeOfDay();
        long diff = timeOfDay - m_CurrentTicks;

        int ticksPerDay = Seasons.ServerConfig.Settings.TicksPerDay;
        if (diff < 0) diff = ticksPerDay;

        int days = (int) (diff / ticksPerDay);
        boolean newDay = days > 0;

        m_TotalTicks += diff;
        m_CurrentTicks = timeOfDay;

        if (newDay) nextDay(days);
        if (m_TotalTicks - m_LastSyncedTick > TICKS_PER_SYNC)
            sendToClients((ServerWorld) m_World);
        markDirty();
    }

    private void nextDay(int days)
    {
        if (days <= 0) days = 1;

        m_Day += days;
        m_DaysInCurrentSeason += days;

        //SeasonWorld.getOrCreate((ServerWorld) m_World).updateDailyEffects(daysLeftInSeason);
        //SeasonCallbacks.ON_NEW_DAY.invoker().onDayChanged(days, m_Day);

        int newSeasonId = m_InternalSeason.ordinal();
        boolean newSeason = false;
        while (m_DaysInCurrentSeason > m_DaysPerSeason)
        {
            m_DaysInCurrentSeason -= m_DaysPerSeason;
            newSeason = true;
            ++newSeasonId;
            if (newSeasonId > Season.MAX_SEASON_ID) newSeasonId = 0;
        }
        if (newSeason) setSeason(Season.values()[newSeasonId], m_DaysInCurrentSeason);
    }

    // TODO maybe move this out?
    public void sendToClients(ServerWorld world)
    {
        if (world.isClient) return;

        m_CachedBuffer.clear();
        m_CachedBuffer.writeLong(m_TotalTicks);                 // 8
        m_CachedBuffer.writeLong(m_CurrentTicks);               // 16
        m_CachedBuffer.writeInt(m_Day);                         // 20
        m_CachedBuffer.writeShort(m_DaysInCurrentSeason);       // 22
        m_CachedBuffer.writeByte(m_InternalSeason.ordinal());   // 23
        m_CachedBuffer.writeBoolean(m_SeasonChanged);           // 24

        for (ServerPlayerEntity player : PlayerLookup.all(Seasons.Instance.getServer()))
            ServerPlayNetworking.send(player, CHANNEL_NAME, m_CachedBuffer);

        m_LastSyncedTick = m_TotalTicks;
        m_SeasonChanged = false;
    }

    // TODO this should probably be handled better ._.
    public void readFromServer(long totalTicks, long currentTicks, int day, int daysInCurrentSeason, Season internalSeason)
    {
        m_TotalTicks = totalTicks;
        m_CurrentTicks = currentTicks;
        m_Day = day;
        m_DaysInCurrentSeason = daysInCurrentSeason;
        m_InternalSeason = internalSeason;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putLong("TotalTicks", m_TotalTicks);
        nbt.putLong("CurrentTicks", m_CurrentTicks);
        nbt.putInt("Day", m_Day);
        nbt.putInt("InternalSeasonId", m_InternalSeason.ordinal());
        nbt.putInt("DaysInCurrentSeason", m_DaysInCurrentSeason);
        return nbt;
    }
}