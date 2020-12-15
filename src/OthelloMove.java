public class OthelloMove
{
	private int row;
	private int col;
	
	
	public OthelloMove(int row, int col)
	{
		this.row = row;
		this.col = col;
	}
	
	
	public int getRow()
	{
		return row;
	}
	
	
	public int getColumn()
	{
		return col;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", getRow(), getColumn());
	}
}
