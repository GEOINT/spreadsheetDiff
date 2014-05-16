
package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.IWorkbook;

/**
 * Appends the diff events to a new workbook
 */
public class AppendDiffToWorkbookListener implements DiffListener{

    private final IWorkbook workbook;

    public AppendDiffToWorkbookListener(IWorkbook workbook) {
        this.workbook = workbook;
    }
    
    @Override
    public void newRow(IRow row) {
       workbook.addRow(row);
    }

}
