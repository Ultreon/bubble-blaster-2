package net.querz.nbt.tag;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A decorator for the Set returned by CompoundTag#entrySet()
 * that disallows setting null values.
 */
@SuppressWarnings("ClassCanBeRecord")
class NotNullEntrySet<K, V> implements Set<Map.Entry<K, V>> {

    private final Set<Map.Entry<K, V>> set;

    NotNullEntrySet(Set<Map.Entry<K, V>> set) {
        this.set = set;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new NotNullEntrySetIterator(set.iterator());
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(Map.Entry<K, V> kvEntry) {
        return set.add(kvEntry);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
        return set.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }

    @Override
    public void clear() {
        set.clear();
    }

    class NotNullEntrySetIterator implements Iterator<Map.Entry<K, V>> {

        private final Iterator<Map.Entry<K, V>> iterator;

        NotNullEntrySetIterator(Iterator<Map.Entry<K, V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            return new NotNullEntry(iterator.next());
        }
    }

    class NotNullEntry implements Map.Entry<K, V> {

        private final Map.Entry<K, V> entry;

        NotNullEntry(Map.Entry<K, V> entry) {
            this.entry = entry;
        }

        @NotNull
        @Override
        public K getKey() {
            return entry.getKey();
        }

        @NotNull
        @Override
        public V getValue() {
            return entry.getValue();
        }

        @NotNull
        @Override
        public V setValue(@NotNull V value) {
            if (value == null) {
                throw new NullPointerException(getClass().getSimpleName() + " does not allow setting null");
            }
            return entry.setValue(value);
        }

        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(Object o) {
            return entry.equals(o);
        }

        @Override
        public int hashCode() {
            return entry.hashCode();
        }
    }
}