package gov.ic.geoint.spreadsheet;

/**
 *
 */
public interface IRow extends Hashable, Iterable<ICell> {

    int getRowNumber();

    public String getSheetName();
}
