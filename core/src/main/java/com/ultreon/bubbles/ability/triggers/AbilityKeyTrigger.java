package com.ultreon.bubbles.ability.triggers;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.ability.AbilityTrigger;
import com.ultreon.bubbles.entity.player.ability.AbilityTriggerType;
import com.ultreon.bubbles.input.DesktopInput;

import java.util.HashMap;

/**
 * Keyboard key trigger.
 */
public class AbilityKeyTrigger extends AbilityTrigger {
    private final int keyCode;
    private final int scanCode;
    private final int modifiers;

    /**
     * Constructor for the trigger.
     * @param keyCode the key code that was used to trigger it.
     * @param scanCode the key's scan code.
     * @param modifiers the keyboard modifiers.
     * @param entity the entity that triggered it.
     */
    public AbilityKeyTrigger(int keyCode, int scanCode, int modifiers, Entity entity) {
        super(AbilityTriggerType.KEY_TRIGGER, entity);

        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public int getScanCode() {
        return this.scanCode;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public boolean isCtrlPressed() {
        return DesktopInput.isCtrlDown();
    }

    public boolean isAltPressed() {
        return DesktopInput.isAltDown();
    }

    public boolean isShiftPressed() {
        return DesktopInput.isShiftDown();
    }

    public boolean isDown(int keyCode) {
        return DesktopInput.isKeyDown(keyCode);
    }

    @Deprecated
    public HashMap<Integer, Boolean> getCurrentlyPressed() {
        return new HashMap<>();
    }
}
