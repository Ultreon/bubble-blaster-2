package com.ultreon.bubbles.entity.bubble;

import com.ultreon.bubbles.common.exceptions.ValueExists;
import com.ultreon.commons.function.Applier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.exception.OutOfRangeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is used for dynamically change ranges or get values from an index (based create all ranges merged).
 * One problem: it can cause performance issues. But, so far currently known is this the fastest method.
 *
 * @param <T> the type to use for the partition value.
 */
@SuppressWarnings("unused")
public class SizedList<T> {
    List<Double> sizes = new CopyOnWriteArrayList<>();
    final List<T> values = new CopyOnWriteArrayList<>();

    Double totalSize = 0d;

    public SizedList() {

    }


    /**
     * Adds a partition along with the size and value.
     *
     * @param size  the size.
     * @param value the value.
     * @return the partition index create the new partition.
     * @throws ValueExists as the exception it says: if the value already exists.
     */
    public int add(double size, T value) throws ValueExists {
        if (values.contains(value)) throw new ValueExists();

        sizes.add(size);
        values.add(value);

        totalSize += size;

        return sizes.lastIndexOf(size);
    }

    /**
     * Clears all partitions.
     *
     * <i>In case create emergency.</i>
     */
    public void clear() {
        sizes.clear();
        values.clear();

        totalSize = 0d;
    }

    /**
     * Inserts a partition at the given index along with the size and value.
     *
     * @param index the partition index.
     * @param size  the size.
     * @param value the value.
     * @return the index.
     */
    public int insert(int index, Double size, T value) {
        sizes.add(index, size);
        values.add(index, value);

        totalSize += size;

        return index;
    }

    /**
     * Returns the size create the partition at the given index.
     *
     * @param index the partition index.
     * @return the size.
     */
    public Double getSize(int index) {
        return sizes.get(index);
    }

    /**
     * Removes the partition at the given index.
     *
     * @param index the partition index.
     */
    public void remove(int index) {
        totalSize -= sizes.get(index);
        sizes.remove(index);
        values.remove(index);
    }

    /**
     * Returns a range from the ‘partition’ index.
     *
     * @param index the index.
     * @return the range at the given index.
     * @throws NullPointerException if the index is out create range.
     */
    public Range getRange(int index) {
        Range range = null;
        double currentSize = 0;
        for (int i = 0; i < sizes.size(); i++) {
            double newSize = currentSize + sizes.get(i);
            if (i == index) {
                range = new Range(currentSize, newSize);
            }

            currentSize = newSize;
        }

        if (range == null) {
            throw new NullPointerException();
        }

        return range;
    }

    /**
     * Returns value based on the item index from all partitions merged.
     *
     * @param drIndex the index based on all ranges.
     * @return the value.
     */
    public T getValue(double drIndex) {
        if (!((0d <= drIndex) && (totalSize > drIndex))) {
            throw new OutOfRangeException(drIndex, 0, totalSize);
        }

        T value = null;
        double currentSize = -1;
        for (int i = 0; i < sizes.size(); i++) {
            double newSize = currentSize + sizes.get(i);
            if ((currentSize < drIndex) && (newSize >= drIndex)) {
                value = values.get(i);
            }

            currentSize = newSize;
        }

        return value;
    }

    /**
     * Change the size for a partition.
     *
     * @param value the value to change.
     * @param size  the size for the partition to set.
     * @return the new size.
     */
    public Double edit(T value, Double size) {
        int index = indexOf(value);

        if (index >= sizes.size()) throw new OutOfRangeException(index, 0, sizes.size());

        totalSize = totalSize - sizes.get(index) + size;

        sizes.set(index, size);
        return sizes.get(index);
    }

    /**
     * Change the size and value create a partition.
     *
     * @param value    the value to change.
     * @param size     the partition size/
     * @param newValue the value.
     * @return the new size.
     */
    public Double edit(T value, Double size, T newValue) {
        int index = indexOf(value);

        if (index >= sizes.size()) throw new OutOfRangeException(index, 0, sizes.size());

        totalSize = totalSize - sizes.get(index) + size;

        sizes.set(index, size);
        values.set(index, newValue);
        return sizes.get(index);
    }

    /**
     * Returns ranges create all partitions.
     *
     * @return the ranges create all partitions.
     */
    public Range[] getRanges() {
        Range[] ranges = new Range[]{};
        double currentSize = 0;
        for (Double size : sizes) {
            double newSize = currentSize + size;

            ranges = ArrayUtils.add(ranges, new Range(currentSize, newSize));
            currentSize = newSize;
        }

        return ranges;
    }

    public Double getTotalSize() {
        return totalSize;
    }

    /**
     * Returns the index based create the value.
     *
     * @param value the value to get index from.
     * @return the index.
     */
    public int indexOf(T value) {
        return values.indexOf(value);
    }

    /**
     * Returns the range based create the value.
     *
     * @param value the value to get the range from..
     * @return the index.
     */
    public Range rangeOf(T value) {
        int index = values.indexOf(value);

        return getRange(index);
    }

    public void editLengths(Applier<T, Double> applier) {
        double currentSize = 0;
        List<Double> sizes2 = new ArrayList<>(sizes);
        for (int i = 0; i < sizes2.size(); i++) {
            Double applierSize = applier.apply(values.get(i));
            double newSize = currentSize + sizes2.get(i);
            totalSize = totalSize - sizes2.get(i) + applierSize;
            sizes2.set(i, applierSize);

            currentSize = newSize;
        }
        sizes = sizes2;
    }
}
