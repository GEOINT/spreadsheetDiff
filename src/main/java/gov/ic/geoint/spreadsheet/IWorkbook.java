package gov.ic.geoint.spreadsheet;

/**
 *
 */
public interface IWorkbook extends Hashable {

    /**
     *
     * @return the number of sheets contained within the workbook
     */
    int numSheets();

    /**
     *
     * @param sheetNum
     * @return the sheet or null
     */
    ISheet getSheet(int sheetNum);

    /**
     * Adds the row to the workbook
     * 
     * @param row 
     */
    void addRow(IRow row);

    public ISheet[] getSheets();
}
