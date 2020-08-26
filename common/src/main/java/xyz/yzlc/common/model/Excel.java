package xyz.yzlc.common.model;

/**
 * @author yzlc
 */
public class Excel {
    private int row;
    private int cell;
    private String value;

    public Excel(int row, int cell, String value) {
        this.row = row;
        this.cell = cell;
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public int getCell() {
        return cell;
    }

    public String getValue() {
        return value;
    }

    public void appendValue(String value) {
        this.value += "\n" + value;
    }

    @Override
    public String toString() {
        return "Excel{" +
                "row=" + row +
                ", cell=" + cell +
                ", value='" + value + '\'' +
                '}';
    }
}
