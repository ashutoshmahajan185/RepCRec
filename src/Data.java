// Represents an instance of a data item
// Data Items initialized site-wise
public class Data {
	
	int data_index;
	int data_value;
	
	/**
	 * @author Ashutosh Mahajan
	 * @param data_index
	 */
	Data(int data_index) {
	
		this.data_index = data_index;
		data_value = 10 * data_index;
	
	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
	public void initializeDataItem() {
		data_value = 10 * data_index;
	}
	
	/**
	 * @author Tushar Anchan
	 * @param value
	 */
	void setValue(int value) {
		this.data_value = value;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	protected Data clone() {
		Data d = new Data(this.data_index);
		d.data_value = this.data_value;
		return d;
	}

}