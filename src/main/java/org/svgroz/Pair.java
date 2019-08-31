package org.svgroz;

public interface Pair<L, R> {
    static <L, R> ImmutablePair<L, R> of(
            final L left,
            final R right
    ) {
        return new ImmutablePair<>(left, right);
    }

    L getLeft();

    R getRight();
}
