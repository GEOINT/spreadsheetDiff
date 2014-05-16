package gov.ic.geoint.spreadsheet.excel;

import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.ISheet;
import java.util.Map;
import java.util.WeakHashMap;
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

    public static ISheet create(Sheet sheet) {
        if (cache.containsKey(sheet)) {
            return cache.get(sheet);
        } else {
            ExcelSheet s = new ExcelSheet(sheet);
            cache.put(sheet, s);
            return s;
        }
    }

    @Override
    public IRow[] getRows() {
        
    }

    @Override
    public IRow getRow(int rowNum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getHash() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
