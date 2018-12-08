import java.util.ArrayList;

public class Instruction {
	
	String operation; // Specifies whether to read or write
	int data_item; // Specifies the data item to be read or written
	Integer write_value; // Specifies the value to be written
	// Integer used so that the value can be NULL for read operation and not zero
	int transaction_id;
	ArrayList<Site> accessSites = new ArrayList<Site>();
	boolean checkReadPermission = false;
	// Read constructor
	/**
	 * @author Ashutosh Mahajan
	 */
	Instruction(int data_item,int transaction_id) {
		
		this.operation = "read";
		this.data_item = data_item;
		this.write_value = null;
		this.transaction_id = transaction_id;
		
	}
	//write constructor
	/**
	 * @author Ashutosh Mahajan
	 */
	Instruction(int data_item,int transaction_id,int write_value){
		this.operation = "write";
		this.data_item = data_item;
		this.write_value = write_value;
		this.transaction_id = transaction_id;
	}
	
	/**
	 * @author Tushar Anchan
	 */
    String getOperation() {
    	return this.operation;
    }
    
    /**
	 * @author Tushar Anchan
	 */
    int getdata_item() {
    	return this.data_item;
    }
    
    /**
	 * @author Tushar Anchan
	 */
    void setAccessSites(ArrayList<Site> sites) {
    	this.accessSites = sites;
    }
    
    /**
	 * @author Tushar Anchan
	 */
    ArrayList<Site> getAccessSites() {
    	return this.accessSites;
    }
    
    /**
	 * @author Tushar Anchan
	 */
    void denyReadPermission() {
    	this.checkReadPermission = true;
    }
    
    /**
	 * @author Ashutosh Mahajan
	 */
    public String toString() {
    	
    	String result = "\nOperation: " + operation + "\nData Item: " + data_item + "\nTransaction: " + transaction_id;
   
		return result;
    }
    
}