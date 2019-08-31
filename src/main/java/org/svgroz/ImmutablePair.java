package org.svgroz;

import java.util.Objects;

public class ImmutablePair<L, R> implements Pair<L, R> {
    private final L left;
    private final R right;

    public ImmutablePair(
            final L left,
            final R right
    ) {
        this.left = left;
        this.right = right;
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "ImmutablePair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
