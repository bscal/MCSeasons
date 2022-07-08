package me.bscal.seasons.common.seasons;

import io.netty.buffer.Unpooled;
import me.bscal.seasons.Seasons;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class SeasonTimer extends PersistentState
{

    private static SeasonTimer Instance;

    public static final Identifier CHANNEL_NAME = new Identifier(Seasons.MOD_ID, "season_sync");
    public static final String STATE_NAME = Seasons.MOD_ID + ":season_timer";
    private static final int SIZE_OF = 24;

    private long m_TotalTicks;
    private long m_CurrentTicks;
    transient private long m_LastSyncedTick;
    private int m_Day;
    private int m_InternalSeasonId;
    private int m_DaysInCurrentSeason;
    transient private boolean m_SeasonChanged;
    transient private final PacketByteBuf m_CachedBuffer;
    transient private final World m_World;

    public SeasonTimer(World world)
    {
        assert world != null: "world parameter cannot be null";
        m_World = world;
        if (!world.isClient && m_World instanceof ServerWorld serverWorld)
        {
            // Loads persistent state and sets Instance
            m_CachedBuffer = new PacketByteBuf(Unpooled.buffer(SIZE_OF));
            Instance = serverWorld.getPersistentStateManager().getOrCreate((nbt) ->
            {
                this.m_TotalTicks = nbt.getLong("TotalTicks");
                this.m_CurrentTicks = nbt.getLong("CurrentTicks");
                this.m_Day = nbt.getInt("Day");
                this.m_InternalSeasonId = nbt.getInt("InternalSeasonId");
                this.m_DaysInCurrentSeason = nbt.getInt("DaysInCurrentSeason");
                return this;
            }, () -> this, STATE_NAME);
        }
        else // ClientSide is only used to store values all times are managed by server and send to clients.
        {
            m_CachedBuffer = null;
            Instance = this;
        }
    }

    // Works on both client and server
    public static SeasonTimer get()
    {
        assert Instance != null: "SeasonTimer Instance must not be null";
        return Instance;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putLong("TotalTicks", m_TotalTicks);
        nbt.putLong("CurrentTicks", m_CurrentTicks);
        nbt.putInt("Day", m_Day);
        nbt.putInt("InternalSeasonId", m_InternalSeasonId);
        nbt.putInt("DaysInCurrentSeason", m_DaysInCurrentSeason);
        return nbt;
    }

    public Season getSeason()
    {
        return Season.values()[m_InternalSeasonId];
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
        return m_InternalSeasonId;
    }

    public int getCurrentDay()
    {
        return m_Day;
    }

    public int getDaysInCurrentSeason()
    {
        return m_DaysInCurrentSeason;
    }

    public float getProgressInSeason()
    {
        return (float) m_DaysInCurrentSeason / (float) Seasons.ServerConfig.Settings.DaysPerSeason;
    }

    public float getProgressInYear()
    {
        int daysPerYear = Seasons.ServerConfig.Settings.DaysPerSeason * 4;
        return (float) (m_Day % daysPerYear) / (float) daysPerYear;
    }


    public SeasonDate getDate()
    {
        int days = m_Day % Seasons.ServerConfig.Settings.DaysPerMonth;
        int totalMonths = m_Day / Seasons.ServerConfig.Settings.DaysPerMonth;
        int years = totalMonths / Seasons.ServerConfig.Settings.MonthsPerYear;
        int months = totalMonths % Seasons.ServerConfig.Settings.MonthsPerYear;
        return new SeasonDate(days, months, years);
    }

    public void setSeason(int seasonTrackerId)
    {
        if (m_World.isClient) return;

        m_InternalSeasonId = MathHelper.clamp(seasonTrackerId, 0, Season.MAX_SEASON_ID);
        m_DaysInCurrentSeason = 0;
        m_SeasonChanged = true;
        SeasonWorld seasonWorld = SeasonWorld.getOrCreate((ServerWorld) m_World);
        seasonWorld.updateSeasonalEffects();
        //SeasonCallbacks.ON_SEASON_CHANGED.invoker().onSeasonChanged(Season.values()[m_InternalSeasonId], seasonWorld);
        sendToClients((ServerWorld) m_World);
    }

    // TODO maybe move this out?
    public void sendToClients(ServerWorld world)
    {
        if (world.isClient) return;

        m_CachedBuffer.clear();
        m_CachedBuffer.writeLong(m_TotalTicks);             // 8
        m_CachedBuffer.writeLong(m_CurrentTicks);           // 16
        m_CachedBuffer.writeInt(m_Day);                     // 20
        m_CachedBuffer.writeShort(m_DaysInCurrentSeason);   // 22
        m_CachedBuffer.writeByte(m_InternalSeasonId);       // 23
        m_CachedBuffer.writeBoolean(m_SeasonChanged);       // 24

        for (ServerPlayerEntity player : PlayerLookup.all(Seasons.Instance.getServer()))
            ServerPlayNetworking.send(player, CHANNEL_NAME, m_CachedBuffer);

        m_LastSyncedTick = m_TotalTicks;
        m_SeasonChanged = false;
    }

    // TODO this should probably be handled better ._.
    public void readFromServer(long totalTicks, long currentTicks, int day, int daysInCurrentSeason, int internalSeasonId)
    {
        if (!m_World.isClient) return;

        m_TotalTicks = totalTicks;
        m_CurrentTicks = currentTicks;
        m_Day = day;
        m_DaysInCurrentSeason = daysInCurrentSeason;
        m_InternalSeasonId = internalSeasonId;
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
        if (m_TotalTicks - m_LastSyncedTick > 20)
            sendToClients((ServerWorld)m_World);
        markDirty();
    }

    private void nextDay(int days)
    {
        if (days <= 0) days = 1;

        m_Day += days;
        m_DaysInCurrentSeason += days;
        int daysLeftInSeason = Seasons.ServerConfig.Settings.DaysPerSeason - m_DaysInCurrentSeason;

        SeasonWorld.getOrCreate((ServerWorld)m_World).updateDailyEffects(daysLeftInSeason);
        //SeasonCallbacks.ON_NEW_DAY.invoker().onDayChanged(days, m_Day);
        if (daysLeftInSeason <= 0)
        {
            m_DaysInCurrentSeason = 0;
            int newSeasonId = m_InternalSeasonId + 1;
            if (newSeasonId > Season.MAX_SEASON_ID) newSeasonId = 0;
            setSeason(newSeasonId);
        }
    }
}