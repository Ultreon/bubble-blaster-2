package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.render.Renderer;

import java.util.ArrayList;

public abstract class QContainer extends QComponent {
    ArrayList<QComponent> components;

    public abstract void renderComponents(Renderer renderer);

    public QComponent add(QComponent component) {
        this.components.add(component);
        return component;
    }

    public QComponent remove(QComponent component) {
        this.components.remove(component);
        return component;
    }

    public QComponent removeComponent(int index) {
        return this.components.remove(index);
    }

    public QComponent getComponent(int index) {
        return components.get(index);
    }

    public ArrayList<QComponent> getComponents() {
        return components;
    }
}
