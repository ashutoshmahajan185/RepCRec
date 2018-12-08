import java.util.ArrayList;

// This is an instance of a Site
// Contains all the attributes and the required functions

public class Site {
	
	int site_ID = 0;
	String state; // State of the site: Up or Down
	boolean can_read = true;
	ArrayList<Data> data_items = new ArrayList<Data>();
	Transaction[] writeLockTable = new Transaction[21];
	ArrayList<ArrayList<Transaction>> readLockTable = new ArrayList<ArrayList<Transaction>>(21);
	
	/**
	 * @author Ashutosh Mahajan
	 */
	Site(int site_ID) {
		
		this.site_ID = site_ID;
		addDataItems();
		this.state = "up";
		for (int i=0; i<21; i++) {
		      readLockTable.add(new ArrayList<Transaction>());
		    }
		
	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
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
	
	/**
	 * @author Tushar Anchan
	 */
	protected Site clone() {
		Site s = new Site(this.site_ID);
		//s.data_items = (ArrayList<Data>) this.data_items.clone();
		s.data_items = new ArrayList<Data>();
		for(Data d:data_items) {
			s.data_items.add(d.clone());
		}
		return s;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void preventReads() {
		this.can_read = false;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void allowReads() {
		this.can_read = true;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	boolean isSiteUp() {
		return this.state.equals("up");
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void failSite() {
		this.state = "down";
		resetReadLockTable();
		resetWriteLockTable();
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void recoverSite() {
		this.state = "up";
		resetReadLockTable();
		resetWriteLockTable();
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void resetReadLockTable() {
		this.readLockTable.clear();
		for (int i=0; i<21; i++) {
		      this.readLockTable.add(new ArrayList<Transaction>());
		    }
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void resetWriteLockTable() {
		this.writeLockTable = new Transaction[21];
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void setWriteLock(Transaction T,int id) {
		writeLockTable[id] = T;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	boolean checkWriteLock(Transaction T, int id) {
		return writeLockTable[id]==T;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void clearWriteLock(int id) {
		writeLockTable[id] = null;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void setReadLock(Transaction T, int id) {
		readLockTable.get(id).add(T);
	}
    
	/**
	 * @author Tushar Anchan
	 */
	void clearReadLock(Transaction T,int id) {
	    readLockTable.get(id).remove(T);
	}
	
	/**
	 * @author Tushar Anchan
	 */
	boolean hasReadLock(Transaction T, int id) {
		return readLockTable.get(id).contains(T);
	}
	
	/**
	 * @author Tushar Anchan
	 */
	boolean isEmptyWriteLock(int id) {
		return writeLockTable[id]==null;
	}
	
	/**
	 * @author Tushar Anchan
	 */
	boolean checkReadLock(int id) {
		return readLockTable.get(id).isEmpty();	
	}
	
	/**
	 * @author Tushar Anchan
	 */
	void writeValue(int id,int value) {
		for(Data data:data_items) {
			if(data.data_index==id) {
				data.setValue(value);
			}
		}
	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
	public String toString() {
		
		String result = "\nSite " + this.site_ID + " --> ";
		for(int j = 0; j < data_items.size(); j ++) {
			int data_index = data_items.get(j).data_index;
			int data_value = data_items.get(j).data_value;
			if(j == data_items.size() - 1) {
				result = result + "x" + data_index + ": " + data_value; 
			} else {
				result = result + "x" + data_index + ": " + data_value + ", "; 
			}
		}
		return result;
	
	}
}