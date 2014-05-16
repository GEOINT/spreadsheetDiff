
package gov.ic.geoint.spreadsheet.diff;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class ObservableDiff implements Diff {

    protected final List<DiffListener> listeners = new ArrayList<>();
    
    public void addListener(DiffListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(DiffListener listener) {
        this.listeners.remove(listener);
    }
}
