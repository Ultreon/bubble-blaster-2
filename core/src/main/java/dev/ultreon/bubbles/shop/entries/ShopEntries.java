package dev.ultreon.bubbles.shop.entries;

import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.init.GameplayEvents;
import dev.ultreon.bubbles.init.StatusEffects;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.shop.CoinContainer;
import dev.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

public class ShopEntries {
    public static final ShopEffect SPEED = ShopEntries.register("speed", new ShopEffect(new CoinContainer(1, 0), StatusEffects.SWIFTNESS, 10));
    public static final ShopExecute BOOST = ShopEntries.register("boost_refill", new ShopExecute("Boost Refill", new CoinContainer(0, 2), (player) -> {
        player.boostRefillTimer = -1;
    }));

    public static final ShopUpgrade SPEED_UPGRADE = ShopEntries.register("speed_upgrade", new ShopUpgrade(new CoinContainer(0, 4), Attribute.SPEED, 3, 5));
    public static final ShopUpgrade DEFENSE_UPGRADE = ShopEntries.register("defense_upgrade", new ShopUpgrade(new CoinContainer(0, 3), Attribute.DEFENSE, 0.5, 5));

    public static final ShopExecute GOLDEN_SPAWN = ShopEntries.register("golden_spawn", new ShopExecute("Golden Spawn", new CoinContainer(5, 0), (player) -> {
        player.getWorld().beginEvent(GameplayEvents.GOLDEN_SPAWN_EVENT);
    }));

    public static final ShopExecute BLOOD_MOON_CANCEL = ShopEntries.register("blood_moon_cancel", new ShopExecute("Blood Moon Cancel", new CoinContainer(5, 0), (player) -> {
        player.getWorld().endEvent(GameplayEvents.BLOOD_MOON_EVENT);
    }));

    public static final ShopExecute POP_3_BUBBLES = ShopEntries.register("pop_3_bubbles", new ShopExecute("Pop 3 Bubbles", new CoinContainer(0, 3), (player) -> {
        player.getWorld().popBubbles(3, player);
    }));

    public static final ShopExecute POP_5_BUBBLES = ShopEntries.register("pop_5_bubbles", new ShopExecute("Pop 5 Bubbles", new CoinContainer(0, 5), (player) -> {
        player.getWorld().popBubbles(5, player);
    }));

    public static final ShopExecute POP_10_BUBBLES = ShopEntries.register("pop_10_bubbles", new ShopExecute("Pop 10 Bubbles", new CoinContainer(0, 10), (player) -> {
        player.getWorld().popBubbles(10, player);
    }));

    private static <T extends ShopEntry> T register(String name, T shopEntry) {
        Registries.SHOP_ENTRIES.register(new Identifier(name), shopEntry);
        return shopEntry;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
