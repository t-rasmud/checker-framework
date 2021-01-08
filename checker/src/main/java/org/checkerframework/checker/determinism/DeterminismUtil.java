package org.checkerframework.checker.determinism;

import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Utility functions for operations on arrays and lists, that are annotated with determinism type
 * qualifiers.
 */
@SuppressWarnings("determinism")
public class DeterminismUtil {

    ///
    /// Reduce (aggregation)
    ///

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param <T> the type of the arguments and return of the function
     * @param a the array to aggregate; must be non-empty
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static <T> @PolyDet("down") T reduce(@PolyDet T[] a, BinaryOperator<T> f) {
        if (a.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Empty array passed to reduce");
        }
        return Arrays.stream(a).reduce(f).get();
    }

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param <T> the type of the arguments and return of the function
     * @param a the array to aggregate
     * @param identity the identity value for the accumulating function
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static <T> @PolyDet("down") T reduce(@PolyDet T[] a, T identity, BinaryOperator<T> f) {
        return Arrays.stream(a).reduce(identity, f);
    }

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param a the array to aggregate; must be non-empty
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static @PolyDet("down") int reduce(int[] a, IntBinaryOperator f) {
        if (a.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Empty array passed to reduce");
        }
        return Arrays.stream(a).reduce(f).getAsInt();
    }

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param a the array to aggregate
     * @param identity the identity value for the accumulating function
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static @PolyDet("down") int reduce(int[] a, int identity, IntBinaryOperator f) {
        return Arrays.stream(a).reduce(identity, f);
    }

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param a the array to aggregate; must be non-empty
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static @PolyDet("down") long reduce(long[] a, LongBinaryOperator f) {
        if (a.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Empty array passed to reduce");
        }
        return Arrays.stream(a).reduce(f).getAsLong();
    }

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param a the array to aggregate
     * @param identity the identity value for the accumulating function
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static @PolyDet("down") long reduce(long[] a, long identity, LongBinaryOperator f) {
        return Arrays.stream(a).reduce(identity, f);
    }

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param a the array to aggregate; must be non-empty
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static @PolyDet("down") double reduce(double[] a, DoubleBinaryOperator f) {
        if (a.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Empty array passed to reduce");
        }
        return Arrays.stream(a).reduce(f).getAsDouble();
    }

    /**
     * Applies a commutative aggregation operation to the given array.
     *
     * @param a the array to aggregate
     * @param identity the identity value for the accumulating function
     * @param f the aggregation function
     * @return the aggregation of {@code a}, with respect to {@code f}
     */
    public static @PolyDet("down") double reduce(
            double[] a, double identity, DoubleBinaryOperator f) {
        return Arrays.stream(a).reduce(identity, f);
    }

    ///
    /// Sorting
    ///

    // These versions return the sorted array, which permits flow-sensitive type refinement.

    /**
     * Sorts the specified array of objects according to the order induced by the specified
     * comparator. Like {@code Arrays.sort}, but also returns the sorted array.
     *
     * @param <T> the class of the objects to be sorted
     * @param a the array to be sorted
     * @param c the comparator to determine the order of the array. A null value indicates that the
     *     elements' natural ordering should be used.
     * @return the sorted array, which is the same object as {@code a}, but with a refined type
     */
    @SuppressWarnings("determinism") // sorting
    public static <T> @PolyDet("down") T @PolyDet("down") [] sort(
            T[] a, @Nullable Comparator<? super T> c) {
        Arrays.sort(a, c);
        return a;
    }

    ///
    /// Duplicates
    ///

    /**
     * Returns true if the given iterator contains duplicate elements.
     *
     * @param <T> the type of elements
     * @param iterator an iterator
     * @return true if the given iterator contains duplicate elements
     */
    public static <T> @PolyDet("down") boolean hasDuplicate(
            @PolyDet Iterable<? extends @PolyDet("use") T> iterator) {
        Set<T> set = new HashSet<T>();
        for (T each : iterator) {
            if (!set.add(each)) {
                // Already contained the element.
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param <T> the type of elements
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    @SafeVarargs
    public static <T> @PolyDet("down") boolean hasDuplicate(@PolyDet("use") T @PolyDet ... a) {
        return hasDuplicate(Arrays.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") boolean @PolyDet ... a) {
        return hasDuplicate(Booleans.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") byte @PolyDet ... a) {
        return hasDuplicate(Bytes.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") char @PolyDet ... a) {
        return hasDuplicate(Chars.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") float @PolyDet ... a) {
        return hasDuplicate(Floats.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") short @PolyDet ... a) {
        return hasDuplicate(Shorts.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") int @PolyDet ... a) {
        return hasDuplicate(Ints.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements. Equality checking uses {@link
     * Double#equals}.
     *
     * @param a an the given array iffs a does not contain duplicate elements
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") double @PolyDet ... a) {
        return hasDuplicate(Doubles.asList(a));
    }

    /**
     * Returns true if the given array contains duplicate elements.
     *
     * @param a an array
     * @return true if the given array contains duplicate elements
     */
    public static @PolyDet("down") boolean hasDuplicate(@PolyDet("use") long @PolyDet ... a) {
        return hasDuplicate(Longs.asList(a));
    }
}
