import java.util.ArrayList;
// Saves the instance of a Transaction

public class Transaction {

	int transaction_ID;
	int start_time;
	String state; // State of the transaction: Running, Waiting, Abort, Commit
	boolean readOnly = false;
	boolean disableTMCheckReadFlag = false;
	ArrayList<Instruction> Instructions = new ArrayList<Instruction>();
	ArrayList<Site> databaseSnapshot = null;

	Transaction(int transaction_ID, int start_time) {

		this.transaction_ID = transaction_ID;
		this.start_time = start_time;

	}

	public void addInstruction(Instruction I) {
		Instructions.add(I);
	}

	public void setreadOnly() {
		this.readOnly = true;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setTMFlag() {
		this.disableTMCheckReadFlag = true;
	}

	public boolean getTMFlag() {
		return this.disableTMCheckReadFlag;
	}

	public void createSnapshot(ArrayList<Site> sites) {
		this.databaseSnapshot = new ArrayList<Site>();
		for (Site s : sites) {
			this.databaseSnapshot.add(s.clone());
		}
		// this.databaseSnapshot = (ArrayList<Site>) sites.clone();
	}

	public void storeAccessSites(ArrayList<Site> sites) {
		this.databaseSnapshot = new ArrayList<Site>();
		for (Site s : sites) {
			if (s.isSiteUp())
				this.databaseSnapshot.add(s);
		}
	}

	public ArrayList<Site> getSnapshot() {
		return this.databaseSnapshot;
	}
	
	public void addSitesToInstruction(Instruction I) {
		I.setAccessSites(this.getSnapshot());
	}

	/**
	 * @param originalSites
	 */
	public void commitInstructions(ArrayList<Site> originalSites) {
		for (Instruction I : Instructions) {
			ArrayList<Site> sites = I.getAccessSites();
			if (I.operation.equals("write")) {
				if (I.data_item % 2 == 0) {
					for (Site s : sites) {
						if (s.isSiteUp() && s.checkWriteLock(this, I.data_item - 1)) {
							s.writeValue(I.data_item, I.write_value);
							System.out.println("wrote value " + I.write_value + " at site : " + s.site_ID);
							if (I.checkReadPermission && !s.canRead) {
								s.allowReads();
								setTMFlag();
							}

						}

					}
				} else {
					int site_id = 1 + I.data_item % 10;
					Site s = originalSites.get(site_id - 1);
					if (s.isSiteUp() && s.checkWriteLock(this, I.data_item - 1)) {
						s.writeValue(I.data_item, I.write_value);
						System.out.println("wrote value " + I.write_value + " at site : " + s.site_ID);
						if (I.checkReadPermission && !s.canRead) {
							s.allowReads();
							setTMFlag();
						}
					}
				}
			}
			if (I.operation.equals("read")) {
				if (I.data_item % 2 == 0) {
					for (Site s : sites) {
						if (I.checkReadPermission && !s.canRead)
							System.out.println("read not allowed");
						else {
							if (s.isSiteUp() && s.hasReadLock(this, I.data_item - 1)) {
								for (Data data : s.data_items) {
									if (data.data_index == I.data_item) {
										System.out.println("Site: " + s.site_ID + " data: " + data.data_value);
									}
								}
							}

						}
					}
				} else {
					int site_id = 1 + I.data_item % 10;
					Site s = originalSites.get(site_id - 1);
					if (I.checkReadPermission && !s.canRead)
						System.out.println("read not allowed");
					else {
						if (s.isSiteUp() && s.hasReadLock(this, I.data_item - 1)) {
							for (Data data : s.data_items) {
								if (data.data_index == I.data_item) {
									System.out.println("Site: " + s.site_ID + " data: " + data.data_value);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param originalSites
	 */
	public void releaseLocks(ArrayList<Site> originalSites) {
		for (Instruction I : Instructions) {
			ArrayList<Site> sites = I.getAccessSites();
			if (I.operation.equals("write")) {
				if (I.data_item % 2 == 0) {
					for (Site s : sites) {
						if (s.isSiteUp() && s.checkWriteLock(this, I.data_item - 1)) {
							s.clearWriteLock(I.data_item - 1);
						}

					}
				} else {
					int site_id = 1 + I.data_item % 10;
					Site s = originalSites.get(site_id - 1);
					if (s.isSiteUp() && s.checkWriteLock(this, I.data_item - 1)) {
						s.clearWriteLock(I.data_item - 1);
					}
				}
			}
			if (I.operation.equals("read")) {
				if (I.data_item % 2 == 0) {
					for (Site s : sites) {
						if (s.isSiteUp() && s.hasReadLock(this, I.data_item - 1)) {
							s.clearReadLock(this, I.data_item - 1);
						}

					}
				} else {
					int site_id = 1 + I.data_item % 10;
					Site s = originalSites.get(site_id - 1);
					if (s.isSiteUp() && s.hasReadLock(this, I.data_item - 1)) {
						s.clearReadLock(this, I.data_item - 1);
					}
				}
			}
		}
	}
}
