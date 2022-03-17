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
import net.minecraft.world.PersistentState;

import java.util.Optional;

public class SeasonTimer extends PersistentState
{
	private static SeasonTimer Instance = null;

	public static final Identifier CHANNEL_NAME = new Identifier(Seasons.MOD_ID, "season_sync");
	public static final String STATE_NAME = Seasons.MOD_ID + ":season_timer";
	private static final int SIZE_OF = 28;

	private long m_TotalTicks, m_CurrentTicks;
	transient private long m_LastTick;
	private int m_Day, m_Month, m_Year;
	private int m_InternalSeasonId;
	private int m_DaysInCurrentSeason;
	transient private boolean m_SeasonChanged;
	transient private final PacketByteBuf m_CachedBuffer;
	transient private final ServerWorld m_World;

	SeasonTimer()
	{
		Instance = this;
		Optional<ServerWorld> world = Seasons.Instance.getOverWorld();
		if (world.isPresent() && !world.get().isClient)
		{
			m_World = world.get();
			// Loads persistent state and sets Instance
			m_CachedBuffer = new PacketByteBuf(Unpooled.buffer(SIZE_OF));
			m_World.getPersistentStateManager().getOrCreate((nbt) -> {
				this.m_TotalTicks = nbt.getLong("TotalTicks");
				this.m_CurrentTicks = nbt.getLong("CurrentTicks");
				this.m_Day = nbt.getInt("Day");
				this.m_Month = nbt.getInt("Month");
				this.m_Year = nbt.getInt("Year");
				this.m_InternalSeasonId = nbt.getInt("InternalSeasonId");
				this.m_DaysInCurrentSeason = nbt.getInt("DaysInCurrentSeason");
				return this;
			}, () -> this, STATE_NAME);
		}
		else // ClientSide is only used to store values all times are managed by server and send to clients.
		{
			m_CachedBuffer = null;
			m_World = null;
		}
	}

	public static SeasonTimer GetOrCreate()
	{
		if (Instance == null) Instance = new SeasonTimer();
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
		nbt.putInt("InternalSeasonId", m_InternalSeasonId);
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

	public int getInternalSeasonId()
	{
		return m_InternalSeasonId;
	}

	public SeasonDate getDate()
	{
		int days = m_Day % Seasons.getSettings().Config.DaysPerMonth;
		int months = m_Day / Seasons.getSettings().Config.DaysPerMonth;
		int years = months / Seasons.getSettings().Config.MonthsPerYear;
		return new SeasonDate(days, months, years);
	}

	public Season getGenericSeason()
	{
		return SeasonalType.FourSeasonPerYear.getSeason(m_InternalSeasonId);
	}

	public Season getSeason(Identifier biomeId)
	{
		return Seasons.getSettings().getSeasonType(biomeId).getSeason(m_InternalSeasonId);
	}

	public void setSeason(int seasonTrackerId)
	{
		m_InternalSeasonId = Math.max(0, Math.min(Seasons.getSettings().Config.MaxSeasons - 1, seasonTrackerId));
		m_DaysInCurrentSeason = 0;
		m_SeasonChanged = true;
		sendToClients();
		markDirty();
	}

	// TODO maybe move this out?
	public void sendToClients()
	{
		if (isClient()) return;
		m_LastTick = m_TotalTicks;
		m_CachedBuffer.clear();
		m_CachedBuffer.resetWriterIndex();
		m_CachedBuffer.writeLong(m_TotalTicks);                // 8
		m_CachedBuffer.writeLong(m_CurrentTicks);            // 16
		m_CachedBuffer.writeShort(m_Day);                    // 18
		m_CachedBuffer.writeShort(m_Month);                    // 20
		m_CachedBuffer.writeInt(m_Year);                    // 24
		m_CachedBuffer.writeShort(m_DaysInCurrentSeason);    // 26
		m_CachedBuffer.writeByte(m_InternalSeasonId);        // 27
		m_CachedBuffer.writeBoolean(m_SeasonChanged);        // 28
		for (ServerPlayerEntity player : PlayerLookup.all(Seasons.Instance.getServer()))
			ServerPlayNetworking.send(player, CHANNEL_NAME, m_CachedBuffer);
		m_SeasonChanged = false;
	}

	// TODO this should probably be handled better ._.
	public void readFromServer(long totalTicks, long currentTicks, int day, int month, int year, int daysInCurrentSeason, int internalSeasonId)
	{
		m_TotalTicks = totalTicks;
		m_CurrentTicks = currentTicks;
		m_Day = day;
		m_Month = month;
		m_Year = year;
		m_DaysInCurrentSeason = daysInCurrentSeason;
		m_InternalSeasonId = internalSeasonId;
	}

	public void addDays(int days)
	{
		if (isClient()) return;
		m_TotalTicks += (long) days * Seasons.getSettings().Config.TicksPerDay;
		nextDay(days);
		markDirty();
	}

	public void updateTime()
	{
		if (isClient()) return;

		long timeOfDay = m_World.getTimeOfDay();
		long diff = timeOfDay - m_CurrentTicks;
		// Wraps the diff values in case we go into another day. 23000 -> 1000 would be a 2000 diff.
		if (diff < 0) diff += Seasons.getSettings().Config.TicksPerDay;
		// Since time wraps at 24000, any values less than current ticks would be a new day
		boolean newDay = timeOfDay < m_CurrentTicks;

		logDebug(diff);

		m_TotalTicks += diff;
		m_CurrentTicks = timeOfDay;

		if (newDay) nextDay(1);
		if (m_TotalTicks - m_LastTick > 20) sendToClients();
		else markDirty();
	}

	private void nextDay(int days)
	{
		m_Day += days;
		m_DaysInCurrentSeason += days;
		int daysPerSeason = Seasons.getSettings().Config.DaysPerSeason;
		if (m_DaysInCurrentSeason >= daysPerSeason)
		{
			m_DaysInCurrentSeason -= daysPerSeason;
			int newSeasonId = m_InternalSeasonId + 1;
			if (newSeasonId >= Seasons.getSettings().Config.MaxSeasons) newSeasonId = 0;
			setSeason(newSeasonId);
		}
		sendToClients();
	}

	private void logDebug(long addedTick)
	{
		Seasons.LOGGER.info(String.format(
             """
			*** Season Debug Info ***
			\tAddedTicks = %d
			\tTotalTicks = %d
			\tCurrentTicks = %d
			\tDate = %d / %d / %d
			\tInternalSeasonId = %d
			\tDaysInCurrentSeason = %d
			""", addedTick, m_TotalTicks, m_CurrentTicks, m_Day, m_Month, m_Year, m_InternalSeasonId, m_DaysInCurrentSeason));
	}

	private boolean isClient()
	{
		return Seasons.Instance.getOverWorld().isEmpty() || Seasons.Instance.getOverWorld().get().isClient;
	}

}