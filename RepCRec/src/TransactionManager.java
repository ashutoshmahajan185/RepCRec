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
   
   public void processInstruction(Instruction I,int transaction_id) {
	   String operation = I.getOperation();
	   Transaction T = transactions.get(transaction_id);
	   if (T.readOnly) {
		   processReadOnly(I,transaction_id);
		
	   }
	   else {
		   if (operation.equals("write")) {
			   processWrite(I,transaction_id);
			   }
		   if (operation.equals("read")) {
			   processRead(I,transaction_id);
			   }
		   
	   }

   }
   
   public void processWrite(Instruction I, int transaction_id) {
	   String operation = I.getOperation();
	   Transaction T = transactions.get(transaction_id);   
	   if (requestWriteLock(I.data_item,I)) {
			System.out.println("lock can be acquired");
			if(I.data_item%2==0) {
				for(Site s:sites) {
					s.setWriteLock(T, I.data_item-1);
					s.writeValue(I.data_item, I.write_value);
					System.out.println("wrote value "+I.write_value+" at site : "+ s.site_ID);
				}
			}
			else {
				int site_id = 1 + I.data_item%10;
				Site s = sites.get(site_id-1);
				s.setWriteLock(T, I.data_item-1);
				s.writeValue(I.data_item, I.write_value);
				System.out.println("wrote value "+I.write_value+" at site : "+ s.site_ID);
			}
		}
		else {
			System.out.println("blocked");
			//add to waiting queue
			waitingInstructions.add(I);
		}
   }
   
   public void processRead(Instruction I, int transaction_id) {
	   String operation = I.getOperation();
	   Transaction T = transactions.get(transaction_id);
	   if (requestReadLock(I.data_item,I)) {
		   System.out.println("lock can be acquired");
		   if(I.data_item%2==0) {
				for(Site s:sites) {
					s.setReadLock(T, I.data_item-1);
					for(Data data:s.data_items) {
						if(data.data_index==I.data_item) {
							System.out.println("Site: "+s.site_ID+" data: "+data.data_value);
						}
					}
				}
			}
			else {
				int site_id = 1 + I.data_item%10;
				Site s = sites.get(site_id-1);
				s.setReadLock(T, I.data_item-1);
				for(Data data:s.data_items) {
					if(data.data_index==I.data_item) {
						System.out.println("Site: "+s.site_ID+" data: "+data.data_value);
					}
				}
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
	   
   
   
   boolean requestWriteLock(int data_item,Instruction I) {
	   if(data_item%2==0) {
		   int countTrues = 0;
		   int countFalses = 0;
		   for(Site s:sites) {
			    if(s.checkWriteLock(data_item-1) && s.readLockTable.get(data_item-1).size()==1 && s.readLockTable.get(data_item-1).get(0).transaction_ID==I.transaction_id) {
			    	countTrues++;
			    }
				if (!s.checkWriteLock(data_item-1) || !s.checkReadLock(data_item-1)) {
					countFalses++;
				}

		   }
			if(countTrues==sites.size()) return true;
			if(countFalses>0) return false;
	   }
	   else {
		   int site_id = 1 + data_item%10;
		   Site s = sites.get(site_id-1);
		   if(s.checkWriteLock(data_item-1) && s.readLockTable.get(data_item-1).size()==1 && s.readLockTable.get(data_item-1).get(0).transaction_ID==I.transaction_id) {
		    	return true;
		    }
		   if (!s.checkWriteLock(data_item-1) || !s.checkReadLock(data_item-1)) {
			   return false;
		   }
	   }

	   return true;
   }
   
   boolean requestReadLock(int data_item,Instruction I) {
	   if (data_item%2==0) {
		   int countTrues = 0;
		   int countFalses = 0;
		   for (Site s: sites) {
			   if(!s.checkWriteLock(data_item-1) && s.writeLockTable[I.data_item-1].transaction_ID==I.transaction_id) {
				   countTrues++;
			   }
			   if (!s.checkWriteLock(data_item-1)) {
				   countFalses++;
			   }
		   }
		   if(countTrues==sites.size()) return true;
		   if(countFalses>0) return false;
	   }
	   else {
		   int site_id = 1 + data_item%10;
		   Site s = sites.get(site_id-1);
		   if(s.writeLockTable[I.data_item-1].transaction_ID==I.transaction_id) {
			   return true;
		   }
		   if (!s.checkWriteLock(data_item-1)) {
			   return false;
		   }
	   }
	   return true;
   }
  
 void endTransaction(int transaction_id) {
	 Transaction T = transactions.get(transaction_id);
	 for(Instruction I:T.Instructions) {
		 if(I.operation.equals("write")) {
			 if(I.data_item%2==0) {
				 for(Site s:sites) {
					 s.clearWriteLock(I.data_item-1);
				 }
			 }
			 else {
				 int site_id = 1 + I.data_item%10;
				   Site s = sites.get(site_id-1);
				   s.clearWriteLock(I.data_item-1);
			 }
		 }
		 if(I.operation.equals("read")) {
			 if(I.data_item%2==0) {
				 for(Site s:sites) {
					 s.clearReadLock(T,I.data_item-1);
				 }
			 }
			 else {
				 int site_id = 1 + I.data_item%10;
				   Site s = sites.get(site_id-1);
				   s.clearReadLock(T,I.data_item-1);
			 }
		 }
	 }
	 System.out.println("T"+T.transaction_ID+" commits");
	 Instruction I = waitingInstructions.poll();
	 if(I!=null) {
		 processInstruction(I,I.transaction_id-1);
	 }
 }
 
}
