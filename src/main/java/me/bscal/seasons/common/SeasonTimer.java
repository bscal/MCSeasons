package me.bscal.seasons.common;

import io.netty.buffer.Unpooled;
import me.bscal.seasons.Seasons;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

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

	SeasonTimer(World world)
	{
		if (!world.isClient())
		{
			m_CachedBuffer = new PacketByteBuf(Unpooled.buffer(SIZE_OF));
			ServerWorld serverWorld = (ServerWorld) world;
			Instance = serverWorld.getPersistentStateManager().get((nbt) -> {
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

	public static SeasonTimer GetOrCreate(World world)
	{
		if (Instance == null)
			Instance = new SeasonTimer(world);
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

	public SeasonState getSeason(final World world, final Biome biome)
	{
		var biomeKey = world.getRegistryManager().get(Registry.BIOME_KEY).getKey(biome);
		return biomeKey.isPresent() ? getSeason(biomeKey.get().getValue()) : getGenericSeason();
	}

	// TODO maybe move this out?
	public void sendToClients()
	{
		if (IsClient())
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

	public void addTicks(long ticks)
	{
		if (IsClient())
			return;
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
	}

	private void nextDay()
	{
		if (IsClient())
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
		if (IsClient())
			return;
		int newSeason = Math.max(0, Math.min(SeasonSettings.MaxSeasons - 1, m_Month / SeasonSettings.MonthsPerSeason));
		if (newSeason != m_SeasonTrackerId)
		{
			m_SeasonTrackerId = newSeason;
			// TODO possible event or handle a new season.
		}
	}

	private boolean IsClient()
	{
		return MinecraftClient.getInstance() != null && MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().world.isClient;
	}

}

record SeasonDate(int Day, int Month, int Year)
{
}
