package gov.ic.geoint.spreadsheet.excel;

import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.ISheet;
import gov.ic.geoint.spreadsheet.util.HashUtil;
import gov.ic.geoint.spreadsheet.util.IteratorWrapper;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 */
public class ExcelSheet implements ISheet {

    private static Map<Sheet, ExcelSheet> cache = new WeakHashMap<>();
    private final Sheet sheet;

    private ExcelSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * Create ExcelSheet if not found in cache.
     *
     * If sheet is null, returns null
     *
     * @param sheet or null
     * @return
     */
    public static ISheet create(Sheet sheet) {
        if (sheet == null) {
            return null;
        }

        if (cache.containsKey(sheet)) {
            return cache.get(sheet);
        } else {
            ExcelSheet s = new ExcelSheet(sheet);
            cache.put(sheet, s);
            return s;
        }
    }

    @Override
    public IRow getRow(int rowNum) {
        return ExcelRow.create(sheet.getRow(rowNum));
    }

    @Override
    public byte[] getHash() {
        return HashUtil.hash(this);
    }

    @Override
    public Iterator<IRow> iterator() {
        return new ExcelRowIterator(sheet.iterator());
    }

    @Override
    public int numRows() {
        return sheet.getPhysicalNumberOfRows();
    }

    @Override
    public String getName() {
        return sheet.getSheetName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sheet '").append(sheet.getSheetName()).append("'");
        return sb.toString();
    }

    private static class ExcelRowIterator extends IteratorWrapper<IRow, Row> {

        public ExcelRowIterator(Iterator<Row> iterator) {
            super(iterator);
        }

        @Override
        protected IRow convert(Row from) {
            return ExcelRow.create(from);
        }

    }
}
