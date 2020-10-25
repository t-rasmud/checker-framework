package org.checkerframework.dataflow.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.checkerframework.javacutil.BugInCF;

/**
 * Base class for arbitrary-size sets that are very efficient (more efficient than HashSet) for 0
 * and 1 elements.
 */
@SuppressWarnings("determinism") // not type checking this collection
public abstract class AbstractMostlySingleton<T extends Object> implements Set<T> {

    /** The possible states of this set. */
    public enum State {
        /** An empty set. */
        EMPTY,
        /** A singleton set. */
        SINGLETON,
        /** A set of arbitrary size. */
        ANY
    }

    /** The current state. */
    protected State state;
    /** The current value, non-null when the state is SINGLETON. */
    protected @Nullable T value;
    /** The wrapped set, non-null when the state is ANY. */
    protected @Nullable Set<T> set;

    /** Create an AbstractMostlySingleton. */
    protected @OrderNonDet AbstractMostlySingleton(State s) {
        this.state = s;
        this.value = null;
    }

    /** Create an AbstractMostlySingleton. */
    protected @OrderNonDet AbstractMostlySingleton(State s, T v) {
        this.state = s;
        this.value = v;
    }

    @Override
    public @PolyDet("down") int size(@PolyDet AbstractMostlySingleton<T> this) {
        switch (state) {
            case EMPTY:
                return 0;
            case SINGLETON:
                return 1;
            case ANY:
                assert set != null : "@AssumeAssertion(nullness): set initialized before";
                return set.size();
            default:
                throw new BugInCF("Unhandled state " + state);
        }
    }

    @Override
    public @PolyDet("down") boolean isEmpty(@PolyDet AbstractMostlySingleton<T> this) {
        return size() == 0;
    }

    @Override
    public @PolyDet Iterator<T> iterator(@PolyDet AbstractMostlySingleton<T> this) {
        switch (state) {
            case EMPTY:
                return Collections.emptyIterator();
            case SINGLETON:
                return new Iterator<T>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public T next() {
                        if (hasNext) {
                            hasNext = false;
                            assert value != null
                                    : "@AssumeAssertion(nullness): previous add is non-null";
                            return value;
                        }
                        throw new NoSuchElementException();
                    }

                    @Override
                    public void remove() {
                        state = State.EMPTY;
                        value = null;
                    }
                };
            case ANY:
                assert set != null : "@AssumeAssertion(nullness): set initialized before";
                return set.iterator();
            default:
                throw new BugInCF("Unhandled state " + state);
        }
    }

    @Override
    public @PolyDet String toString(@PolyDet AbstractMostlySingleton<T> this) {
        switch (state) {
            case EMPTY:
                return "[]";
            case SINGLETON:
                return "[" + value + "]";
            case ANY:
                assert set != null : "@AssumeAssertion(nullness): set initialized before";
                return set.toString();
            default:
                throw new BugInCF("Unhandled state " + state);
        }
    }

    @Override
    public @PolyDet("down") boolean addAll(
            @PolyDet AbstractMostlySingleton<T> this, @PolyDet("use") Collection<? extends T> c) {
        boolean res = false;
        for (T elem : c) {
            res |= add(elem);
        }
        return res;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S> @Nullable S @PolyNull [] toArray(S @PolyNull [] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(@Nullable Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
