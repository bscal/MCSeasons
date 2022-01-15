package me.bscal.seasons.common.utils;

import net.minecraft.util.Identifier;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public final class IdentifierSerializer implements TypeSerializer<Identifier>
{
	public static final IdentifierSerializer Instance = new IdentifierSerializer();

	private static final String ID = "id";

	private IdentifierSerializer() {}

	private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
		if (!source.hasChild(path)) {
			throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
		}
		return source.node(path);
	}

	@Override
	public Identifier deserialize(Type type, ConfigurationNode node) throws SerializationException
	{
		String id = nonVirtualNode(node, ID).getString();
		return Identifier.tryParse(id);
	}

	@Override
	public void serialize(Type type, Identifier obj, ConfigurationNode node) throws SerializationException
	{
		node.node(ID).set(obj.toString());
	}
}
