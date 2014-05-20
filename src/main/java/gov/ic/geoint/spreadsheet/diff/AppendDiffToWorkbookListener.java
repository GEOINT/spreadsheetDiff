package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.IWorkbook;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Appends the diff events to a new workbook
 */
public class AppendDiffToWorkbookListener implements DiffListener {

    private final IWorkbook workbook;
    private final static Logger logger = Logger.getLogger(AppendDiffToWorkbookListener.class.getName());

    public AppendDiffToWorkbookListener(IWorkbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public void newRow(IRow row) {
        workbook.addRow(row);
    }

    @Override
    public void complete() {
        try {
            workbook.save();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Problems saving diff file.", ex);
        }
    }

}
