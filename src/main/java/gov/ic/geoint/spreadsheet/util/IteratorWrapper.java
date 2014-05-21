package gov.ic.geoint.spreadsheet.util;

import java.util.Iterator;

/**
 *
 * @param <T>
 * @param <F>
 */
public abstract class IteratorWrapper<T, F> implements Iterator<T> {

    private final Iterator<F> iterator;

    public IteratorWrapper(Iterator<F> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return convert(iterator.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

    abstract protected T convert(F from);

}
