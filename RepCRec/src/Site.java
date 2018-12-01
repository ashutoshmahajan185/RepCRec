import java.util.ArrayList;

// This is an instance of a Site
// Contains all the attributes and the required functions

public class Site {
	
	int site_ID = 0;
	String state; // State of the site: Up or Down

	ArrayList<Data> data_items = new ArrayList<Data>();
	Transaction[] writeLockTable = new Transaction[21];
	ArrayList<ArrayList<Transaction>> readLockTable = new ArrayList<ArrayList<Transaction>>(21);
	
	Site(int site_ID) {
		
		this.site_ID = site_ID;
		addDataItems();
		for (int i=0; i<21; i++) {
		      readLockTable.add(new ArrayList<Transaction>());
		    }
		
	}
	
	void addDataItems() {
		
		// Adding even indexed data items
		for(int i = 1; i <= 10; i ++) {
			data_items.add(new Data(i * 2));
		}
		// Adding odd indexed data items
		for(int i = 0; i < 10; i ++) {
			if(((i * 2 + 1) % 10 + 1) == site_ID) {
				data_items.add(new Data(i * 2 + 1));
			}
			
		}
		
	}

	void initializeSite() {
		
	}
	
	void setWriteLock(Transaction T,int id) {
		writeLockTable[id] = T;
	}
	
	void clearWriteLock(int id) {
		writeLockTable[id] = null;
	}
	
	void setReadLock(Transaction T, int id) {
		readLockTable.get(id).add(T);
	}
    
	void clearReadLock(Transaction T,int id) {
	    readLockTable.get(id).remove(T);	
	}
	
	boolean checkWriteLock(int id) {
		return writeLockTable[id]==null;
	}
	
	boolean checkReadLock(int id) {
		return readLockTable.get(id).isEmpty();	
	}
	
	void writeValue(int id,int value) {
		for(Data data:data_items) {
			if(data.data_index==id) {
				data.setValue(value);
			}
		}
	}
}
