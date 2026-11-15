package com.umm.strukturdata.datastructure;

public class ArrayStack<T> {
    private static final int INITIAL_CAPACITY = 16;
    private Object[] elements;
    private int top;

    public ArrayStack() {
        this.elements = new Object[INITIAL_CAPACITY];
        this.top = 0;
    }

    public void push(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Item stack tidak boleh null");
        }
        if (top == elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            for (int i = 0; i < top; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
        elements[top++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack kosong (underflow)");
        }
        T item = (T) elements[--top];
        elements[top] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack kosong");
        }
        return (T) elements[top - 1];
    }

    @SuppressWarnings("unchecked")
    public T getFromTop(int offset) {
        if (offset < 0 || offset >= top) {
            throw new IndexOutOfBoundsException("Offset stack tidak valid: " + offset);
        }
        return (T) elements[top - 1 - offset];
    }

    public int size() {
        return top;
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public boolean validateInvariant() {
        if (elements == null || elements.length == 0) return false;
        if (top < 0 || top > elements.length) return false;
        for (int i = 0; i < top; i++) {
            if (elements[i] == null) return false;
        }
        for (int i = top; i < elements.length; i++) {
            if (elements[i] != null) return false;
        }
        return true;
    }
}