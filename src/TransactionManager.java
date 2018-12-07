import java.util.ArrayList;
import java.util.LinkedList;

/**
* An Instance of the Transaction Manager. It never fails. It is responsible for processing all the instructions and the Transactions
* @author Tushar Anchan
* @author Ashutosh Mahajan
*/

public class TransactionManager {

	ArrayList<Site> sites = new ArrayList<Site>();
	ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	LinkedList<Instruction> waitingInstructions = new LinkedList<Instruction>();

	Graph graph = new Graph();

	boolean addflagToCheckRead = false;

	TransactionManager() {
		initializeSites();
	}

	/**
	 * Initializes all sites. Creates a site instance and stores the list of all sites
	 */
	public void initializeSites() {

		for (int i = 1; i <= 10; i++)
			sites.add(new Site(i));

	}

	/**
	 * Sets the flag to check the permission of a read Instruction
	 */
	void setReadFlag() {
		this.addflagToCheckRead = true;
	}

	/**
	 * Disables the flag to check the permission of a read Instruction
	 */
	void disableReadFlag() {
		this.addflagToCheckRead = false;
	}

	/** 
	 * Fails the site given: sets it as down and resets its locktables
	 * @param site_id
	 */
	public void failSite(int site_id) {
		sites.get(site_id - 1).failSite();
	}

	/**
	 * Recovers the site back and sets the flag to check the read permission in future
	 * @param site_id
	 */
	public void recoverSite(int site_id) {
		sites.get(site_id - 1).recoverSite();
		sites.get(site_id - 1).preventReads();
		setReadFlag();
	}

	/**
	 * method to begin the transaction
	 * @param transaction_ID
	 * @param timer
	 */
	public void beginTransaction(int transaction_ID, int timer) {
		transactions.add(new Transaction(transaction_ID, timer));
	}

	/** 
	 * adds the instruction to the list of instructions in a transaction
	 * @param id
	 * @param I
	 */
	public void addInstruction(int id, Instruction I) {
		Transaction T = transactions.get(id);
		T.addInstruction(I);
	}

	/**
	 * creates a snapshot of the sites
	 * @param id
	 */
	public void createSnapshot(int id) {
		Transaction T = transactions.get(id);
		ArrayList<Site> snapshot = (ArrayList<Site>)sites.clone();
		T.createSnapshot(snapshot);
	}

	/**
	 * This method reads the type of the Instruction to process and calls the appropriate function to process it.
	 * @param I Instruction
	 * @param transaction_id transaction id
	 * @param flag 
	 */
	public void processInstruction(Instruction I, int transaction_id, boolean flag) {
		String operation = I.getOperation();
		Transaction T = transactions.get(transaction_id);
		if (T.readOnly) {
			processReadOnly(I, transaction_id);

		} else {

			if(T.databaseSnapshot == null)
				T.storeAccessSites(sites);

			if(operation.equals("write")) {
				processWrite(I, transaction_id, flag, addflagToCheckRead);
			}
			if(operation.equals("read")) {
				processRead(I, transaction_id, flag, addflagToCheckRead);
			}

		}

	}

	/**
	 * The is the main method to process a Write Instruction. It first calls the request Lock function and grants the lock accordingly or blocks the Instruction in the waiting queue. Note that the write locks are granted to all available sites as per the algorithm.
	 * @param I 
	 * @param transaction_id
	 * @param flag
	 * @param addFlagToCheckRead to check the read permission
	 */
	public void processWrite(Instruction I, int transaction_id, boolean flag, boolean addFlagToCheckRead) {
		String operation = I.getOperation();
		Transaction T = transactions.get(transaction_id);
		T.storeAccessSites(sites);
		T.addSitesToInstruction(I);
		ArrayList<Site> accessedSites = I.getAccessSites();
		if(addFlagToCheckRead)
			I.denyReadPermission();
		if(requestWriteLock(I.data_item, I, flag, accessedSites)) {
			System.out.println("Lock can be acquired");
			//System.out.println("Deadlock detection---" + I);
			graph.reverseEdge("T" + T.transaction_ID, "x" + I.data_item);
			//System.out.println(graph);
			if (I.data_item % 2 == 0) {
				for (Site s : accessedSites) {
					s.setWriteLock(T, I.data_item - 1);
				}
				if (accessedSites.isEmpty()) {
					System.out.println("blocked");
					// add to waiting queue
					waitingInstructions.add(I);
				}
			} else {
				int site_id = 1 + I.data_item % 10;
				Site s = sites.get(site_id - 1);
				int actualSiteIndex = accessedSites.indexOf(s);
				if (actualSiteIndex != -1) {
					Site actualSite = accessedSites.get(actualSiteIndex);
					actualSite.setWriteLock(T, I.data_item - 1);
				} else {
					// else add to waiting maybe
					System.out.println("blocked");
					// add to waiting queue
					waitingInstructions.add(I);
				}
				
			}
		} else {
			System.out.println("blocked");
			// add to waiting queue
			waitingInstructions.add(I);
		}
	}

	/**
	 * Similar to processWrite. The difference here is that after the locks are granted, it also reads the value from the site and outputs to console. Note that the read lock here is granted to the first available site as per the algorithm requirements. If no site is available, the Instruction is blocked.
	 * @param I Instruction
	 * @param transaction_id transaction id
	 * @param flag
	 * @param addFlagToCheckRead to check read permission
	 */
	public void processRead(Instruction I, int transaction_id, boolean flag, boolean addFlagToCheckRead) {
		String operation = I.getOperation();
		Transaction T = transactions.get(transaction_id);
		T.storeAccessSites(sites);
		T.addSitesToInstruction(I);
		ArrayList<Site> accessedSites = I.getAccessSites();
		if (addFlagToCheckRead)
			I.denyReadPermission();
		if (requestReadLock(I.data_item, I, flag, accessedSites)) {
			System.out.println("lock can be acquired");
			graph.reverseEdge("T" + T.transaction_ID, "x" + I.data_item);
			if (I.data_item % 2 == 0) {
				if (!accessedSites.isEmpty()) {
					for (Site s : accessedSites) {
						if (I.checkReadPermission && !s.canRead)
							continue;
						s.setReadLock(T, I.data_item - 1);
						for (Data data : s.data_items) {
							if (data.data_index == I.data_item) {
								System.out.println("Site: " + s.site_ID + " data: " + data.data_value);
							}
						}
						break;
					}
				} else {
					System.out.println("blocked");
					waitingInstructions.add(I);
				}
			} else {
				int site_id = 1 + I.data_item % 10;
				Site s = sites.get(site_id - 1);
				int actualSiteIndex = accessedSites.indexOf(s);
				if (actualSiteIndex != -1) {
					Site actualSite = accessedSites.get(actualSiteIndex);
					actualSite.setReadLock(T, I.data_item - 1);
					if (I.checkReadPermission && !s.canRead)
						System.out.println("read not allowed");
					else {
						for (Data data : s.data_items) {
							if (data.data_index == I.data_item) {
								System.out.println("Site: " + s.site_ID + " data: " + data.data_value);
							}
						}
					}
				} else {
					// else add to waiting maybe
					System.out.println("blocked");
					waitingInstructions.add(I);
				}
				
			}
		} else {
			System.out.println("blocked");
			waitingInstructions.add(I);
		}
	}

	/**
	 * This is read method to process ReadOnly transactions. It accesses the snapshot of the sites when the transaction was created and reads the values from it. This does not request any locks.
	 * @param I Instruction 
	 * @param transaction_id transaction id
	 */
	public void processReadOnly(Instruction I, int transaction_id) {
		Transaction T = transactions.get(transaction_id);
		ArrayList<Site> snapshot = T.getSnapshot();
		if (I.data_item % 2 == 0) {
			doOnce: for (Site s : snapshot) {
				if (s.isSiteUp()) {
					for (Data data : s.data_items) {
						if (data.data_index == I.data_item) {
							System.out.println("Site: " + s.site_ID + " data: " + data.data_value);
						}
						break doOnce;
					}
				}
			}
		} else {
			int site_id = 1 + I.data_item % 10;
			Site s = snapshot.get(site_id - 1);
			if (s.isSiteUp()) {
				for (Data data : s.data_items) {
					if (data.data_index == I.data_item) {
						System.out.println("Site: " + s.site_ID + " data: " + data.data_value);
					}
				}
			} else {
				System.out.println("blocked");
				waitingInstructions.add(I);
			}
		}
	}

	/**
	 * checks if there is an instruction in the waiting queue waiting to get a lock on the same variable as the input Instruction
	 * @param I Instruction
	 * @return
	 */
	boolean isAlreadyWaitingInstruction(Instruction I) {
		for (Instruction W : waitingInstructions) {
			if (W.data_item == I.data_item)
				return true;
		}
		return false;
	}

	/**
	 * This method checks if the Instruction can be given a write lock. It checks all sites of a variable for read locks and write locks and returns a boolean accordingly. This method also handles the case where a read lock is to be promoted into a write lock.
	 * @param data_item variable
	 * @param I Instruction
	 * @param flag
	 * @param sites accessed sites(that was up)
	 * @return
	 */
	boolean requestWriteLock(int data_item, Instruction I, boolean flag, ArrayList<Site> sites) {
		if (!flag && waitingInstructions.size() > 0 && isAlreadyWaitingInstruction(I))
			return false;
		if (data_item % 2 == 0) {
			int countTrues = 0;
			int countFalses = 0;
			for (Site s : sites) {
				if (s.isEmptyWriteLock(data_item - 1) && s.readLockTable.get(data_item - 1).size() == 1
						&& s.readLockTable.get(data_item - 1).get(0).transaction_ID == I.transaction_id) {
					countTrues++;
				}
				if (!s.isEmptyWriteLock(data_item - 1) || !s.checkReadLock(data_item - 1)) {
					countFalses++;
				}

			}
			if (countTrues == 1)
				return true;
			if (countFalses > 0)
				return false;
		} else {
			int site_id = 1 + data_item % 10;
			Site s = sites.get(site_id - 1);
			if (s.isEmptyWriteLock(data_item - 1) && s.readLockTable.get(data_item - 1).size() == 1
					&& s.readLockTable.get(data_item - 1).get(0).transaction_ID == I.transaction_id) {
				return true;
			}
			if (!s.isEmptyWriteLock(data_item - 1) || !s.checkReadLock(data_item - 1)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Similar to the requestWriteLock method, this method checks for any write lock conflicts at all sites of a variable. This method also handles the case where the lock is to be promoted into a write lock.
	 * @param data_item variable
	 * @param I Instruction
	 * @param flag
	 * @param sites accessed sites(that was up)
	 * @return
	 */
	boolean requestReadLock(int data_item, Instruction I, boolean flag, ArrayList<Site> sites) {
		if (!flag && waitingInstructions.size() > 0 && isAlreadyWaitingInstruction(I))
			return false;
		if (data_item % 2 == 0) {
			int countTrues = 0;
			int countFalses = 0;
			for (Site s : sites) {
				if (!s.isEmptyWriteLock(data_item - 1)
						&& s.writeLockTable[I.data_item - 1].transaction_ID == I.transaction_id) {
					countTrues++;
				}
				if (!s.isEmptyWriteLock(data_item - 1)) {
					countFalses++;
				}
			}
			if (countTrues == sites.size())
				return true;
			if (countFalses > 0)
				return false;
		} else {
			int site_id = 1 + data_item % 10;
			Site s = sites.get(site_id - 1);
			if (!s.isEmptyWriteLock(data_item - 1)
					&& s.writeLockTable[I.data_item - 1].transaction_ID == I.transaction_id) {
				return true;
			}
			if (!s.isEmptyWriteLock(data_item - 1)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This the method to process the “end” instruction for a transaction. It first calls a method to perform deadlock detection and then validates all the instructions for the transaction by checking if it has locks on all appropriate sites or if any site failed after an access. In addition, it calls methods to commit or abort the transaction and further processes the next instruction from the waiting queue.
	 * @param transaction_id transaction id
	 */
	void endTransaction(int transaction_id) {
		
		boolean flag = false;
		Transaction youngest_transaction = transactions.get(getYoungestTransaction());
		boolean deadlock = graph.detectDeadlock();
		boolean getnextblocked = false;
		System.out.println("Deadlock " + deadlock);
		if(deadlock) {
			//System.out.println("Deadlock detected!");
			//System.out.println("Aborting the youngest transaction T" + youngest_transaction.transaction_ID);
			abortTransaction(youngest_transaction);
			
			Instruction I = waitingInstructions.pollLast();
			if (I != null) {
				processInstruction(I, I.transaction_id - 1, true);
			}
			getnextblocked = true;
			deadlock = false;
		} else {
			System.out.println("Deadlock not detected!");
		}
		Transaction T = transactions.get(transaction_id);
		if (T.isReadOnly())
			System.out.println("Transaction T" + T.transaction_ID + " commits!:)");
		else {
			flag = true;
			for (Instruction I : T.Instructions) {
				ArrayList<Site> accessedSites = I.getAccessSites();
				if (I.operation.equals("write")) {
					if (I.data_item % 2 == 0) {
						for (Site s : accessedSites) {
							if (!(s.isSiteUp() && s.checkWriteLock(T, I.data_item - 1))) {
								flag = false;
								break;
							}
						}
					} else {
						int site_id = 1 + I.data_item % 10;
						Site s = sites.get(site_id - 1);
						if (!(s.isSiteUp() && accessedSites.contains(s) && s.checkWriteLock(T, I.data_item - 1))) {
							flag = false;
							break;
						}
					}
				}
				if (I.operation.equals("read")) {
					if (I.data_item % 2 == 0) {
						boolean flag2 = false;
						for (Site s : accessedSites) {
							flag2 = flag2 || !s.isSiteUp() || !s.hasReadLock(T, I.data_item - 1);

						}
						if (!flag2) {
							flag = false;
							break;
						}
					} else {
						int site_id = 1 + I.data_item % 10;
						Site s = sites.get(site_id - 1);
						if (!(s.isSiteUp() && accessedSites.contains(s) && s.hasReadLock(T, I.data_item - 1))) {
							flag = false;
							break;
						}
					}
				}
			}

			// Commit Transaction
			if (flag) {
				commitTransaction(T);
			} else { // Abort Transaction
				abortTransaction(T);
			}
			
			if(!getnextblocked) {
			Instruction I = waitingInstructions.poll();
			if (I != null) {
				processInstruction(I, I.transaction_id - 1, true);
			}
			}
		}
	}

	private void commitTransaction(Transaction T) {
		
		T.commitInstructions(sites);
		releaseLocks(sites,T);
		if(T.getTMFlag())
			disableReadFlag();
		System.out.println("Transaction T" + T.transaction_ID + " commits!:)");
		
	}

	private void abortTransaction(Transaction T) {

		System.out.println("Transaction T" + T.transaction_ID + " aborts!:(");	
		for(Instruction I: T.Instructions) {
			waitingInstructions.remove(I);
		}
	
		releaseLocks(sites,T);
	}
	
	private void releaseLocks(ArrayList<Site> originalSites, Transaction T) {
			
			for (Instruction I : T.Instructions) {
				ArrayList<Site> sites = I.getAccessSites();
				if (I.operation.equals("write")) {
					if (I.data_item % 2 == 0) {
						for (Site s : sites) {
							if (s.isSiteUp() && s.checkWriteLock(T, I.data_item - 1)) {
								s.clearWriteLock(I.data_item - 1);
								
							}

						}
					} else {
						int site_id = 1 + I.data_item % 10;
						Site s = originalSites.get(site_id - 1);
						if (s.isSiteUp() && s.checkWriteLock(T, I.data_item - 1)) {
							s.clearWriteLock(I.data_item - 1);
						}
					}
				}
				if (I.operation.equals("read")) {
					if (I.data_item % 2 == 0) {
						for (Site s : sites) {
							if (s.isSiteUp() && s.hasReadLock(T, I.data_item - 1)) {
								s.clearReadLock(T, I.data_item - 1);
							}
						}
					} else {
						int site_id = 1 + I.data_item % 10;
						Site s = originalSites.get(site_id - 1);
						if (s.isSiteUp() && s.hasReadLock(T, I.data_item - 1)) {
							s.clearReadLock(T, I.data_item - 1);
						}
					}
				}
				graph.removeEdge("T" + T.transaction_ID);
			}
		}

	private int getYoungestTransaction() {

		int index = 0;
		for(int i = 0; i < transactions.size(); i ++) {
			int min_age = 0;
			//System.out.println("Transaction T" + transactions.get(i).transaction_ID + "'s start time = " + transactions.get(i).start_time);
			if(min_age < transactions.get(i).start_time) {
				min_age = transactions.get(i).start_time;
				index = i;
			}
		}

		return index;
	}

	public void dump() {

		for(int i = 0; i < sites.size(); i ++) {
			System.out.println(sites.get(i));
		}

	}

	public void dump(int dump_parameter) {
		
		System.out.println(sites.get(dump_parameter));
		
	}

	public void dump(String dump_parameter) {
		
		
		
	}
	

}