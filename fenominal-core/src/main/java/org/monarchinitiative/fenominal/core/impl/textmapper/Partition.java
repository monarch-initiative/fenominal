package org.monarchinitiative.fenominal.core.impl.textmapper;


import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;


public final class Partition<T> extends AbstractList<List<T>> {

    private final List<T> list;
    private final int chunkSize;

    public Partition(List<T> list, int chunkSize) {
        this.list = new ArrayList<>(list);
        this.chunkSize = chunkSize;
    }

    public static <T> Partition<T> ofSize(List<T> list, int chunkSize) {
        return new Partition<>(list, chunkSize);
    }

    @Override
    public List<T> get(int index) {
        int end = Math.min(index + chunkSize, list.size());
        if (index > end) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of the list range <0," + (size() - 1) + ">");
        }
        return new ArrayList<>(list.subList(index, end));
    }

    @Override
    public int size() {
        return list.size() - chunkSize + 1;
    }
}
