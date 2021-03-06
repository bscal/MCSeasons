package me.bscal.seasons.common;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.utils.IdentifierSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.File;

public class Config<T>
{

    public CommentedConfigurationNode Root;
    public T Settings;
    private final File m_File;
    private final ConfigurationOptions m_Options;

    public Config(T settings, File file, ConfigurationOptions options)
    {
        Settings = settings;
        m_File = file;
        m_Options = options;
    }

    public void load()
    {
        var loader = HoconConfigurationLoader.builder().defaultOptions(m_Options).file(m_File).build();
        try
        {
            Root = loader.load();
            if (Settings != null)
            {
                Settings = (T) Root.get(Settings.getClass());
            }
            loader.save(Root);
        } catch (ConfigurateException e)
        {
            Seasons.LOGGER.error(e);
        }
    }

    public void save()
    {
        var loader = HoconConfigurationLoader.builder().defaultOptions(m_Options).file(m_File).build();
        try
        {
            Root.set(Settings.getClass(), Settings);
            loader.save(Root);
        } catch (ConfigurateException e)
        {
            Seasons.LOGGER.error(e);
        }
    }

    public static Config<ServerSettings> initServerConfig()
    {
        var file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "seasons/server-config.conf");
        var options = ConfigurationOptions.defaults()
                .serializers(builder -> builder.register(Identifier.class, IdentifierSerializer.Instance))
                .shouldCopyDefaults(true);
        var config = new Config<>(new ServerSettings(), file, options);
        config.load();
        return config;
    }

    public static Config<ClientSettings> initClientConfig()
    {
        var file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "seasons/client-config.conf");
        var options = ConfigurationOptions.defaults()
                .serializers(builder -> builder.register(Identifier.class, IdentifierSerializer.Instance))
                .shouldCopyDefaults(true);
        var config = new Config<>(new ClientSettings(), file, options);
        config.load();
        return config;
    }

    @ConfigSerializable
    public static class ServerSettings
    {
        @Setting("DaysPerMonth")
        @Comment("Minecraft day is 20 minutes. Default is 10 (3.3 irl hours)")
        public int DaysPerMonth = 10;
        @Setting("MonthsPerYear")
        @Comment("Default: 12")
        public int MonthsPerYear = 12;
        @Setting("MonthsPerSeason")
        @Comment("Default: 3")
        public int MonthsPerSeason = 3;
        @Setting("TicksPerDay")
        @Comment("You should not change this value unless you know what your doing. Minecraft's day is 24000 ticks")
        public int TicksPerDay = 24000;
    }

    @ConfigSerializable
    public static class ClientSettings
    {
        @Setting("GraphicsLevel")
        @Comment("Fancy - Fancy fall leaves, Fast - Basic fall leaves, Disabled - No fall leaves")
        public SeasonsGraphicsLevel GraphicsLevel = SeasonsGraphicsLevel.Fancy;
        @Setting("EnableSeasonalColors")
        public boolean EnableSeasonalColors = true;
        @Setting("EnableFallsColors")
        @Comment("Enables random fall leave colors")
        public boolean EnableFallColors = true;
        @Setting("LeafFallDistance")
        @Comment("0 - Disables")
        public int LeafFallDistance = 32;
    }

    public enum SeasonsGraphicsLevel
    {
        Disabled, Fast, Fancy
    }

}
