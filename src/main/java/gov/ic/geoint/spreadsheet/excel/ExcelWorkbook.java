package gov.ic.geoint.spreadsheet.excel;

import gov.ic.geoint.spreadsheet.ICell;
import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.ISheet;
import gov.ic.geoint.spreadsheet.IWorkbook;
import gov.ic.geoint.spreadsheet.util.HashUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 */
public class ExcelWorkbook implements IWorkbook {

    private final File file;
    private final Workbook wb;
    private final static Logger logger = Logger.getLogger(ExcelWorkbook.class.getName());

    public ExcelWorkbook(File file) {
        this.file = file;
        if (file.exists()) {
            try (InputStream in = new BufferedInputStream(new FileInputStream(file));) {
                this.wb = new HSSFWorkbook(in);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            this.wb = new HSSFWorkbook();
        }
    }

    @Override
    public int numSheets() {
        return wb.getNumberOfSheets();
    }

    @Override
    public ISheet getSheet(String sheetName) {
        return ExcelSheet.create(wb.getSheet(sheetName));
    }

    @Override
    public synchronized void addRow(IRow row) {

        Sheet sheet = wb.getSheet(row.getSheetName());
        if (sheet == null) {
            sheet = wb.createSheet(row.getSheetName());
        }

        final int lastRow = sheet.getLastRowNum();
        Row r = sheet.getRow(lastRow);
        if (r == null) {
            //we're here if this is the first row in the sheet
            r = sheet.createRow(lastRow);
        } else {
            //we're here if the row already existed, create the next one
            r = sheet.createRow(lastRow + 1);
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Adding row {0} to sheet {1}",
                    new Object[]{r.getRowNum(), sheet.getSheetName()});
        }

        for (ICell cell : row) {
            Cell c = r.createCell(cell.getColumnNum());
            c.setCellValue(cell.getValue());
        }
    }

    @Override
    public String[] getSheetNames() {
        final int numSheets = numSheets();
        final String[] names = new String[numSheets];
        for (int i = 0; i < numSheets; i++) {
            names[i] = wb.getSheetName(i);
        }
        return names;
    }

    @Override
    public byte[] getHash() {
        return HashUtil.hash(this);
    }

    @Override
    public Iterator<ISheet> iterator() {
        return new ExcelSheetIterator(wb);
    }

    @Override
    public void save() throws IOException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            wb.write(out);
        }
    }

    private static class ExcelSheetIterator implements Iterator<ISheet> {

        private final Workbook wb;
        private int position;

        public ExcelSheetIterator(Workbook wb) {
            this.wb = wb;
        }

        @Override
        public boolean hasNext() {
            return wb.getNumberOfSheets() >= position;
        }

        @Override
        public ISheet next() {
            try {
                return ExcelSheet.create(wb.getSheetAt(position));
            } finally {
                position++;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
