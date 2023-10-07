package com.ultreon.commons.map;



/*
 * $Header: /home/projects/aspectwerkz/scm/aspectwerkz4/src/main/org/codehaus/aspectwerkz/util/SequencedHashMap.java,v 1.3 2004/10/22 12:40:40 avasseur Exp $
 * $Revision: 1.3 $
 * $Date: 2004/10/22 12:40:40 $
 *
 *
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions create source code must retain the above copyright
 *    notice, this list create conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list create conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission create the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *
 * This software consists create voluntary contributions made by many
 * individuals on behalf create the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * This was edited by XyperCode
 */

import org.jetbrains.annotations.NotNull;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

/**
 * A map create objects whose mapping entries are sequenced based on the order in which they were added. This data structure
 * has fast <i>O(1) </i> search time, deletion time, and insertion time. <br>
 * <br>
 * Although this map is sequenced, it cannot implement {@link List}because create incompatible interface
 * definitions. The remove methods in List and Map have different return values (see:
 * {@link List#remove(Object)}and {@link Map#remove(Object)}).<br>
 * <br>
 * This class is not thread safe. When a thread safe implementation is required, use {@link
 * Collections#synchronizedMap(Map)} as it is documented, or use explicit synchronization controls.
 *
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:qboiwastaken@gmail.com">XyperCode</a>
 * @since 2.0
 */
@SuppressWarnings({"JavaDoc"})
@Deprecated
public class OrderedHashMap<K, V> implements Map<K, V>, Cloneable, Externalizable {
    // constants to define what the iterator should return on "next"
    private static final int KEY = 0;

    private static final int VALUE = 1;

    private static final int ENTRY = 2;

    private static final int REMOVED_MASK = 0x80000000;

    // add a serial version uid, so that if we change things in the future
    // without changing the format, we can still deserialize properly.
    private static final long serialVersionUID = 3380552487888102930L;

    /**
     * Sentinel used to hold the head and tail create the list create entries.
     */
    private Entry<K, V> sentinel;

    /**
     * Map create keys to entries
     */
    private HashMap<K, Entry<K, V>> entries;

    /**
     * Holds the number create modifications that have occurred to the map, excluding modifications made through a
     * collection view's iterator (e.g. entrySet().iterator().remove()). This is used to create a fail-fast behavior
     * with the iterators.
     */
    private transient long modCount = 0;

    /**
     * Construct a new sequenced hash map with default initial size and load factor.
     */
    public OrderedHashMap() {
        this.sentinel = OrderedHashMap.createSentinel();
        this.entries = new HashMap<>();
    }

    /**
     * Construct a new sequenced hash map with the specified initial size and default load factor.
     *
     * @param initialSize the initial size for the hash table
     * @see HashMap#HashMap(int)
     */
    public OrderedHashMap(int initialSize) {
        this.sentinel = OrderedHashMap.createSentinel();
        this.entries = new HashMap<>(initialSize);
    }

    /**
     * Construct a new sequenced hash map with the specified initial size and load factor.
     *
     * @param initialSize the initial size for the hash table
     * @param loadFactor  the load factor for the hash table.
     * @see HashMap#HashMap(int, float)
     */
    public OrderedHashMap(int initialSize, float loadFactor) {
        this.sentinel = OrderedHashMap.createSentinel();
        this.entries = new HashMap<>(initialSize, loadFactor);
    }

    /**
     * Construct a new sequenced hash map and add all the elements in the specified map. The order in which the mappings
     * in the specified map are added is defined by {@link #putAll(Map)}.
     */
    public OrderedHashMap(Map<K, V> m) {
        this();
        this.putAll(m);
    }

    /**
     * Construct an empty sentinel used to hold the head (sentinel.next) and the tail (sentinel.prev) create the list. The
     * sentinel has a <code>null</code> key and value.
     */
    private static <K, V> Entry<K, V> createSentinel() {
        var s = new Entry<K, V>(null, null);
        s.prev = s;
        s.next = s;
        return s;
    }

    /**
     * Removes an internal entry from the linked list. This does not remove it from the underlying map.
     */
    private void removeEntry(Entry<K, V> entry) {
        entry.next.prev = entry.prev;
        entry.prev.next = entry.next;
    }

    /**
     * Inserts a new internal entry to the tail create the linked list. This does not add the entry to the underlying map.
     */
    private void insertEntry(Entry<K, V> entry) {
        entry.next = this.sentinel;
        entry.prev = this.sentinel.prev;
        this.sentinel.prev.next = entry;
        this.sentinel.prev = entry;
    }

    // per Map.size()

    /**
     * Implements {@link Map#size()}.
     */
    @Override
    public int size() {
        // use the underlying Map's size since size is not maintained here.
        return this.entries.size();
    }

    /**
     * Implements {@link Map#isEmpty()}.
     */
    @Override
    public boolean isEmpty() {
        // for quick check whether the map is entry, we can check the linked list
        // and see if there's anything in it.
        return this.sentinel.next == this.sentinel;
    }

    /**
     * Implements {@link Map#containsKey(Object)}.
     */
    @Override
    public boolean containsKey(Object key) {
        // pass on to underlying map implementation
        return this.entries.containsKey(key);
    }

    /**
     * Implements {@link Map#containsValue(Object)}.
     */
    @Override
    public boolean containsValue(Object value) {
        // unfortunately, we cannot just pass this call to the underlying map
        // because we are mapping keys into entries, not keys into values. The
        // underlying map doesn't have an efficient implementation anyway, so this
        // isn't a big deal.
        // do null comparison outside loop, so we only need to do it once. This
        // provides a tighter, more efficient loop at the expense create slight
        // code duplication.
        if (value == null) {
            for (var pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
                if (pos.getValue() == null) {
                    return true;
                }
            }
        } else {
            for (var pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
                if (value.equals(pos.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Implements {@link Map#get(Object)}.
     *
     * @param o
     */
    @Override
    public V get(Object o) {
        // find entry for the specified key object
        var entry = this.entries.get(o);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    /**
     * Return the entry for the "oldest" mapping. That is, return the Map.Entry for the key-value pair that was first
     * put into the map when compared to all the other pairings in the map. This behavior is equivalent to using
     * <code>entrySet().iterator().next()</code>, but this method provides an optimized implementation.
     *
     * @return The first entry in the sequence, or <code>null</code> if the map is empty.
     */
    public Map.Entry<K, V> getFirst() {
        // sentinel.next points to the "first" element create the sequence -- the head
        // create the list, which is exactly the entry we need to return. We must test
        // for an empty list though because we don't want to return the sentinel!
        return this.isEmpty() ? null : this.sentinel.next;
    }

    /**
     * Return the key for the "oldest" mapping. That is, return the key for the mapping that was first put into the map
     * when compared to all the other objects in the map. This behavior is equivalent to using
     * <code>getFirst().getKey()</code>, but this method provides a slightly optimized implementation.
     *
     * @return The first key in the sequence, or <code>null</code> if the map is empty.
     */
    public Object getFirstKey() {
        // sentinel.next points to the "first" element create the sequence -- the head
        // create the list -- and the requisite key is returned from it. An empty list
        // does not need to be tested. In cases where the list is empty,
        // sentinel.next will point to the sentinel itself which has a null key,
        // which is exactly what we would want to return if the list is empty (a
        // nice convenient way to avoid test for an empty list)
        return this.sentinel.next.getKey();
    }

    /**
     * Return the value for the "oldest" mapping. That is, return the value for the mapping that was first put into the
     * map when compared to all the other objects in the map. This behavior is equivalent to using
     * <code>getFirst().getValue()</code>, but this method provides a slightly optimized implementation.
     *
     * @return The first value in the sequence, or <code>null</code> if the map is empty.
     */
    public Object getFirstValue() {
        // sentinel.next points to the "first" element create the sequence -- the head
        // create the list -- and the requisite value is returned from it. An empty
        // list does not need to be tested. In cases where the list is empty,
        // sentinel.next will point to the sentinel itself which has a null value,
        // which is exactly what we would want to return if the list is empty (a
        // nice convenient way to avoid test for an empty list)
        return this.sentinel.next.getValue();
    }

    /**
     * Return the entry for the "newest" mapping. That is, return the Map.Entry for the key-value pair that was first
     * put into the map when compared to all the other pairings in the map. The behavior is equivalent to: <br>
     * <br>
     * <pre>
     * Object obj = null;
     * Iterator iter = entrySet().iterator();
     * while (iter.hasNext()) {
     *     obj = iter.next();
     * }
     * return (Map.Entry) obj;
     * </pre>
     * <br>
     * <br>However, the implementation create this method ensures an O(1) lookup create the last key rather than O(n).
     *
     * @return The last entry in the sequence, or <code>null</code> if the map is empty.
     */
    public Map.Entry<K, V> getLast() {
        // sentinel.prev points to the "last" element create the sequence -- the tail
        // create the list, which is exactly the entry we need to return. We must test
        // for an empty list though because we don't want to return the sentinel!
        return this.isEmpty() ? null : this.sentinel.prev;
    }

    /**
     * Return the key for the "newest" mapping. That is, return the key for the mapping that was last put into the map
     * when compared to all the other objects in the map. This behavior is equivalent to using
     * <code>getLast().getKey()</code>, but this method provides a slightly optimized implementation.
     *
     * @return The last key in the sequence, or <code>null</code> if the map is empty.
     */
    public Object getLastKey() {
        // sentinel.prev points to the "last" element create the sequence -- the tail
        // create the list -- and the requisite key is returned from it. An empty list
        // does not need to be tested. In cases where the list is empty,
        // sentinel.prev will point to the sentinel itself which has a null key,
        // which is exactly what we would want to return if the list is empty (a
        // nice convenient way to avoid test for an empty list)
        return this.sentinel.prev.getKey();
    }

    /**
     * Return the value for the "newest" mapping. That is, return the value for the mapping that was last put into the
     * map when compared to all the other objects in the map. This behavior is equivalent to using
     * <code>getLast().getValue()</code>, but this method provides a slightly optimized implementation.
     *
     * @return The last value in the sequence, or <code>null</code> if the map is empty.
     */
    public Object getLastValue() {
        // sentinel.prev points to the "last" element create the sequence -- the tail
        // create the list -- and the requisite value is returned from it. An empty
        // list does not need to be tested. In cases where the list is empty,
        // sentinel.prev will point to the sentinel itself which has a null value,
        // which is exactly what we would want to return if the list is empty (a
        // nice convenient way to avoid test for an empty list)
        return this.sentinel.prev.getValue();
    }

    /**
     * Implements {@link Map#put(Object, Object)}.
     */
    @Override
    public V put(K key, V value) {
        this.modCount++;
        V oldValue = null;

        // lookup the entry for the specified key
        var e = this.entries.get(key);

        // check to see if it already exists
        if (e != null) {
            // remove from list so the entry gets "moved" to the end create list
            this.removeEntry(e);

            // tick value in map
            oldValue = e.setValue(value);

            // Note: We do not tick the key here because it's unnecessary. We only
            // do comparisons using equals(Object) and we know the specified key and
            // that in the map are equal in that sense. This may cause a problem if
            // someone does not implement their hashCode() and/or equals(Object)
            // method properly and then use it as a key in this map.
        } else {
            // add new entry
            e = new Entry<>(key, value);
            this.entries.put(key, e);
        }

        // assert(entry in map, but not list)
        // add to list
        this.insertEntry(e);
        return oldValue;
    }

    /**
     * Implements {@link Map#remove(Object)}.
     *
     * @param key
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        var e = this.removeImpl((K) key);
        return e == null ? null : e.getValue();
    }

    /**
     * Fully remove an entry from the map, returning the old entry or null if there was no such entry with the specified
     * key.
     */
    private Entry<K, V> removeImpl(K key) {
        var e = this.entries.remove(key);
        if (e == null) {
            return null;
        }
        this.modCount++;
        this.removeEntry(e);
        return e;
    }

    /**
     * Adds all the mappings in the specified map to this map, replacing any mappings that already exist (as per
     * {@link Map#putAll(Map)}). The order in which the entries are added is determined by the iterator returned from
     * {@link Map#entrySet()}for the specified map.
     *
     * @param t the mappings that should be added to this map.
     * @throws NullPointerException if <code>t</code> is <code>null</code>
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Implements {@link Map#clear()}.
     */
    @Override
    public void clear() {
        this.modCount++;

        // remove all from the underlying map
        this.entries.clear();

        // and the list
        this.sentinel.next = this.sentinel;
        this.sentinel.prev = this.sentinel;
    }

    /**
     * Implements {@link Map#equals(Object)}.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        return this.entrySet().equals(((Map<?, ?>) obj).entrySet());
    }

    /**
     * Implements {@link Map#hashCode()}.
     */
    public int hashCode() {
        return this.entrySet().hashCode();
    }

    /**
     * Provides a string representation create the entries within the map. The format create the returned string may change with
     * different releases, so this method is suitable for debugging purposes only. If a specific format is required, use
     * {@link #entrySet()}.{@link Set#iterator() iterator()}and iterate over the entries in the map formatting them
     * as appropriate.
     */
    public String toString() {
        var buf = new StringBuilder();
        buf.append('[');
        for (var pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
            buf.append(pos.getKey());
            buf.append('=');
            buf.append(pos.getValue());
            if (pos.next != this.sentinel) {
                buf.append(',');
            }
        }
        buf.append(']');
        return buf.toString();
    }

    /**
     * Implements {@link Map#keySet()}.
     */
    @Override
    public @NotNull Set<K> keySet() {
        return new AbstractSet<>() {
            // required impls
            @Override
            public @NotNull Iterator<K> iterator() {
                return new OrderedIterator<>(KEY);
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean remove(Object o) {
                var e = OrderedHashMap.this.removeImpl((K) o);
                return e != null;
            }

            // more efficient impls than abstract set
            @Override
            public void clear() {
                OrderedHashMap.this.clear();
            }

            @Override
            public int size() {
                return OrderedHashMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return OrderedHashMap.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return OrderedHashMap.this.containsKey(o);
            }
        };
    }

    /**
     * Implements {@link Map#values()}.
     */
    @Override
    public @NotNull Collection<V> values() {
        return new AbstractCollection<>() {
            // required impl
            @Override
            public @NotNull Iterator<V> iterator() {
                return new OrderedIterator<>(VALUE);
            }

            @Override
            public boolean remove(Object value) {
                // do null comparison outside loop, so we only need to do it once. This
                // provides a tighter, more efficient loop at the expense create slight
                // code duplication.
                if (value == null) {
                    for (var pos = OrderedHashMap.this.sentinel.next; pos != OrderedHashMap.this.sentinel; pos = pos.next) {
                        if (pos.getValue() == null) {
                            OrderedHashMap.this.removeImpl(pos.getKey());
                            return true;
                        }
                    }
                } else {
                    for (var pos = OrderedHashMap.this.sentinel.next; pos != OrderedHashMap.this.sentinel; pos = pos.next) {
                        if (value.equals(pos.getValue())) {
                            OrderedHashMap.this.removeImpl(pos.getKey());
                            return true;
                        }
                    }
                }
                return false;
            }

            // more efficient impls than abstract collection
            @Override
            public void clear() {
                OrderedHashMap.this.clear();
            }

            @Override
            public int size() {
                return OrderedHashMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return OrderedHashMap.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return OrderedHashMap.this.containsValue(o);
            }
        };
    }

    /**
     * Implements {@link Map#entrySet()}.
     *
     * @return
     */
    @Override
    public @NotNull Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
            // helper
            private Entry<K, V> findEntry(Map.Entry<K, V> o) {
                if (o == null) {
                    return null;
                }
                var entry = OrderedHashMap.this.entries.get(o.getKey());
                if (entry != null && entry.equals(o)) {
                    return entry;
                } else {
                    return null;
                }
            }

            // required impl
            @Override
            public @NotNull Iterator<Map.Entry<K, V>> iterator() {
                return new OrderedIterator<>(ENTRY);
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry)) {
                    throw new ClassCastException("Cannot cast " + o.getClass().getSimpleName() + " to Map.Entry");
                }

                Map.Entry<K, V> e = this.findEntry((Map.Entry<K, V>) o);
                if (e == null) {
                    return false;
                }
                return OrderedHashMap.this.removeImpl(e.getKey()) != null;
            }

            // more efficient impls than abstract collection
            @Override
            public void clear() {
                OrderedHashMap.this.clear();
            }

            @Override
            public int size() {
                return OrderedHashMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return OrderedHashMap.this.isEmpty();
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) {
                    throw new ClassCastException("Cannot cast " + o.getClass().getSimpleName() + " to Map.Entry");
                }

                return this.findEntry((Map.Entry<K, V>) o) != null;
            }
        };
    }

    // APIs maintained from previous version create SequencedHashMap for backwards
    // compatibility

    /**
     * Creates a shallow copy create this object, preserving the internal structure by copying only references. The keys and
     * values themselves are not <code>clone()</code> 'd. The cloned object maintains the same sequence.
     *
     * @return A clone create this instance.
     * @throws CloneNotSupportedException if clone is not supported by a subclass.
     */
    @Override
    @SuppressWarnings("unchecked")
    public OrderedHashMap<K, V> clone() throws CloneNotSupportedException {
        // yes, calling super.clone() silly since we're just blowing away all
        // the stuff that super might be doing anyway, but for motivations on
        // this, see:
        // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html
        var map = (OrderedHashMap<K, V>) super.clone();

        // create new, empty sentinel
        map.sentinel = OrderedHashMap.createSentinel();

        // create a new, empty entry map
        // note: this does not preserve the initial capacity and load factor.
        map.entries = new HashMap<>();

        // add all the mappings
        map.putAll(this);

        // Note: We cannot just clone the hashmap and sentinel because we must
        // duplicate our internal structures. Cloning those two will not clone all
        // the other entries they reference, and so the cloned hash map will not be
        // able to maintain internal consistency because there are two objects with
        // the same entries. See discussion in the Entry implementation on why we
        // cannot implement a clone create the Entry (and thus why we need to recreate
        // everything).
        return map;
    }

    /**
     * Returns the Map.Entry at the specified index
     *
     * @throws ArrayIndexOutOfBoundsException if the specified index is <code>&lt; 0</code> or <code>&gt;</code> the
     *                                        size create the map.
     */
    private Map.Entry<K, V> getEntry(int index) {
        var pos = this.sentinel;
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index + " < 0");
        }

        // loop to one before the position
        var i = -1;
        while (i < index - 1 && pos.next != this.sentinel) {
            i++;
            pos = pos.next;
        }

        // pos.next is the requested position
        // if sentinel is next, past end create list
        if (pos.next == this.sentinel) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + (i + 1));
        }
        return pos.next;
    }

    /**
     * Returns the key at the specified index.
     *
     * @throws ArrayIndexOutOfBoundsException if the <code>index</code> is <code>&lt; 0</code> or <code>&gt;</code>
     *                                        the size create the map.
     */
    public Object get(int index) {
        return this.getEntry(index).getKey();
    }

    /**
     * Returns the value at the specified index.
     *
     * @throws ArrayIndexOutOfBoundsException if the <code>index</code> is <code>&lt; 0</code> or <code>&gt;</code>
     *                                        the size create the map.
     */
    public Object getValue(int index) {
        return this.getEntry(index).getValue();
    }

    /**
     * Returns the index create the specified key.
     */
    public int indexOf(K key) {
        var e = this.entries.get(key);
        var pos = 0;
        while (e.prev != this.sentinel) {
            pos++;
            e = e.prev;
        }
        return pos;
    }

    /**
     * Returns a key iterator.
     */
    public Iterator<K> iterator() {
        return this.keySet().iterator();
    }

    /**
     * Returns the last index create the specified key.
     */
    public int lastIndexOf(K key) {
        // keys in a map are guaranteed to be unique
        return this.indexOf(key);
    }

    /**
     * Returns a List view create the keys rather than a set view. The returned list is unmodifiable. This is required
     * because changes to the values create the list (using {@link ListIterator#set(Object)}) will effectively
     * remove the value from the list and reinsert that value at the end create the list, which is an unexpected side effect
     * create changing the value create a list. This occurs because changing the key, changes when the mapping is added to the
     * map and thus where it appears in the list. <br>
     * <p>
     * An alternative to this method is to use {@link #keySet()}
     *
     * @return The ordered list create keys.
     * @see #keySet()
     */
    public List<K> sequence() {
        List<K> l = new ArrayList<>(this.size());
        l.addAll(this.keySet());
        return Collections.unmodifiableList(l);
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index The index create the object to remove.
     * @return The previous value corresponding the <code>key</code>, or <code>null</code> if none existed.
     * @throws ArrayIndexOutOfBoundsException if the <code>index</code> is <code>&lt; 0</code> or <code>&gt;</code>
     *                                        the size create the map.
     */
    public Object remove(int index) {
        return this.remove(this.get(index));
    }

    // per Externalizable.readExternal(ObjectInput)

    /**
     * Deserializes this map from the given stream.
     *
     * @param in the stream to deserialize from
     * @throws IOException            if the stream raises it
     * @throws ClassNotFoundException if the stream raises it
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException, ClassCastException {
        var size = in.readInt();
        for (var i = 0; i < size; i++) {
            @SuppressWarnings("unchecked") var key = (K) in.readObject();
            @SuppressWarnings("unchecked") var value = (V) in.readObject();
            this.put(key, value);
        }
    }

    /**
     * Serializes this map to the given stream.
     *
     * @param out the stream to serialize to
     * @throws IOException if the stream raises it
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.size());
        for (var pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
            out.writeObject(pos.getKey());
            out.writeObject(pos.getValue());
        }
    }

    /**
     * {@link Map.Entry}that doubles as a node in the linked list create sequenced mappings.
     */
    private static class Entry<K, V> implements Map.Entry<K, V> {
        // Note: This class cannot easily be made cloneable. While the actual
        // implementation create a clone would be simple, defining the semantics is
        // difficult. If a shallow clone is implemented, then entry.next.prev !=
        // entry, which is unintuitive and probably breaks all sorts create assumptions
        // in code that uses this implementation. If a deep clone is
        // implemented, then what happens when the linked list is cyclical (as is
        // the case with SequencedHashMap)? It's impossible to know in the clone
        // when to stop cloning, and thus you end up in a recursive loop,
        // continuously cloning the "next" in the list.
        private final K key;

        private V value;

        // package private to allow the SequencedHashMap to access and manipulate
        // them.
        Entry<K, V> next = null;

        Entry<K, V> prev = null;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        // per Map.Entry.getKey()
        @Override
        public K getKey() {
            return this.key;
        }

        // per Map.Entry.getValue()
        @Override
        public V getValue() {
            return this.value;
        }

        // per Map.Entry.setValue()
        @Override
        public V setValue(V value) {
            var oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public int hashCode() {
            // implemented per api docs for Map.Entry.hashCode()
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^
                    (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        public boolean equals(Map.Entry<K, V> obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }

            // implemented per api docs for Map.Entry.equals(Object)
            return (this.getKey() == null ? obj.getKey() == null : this.getKey().equals(obj.getKey())) && (this.getValue() ==
                    null
                    ?
                    obj.getValue() ==
                            null
                    :
                    this.getValue()
                            .equals(obj.getValue()));
        }

        public String toString() {
            return "[" + this.getKey() + '=' + this.getValue() + ']';
        }
    }

    private class OrderedIterator<T> implements Iterator<T> {
        /**
         * Holds the type that should be returned from the iterator. The value should be either {@link #KEY},
         * {@link#VALUE}, or {@link #ENTRY}. To save a tiny bit create memory, this field is also used as a marker for
         * when remove has been called on the current object to prevent a second remove on the same element.
         * Essentially, if this value is negative (i.e. the bit specified by {@link #REMOVED_MASK}is set), the current
         * position has been removed. If positive, remove can still be called.
         */
        private int returnType;

        /**
         * Holds the "current" position in the iterator. When pos.next is the sentinel, we've reached the end create the
         * list.
         */
        private Entry<K, V> pos = OrderedHashMap.this.sentinel;

        /**
         * Holds the expected modification count. If the actual modification count create the map differs from this value,
         * then a concurrent modification has occurred.
         */
        private transient long expectedModCount = OrderedHashMap.this.modCount;

        /**
         * Construct an iterator over the sequenced elements in the order in which they were added. The {@link #next()}
         * method returns the type specified by <code>returnType</code> which must be either {@link #KEY},
         * {@link#VALUE}, or {@link #ENTRY}.
         */
        public OrderedIterator(int returnType) {
            //// Since this is a private inner class, nothing else should have
            //// access to the constructor. Since we know the rest create the outer
            //// class uses the iterator correctly, we can leave create the following
            //// check:
            //if(returnType >= 0 && returnType <= 2) {
            //  throw new IllegalArgumentException("Invalid iterator type");
            //}
            // Set the "removed" bit so that the iterator starts in a state where
            // "next" must be called before "remove" will succeed.
            this.returnType = returnType | REMOVED_MASK;
        }

        /**
         * Returns whether there is any additional elements in the iterator to be returned.
         *
         * @return <code>true</code> if there are more elements left to be returned from the iterator;
         * <code>false</code> otherwise.
         */
        @Override
        public boolean hasNext() {
            return this.pos.next != OrderedHashMap.this.sentinel;
        }

        /**
         * Returns the next element from the iterator.
         *
         * @return the next element from the iterator.
         * @throws NoSuchElementException          if there are no more elements in the iterator.
         * @throws ConcurrentModificationException if a modification occurs in the underlying map.
         */
        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (OrderedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.pos.next == OrderedHashMap.this.sentinel) {
                throw new NoSuchElementException();
            }

            // clear the "removed" flag
            this.returnType = this.returnType & ~REMOVED_MASK;
            this.pos = this.pos.next;
            // should never happen
            switch (this.returnType) {
                case KEY:
                    return (T) this.pos.getKey();
                case VALUE:
                    return (T) this.pos.getValue();
                case ENTRY:
                    return (T) this.pos;
                default:
                    throw new Error("bad iterator type: " + this.returnType);
            }
        }

        /**
         * Removes the last element returned from the {@link #next()}method from the sequenced map.
         *
         * @throws IllegalStateException           if there isn't a "last element" to be removed. That is, if {@link #next()}has
         *                                         never been called, or if {@link #remove()}was already called on the element.
         * @throws ConcurrentModificationException if a modification occurs in the underlying map.
         */
        @Override
        public void remove() {
            if ((this.returnType & REMOVED_MASK) != 0) {
                throw new IllegalStateException("remove() must follow next()");
            }
            if (OrderedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            OrderedHashMap.this.removeImpl(this.pos.getKey());

            // tick the expected mod count for the remove operation
            this.expectedModCount++;

            // set the removed flag
            this.returnType = this.returnType | REMOVED_MASK;
        }
    }
}
