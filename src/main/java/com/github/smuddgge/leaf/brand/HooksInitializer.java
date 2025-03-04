package com.github.smuddgge.leaf.brand;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler;
import com.velocitypowered.proxy.connection.backend.ConfigSessionHandler;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.StateRegistry;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.elytrium.commons.utils.reflection.ReflectionException;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Represents the hook's initializer.
 * Based on Elytrium's VelocityTools.
 */
public final class HooksInitializer {

    /**
     * Used to initialize the brand changer.
     */
    @SuppressWarnings("unchecked")
    public static void init() {
        try {

            // Get the server connection field.
            BrandPluginMessageHook.SERVER_CONNECTION_BACKEND_PLAY_FIELD = MethodHandles
                    .privateLookupIn(BackendPlaySessionHandler.class, MethodHandles.lookup())
                    .findGetter(BackendPlaySessionHandler.class, "serverConn", VelocityServerConnection.class);

            BrandPluginMessageHook.SERVER_CONNECTION_CONFIG_FIELD = MethodHandles
                    .privateLookupIn(ConfigSessionHandler.class, MethodHandles.lookup())
                    .findGetter(ConfigSessionHandler.class, "serverConn", VelocityServerConnection.class);

            MethodHandle versionsField = MethodHandles.privateLookupIn(StateRegistry.PacketRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.PacketRegistry.class, "versions", Map.class);

            MethodHandle packetIdToSupplierField = MethodHandles
                    .privateLookupIn(StateRegistry.PacketRegistry.ProtocolRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.PacketRegistry.ProtocolRegistry.class, "packetIdToSupplier", IntObjectMap.class);

            MethodHandle packetClassToIdField = MethodHandles
                    .privateLookupIn(StateRegistry.PacketRegistry.ProtocolRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.PacketRegistry.ProtocolRegistry.class, "packetClassToId", Object2IntMap.class);

            BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> consumer
                    = HooksInitializer.getConsumer(packetIdToSupplierField, packetClassToIdField);

            MethodHandle clientsideGetter = MethodHandles.privateLookupIn(StateRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.class, "clientbound", StateRegistry.PacketRegistry.class);

            StateRegistry.PacketRegistry playClientside = (StateRegistry.PacketRegistry) clientsideGetter.invokeExact(StateRegistry.PLAY);

            StateRegistry.PacketRegistry configClientside = (StateRegistry.PacketRegistry) clientsideGetter.invokeExact(StateRegistry.CONFIG);

            ((Map<ProtocolVersion, StateRegistry.PacketRegistry.ProtocolRegistry>) versionsField.invokeExact(playClientside)).forEach(consumer);

            ((Map<ProtocolVersion, StateRegistry.PacketRegistry.ProtocolRegistry>) versionsField.invokeExact(configClientside)).forEach(consumer);

        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Used to get the consumer.
     *
     * @param packetIdToSupplierField The packet id to supplier field.
     * @param packetClassToIdField    The packet class to id field.
     * @return The consumer.
     */
    @SuppressWarnings("unchecked")
    private static @NotNull BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> getConsumer(MethodHandle packetIdToSupplierField, MethodHandle packetClassToIdField) {
        BrandPluginMessageHook hook = new BrandPluginMessageHook();

        BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> consumer = (version, registry) -> {
            try {
                IntObjectMap<Supplier<? extends MinecraftPacket>> packetIdToSupplier
                        = (IntObjectMap<Supplier<? extends MinecraftPacket>>) packetIdToSupplierField.invoke(registry);

                Object2IntMap<Class<? extends MinecraftPacket>> packetClassToId
                        = (Object2IntMap<Class<? extends MinecraftPacket>>) packetClassToIdField.invoke(registry);

                int packetId = packetClassToId.getInt(hook.getType());
                packetClassToId.put(hook.getHookClass(), packetId);
                packetIdToSupplier.remove(packetId);
                packetIdToSupplier.put(packetId, hook.getHook());
            } catch (Throwable exception) {
                exception.printStackTrace();
            }
        };
        return consumer;
    }
}
