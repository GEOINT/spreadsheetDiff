package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.ISheet;
import gov.ic.geoint.spreadsheet.IWorkbook;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compares each sheet within the provided workbooks, row by row, to determine
 * if there are any new/updated rows.
 */
public class RowChangeDiff extends ObservableDiff {

    private final IWorkbook base;
    private final IWorkbook change;
    private final static Logger logger = Logger.getLogger(RowChangeDiff.class.getName());

    public RowChangeDiff(IWorkbook base, IWorkbook change) {
        this.base = base;
        this.change = change;
    }

    @Override
    public void diff() throws InterruptedException {
        final int maxSheets = Math.max(base.numSheets(), change.numSheets());

        //use one thread per sheet, up to twice the number of processors available
        ExecutorService exec = Executors.newFixedThreadPool(
                Math.min(Runtime.getRuntime().availableProcessors() * 2,
                        maxSheets));

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Initialized row diff thread pool.");
        }

        //use the sheets from the change workbook to reference those from 
        //the base -- if there are missing sheets in the change sheet, that's 
        //ok...we don't want to display these anyway
        for (String sheetName : change.getSheetNames()) {
            SheetRowChangeTask task
                    = new SheetRowChangeTask(base.getSheet(sheetName),
                            change.getSheet(sheetName));

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Submitting row diff task to compare "
                        + "sheet name ''{0}''", sheetName);
            }

            exec.submit(task);
        }

        //wait for exec to complete
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.DAYS); //todo make this configurable
    }

    private class SheetRowChangeTask implements Runnable {

        private final ISheet baseSheet;
        private final ISheet changeSheet;

        public SheetRowChangeTask(ISheet baseSheet, ISheet changeSheet) {
            this.baseSheet = baseSheet;
            this.changeSheet = changeSheet;
        }

        @Override
        public void run() {

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Running sheet row change diff for "
                        + "sheet {0}", baseSheet.getName());
            }

            Set<byte[]> baseRowHashes = new TreeSet<>(new ByteArrayComparator());

            //first get row hashes for all rows in the base sheet
            for (IRow r : baseSheet) {
                baseRowHashes.add(r.getHash());
            }

            //see if there are rows that do not match existing hashes
            for (IRow r : changeSheet) {
                if (!baseRowHashes.contains(r.getHash())) {

                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "Found new row ''{0}'' in "
                                + "sheet ''{1}''",
                                new Object[]{r.getRowNumber(), changeSheet.getName()});
                    }

                    for (DiffListener l : listeners) {
                        l.newRow(r);
                    }
                }
            }
        }

    }

    private static class ByteArrayComparator implements Comparator<byte[]> {

        @Override
        public int compare(byte[] left, byte[] right) {
            for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
                int a = (left[i] & 0xff);
                int b = (right[j] & 0xff);
                if (a != b) {
                    return a - b;
                }
            }
            return left.length - right.length;
        }
    }
}
