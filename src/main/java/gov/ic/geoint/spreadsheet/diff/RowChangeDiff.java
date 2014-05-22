package gov.ic.geoint.spreadsheet.diff;

import gov.ic.geoint.spreadsheet.ICell;
import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.ISheet;
import gov.ic.geoint.spreadsheet.IWorkbook;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
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
    private final static Logger logger = Logger.getLogger(
            RowChangeDiff.class.getName());

    public RowChangeDiff(IWorkbook base, IWorkbook change) {
        this.base = base;
        this.change = change;
    }

    /**
     * Synchronously conduct diff
     *
     * @throws InterruptedException
     */
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

        //notify listeners on separate thread
        BlockingQueue<IRow> rowQueue = new LinkedBlockingQueue<>();
        RowChangeProcessor rowProcessor = new RowChangeProcessor(rowQueue);
        exec.submit(rowProcessor);

        //process each sheet comparison on a separate thread
        List<SheetRowChangeTask> changeTasks = new ArrayList<>();

        //use the sheets from the change workbook to reference those from 
        //the base -- if there are missing sheets in the change sheet, that's 
        //ok...we don't want to display these anyway
        for (String sheetName : change.getSheetNames()) {
            SheetRowChangeTask task
                    = new SheetRowChangeTask(rowQueue, base.getSheet(sheetName),
                            change.getSheet(sheetName));

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Submitting row diff task to compare "
                        + "sheet name ''{0}''", sheetName);
            }

            changeTasks.add(task);
        }

        //submit all spreadsheet compare tasks and synchronously wait for all 
        //to be done
        List<Future<ISheet>> results = exec.invokeAll(changeTasks);

        if (logger.isLoggable(Level.FINE)) {
            for (Future<ISheet> r : results) {
                try {
                    final ISheet sheet = r.get();
                    logger.log(Level.FINE, "Spreadsheet row change task "
                            + "complete for sheet {0}", sheet.getName());
                } catch (ExecutionException | InterruptedException ex) {
                    logger.log(Level.SEVERE, "Spreadsheet row change task did "
                            + "not complete successfully", ex);
                }
            }
        }

        //tell the processing thread to shut down by passing poison pill
        rowQueue.add(new PoisonRow());
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.DAYS); //todo make this configurable
        for (DiffListener l : listeners) {
            l.complete();
        }
    }

    /**
     * made this a callable because I wanted to use ExecutorService#invokeAll,
     * return the comparison sheet
     */
    private class SheetRowChangeTask implements Callable<ISheet> {

        private final ISheet baseSheet;
        private final ISheet changeSheet;
        private final Queue<IRow> queue;

        public SheetRowChangeTask(Queue<IRow> queue, ISheet baseSheet,
                ISheet changeSheet) {
            this.baseSheet = baseSheet;
            this.changeSheet = changeSheet;
            this.queue = queue;
        }

        @Override
        public ISheet call() {

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Running sheet row change diff for "
                        + "sheet {0}; there are {1} rows",
                        new Object[]{changeSheet.getName(), changeSheet.numRows()});
            }

            try {
                Set<byte[]> baseRowHashes = new TreeSet<>(new ByteArrayComparator());

                //first get row hashes for all rows in the base sheet
                if (baseSheet != null) { //may be null if sheet is new
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "finding row hashes on "
                                + "base sheet {0}", baseSheet.getName());
                    }
                    for (IRow r : baseSheet) {
                        final byte[] rowHash = r.getHash();
                        if (rowHash != null) {
                            if (logger.isLoggable(Level.FINEST)) {
                                logger.log(Level.FINEST, "Hash for row {0}:{1} is {2}",
                                        new Object[]{baseSheet.getName(),
                                            r.getRowNumber(), new String(rowHash)});
                            }
                            baseRowHashes.add(rowHash);
                        } else {
                            if (logger.isLoggable(Level.FINEST)) {
                                logger.log(Level.FINEST, "No hash for row {0}:{1}",
                                        new Object[]{baseSheet.getName(),
                                            r.getRowNumber()});
                            }
                        }
                    }
                } else {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "basesheet for spreadsheet "
                                + "'{0}' is null", changeSheet.getName());
                    }
                }

                //see if there are rows that do not match existing hashes
                for (IRow r : changeSheet) {
                    final byte[] rowHash = r.getHash();
                    if (rowHash != null && !baseRowHashes.contains(rowHash)) {

                        if (logger.isLoggable(Level.FINEST)) {
                            logger.log(Level.FINEST, "Found new row ''{0}'' in "
                                    + "sheet ''{1}''",
                                    new Object[]{r.getRowNumber(),
                                        changeSheet.getName()});
                        }
                        queue.add(r);
                    } else {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.log(Level.FINEST, "Row {0} in sheet {1} is "
                                    + "blank or not new.",
                                    new Object[]{r.getRowNumber(), r.getSheetName()});
                        }
                    }
                }

            } catch (Throwable ex) {
                logger.log(Level.SEVERE, "Problem running diff on sheet "
                        + changeSheet.getName(), ex);
            }

            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Completed comparing sheets {0}",
                        changeSheet.getName());
            }
            return changeSheet;
        }

    }

    private class RowChangeProcessor implements Runnable {

        private final BlockingQueue<IRow> queue;

        public RowChangeProcessor(BlockingQueue<IRow> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    IRow r = queue.take();
                    if (r instanceof PoisonRow) {
                        //stop processing
                        break;
                    }
                    for (DiffListener l : listeners) {
                        try {
                            l.newRow(r);
                        } catch (Throwable ex) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Error while notifying listener ")
                                    .append(l.getClass().getName())
                                    .append(" with new row notification for row ")
                                    .append(r.toString());
                            logger.log(Level.SEVERE, sb.toString(), ex);
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.interrupted();
                }
            }
        }
    }

    private static class PoisonRow implements IRow {

        @Override
        public int getRowNumber() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public String getSheetName() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public byte[] getHash() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Iterator<ICell> iterator() {
            throw new UnsupportedOperationException("Not supported");
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
