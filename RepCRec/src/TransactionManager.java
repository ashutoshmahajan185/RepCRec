import java.util.ArrayList;
import java.util.LinkedList;

// Instance of a Transaction Manager. Never Fails

public class TransactionManager {
	
	ArrayList<Site> sites = new ArrayList<Site>();
	ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	LinkedList<Instruction> waitingInstructions = new LinkedList<Instruction>();
	TransactionManager() {
		initializeSites();
	}
	
	public void initializeSites() {
		
		for(int i = 1; i <= 10; i ++)
		sites.add(new Site(i));
		
	}
	
	public void failSite(int site_id) {
		sites.get(site_id-1).failSite();	
	}
	
	public void recoverSite(int site_id) {
		sites.get(site_id-1).recoverSite();
	}
	
	public void beginTransaction(int transaction_ID, int timer) {
		transactions.add(new Transaction(transaction_ID, timer));
	}
	
   public void addInstruction(int id,Instruction I) {
	  Transaction T = transactions.get(id);
	  T.addInstruction(I); 
   }
   
   public void createSnapshot(int id) {
	   Transaction T = transactions.get(id);
	   ArrayList<Site> snapshot = (ArrayList<Site>) sites.clone();
	   T.createSnapshot(snapshot);
   }
   
   public void processInstruction(Instruction I,int transaction_id,boolean flag) {
	   String operation = I.getOperation();
	   Transaction T = transactions.get(transaction_id);
	   if (T.readOnly) {
		   processReadOnly(I,transaction_id);
		
	   }
	   else {
		   
		   if(T.databaseSnapshot==null) T.storeAccessSites(sites);
		   
		   if (operation.equals("write")) {
			   processWrite(I,transaction_id,flag);
			   }
		   if (operation.equals("read")) {
			   processRead(I,transaction_id,flag);
			   }
		   
	   }

   }
   
   public void processWrite(Instruction I, int transaction_id,boolean flag) {
	   String operation = I.getOperation();
	   Transaction T = transactions.get(transaction_id); 
	   T.storeAccessSites(sites);
	   T.addSitesToInstruction(I);
	   ArrayList<Site> accessedSites = I.getAccessSites();
	   if (requestWriteLock(I.data_item,I,flag,accessedSites)) {
			System.out.println("lock can be acquired");
			if(I.data_item%2==0) {
				for(Site s:accessedSites) {
					s.setWriteLock(T, I.data_item-1);
				}
			}
			else {
				int site_id = 1 + I.data_item%10;
				Site s = sites.get(site_id-1);
				int actualSiteIndex = accessedSites.indexOf(s);
				if(actualSiteIndex!=-1) {
					Site actualSite = accessedSites.get(actualSiteIndex);
				    actualSite.setWriteLock(T, I.data_item-1);
				}
				//else add to waiting maybe
			}
		}
		else {
			System.out.println("blocked");
			//add to waiting queue
			waitingInstructions.add(I);
		}
   }
   
   public void processRead(Instruction I, int transaction_id,boolean flag) {
	   String operation = I.getOperation();
	   Transaction T = transactions.get(transaction_id);
	   T.storeAccessSites(sites);
	   T.addSitesToInstruction(I);
	   ArrayList<Site> accessedSites = I.getAccessSites();
	   if (requestReadLock(I.data_item,I,flag,accessedSites)) {
		   System.out.println("lock can be acquired");
		   if(I.data_item%2==0) {
				for(Site s:accessedSites) {
					s.setReadLock(T, I.data_item-1);
				}
			}
			else {
				int site_id = 1 + I.data_item%10;
				Site s = sites.get(site_id-1);
				int actualSiteIndex = accessedSites.indexOf(s);
				if(actualSiteIndex!=-1) {
					Site actualSite = accessedSites.get(actualSiteIndex);
				    actualSite.setReadLock(T, I.data_item-1);
				}
				//else add to waiting maybe
			}
	   }
	   else {
		   System.out.println("blocked");
		   waitingInstructions.add(I);
	   }
   }
   
   public void processReadOnly(Instruction I, int transaction_id) {
	   Transaction T = transactions.get(transaction_id);
	   ArrayList<Site> snapshot = T.getSnapshot();
	   if(I.data_item%2==0) {
			for(Site s:snapshot) {
				for(Data data:s.data_items) {
					if(data.data_index==I.data_item) {
						System.out.println("Site: "+s.site_ID+" data: "+data.data_value);
					}
				}
			}
		}
		else {
			int site_id = 1 + I.data_item%10;
			Site s = snapshot.get(site_id-1);
			for(Data data:s.data_items) {
				if(data.data_index==I.data_item) {
					System.out.println("Site: "+s.site_ID+" data: "+data.data_value);
				}
			}
		}
  }
	   
   boolean isAlreadyWaitingInstruction(Instruction I) {
	   for(Instruction W:waitingInstructions) {
		   if(W.data_item==I.data_item) return true;
	   }
	   return false;
   }
   
   boolean requestWriteLock(int data_item,Instruction I,boolean flag, ArrayList<Site> sites) {
	   if(!flag && waitingInstructions.size()>0 && isAlreadyWaitingInstruction(I)) return false;
	   if(data_item%2==0) {
		   int countTrues = 0;
		   int countFalses = 0;
		   for(Site s:sites) {
			    if(s.isEmptyWriteLock(data_item-1) && s.readLockTable.get(data_item-1).size()==1 && s.readLockTable.get(data_item-1).get(0).transaction_ID==I.transaction_id) {
			    	countTrues++;
			    }
				if (!s.isEmptyWriteLock(data_item-1) || !s.checkReadLock(data_item-1)) {
					countFalses++;
				}

		   }
			if(countTrues==sites.size()) return true;
			if(countFalses>0) return false;
	   }
	   else {
		   int site_id = 1 + data_item%10;
		   Site s = sites.get(site_id-1);
		   if(!s.isEmptyWriteLock(data_item-1) && s.readLockTable.get(data_item-1).size()==1 && s.readLockTable.get(data_item-1).get(0).transaction_ID==I.transaction_id) {
		    	return true;
		    }
		   if (!s.isEmptyWriteLock(data_item-1) || !s.checkReadLock(data_item-1)) {
			   return false;
		   }
	   }

	   return true;
   }
   
   boolean requestReadLock(int data_item,Instruction I,boolean flag, ArrayList<Site> sites) {
	   if(!flag && waitingInstructions.size()>0 && isAlreadyWaitingInstruction(I)) return false;
	   if (data_item%2==0) {
		   int countTrues = 0;
		   int countFalses = 0;
		   for (Site s: sites) {
			   if(!s.isEmptyWriteLock(data_item-1) && s.writeLockTable[I.data_item-1].transaction_ID==I.transaction_id) {
				   countTrues++;
			   }
			   if (!s.isEmptyWriteLock(data_item-1)) {
				   countFalses++;
			   }
		   }
		   if(countTrues==sites.size()) return true;
		   if(countFalses>0) return false;
	   }
	   else {
		   int site_id = 1 + data_item%10;
		   Site s = sites.get(site_id-1);
		   if(!s.isEmptyWriteLock(data_item-1) && s.writeLockTable[I.data_item-1].transaction_ID==I.transaction_id) {
			   return true;
		   }
		   if (!s.isEmptyWriteLock(data_item-1)) {
			   return false;
		   }
	   }
	   return true;
   }
  
 void endTransaction(int transaction_id) {
	 Transaction T = transactions.get(transaction_id);
	 if(T.isReadOnly()) System.out.println("T"+T.transaction_ID + " commits ");
	 else {
	 boolean flag = true;
	 for(Instruction I:T.Instructions) {
		 ArrayList<Site> accessedSites = I.getAccessSites();
		 if(I.operation.equals("write")) {
			 if(I.data_item%2==0) {
				 for(Site s:accessedSites) {
					 if(!(s.isSiteUp() && s.checkWriteLock(T, I.data_item-1))) {
						 flag = false;
						 break;
					 } 
				 }
			 }
			 else {
				 int site_id = 1 + I.data_item%10;
				   Site s = sites.get(site_id-1);
				   if(!(s.isSiteUp() && accessedSites.contains(s) && s.checkWriteLock(T, I.data_item-1))) {
					   flag = false;
					   break;
					 }
			 }
		 }
		 if(I.operation.equals("read")) {
			 if(I.data_item%2==0) {
				 for(Site s:accessedSites) {
					 if(!(s.isSiteUp() && s.hasReadLock(T,I.data_item-1))) {
						 flag = false;
						 break;
					 }
				 }
			 }
			 else {
				 int site_id = 1 + I.data_item%10;
				   Site s = sites.get(site_id-1);
				   if(!(s.isSiteUp() && accessedSites.contains(s) && s.hasReadLock(T,I.data_item-1))) {
						 flag = false;
						 break;
					 }
			 }
		 }
	 }
	
	 if(flag) {
		 System.out.println("T"+T.transaction_ID+" commits");
		 T.commitInstructions(sites);
		 T.releaseLocks(sites);
	 }
	 else {
		 System.out.println("T"+T.transaction_ID+" aborts");
		 waitingInstructions.remove(T);
		 T.releaseLocks(sites);
	 }
	 
	 Instruction I = waitingInstructions.poll();
	 if(I!=null) {
		 processInstruction(I,I.transaction_id-1,true);
	 }
	 }
 }
 
}
