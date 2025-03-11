package dev.ultreon.bubbles;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import dev.ultreon.bubbles.gamemode.TimedMode;
import dev.ultreon.bubbles.notification.Notification;
import dev.ultreon.libs.translations.v1.LanguageManager;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A class that handles Discord Rich Presence integration. The Discord IPC client is used to send
 * Rich Presence updates to Discord.
 */
public class DiscordRPC implements RpcHandler {
    /**
     * The current game activity.
     */
    private GameActivity activity;

    /**
     * A flag that indicates whether the Rich Presence has been updated since the last update.
     */
    private boolean updated;
    private IPCClient client;

    public DiscordRPC() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.client.close();
            } catch (Throwable ignored) {
                // Ignored
            }
        }));
    }

    /**
     * Starts the DiscordRPC class, which initializes the Discord IPC client and sets up the IPC listener
     * for the client.
     */
    @Override
    public void start() {
        // Initialize the Discord IPC client with the application ID.
        this.client = new IPCClient(933147296311427144L);

        // Sets up the IPC listener for the Discord IPC client. This method is called when the client is ready to start
        // sending Rich Presence updates to Discord.
        this.client.setListener(new IPCListener() {
            /**
             * Event handler for when the IPC client is ready to send Rich Presence updates. Sets up a scheduled
             * task to update the Rich Presence every 200 milliseconds. Additionally, set up a shutdown hook to
             * cancel the scheduled task and close the IPC client when the client is about to shut down.
             *
             * @param client the IPC client
             */
            @Override
            public void onReady(IPCClient client) {
                BubbleBlaster.getLogger().info("Discord RPC is ready!");

                // Create a Rich Presence builder and set the initial state and details
                var builder = new RichPresence.Builder();
                var game = BubbleBlaster.getInstance();
                DiscordRPC.this.activity(builder, game);

                // Send the initial Rich Presence
                client.sendRichPresence(builder.build());

                // Schedule a task to update the Rich Presence every 200 milliseconds
                var scheduledFuture = BubbleBlaster.getInstance().schedulerService.scheduleWithFixedDelay(() -> this.update(client), 0, 200, TimeUnit.MILLISECONDS);
                // Set up a shutdown hook to cancel the scheduled task and close the IPC client
                Runtime.getRuntime().addShutdownHook(new Thread(() -> scheduledFuture.cancel(false)));
            }
            /**
             * Updates the Discord Rich Presence with the current game activity.
             *
             * @param client The IPC client used to send the Rich Presence.
             */
            private void update(IPCClient client) {
                // Only update if the Rich Presence has been updated since the last update.
                if (!DiscordRPC.this.updated) {
                    return;
                }

                // Update flag to false to prevent multiple updates.
                DiscordRPC.this.updated = false;

                var builder = new RichPresence.Builder();

                // Determine the game activity and set the appropriate Rich Presence details.
                DiscordRPC.this.activity(builder, BubbleBlaster.getInstance());

                // Send the updated Rich Presence to the Discord client.
                client.sendRichPresence(builder.build());

            }
            /**
             * Called when a user requests to join the game.
             *
             * @param client The IPC client.
             * @param secret The secret to connect to the server.
             * @param user The user who requested the join.
             */
            @Override
            public void onActivityJoinRequest(IPCClient client, String secret, User user) {
                if (user.isBot()) {
                    // This should never happen, but still funny to add!
                    BubbleBlaster.getInstance().notifications.notify(Notification.builder("Discord RPC", "Beep boop, a robot requested to join!").subText("discordrpc").build());
                    return;
                }

                // Add notification with user's name and discriminator
                var name = user.getName();
                var discriminator = user.getDiscriminator();
                var notificationMessage = "Join request received from " + name + "#" + discriminator + "!";
                BubbleBlaster.getInstance().notifications.notify(Notification.builder("Discord RPC", notificationMessage).subText("discordrpc").build());
            }
            /**
             * Called when a user has accepted the join request and this client should connect to the server.
             *
             * @param client The IPC client.
             * @param secret The secret to connect to the server.
             */
            @Override
            public void onActivityJoin(IPCClient client, String secret) {
                IPCListener.super.onActivityJoin(client, secret);

                // Check if the secret is valid
                if (!secret.matches("[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+):\\d+")) {
                    BubbleBlaster.getInstance().notifications.notify(Notification.builder("Discord RPC", "Invalid join request!").subText("discordrpc").build());
                }
            }

            /**
             * Called when the client is disconnected from Discord.
             *
             * @param client The IPC client.
             * @param t      The cause of the disconnection. Maybe null.
             */
            @Override
            public void onDisconnect(IPCClient client, Throwable t) {
                if (!BubbleBlaster.getInstance().isRunning()) return;
                BubbleBlaster.getInstance().notifications.notify(Notification.builder("Discord RPC", "Disconnected from Discord!").subText("discordrpc").build());
            }

            /**
             * Called when a 'spectate' request is received.
             *
             * @param client The IPC client.
             * @param secret The secret of the 'spectate' request.
             */
            @Override
            public void onActivitySpectate(IPCClient client, String secret) {
                BubbleBlaster.getInstance().notifications.notify(Notification.builder("Discord RPC", "Spectate request received, wait what?").subText("discordrpc").build());
            }

            /**
             * Called when a packet is received.
             *
             * @param client The IPC client.
             * @param packet The received packet.
             */
            @Override
            public void onPacketReceived(IPCClient client, Packet packet) {
                IPCListener.super.onPacketReceived(client, packet);

                if (packet.getOp() == Packet.OpCode.CLOSE) {
                    BubbleBlaster.getLogger().info("Discord RPC disconnected!");
                    client.close();
                }
            }
        });

        CompletableFuture.runAsync(() -> {
            try {
                this.client.connect();
            } catch (NoDiscordClientException e) {
                BubbleBlaster.getInstance().notifications.notify(Notification.builder("Discord RPC", "Unable to connect to Discord!").subText("discordrpc").build());
                BubbleBlaster.getLogger().error("Unable to connect to Discord", e);
                try {
                    this.client.close();
                } catch (Throwable ignored) {
                    // Ignored
                }
            } catch (Throwable e) {
                BubbleBlaster.getLogger().error("Unable to connect to Discord", e);
                try {
                    this.client.close();
                } catch (Throwable ignored) {
                    // Ignored
                }
            }
        });
    }

    private boolean activity(RichPresence.Builder builder, BubbleBlaster quantumClient) {
        if (this.activity == null) { // Loading
            builder.setState("Loading...")
                    .setDetails("Version: " + BubbleBlaster.getGameVersion())
                    .setStartTimestamp(OffsetDateTime.now());
            return false;
        }
        if (this.activity.equals(GameActivity.PLAYING)) {
            var player = quantumClient.player;
            if (player == null) return true;
            var world = quantumClient.world;
            if (world == null) return true;
            var now = OffsetDateTime.now();
            builder.setState("Playing " + LanguageManager.INSTANCE.get(Locale.ENGLISH).get(world.getGamemode().getTranslationId()))
                    .setDetails("Level " + player.getLevel())
                    .setStartTimestamp(now)
                    .setLargeImage("icon", "Version: " + BubbleBlaster.getGameVersion());

            if (world.getGamemode() instanceof TimedMode) {
                var timedMode = (TimedMode) world.getGamemode();
                builder.setEndTimestamp(OffsetDateTime.ofInstant(Instant.EPOCH.plus(timedMode.getEndTime(), ChronoUnit.MILLIS), now.getOffset()));
            }
        } else if (this.activity.equals(GameActivity.MENUS)) {
            builder.setState("In the menus")
                    .setDetails("Version: " + BubbleBlaster.getGameVersion())
                    .setStartTimestamp(OffsetDateTime.now())
                    .setLargeImage("icon", "Version: " + BubbleBlaster.getGameVersion());
        } else if (this.activity.equals(GameActivity.LOADING)) {
            builder.setState("Loading...")
                    .setDetails("Version: " + BubbleBlaster.getGameVersion())
                    .setStartTimestamp(OffsetDateTime.now());
        } else {
            builder.setState("Nowhere to be found")
                    .setDetails("Seems like RPC hits its limits...")
                    .setStartTimestamp(OffsetDateTime.now());
        }
        return false;
    }

    @Override
    public void close() {
        try {
            this.client.close();
        } catch (Throwable ignored) {
            // Ignored
        }
    }

    @Override
    public void setActivity(GameActivity newActivity) {
        this.activity = newActivity;
        this.updated = true;
    }
}
