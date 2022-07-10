package me.bscal.seasons.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

// TODO some networking stuff to sync noise seed with players, and seasonal events
public final class SeasonPackets
{

    private SeasonPackets() {}

    public static PacketByteBuf writeAutumnUpdateS2C(int seed)
    {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(seed);
        return buf;
    }

    public static int readAutumnUpdateS2C(PacketByteBuf buffer)
    {
        return buffer.readInt();
    }

    public static PacketByteBuf writeSeasonEventS2C()
    {
        return null;
    }

    public static void readSeasonEventClient(PacketByteBuf buffer)
    {

    }

}
