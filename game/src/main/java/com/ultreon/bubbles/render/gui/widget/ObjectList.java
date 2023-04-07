package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private List<SelectHandler<T>> selectHandlers = new ArrayList<>();

    public ObjectList(List<T> items, int entryHeight, int gap, int x, int y, int width, int height) {
        super(new Rectangle(0, 0, width, calculateViewHeight(items, entryHeight, gap)), x, y, width, height);

        this.entryType = items.getClass().getComponentType();
        this.entryHeight = entryHeight;
        this.gap = gap;

        this.listContent = this.getViewport().add(new Container(0, 0, width, getViewport().getHeight()) {
            @Override
            public void render(Renderer renderer) {
                var y = 0;
                for (var entry : ObjectList.this.entries) {
                    entry.setPos(0, y);
                    entry.setSize(width, entryHeight);
                    Renderer entryRenderer = renderer.subInstance(0, y, ObjectList.this.width, entryHeight);
                    entry.render(entryRenderer);
                    y += entryHeight + gap;
                }
            }
        });

        items.forEach(this::addItem);
    }

    public void addSelectHandler(SelectHandler<T> handler) {
        selectHandlers.add(handler);
    }

    public void removeSelectHandler(SelectHandler<T> handler) {
        selectHandlers.remove(handler);
    }

    private static int calculateViewHeight(List<?> entries, int entryHeight, int gap) {
        return entries.size() * (entryHeight + gap) - gap;
    }

    private void recalculateViewport() {
        int viewHeight = calculateViewHeight(entries, entryHeight, gap);
        this.getViewport().setViewportSize(width - SCROLLBAR_WIDTH, viewHeight);
        listContent.setHeight(viewHeight);
    }

    public void setEntryRenderer(EntryRenderer<T> renderer) {
        this.entryRenderer = renderer;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public ListEntry<T, ? extends T> getSelected() {
        return selected;
    }

    public void setSelected(ListEntry<T, ? extends T> selected) {
        this.selected = selected;
    }

    public Class<?> getEntryType() {
        return entryType;
    }

    public <C extends T> ListEntry<T, C> addItem(C item) {
        ListEntry<T, C> entry = new ListEntry<>(this, item, 0, getViewport().getViewportSize().height + gap, width, height);
        entries.add(entry);
        listContent.add(entry);
        recalculateViewport();
        return entry;
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        return super.mousePress(x, y, button);
    }

    @SuppressWarnings("unchecked")
    public ListEntry<T, ? extends T> getEntryAt(int x, int y) {
        return (ListEntry<T, ? extends T>) getExactWidgetAt(x, y);
    }

    public <C extends T> C removeItem(ListEntry<T, C> entry) {
        entries.remove(entry);
        listContent.remove(entry);
        recalculateViewport();
        return entry.value;
    }

    public ListEntry<T, ? extends T> removeItem(int index) {
        ListEntry<T, ? extends T> item = entries.remove(index);
        recalculateViewport();
        return item;
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public boolean hasItem(T o) {
        return entries.stream().anyMatch(entry -> entry.value == o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return entries.stream().map(entry -> (T)entry.value).iterator();
    }

    @SafeVarargs
    public final T[] toArray(T... t) {
        return entries.toArray(t);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(entries).containsAll(c);
    }

    public void addAllItems(@NotNull Collection<? extends T> c) {
        c.forEach(this::addItem);
    }

    public boolean removeAllItems(@NotNull Collection<?> c) {
        boolean b = entries.removeIf(entry -> c.contains(entry.value));
        recalculateViewport();
        return b;
    }

    public void clearList() {
        entries.clear();
        recalculateViewport();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        recalculateViewport();
    }

    @FunctionalInterface
    public interface EntryRenderer<T> {
        void render(Renderer renderer, int width, int height, T entry, boolean b);
    }

    public static class ListEntry<T, C extends T> extends GuiComponent {
        private final ObjectList<T> list;
        public final C value;

        /**
         * @param value  value of the list entry
         * @param x      position create the widget
         * @param y      position create the widget
         * @param width  size create the widget
         * @param height size create the widget
         */
        public ListEntry(ObjectList<T> list, C value, int x, int y, int width, int height) {
            super(x, y, width, height);
            this.list = list;
            this.value = value;
        }

        public void render(Renderer renderer1) {
            list.entryRenderer.render(renderer1, list.width - SCROLLBAR_WIDTH, list.entryHeight, value, list.selected == this && list.selectable);
        }

        @Override
        public boolean mousePress(int x, int y, int button) {
            list.selected = this;
            list.selectHandlers.forEach(selectHandler -> selectHandler.onSelect(this));
            return super.mousePress(x, y, button);
        }

        @Override
        public boolean mouseClick(int x, int y, int button, int count) {
            return super.mouseClick(x, y, button, count);
        }
    }

    @FunctionalInterface
    public interface SelectHandler<T> {
        void onSelect(ListEntry<T, ? extends T> entry);
    }
}
