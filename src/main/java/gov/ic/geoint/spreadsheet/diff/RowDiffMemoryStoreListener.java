package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.IRow;
import java.util.ArrayList;
import java.util.List;

/**
 * Listens to the diff callback for rows caches them in heap memory
 */
public class RowDiffMemoryStoreListener implements DiffListener {

    private final List<IRow> rows = new ArrayList<>();

    public List<IRow> getRows() {
        return rows;
    }

    @Override
    public void newRow(IRow row) {
        rows.add(row);
    }

    @Override
    public void complete() {
    }

}
