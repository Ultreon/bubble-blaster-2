package dev.ultreon.bubbles.shop.entries;

import dev.ultreon.bubbles.effect.StatusEffect;
import dev.ultreon.bubbles.effect.StatusEffectInstance;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.random.valuesource.ConstantValueSource;
import dev.ultreon.bubbles.random.valuesource.ValueSource;
import dev.ultreon.bubbles.shop.CoinContainer;
import dev.ultreon.bubbles.util.RomanNumbers;

import static dev.ultreon.bubbles.BubbleBlaster.TPS;

public class ShopEffect extends ShopEntry {
    private final StatusEffect effect;
    private final ValueSource duration;
    private final ValueSource strength;

    public ShopEffect(CoinContainer price, StatusEffect effect, long duration) {
        this(price, effect, ConstantValueSource.of(duration));
    }

    public ShopEffect(CoinContainer price, StatusEffect effect, ValueSource duration) {
        this(price, effect, duration, ConstantValueSource.of(1));
    }

    public ShopEffect(CoinContainer price, StatusEffect effect, ValueSource duration, ValueSource strength) {
        super(price);
        this.effect = effect;
        this.duration = duration;
        this.strength = strength;
    }

    @Override
    public void purchase(Player buyer) {
        buyer.addEffect(new StatusEffectInstance(this.effect, (long) this.duration.getValue() * TPS, (int) this.strength.getValue()));
    }

    @Override
    protected String getDisplayName() {
        if (this.strength.getValue() == 1) {
            return this.effect.getTranslationText() + " (" + this.duration.getTranslationText() + " seconds)";
        }
        return this.effect.getTranslationText() + " " + RomanNumbers.toRoman((int) this.strength.getValue()) + " (" + this .duration.getTranslationText() + " seconds)";
    }

    public ValueSource getDuration() {
        return this.duration;
    }
}
