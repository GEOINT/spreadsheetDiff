package gov.ic.geoint.spreadsheet.excel;

import gov.ic.geoint.spreadsheet.ICell;
import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.util.HashUtil;
import gov.ic.geoint.spreadsheet.util.IteratorWrapper;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 */
public class ExcelRow implements IRow {

    private static final Map<Row, ExcelRow> cache
            = Collections.synchronizedMap(new WeakHashMap<Row, ExcelRow>());
    private final Row row;

    private ExcelRow(Row r) {
        this.row = r;
    }

    public static IRow create(Row row) {
        if (cache.containsKey(row)) {
            return cache.get(row);
        } else {
            ExcelRow r = new ExcelRow(row);
            cache.put(row, r);
            return r;
        }
    }

    @Override
    public byte[] getHash() {
        return HashUtil.hash(this);
    }

    @Override
    public int getRowNumber() {
        return row.getRowNum();
    }

    @Override
    public String getSheetName() {
        return row.getSheet().getSheetName();
    }

    @Override
    public Iterator<ICell> iterator() {
        return new ExcelCellIterator(row.iterator());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("row: sheet \"")
                .append(row.getSheet().getSheetName())
                .append("\"; row number \"")
                .append(row.getRowNum())
                .append("\"");
        return sb.toString();
    }

    private static class ExcelCellIterator extends IteratorWrapper<ICell, Cell> {

        public ExcelCellIterator(Iterator<Cell> iterator) {
            super(iterator);
        }

        @Override
        protected ICell convert(Cell from) {
            return ExcelCell.create(from);
        }

    }

}
