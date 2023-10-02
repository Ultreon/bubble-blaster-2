package com.ultreon.bubbles.command;

import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.libs.commons.v0.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class SpawnCommand implements CommandExecutor {
    private World world;

    @Override
    public boolean execute(Player player, String[] args) {
        /*if (args.length == 1) {
            if (args[0].equals("clear")) {
                if (player.getActiveBubbles().isEmpty()) {
                    player.sendMessage("No bubble are active");
                    return true;
                }

                int amount = player.getActiveBubbles().size();

                //noinspection unchecked
                for (Bubble<?> bubble : (HashSet<Bubble<?>>) player.getActiveBubbles().clone()) {
                    player.removeBubble(bubble);
                }

                player.sendMessage("Removed " + amount + " bubbles.");

                return true;
            }
        } else */
        if (args.length >= 2) {
            if (args[0].equals("spawn")) {
                BubbleType bubble;
                try {
                    bubble = Registries.BUBBLES.getValue(Identifier.parse(args[1]));
                } catch (InvalidValueException exception) {
                    player.sendMessage("Invalid key: " + args[1]);
                    return false;
                }

                if (bubble == null) {
                    player.sendMessage("Bubble with key ‘" + args[1] + "’ was not found.");
                    return true;
                }

                String[] jsonParts = ArrayUtils.subarray(args, 1, args.length);
                String json = StringUtils.join(jsonParts, ' ');

                try {
//                    Objects.requireNonNull(BubbleBlaster.getInstance().getWorld()).spawnEntityFromState(BsonDocument.parse(json));
//                    SNBTUtil.fromSNBT(json); // TODO: Allow deserializing a string to UBO data.
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("Couldn't spawn entity.");
                }
                return true;
            }
        }/* else if (args.length == 3) {
            if (args[0].equals("spawn")) {
                int radius;
                try {
                    radius = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Invalid number for duration: ‘" + args[2] + "’");
                    return false;
                }

                AbstractBubble bubble;
                try {
                    bubble = Registry.getRegistry(AbstractBubble.class).get(ResourceLocation.fromString(args[1]));
                } catch (InvalidValueException exception) {
                    player.sendMessage("Invalid key: " + args[1]);
                    return false;
                }

                if (bubble == null) {
                    player.sendMessage("Bubble with key ‘" + args[1] + "’ was not found.");
                    return true;
                }
                try {
                    player.getGameType().spawnBubble(radius, null, bubble);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    player.sendMessage("Illegal Access: " + e.getLocalizedMessage());
                }
                return true;
            }
        } else if (args.length == 4) {
            if (args[0].equals("spawn")) {
                int radius;
                try {
                    radius = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Invalid number for radius: ‘" + args[2] + "’");
                    return false;
                }

                double speed;
                try {
                    speed = Double.parseDouble(args[3]);
                } catch (NumberFormatException exception) {
                    try {
                        speed = Integer.parseInt(args[3]);
                    } catch (NumberFormatException exception1) {
                        player.sendMessage("Invalid number for strength: ‘" + args[3] + "’");
                        return false;
                    }
                }

                AbstractBubble bubble;
                try {
                    bubble = Registry.getRegistry(AbstractBubble.class).get(ResourceLocation.fromString(args[1]));
                } catch (InvalidValueException exception) {
                    player.sendMessage("Invalid key: " + args[1]);
                    return false;
                }

                if (bubble == null) {
                    player.sendMessage("Bubble with key ‘" + args[1] + "’ was not found.");
                    return true;
                }

                try {
                    player.getGameType().spawnBubble(radius, speed, bubble);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    player.sendMessage("Illegal Access: " + e.getLocalizedMessage());
                }
                return true;
            }
        }*/

//        player.sendMessage("Usage: /bubble clear");
        player.sendMessage("Usage: /bubble spawn <bubble:key> [radius:int] [speed:double]");

        return false;
    }

    public World getWorld() {
        return this.world;
    }
}
