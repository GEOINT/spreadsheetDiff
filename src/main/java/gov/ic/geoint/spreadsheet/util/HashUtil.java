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
    private final static byte[] INPUT_SEPARATOR = "//".getBytes();

    public static byte[] hash(ICell c) {
        MessageDigest md = getDigest();
        return md.digest(c.getValue().getBytes());
    }

    public static byte[] hash(IRow r) {
        MessageDigest md = getDigest();
        for (ICell c : r.getCells()) {
            md.update(c.getValue().getBytes());
            md.update(INPUT_SEPARATOR); //trailing separator...it doesn't matter
        }
        return md.digest();
    }

    public static byte[] hash(ISheet s) {
        MessageDigest md = getDigest();
        for (IRow r : s.getRows()) {
            md.update(r.getHash());
            md.update(INPUT_SEPARATOR); //trailing separator...it doesn't matter
        }
        return md.digest();
    }

    public static byte[] hash(IWorkbook w) {
        MessageDigest md = getDigest();
        for (ISheet s : w.getSheets()) {
            md.update(s.getHash());
            md.update(INPUT_SEPARATOR);
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
