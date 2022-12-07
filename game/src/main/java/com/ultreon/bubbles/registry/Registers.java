package com.ultreon.bubbles.registry;

import com.google.common.annotations.Beta;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.effect.StatusEffect;
import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.init.*;
import com.ultreon.bubbles.item.ItemType;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.bubbles.render.screen.gui.cursor.RegistrableCursor;

/**
 * Registers, use this class to create deferred registers for specific things in the game.
 *
 * @see DelayedRegister
 */
@SuppressWarnings({"rawtypes"})
public class Registers {
    /**
     * Ammo type register.
     * Ammo types are what it says ammo types, you can create different ammo types doing different things.
     *
     * @see AmmoTypes
     */
    public static final Registry<AmmoType> AMMO_TYPES = Registry.create(AmmoType.class, new Identifier("AmmoTypes"));

    /**
     * Entity register.
     * Entities are one create the core things in a game, they are used for creating the player, enemies or in this case also bubbles.
     *
     * @see Entities
     */
    public static final Registry<EntityType> ENTITIES = Registry.create(EntityType.class, new Identifier("Entities"));

    /**
     * Bubble register.
     * Bubbles are the core mechanic create the game, without the bubbles the game shouldn't exist.
     *
     * @see Bubbles
     */
    public static final Registry<BubbleType> BUBBLES = Registry.create(BubbleType.class, new Identifier("Bubbles"));

    /**
     * Effect register.
     * Effects are classes for doing things over time after activating, and can stop after some time.
     *
     * @see Effects
     */
    public static final Registry<StatusEffect> EFFECTS = Registry.create(StatusEffect.class, new Identifier("Effects"));

    /**
     * Ability register.
     * Abilities are classes for doing things like teleportation.
     *
     * @see Abilities
     */
    public static final Registry<AbilityType> ABILITIES = Registry.create(AbilityType.class, new Identifier("Abilities"));

    /**
     * Game state register.
     *
     * @see GameplayEvents
     */
    public static final Registry<GameplayEvent> GAME_EVENTS = Registry.create(GameplayEvent.class, new Identifier("GameStates"));

    /**
     * Game type register.
     *
     * @see Gamemodes
     */
    public static final Registry<Gamemode> GAME_TYPES = Registry.create(Gamemode.class, new Identifier("GameTypes"));

    /**
     * Cursor register.
     */
    public static final Registry<RegistrableCursor> CURSORS = Registry.create(RegistrableCursor.class, new Identifier("Cursors"));

    /**
     * Items register, will be used in the future.
     */
    @Beta
    public static final Registry<ItemType> ITEMS = Registry.create(ItemType.class, new Identifier("Items"));

    /**
     * Texture collection register.
     * Used for mapping and preloading textures.
     *
     * @see TextureCollections
     */
    public static final Registry<TextureCollection> TEXTURE_COLLECTIONS = Registry.create(TextureCollection.class, new Identifier("TextureCollections"));
}
