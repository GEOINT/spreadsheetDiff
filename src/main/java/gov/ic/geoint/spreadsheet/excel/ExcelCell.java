package gov.ic.geoint.spreadsheet.excel;

import gov.ic.geoint.spreadsheet.ICell;
import gov.ic.geoint.spreadsheet.util.HashUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.Cell.*;
import org.apache.poi.ss.usermodel.CellValue;

/**
 *
 */
public class ExcelCell implements ICell {

    private static final Map<Cell, ExcelCell> cache
            = Collections.synchronizedMap(new WeakHashMap<Cell, ExcelCell>());
    private final Cell cell;
    private HSSFFormulaEvaluator evaluator;
    private final static String DATE_FORMAT = "dd MMM yyyy";

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
        switch (cell.getCellType()) {
            case CELL_TYPE_BLANK:
                return "";
            case CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date
                            = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                    DateFormat format = new SimpleDateFormat(DATE_FORMAT);
                    return format.format(date);
                } else {
                    final double value = cell.getNumericCellValue();
                    //if it's a whole number, drop the decimal ".0"
                    if (Math.rint(value) == value) {
                        return String.valueOf((int) value);
                    }
                    return String.valueOf(value);
                }
            case CELL_TYPE_FORMULA:
                CellValue value = getFormulaEvaluator().evaluate(cell);
                return value.toString().trim();
            case CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case CELL_TYPE_ERROR:
                return "!error!";
            default:
                return "";
        }
    }

    private synchronized HSSFFormulaEvaluator getFormulaEvaluator() {
        if (evaluator == null) {
            evaluator = new HSSFFormulaEvaluator((HSSFWorkbook) cell.getSheet().getWorkbook());
        }
        return evaluator;
    }

}
