package com.umm.strukturdata.datastructure;

public interface StrukturData<T> {
    void add(T item);
    void insert(int index, T item);
    T get(int index);
    void set(int index, T item);
    T remove(int index);
    int size();
    boolean isEmpty();
    void clear();
    boolean validateInvariant();
}