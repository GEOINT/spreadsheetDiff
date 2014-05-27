package gov.ic.geoint.spreadsheet.util;

import gov.ic.geoint.spreadsheet.ICell;
import gov.ic.geoint.spreadsheet.IRow;
import gov.ic.geoint.spreadsheet.ISheet;
import gov.ic.geoint.spreadsheet.IWorkbook;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class HashUtil {

    private final static String DIGEST_TYPE = "MD5"; //TODO make this configurable

    /**
     * Returns hash bytes or null if the value is blank/null
     *
     * @param c
     * @return
     */
    public static byte[] hash(ICell c) {
        String value = c.getValue();
        if (value == null || value.contentEquals("")) {
            return null;
        }
        MessageDigest md = getDigest();
        return md.digest(value.getBytes());
    }

    /**
     * Returns hash bytes or null if the cells of the row contains no contents
     *
     * @param r
     * @return
     */
    public static byte[] hash(IRow r) {
        MessageDigest md = getDigest();
        boolean hasContents = false;
        for (ICell c : r) {
            //don't bother adjusting the hash if the cell is null
            byte[] cellDigest = hash(c);
            if (cellDigest != null) {
                hasContents = true;
                md.update(cellDigest);
            }
        }
        return (hasContents) ? md.digest() : null;
    }

    public static byte[] hash(ISheet s) {
        MessageDigest md = getDigest();
        for (IRow r : s) {
            byte[] rowHash = r.getHash();
            if (rowHash != null) {
                md.update(r.getHash());
            }
        }
        return md.digest();
    }

    public static byte[] hash(IWorkbook w) {
        MessageDigest md = getDigest();
        for (ISheet s : w) {
            md.update(s.getHash());
        }
        return md.digest();
    }

    private static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance(DIGEST_TYPE);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unexpected error, hashing algorith "
                    + DIGEST_TYPE + " is not available.");
        }
    }
}
