
package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.IWorkbook;

/**
 *
 */
public final class DiffFactory {
/**
     * Find the difference between the two workbooks based on row contents.
     * 
     * @param base
     * @param change
     * @return 
     */
    public static RowChangeDiff findChangedRows (IWorkbook base, IWorkbook change) {
        return new RowChangeDiff(base, change);
    }
}
