package com.ultreon.bubbles.common.random;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RNG Class, for randomizing or get random numbers from arguments.
 *
 * @author XyperCode
 */
@SuppressWarnings({"ClassCanBeRecord"})
public class Rng {
    private final GameRandom random;
    private final int index;
    private final int subIndex;

    /**
     * RNG Constructor.
     *
     * @param random   the {@link GameRandom} instance.
     * @param index    the RNG-Index.
     * @param subIndex the RNG Sub-index.
     */
    public Rng(GameRandom random, int index, int subIndex) {
        this.random = random;
        this.index = index;
        this.subIndex = subIndex;
    }

    public static void serializeText(String text) {

    }

    /**
     * @return get random number generator.
     */
    public GameRandom getRandom() {
        return this.random;
    }

    /**
     * @return get rng-index.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @return get rng sub-index.
     */
    public int getSubIndex() {
        return this.subIndex;
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public int getNumber(int min, int max, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public int getNumber(int min, int max, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public int getNumber(int min, int max, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public int getNumber(int min, int max, char[] modifier, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);
        for (char b : modifier) {
            list.add((int) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public int getNumber(int min, int max, char[] modifier, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);
        for (char b : modifier) {
            list.add((long) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public int getNumber(int min, int max, char[] modifier, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));
        for (char b : modifier) {
            list.add(BigInteger.valueOf(b));
        }

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public long getNumber(long min, long max, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);


        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public long getNumber(long min, long max, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public long getNumber(long min, long max, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public long getNumber(long min, long max, char[] modifier, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);
        for (char b : modifier) {
            list.add((int) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public long getNumber(long min, long max, char[] modifier, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);
        for (char b : modifier) {
            list.add((long) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public long getNumber(long min, long max, char[] modifier, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));
        for (char b : modifier) {
            list.add(BigInteger.valueOf(b));
        }

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigInteger getNumber(BigInteger min, BigInteger max, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigInteger getNumber(BigInteger min, BigInteger max, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigInteger getNumber(BigInteger min, BigInteger max, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigInteger getNumber(BigInteger min, BigInteger max, char[] modifier, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);
        for (char b : modifier) {
            list.add((int) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigInteger getNumber(BigInteger min, BigInteger max, char[] modifier, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);
        for (char b : modifier) {
            list.add((long) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigInteger getNumber(BigInteger min, BigInteger max, char[] modifier, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));
        for (char b : modifier) {
            list.add(BigInteger.valueOf(b));
        }

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public float getNumber(float min, float max, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public float getNumber(float min, float max, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public float getNumber(float min, float max, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public float getNumber(float min, float max, char[] modifier, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);
        for (char b : modifier) {
            list.add((int) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public float getNumber(float min, float max, char[] modifier, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);
        for (char b : modifier) {
            list.add((long) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public float getNumber(float min, float max, char[] modifier, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));
        for (char b : modifier) {
            list.add(BigInteger.valueOf(b));
        }

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public double getNumber(double min, double max, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public double getNumber(double min, double max, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public double getNumber(double min, double max, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public double getNumber(double min, double max, char[] modifier, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);
        for (char b : modifier) {
            list.add((int) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public double getNumber(double min, double max, char[] modifier, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);
        for (char b : modifier) {
            list.add((long) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public double getNumber(double min, double max, char[] modifier, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));
        for (char b : modifier) {
            list.add(BigInteger.valueOf(b));
        }

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigDecimal getNumber(BigDecimal min, BigDecimal max, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigDecimal getNumber(BigDecimal min, BigDecimal max, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigDecimal getNumber(BigDecimal min, BigDecimal max, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigDecimal getNumber(BigDecimal min, BigDecimal max, char[] modifier, int... input) {
        List<Integer> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, this.subIndex);
        list.add(0, this.index);
        for (char b : modifier) {
            list.add((int) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToInt(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigDecimal getNumber(BigDecimal min, BigDecimal max, char[] modifier, long... input) {
        List<Long> list = Arrays.stream(input).boxed().collect(Collectors.toList());
        list.add(0, (long) this.subIndex);
        list.add(0, (long) this.index);
        for (char b : modifier) {
            list.add((long) b);
        }

        return this.random.getNumber(min, max, list.stream().mapToLong(i -> i).toArray());
    }

    /**
     * @param min   minimum value.
     * @param max   maximum value.
     * @param input the randomizing arguments.
     * @return the random integer.
     */
    public BigDecimal getNumber(BigDecimal min, BigDecimal max, char[] modifier, BigInteger... input) {
        List<BigInteger> list = Arrays.asList(input);
        list.add(0, BigInteger.valueOf(this.subIndex));
        list.add(0, BigInteger.valueOf(this.index));
        for (char b : modifier) {
            list.add(BigInteger.valueOf(b));
        }

        return this.random.getNumber(min, max, list.toArray(new BigInteger[]{}));
    }
}
