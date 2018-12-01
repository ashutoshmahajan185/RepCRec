import java.util.ArrayList;

// Instance of a Transaction Manager. Never Fails

public class TransactionManager {
	
	ArrayList<Site> sites = new ArrayList<Site>();
	ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	
	TransactionManager() {
		initializeSites();
	}
	
	public void initializeSites() {
		
		for(int i = 1; i <= 10; i ++)
		sites.add(new Site(i));
		
	}
	
	public void beginTransaction(int transaction_ID, int timer) {
		
		transactions.add(new Transaction(transaction_ID, timer));
		
	}
	

}
