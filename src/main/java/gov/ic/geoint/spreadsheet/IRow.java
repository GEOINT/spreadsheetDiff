package gov.ic.geoint.spreadsheet;

import java.util.List;

/**
 *
 */
public interface IRow extends Hashable {

    int getRowNumber();

    ICell[] getCells();

    public int getSheetNum();
}
