package com.ultreon.bubbles.registry;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.google.common.annotations.Beta;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.effect.StatusEffect;
import com.ultreon.bubbles.entity.EntityFlag;
import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.item.ItemType;
import com.ultreon.bubbles.media.SoundEvent;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.Registry;



public class Registries {
    public static final Registry<AmmoType> AMMO_TYPES = Registry.create(new Identifier("ammo"));
    public static final Registry<EntityType<?>> ENTITIES = Registry.create(new Identifier("entity"));
    public static final Registry<BubbleType> BUBBLES = Registry.create(new Identifier("bubble"));
    public static final Registry<StatusEffect> EFFECTS = Registry.create(new Identifier("status_effect"));
    public static final Registry<AbilityType<?>> ABILITIES = Registry.create(new Identifier("ability"));
    public static final Registry<GameplayEvent> GAMEPLAY_EVENTS = Registry.create(new Identifier("gameplay_event"));
    public static final Registry<BitmapFont> BITMAP_FONTS = Registry.create(new Identifier("font"));
    public static final Registry<Gamemode> GAMEMODES = Registry.create(new Identifier("gamemode"));
    public static final Registry<Cursor> CURSORS = Registry.create(new Identifier("cursor"));
    @Beta
    public static final Registry<ItemType> ITEMS = Registry.create(new Identifier("item"));
    public static final Registry<TextureCollection> TEXTURE_COLLECTIONS = Registry.create(new Identifier("texture_collection"));
    public static final Registry<SoundEvent> SOUNDS = Registry.create(new Identifier("sound"));
    public static final Registry<EntityFlag> ENTITY_FLAGS = Registry.create(new Identifier("entity_flag"));
}
