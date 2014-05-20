package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.excel.ExcelWorkbook;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class RowChangeDiffTest {

    private final static String XLS_BASE = "base.xls";
    private final static String XLS_APPEND_ROW = "appendedRow.xls";
    private final static String XLS_INSERTED_ROW = "insertedRow.xls";
    private final static String XLS_NEW_SHEET = "newSheet.xls";
    
    /**
     * Test that a row simply appended to a sheet is detected
     *
     * @throws Exception
     */
    @Test
    public void testAppendRowChange() throws Exception {
        RowChangeDiff diff = new RowChangeDiff(
                new ExcelWorkbook(getTestFile(XLS_BASE)),
                new ExcelWorkbook(getTestFile(XLS_APPEND_ROW)));

        RowDiffMemoryStoreListener l = new RowDiffMemoryStoreListener();
        diff.addListener(l);
        diff.diff();

        assertEquals("invalid number of changed rows", 1, l.getRows().size());

        IRow row = l.getRows().get(0);
        assertEquals("invalid sheet name", "Sheet1", row.getSheetName());
        assertEquals("invalid row changed", 5, row.getRowNumber()); //0-indexed

    }

    /**
     * test that a row inserted in the center of a sheet results in only a
     * single row change (and not all rows under it to be changed)
     *
     * @throws Exception
     */
    @Test
    public void testInsertedRow() throws Exception {
        RowChangeDiff diff = new RowChangeDiff(
                new ExcelWorkbook(getTestFile(XLS_BASE)),
                new ExcelWorkbook(getTestFile(XLS_INSERTED_ROW)));

        RowDiffMemoryStoreListener l = new RowDiffMemoryStoreListener();
        diff.addListener(l);
        diff.diff();

        assertEquals("invalid number of changed rows", 1, l.getRows().size());

        IRow row = l.getRows().get(0);
        assertEquals("Invalid sheet name", "Sheet1", row.getSheetName());
        assertEquals("invalid row changed", 3, row.getRowNumber());
    }
    
    @Test
    public void testNewSheet() throws Exception {
        RowChangeDiff diff = new RowChangeDiff(
                new ExcelWorkbook(getTestFile(XLS_BASE)),
                new ExcelWorkbook(getTestFile(XLS_INSERTED_ROW)));

        RowDiffMemoryStoreListener l = new RowDiffMemoryStoreListener();
        diff.addListener(l);
        diff.diff();

        assertEquals("invalid number of changed rows", 1, l.getRows().size());

        IRow row = l.getRows().get(0);
        assertEquals("Invalid sheet name", "Sheet4", row.getSheetName());
        assertEquals("invalid row changed", 0, row.getRowNumber());
    }

    private File getTestFile(String fileName) {
        return new File(RowChangeDiffTest.class.getClassLoader().getResource(fileName).getFile());
    }

}
