package gov.ic.geoint.spreadsheet;

/**
 *
 */
public interface ICell extends Hashable {

    /**
     *
     * @return
     */
    public int getColumnNum();

    /**
     *
     * @return
     */
    public int getRowNum();

    /**
     *
     * @return
     */
    public String getValue();

}
