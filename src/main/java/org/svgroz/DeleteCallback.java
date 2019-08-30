package org.svgroz;

@FunctionalInterface
public interface DeleteCallback<T> {
    void delete(T t);
}
