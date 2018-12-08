// Represents an instance of a data item
// Data Items initialized site-wise
public class Data {
	
	int data_index;
	int data_value;
	
	
	
	// Constructor for initializing data items
	Data(int data_index) {
	
		this.data_index = data_index;
		data_value = 10 * data_index;
	
	}
	
	// Method for even indexed data item
	public void initializeDataItem() {
		data_value = 10 * data_index;
	}
	
	int getIndex() {
		return this.data_index;
	}
	
	void setValue(int value) {
		this.data_value = value;
	}
	
	protected Data clone() {
		Data d = new Data(this.data_index);
		d.data_value = this.data_value;
		return d;
	}
}