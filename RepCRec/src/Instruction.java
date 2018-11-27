
public class Instruction {
	
	String operation; // Specifies whether to read or write
	int data_item; // Specifies the data item to be read or written
	Integer write_value; // Specifies the value to be written
	// Integer used so that the value can be NULL for read operation and not zero
	
	// Read constructor
	Instruction(int data_item) {
		
		this.operation = "read";
		this.data_item = data_item;
		this.write_value = null;
		
	}
	

}
