package gov.ic.geoint.spreadsheet;

/**
 *
 */
public interface ISheet extends Hashable, Iterable<IRow> {

    IRow getRow(int rowNum);

    String getName();
}
