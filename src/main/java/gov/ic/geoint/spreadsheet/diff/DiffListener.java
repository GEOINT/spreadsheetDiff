package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.IRow;

/**
 * Callback interface for diff checking.
 */
public interface DiffListener {

    void newRow(IRow row);

    /**
     * Called when diff is complete
     */
    void complete();
    //TODO add more methods for callback...this is all I needed for now
}
