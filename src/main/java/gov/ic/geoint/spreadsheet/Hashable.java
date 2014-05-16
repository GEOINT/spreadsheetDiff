package gov.ic.geoint.spreadsheet;

/**
 *
 */
public interface Hashable {

    /**
     * Return a cryptographic hash for the object.
     *
     * The hash is unique for the value of the object...if the value changes,
     * the hash changes.
     *
     * @return
     */
    public byte[] getHash();
}
