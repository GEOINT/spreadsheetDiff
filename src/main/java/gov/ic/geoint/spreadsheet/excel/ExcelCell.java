package gov.ic.geoint.spreadsheet.excel;

import gov.ic.geoint.spreadsheet.ICell;
import gov.ic.geoint.spreadsheet.util.HashUtil;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 */
public class ExcelCell implements ICell {

    private static final Map<Cell, ExcelCell> cache = new WeakHashMap<>();
    private final Cell cell;

    public ExcelCell(Cell cell) {
        this.cell = cell;
    }

    public static ICell create(Cell cell) {
        if (cache.containsKey(cell)) {
            return cache.get(cell);
        } else {
            ExcelCell c = new ExcelCell(cell);
            cache.put(cell, c);
            return c;
        }
    }

    @Override
    public byte[] getHash() {
        return HashUtil.hash(this);
    }

    @Override
    public int getColumnNum() {
        return cell.getColumnIndex();
    }

    @Override
    public int getRowNum() {
        return cell.getRowIndex();
    }

    @Override
    public String getValue() {
        return cell.getStringCellValue();
    }

}
