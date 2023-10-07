package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ObjectList<T> extends ScrollableView implements Iterable<T> {
    private final List<ListEntry<T, ? extends T>> entries = new ArrayList<>();
    private final int entryHeight;
    private final int gap;
    private final Class<?> entryType;
    private final Container listContent;
    private boolean selectable;
    private EntryRenderer<T> entryRenderer;
    private ListEntry<T, ? extends T> selected;
    private final List<SelectHandler<T>> selectHandlers = new ArrayList<>();

    public ObjectList(List<T> items, int entryHeight, int gap, int x, int y, int width, int height) {
        super(new Rectangle(0, 0, width, ObjectList.calculateViewHeight(items, entryHeight, gap)), x, y, width, height);

        this.entryType = items.getClass().getComponentType();
        this.entryHeight = entryHeight;
        this.gap = gap;

        this.listContent = this.add(new Container(x, y, width, this.getViewport().getHeight()) {
            @Override
            public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
                var y = ObjectList.this.getViewport().innerYOffset;
                for (var entry : ObjectList.this.entries) {
                    entry.setPos(ObjectList.this.x, y);
                    entry.setSize(this.width, entryHeight);
                    entry.render(renderer, mouseX, mouseY, deltaTime);
                    y += entryHeight + gap;
                }
            }
        });

        items.forEach(this::addItem);
    }

    @Override
    public void setX(int x) {
        this.listContent.setX(x);
    }

    public void addSelectHandler(SelectHandler<T> handler) {
        this.selectHandlers.add(handler);
    }

    public void removeSelectHandler(SelectHandler<T> handler) {
        this.selectHandlers.remove(handler);
    }

    private static int calculateViewHeight(List<?> entries, int entryHeight, int gap) {
        return entries.size() * (entryHeight + gap) - gap;
    }

    private void recalculateViewport() {
        var viewHeight = ObjectList.calculateViewHeight(this.entries, this.entryHeight, this.gap);
        this.getViewport().setViewportSize(this.width - SCROLLBAR_WIDTH, viewHeight);
        this.listContent.setHeight(viewHeight);
    }

    public void setEntryRenderer(EntryRenderer<T> renderer) {
        this.entryRenderer = renderer;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public ListEntry<T, ? extends T> getSelected() {
        return this.selected;
    }

    public void setSelected(ListEntry<T, ? extends T> selected) {
        this.selected = selected;
    }

    public Class<?> getEntryType() {
        return this.entryType;
    }

    @CanIgnoreReturnValue
    public <C extends T> ListEntry<T, C> addItem(C item) {
        var entry = new ListEntry<>(this, item, 0, (int) (this.getViewport().getViewportSize().y + this.gap), this.width, this.height, this.entries.size());
        this.entries.add(entry);
        this.listContent.add(entry);
        this.recalculateViewport();
        return entry;
    }

    @SuppressWarnings("unchecked")
    public ListEntry<T, ? extends T> getEntryAt(int x, int y) {
        return (ListEntry<T, ? extends T>) this.getExactWidgetAt(x, y);
    }

    @CanIgnoreReturnValue
    public <C extends T> C removeItem(ListEntry<T, C> entry) {
        this.entries.remove(entry);
        this.listContent.remove(entry);
        this.recalculateViewport();
        return entry.value;
    }

    @CanIgnoreReturnValue
    public ListEntry<T, ? extends T> removeItem(int index) {
        var item = this.entries.remove(index);
        this.recalculateViewport();
        return item;
    }

    public int size() {
        return this.entries.size();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public boolean hasItem(T o) {
        return this.entries.stream().anyMatch(entry -> entry.value == o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.entries.stream().map(entry -> (T)entry.value).iterator();
    }

    @SafeVarargs
    public final T[] toArray(T... t) {
        return this.entries.toArray(t);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(this.entries).containsAll(c);
    }

    public void addAllItems(@NotNull Collection<? extends T> c) {
        c.forEach(this::addItem);
    }

    public boolean removeAllItems(@NotNull Collection<?> c) {
        var b = this.entries.removeIf(entry -> c.contains(entry.value));
        this.recalculateViewport();
        return b;
    }

    public void clearList() {
        this.entries.clear();
        this.recalculateViewport();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.recalculateViewport();
    }

    @FunctionalInterface
    public interface EntryRenderer<T> {
        void render(Renderer renderer, float width, float height, float y, T entry, boolean selected, boolean hovered);
    }

    public static class ListEntry<T, C extends T> extends GuiComponent {
        private final ObjectList<T> list;
        public final C value;
        private int index;

        /**
         * @param value  value of the list entry
         * @param x      position create the widget
         * @param y      position create the widget
         * @param width  size create the widget
         * @param height size create the widget
         */
        public ListEntry(ObjectList<T> list, C value, int x, int y, int width, int height, int index) {
            super(x, y, width, height);
            this.list = list;
            this.value = value;
            this.index = index;
        }

        @Override
        public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
            this.x = this.list.x;
            this.y = (int) (-this.list.getViewport().yScroll + (this.list.entryHeight + this.list.gap) * this.index);
            this.width = this.list.width - SCROLLBAR_WIDTH;
            this.height = this.list.entryHeight;
            renderer.scissored(this.getBounds(), bounds -> {
                if (bounds.width <= 0 || bounds.height <= 0) return;
                this.list.entryRenderer.render(renderer, this.width, this.height, this.y, this.value, this.list.selected == this && this.list.selectable, this.isHovered());
            });
        }

        @Override
        public boolean isHovered() {
            return this.list.isHovered() && super.isHovered();
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public boolean mousePress(int x, int y, int button) {
            if (!this.list.selectable) return super.mousePress(x, y, button);
            this.list.selected = this;
            this.list.selectHandlers.forEach(selectHandler -> selectHandler.onSelect(this));
            return true;
        }
    }

    @FunctionalInterface
    public interface SelectHandler<T> {
        void onSelect(ListEntry<T, ? extends T> entry);
    }
}
