package gov.ic.geoint.spreadsheet;

import java.io.IOException;

/**
 *
 */
public interface IWorkbook extends Hashable, Iterable<ISheet> {

    /**
     *
     * @return the number of sheets contained within the workbook
     */
    int numSheets();

    /**
     *
     * @param sheetName
     * @return the sheet or null
     */
    ISheet getSheet(String sheetName);

    /**
     * Return all of the current sheet names.
     *
     * @return
     */
    String[] getSheetNames();

    /**
     * Adds the row to the workbook
     *
     * @param row
     */
    void addRow(IRow row);

    public void save() throws IOException;

}
