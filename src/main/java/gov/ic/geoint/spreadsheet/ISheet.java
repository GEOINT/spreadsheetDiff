
package gov.ic.geoint.spreadsheet;

/**
 *
 */
public interface ISheet  extends Hashable{

    IRow[] getRows();
    IRow getRow(int rowNum);
}
