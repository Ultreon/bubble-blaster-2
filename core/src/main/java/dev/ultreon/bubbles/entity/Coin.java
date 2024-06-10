package dev.ultreon.bubbles.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.entity.types.EntityType;
import dev.ultreon.bubbles.init.Entities;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.world.World;
import org.jetbrains.annotations.NotNull;

import static dev.ultreon.bubbles.BubbleBlaster.id;

public class Coin extends Entity {
    private Type coinType;

    /**
     * The entity constructor.
     *
     * @param type  the type of entity to use.
     * @param world the world where it would spawn in.
     */
    public Coin(EntityType<?> type, @NotNull World world) {
        super(type, world);

        this.attributes.setBase(Attribute.DEFENSE, 1f);
        this.attributes.setBase(Attribute.ATTACK, 0.75f);
        this.attributes.setBase(Attribute.MAX_HEALTH, 30f);
        this.attributes.setBase(Attribute.SPEED, 60f);

        this.markAsCollidable(Entities.PLAYER);

        this.rotation = -180;
    }

    public Coin(World world) {
        this(Entities.COIN, world);
    }

    @Override
    public void onCollision(Entity other, double deltaTime) {
        super.onCollision(other, deltaTime);

        if (other instanceof Player) {
            ((Player) other).collectCoin(this);
            this.delete();
        }
    }

    @Override
    public void onSpawn(Vector2 pos, World world) {
        super.onSpawn(pos, world);

        int i = this.random.nextInt(0, 5);
        if (i == 0) {
            this.setCoinType(Type.GOLD);
        } else {
            this.setCoinType(Type.SILVER);
        }
    }

    @Override
    public Shape2D getShape() {
        return new Circle(this.pos, this.radius());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.pos.x - 42, this.pos.y - 42, 84, 84);
    }

    @Override
    public float radius() {
        return 42;
    }

    @Override
    public void render(Renderer renderer) {
        renderer.blit(this.getTexture(), this.getBounds());
    }

    private Texture getTexture() {
        return this.game.getTextureManager().getOrLoadTexture(id(String.format("coin/%s", this.getCoinType().name().toLowerCase())));
    }

    public Type getCoinType() {
        return this.coinType;
    }

    public void setCoinType(Type coinType) {
        this.coinType = coinType;
    }

    public enum Type {
        GOLD,
        SILVER
    }
}
