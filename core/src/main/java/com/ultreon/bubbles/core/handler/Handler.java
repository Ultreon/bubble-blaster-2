package com.ultreon.bubbles.core.handler;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;

import java.util.LinkedList;

/**
 * @author XyperCode
 */
public class Handler {
    final LinkedList<Entity> object = new LinkedList<>();

    /**
     * @author XyperCode
     */
    @SuppressWarnings("EmptyMethod")
    public void tick() {
//        for (int i = 0; i < object.size(); i++) {
//            GameObject tempObject = object.get(i);
//            tempObject.tick();
//        }
    }

    /**
     * @author XyperCode
     */
    @SuppressWarnings("EmptyMethod")
    public void render() {
//        for (int i = 0; i  < object.size(); i++) {
//            GameObject tempObject = object.get(i);
//
//            tempObject.render(g);
//        }
    }

    /**
     * @author XyperCode
     */
    public void addObject(Entity object) {
        this.object.add(object);
    }

    public void removeObject(Entity object) {
        this.object.remove(object);
    }

    public void clearEnemies() {
        for (int i = 0; i < object.size(); i++) {
            Entity tempObject = object.get(i);

            if (tempObject instanceof Player) {
                object.clear();
                addObject(tempObject);
            }
        }
    }
}
