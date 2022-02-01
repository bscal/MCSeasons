package me.bscal.seasons.common.seasons;

import io.netty.buffer.Unpooled;
import me.bscal.seasons.Seasons;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;

public class SeasonTimer extends PersistentState
{
	private static SeasonTimer Instance = null;

	public static final Identifier CHANNEL_NAME = new Identifier(Seasons.MOD_ID, "season_sync");
	public static final String STATE_NAME = Seasons.MOD_ID + ":season_timer";
	private static final int SIZE_OF = 28;

	private long m_TotalTicks, m_CurrentTicks;
	private int m_Day, m_Month, m_Year;
	private int m_SeasonTrackerId;
	private int m_DaysInCurrentSeason;
	private long m_LastTick;
	private boolean m_SeasonChanged;
	private final PacketByteBuf m_CachedBuffer;

	SeasonTimer()
	{
		var world = Seasons.Instance.getOverWorld();
		if (world.isPresent() && !world.get().isClient)
		{
			// Loads persistent state and sets Instance
			m_CachedBuffer = new PacketByteBuf(Unpooled.buffer(SIZE_OF));
			world.get().getPersistentStateManager().getOrCreate((nbt) -> {
				this.m_TotalTicks = nbt.getLong("TotalTicks");
				this.m_CurrentTicks = nbt.getLong("CurrentTicks");
				this.m_Day = nbt.getInt("Day");
				this.m_Month = nbt.getInt("Month");
				this.m_Year = nbt.getInt("Year");
				this.m_SeasonTrackerId = nbt.getInt("SeasonTrackerId");
				this.m_DaysInCurrentSeason = nbt.getInt("DaysInCurrentSeason");
				return this;
			}, () -> this, STATE_NAME);
		}
		else // ClientSide is only used to store values all times are managed by server and send to clients.
		{
			m_CachedBuffer = null;
		}
		Instance = this;
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
		nbt.putInt("DaysInCurrentSeason", m_DaysInCurrentSeason);
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
		return SeasonType.FourSeasonPerYear.getSeason(m_SeasonTrackerId);
	}

	public SeasonState getSeason(Identifier biomeId)
	{
		return Seasons.getSettings().getSeasonType(biomeId).getSeason(m_SeasonTrackerId);
	}

	public void setSeason(int seasonTrackerId)
	{
		m_SeasonTrackerId = Math.max(0, Math.min(Seasons.getSettings().Config.MaxSeasons - 1, seasonTrackerId));
		m_DaysInCurrentSeason = 0;
		m_SeasonChanged = true;
		sendToClients();
		markDirty();
	}

	// TODO maybe move this out?
	public void sendToClients()
	{
		if (isClient())
			return;
		m_LastTick = m_TotalTicks;
		m_CachedBuffer.clear();
		m_CachedBuffer.resetWriterIndex();
		m_CachedBuffer.writeLong(m_TotalTicks);				// 8
		m_CachedBuffer.writeLong(m_CurrentTicks);			// 16
		m_CachedBuffer.writeShort(m_Day);					// 18
		m_CachedBuffer.writeShort(m_Month);					// 20
		m_CachedBuffer.writeInt(m_Year);					// 24
		m_CachedBuffer.writeShort(m_DaysInCurrentSeason);	// 26
		m_CachedBuffer.writeByte(m_SeasonTrackerId);		// 27
		m_CachedBuffer.writeBoolean(m_SeasonChanged);		// 28
		for (ServerPlayerEntity player : PlayerLookup.all(Seasons.Instance.getServer()))
			ServerPlayNetworking.send(player, CHANNEL_NAME, m_CachedBuffer);
		m_SeasonChanged = false;
	}

	// TODO this should probably be handled better ._.
	public void readFromServer(long totalTicks, long currentTicks, int day, int month, int year, int daysInCurrentSeason, int seasonalSectionTracker)
	{
		m_TotalTicks = totalTicks;
		m_CurrentTicks = currentTicks;
		m_Day = day;
		m_Month = month;
		m_Year = year;
		m_DaysInCurrentSeason = daysInCurrentSeason;
		m_SeasonTrackerId = seasonalSectionTracker;
	}

	public void setTimeOfDay(long timeOfDay)
	{
		// checks if minecraft timeOfDay has ticked over to a new day
		long diff = timeOfDay - (timeOfDay < m_CurrentTicks ? m_CurrentTicks - Seasons.getSettings().Config.TicksPerDay : m_CurrentTicks);
		addTicks(diff);
	}

	public void addDays(int days)
	{
		if (isClient())
			return;
		m_TotalTicks += (long)days * Seasons.getSettings().Config.TicksPerDay;
		m_Day += days;
		int addedMonths = days / Seasons.getSettings().Config.DaysPerMonth;
		m_Month += addedMonths;
		m_Year += addedMonths / Seasons.getSettings().Config.MonthsPerYear;
		tryNextSeason();
		sendToClients();
		markDirty();
	}

	public void addTicks(long ticks)
	{
		if (isClient())
			return;
		if (Seasons.getSettings().DebugMode)
			logDebug(ticks);
		m_TotalTicks += ticks;
		m_CurrentTicks += ticks;
		if (m_CurrentTicks >= Seasons.getSettings().Config.TicksPerDay)
		{
			m_CurrentTicks -= Seasons.getSettings().Config.TicksPerDay;
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
		if (m_Day++ >= Seasons.getSettings().Config.DaysPerMonth)
		{
			m_Day = 0;
			if (m_Month++ >= Seasons.getSettings().Config.MonthsPerYear)
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

		if (m_DaysInCurrentSeason >= Seasons.getSettings().Config.DaysPerSeason)
		{
			int newSeasonId = m_SeasonTrackerId + 1;
			if (newSeasonId >= Seasons.getSettings().Config.MaxSeasons)
				newSeasonId = 0;
			setSeason(newSeasonId);
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