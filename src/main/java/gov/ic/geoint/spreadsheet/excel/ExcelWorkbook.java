package gov.ic.geoint.spreadsheet.excel;

import gov.ic.geoint.spreadsheet.ICell;
import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.ISheet;
import gov.ic.geoint.spreadsheet.IWorkbook;
import gov.ic.geoint.spreadsheet.util.HashUtil;
import java.io.File;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 */
public class ExcelWorkbook implements IWorkbook {

    private final File file;
    private final Workbook wb;

    public ExcelWorkbook(File file) {
        this.file = file;
        this.wb = new HSSFWorkbook();
    }

    @Override
    public int numSheets() {
        return wb.getNumberOfSheets();
    }

    @Override
    public ISheet getSheet(int sheetNum) {
        return ExcelSheet.create(wb.getSheetAt(sheetNum));
    }

    @Override
    public void addRow(IRow row) {
        Row r = wb.getSheetAt(row.getSheetNum()).createRow(row.getRowNumber());
        for (ICell cell : row.getCells()) {
            Cell c = r.createCell(cell.getColumnNum());
            c.setCellValue(cell.getValue());
        }
    }

    @Override
    public ISheet[] getSheets() {
        
    }

    @Override
    public byte[] getHash() {
        return HashUtil.hash(this);
    }

}
