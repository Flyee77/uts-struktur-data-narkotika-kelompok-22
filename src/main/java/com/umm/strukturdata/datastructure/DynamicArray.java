package com.umm.strukturdata.datastructure;

public class DynamicArray<T> implements StrukturData<T> {
    private static final int INITIAL_CAPACITY = 10;
    private Object[] data;
    private int size;

    public DynamicArray() {
        this.data = new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Kapasitas awal harus lebih besar dari 0");
        }
        this.data = new Object[initialCapacity];
        this.size = 0;
    }

    private void ensureCapacity() {
        if (size >= data.length) {
            int newCapacity = data.length * 2;
            Object[] newData = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newData[i] = data[i];
            }
            data = newData;
        }
    }

    @Override
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Elemen tidak boleh null");
        }
        ensureCapacity();
        data[size++] = item;
    }

    @Override
    public void insert(int index, T item) {
        if (item == null) {
            throw new IllegalArgumentException("Elemen tidak boleh null");
        }
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Indeks di luar batas: " + index);
        }
        ensureCapacity();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = item;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indeks di luar batas: " + index);
        }
        return (T) data[index];
    }

    @Override
    public void set(int index, T item) {
        if (item == null) {
            throw new IllegalArgumentException("Elemen tidak boleh null");
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indeks di luar batas: " + index);
        }
        data[index] = item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indeks di luar batas: " + index);
        }
        T removedItem = (T) data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
        return removedItem;
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean validateInvariant() {
        if (data == null || data.length == 0) return false;
        if (size < 0 || size > data.length) return false;
        for (int i = 0; i < size; i++) {
            if (data[i] == null) return false;
        }
        for (int i = size; i < data.length; i++) {
            if (data[i] != null) return false;
        }
        return true;
    }
}