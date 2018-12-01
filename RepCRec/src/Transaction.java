// Saves the instance of a Transaction

public class Transaction {

	int transaction_ID;
	int start_time;
	String state; // State of the transaction: Running, Waiting, Abort, Commit
	//
	
	
	Transaction(int transaction_ID, int start_time) {
		
		this.transaction_ID = transaction_ID;
		this.start_time	= start_time;
		
	}
	
}
