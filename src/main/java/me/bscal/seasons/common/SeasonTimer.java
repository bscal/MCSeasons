package me.bscal.seasons.common;

import io.netty.buffer.Unpooled;
import me.bscal.seasons.Seasons;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;

public final class SeasonTimer extends PersistentState
{
	private static SeasonTimer Instance = null;

	public static final Identifier CHANNEL_NAME = new Identifier(Seasons.MOD_ID, "season_sync");
	public static final String STATE_NAME = Seasons.MOD_ID + ":season_timer";
	private static final int SIZE_OF = 8 + 8 + 2 + 2 + 4 + 1;

	private long m_TotalTicks, m_CurrentTicks;
	private int m_Day, m_Month, m_Year;
	private int m_SeasonTrackerId;
	private long m_LastTick;
	private final PacketByteBuf m_CachedBuffer;

	SeasonTimer()
	{
		var world = Seasons.Instance.getOverWorld();
		if (world.isPresent() && !world.get().isClient)
		{
			m_CachedBuffer = new PacketByteBuf(Unpooled.buffer(SIZE_OF));
			Instance = world.get().getPersistentStateManager().get((nbt) -> {
				this.m_TotalTicks = nbt.getLong("TotalTicks");
				this.m_CurrentTicks = nbt.getLong("CurrentTicks");
				this.m_Day = nbt.getInt("Day");
				this.m_Month = nbt.getInt("Month");
				this.m_Year = nbt.getInt("Year");
				this.m_SeasonTrackerId = nbt.getInt("SeasonTrackerId");
				return this;
			}, STATE_NAME);
		}
		else
		{
			m_CachedBuffer = null;
			Instance = this;
		}
	}

	public static SeasonTimer GetOrCreate()
	{
		if (Instance == null)
			Instance = new SeasonTimer();
		return Instance;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		nbt.putLong("TotalTicks", m_TotalTicks);
		nbt.putLong("CurrentTicks", m_CurrentTicks);
		nbt.putInt("Day", m_Day);
		nbt.putInt("Month", m_Month);
		nbt.putInt("Year", m_Year);
		nbt.putInt("SeasonTrackerId", m_SeasonTrackerId);
		return nbt;
	}

	public long getTotalTicks()
	{
		return m_TotalTicks;
	}

	public long getCurrentTicks()
	{
		return m_CurrentTicks;
	}

	public int getSeasonalSectionTracker()
	{
		return m_SeasonTrackerId;
	}

	public SeasonDate getDate()
	{
		return new SeasonDate(m_Day, m_Month, m_Year);
	}

	public SeasonState getGenericSeason()
	{
		return SeasonTypes.FourSeasonPerYear.getSeason(m_SeasonTrackerId);
	}

	public SeasonState getSeason(Identifier biomeId)
	{
		return SeasonSettings.getSeasonType(biomeId).getSeason(m_SeasonTrackerId);
	}

	public void setSeason(int seasonTrackerId)
	{
		m_SeasonTrackerId = Math.max(0, Math.min(SeasonSettings.MaxSeasons - 1, seasonTrackerId));
	}

	// TODO maybe move this out?
	public void sendToClients()
	{
		if (isClient())
			return;
		m_LastTick = m_TotalTicks;
		m_CachedBuffer.clear();
		m_CachedBuffer.resetWriterIndex();
		m_CachedBuffer.writeLong(m_TotalTicks);
		m_CachedBuffer.writeLong(m_CurrentTicks);
		m_CachedBuffer.writeShort(m_Day);
		m_CachedBuffer.writeShort(m_Month);
		m_CachedBuffer.writeInt(m_Year);
		m_CachedBuffer.writeByte(m_SeasonTrackerId);
		for (ServerPlayerEntity player : PlayerLookup.all(Seasons.Instance.getServer()))
			ServerPlayNetworking.send(player, CHANNEL_NAME, m_CachedBuffer);
	}

	public void readFromServer(long totalTicks, long currentTicks, int day, int month, int year, int seasonalSectionTracker)
	{
		m_TotalTicks = totalTicks;
		m_CurrentTicks = currentTicks;
		m_Day = day;
		m_Month = month;
		m_Year = year;
		m_SeasonTrackerId = seasonalSectionTracker;
	}

	public void setTimeOfDay(long timeOfDay)
	{
		// checks if minecraft timeOfDay has ticked over to a new day
		long diff = timeOfDay - (timeOfDay < m_CurrentTicks ? m_CurrentTicks - SeasonSettings.TicksPerDay : m_CurrentTicks);
		addTicks(diff);
	}

	public void addTicks(long ticks)
	{
		if (isClient())
			return;
		if (SeasonSettings.DebugMode)
			logDebug(ticks);
		m_TotalTicks += ticks;
		m_CurrentTicks += ticks;
		if (m_CurrentTicks >= SeasonSettings.TicksPerDay)
		{
			m_CurrentTicks -= SeasonSettings.TicksPerDay;
			nextDay();
		}
		if (m_CurrentTicks < 0)
			m_CurrentTicks = 0;
		if (m_TotalTicks - m_LastTick > 20)
			sendToClients();
		markDirty();
	}

	private void nextDay()
	{
		if (isClient())
			return;
		if (m_Day++ >= SeasonSettings.DaysPerMonth)
		{
			m_Day = 0;
			if (m_Month++ >= SeasonSettings.MonthsPerYear)
			{
				m_Month = 0;
				m_Year++;
			}
			tryNextSeason();
		}
		sendToClients();
	}

	private void tryNextSeason()
	{
		if (isClient())
			return;
		int newSeason = Math.max(0, Math.min(SeasonSettings.MaxSeasons - 1, m_Month / SeasonSettings.MonthsPerSeason));
		if (newSeason != m_SeasonTrackerId)
		{
			m_SeasonTrackerId = newSeason;
			// TODO possible event or handle a new season.
		}
	}

	private void logDebug(long addedTick)
	{
		Seasons.LOGGER.info(String.format("""
				Adding tick: %d\s
				-> TotalTicks %d | CurrentTick %d | SeasonId %d\s
				-> D/M/Y %d/%d/%d
				""", addedTick, m_TotalTicks, m_LastTick, m_SeasonTrackerId, m_Day, m_Month, m_Year));
	}

	private boolean isClient()
	{
		return Seasons.Instance.getServer().getOverworld().isClient;
	}

}