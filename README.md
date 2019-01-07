# RepCRec
Implementation of a distributed database,  complete with multi-version concurrency control, deadlock detection, replication, and failure recovery.

# Authors:
Ashutosh Mahajan

Tushar Anchan

# Modules:
Transaction Manager:

    Multiversion Read Concurrency(For Read-Only transactions) Available Copies Algorithm(For distributed Read-Write Transactions)
    
    Failure and Recovery
    
    Commit and Abort
    
Deadlock Manager:

    Cycle Detection
    
    Selection of Youngest Transaction to Abort
    
# Input-Output and Execution of the Program:
The program takes as input a file in the form of a test script which contains a sequence of operations. The file can be fed directly via the program or through the command line.
If the supplied test cases are used we only need to enter the file name.
The output is dumped onto the console after the program is executed.
The dump() functions are provided, which can be added to the end of the test script to get the values of the data items at individual sites. It can be used in 3 formats


    dump() → Gives values of all data items at all sites 
    
    dump(i) → Gives values of all data items at site i 
    
    dump(xi) → Gives values of data item xi at all sites
    
# Example Scripts:
    
    begin(T1)
    begin(T2)
    begin(T3)
    W(T3,x2,22) //T3 gets writelocks on all sites at x2
    W(T2,x4,44) //T2 gets writelocks on all sites at x4
    R(T3,x4) //T3 is blocked as T2 holds locks at x4 on all sites
    end(T2) //T2 commits and writes 44 at all sites at x4 then T3 is given the lock at the first available site at x4 and it reads the value 44.
    end(T3) //T3 commits and writes 22 at x2 at all sites.
    R(T1,x2) //T1 gets the lock at the first available site and reads the value 22. end(T1) //T1 commits

    begin(T1)
    begin(T2)
    R(T1,x3) // T3 gets the lock at site 4 and reads the value 30.
    W(T2,x8,88) // T2 gets the lock at all sites at x8
    fail(2) // site 2 is down and its lock tables are erased.
    R(T2,x3) // T2 gets the lock at site 4 and reads the value 30.
    W(T1, x4,91) // T1 gets the lock at all sites except 2.
    recover(2) // site 2 is up and running
    end(T2) // T2 is aborted because site 2 failed after it accessed and the lock info is lost. end(T1) // T1 commits and writes value 91 at all sites except site 2.


# Pseudocode:
```
    ProcessInstruction(Instruction, Transaction id){ 
    if(readonly) processReadOnly(Instruction, id) 
    If(write) processWrite(Instruction id)
    if(read) processRead(Instruction id)
    }
    
    ProcessWrite() {
    if (write-lock can be granted) : Set write-lock on appropriate sites 
    Else : add the Instruction to the waiting queue
    }
    
    ProcessRead() {
    if (readlock can be granted) :
       Set readlock on the first available site
       Read the value to console
    Else : add the Instruction to the waiting queue 
    }
    
    ProcessReadOnly() {
       Read from the first available site from the cloned snapshot of sites created when the
       transaction begins 
    }
    
    EndTransaction() {
        Perform deadlock detection
        (abort youngest transaction if true)
        Validate transaction by checking all instructions had appropriate locks on appropriate sites.
        Commit the transaction if validation was true or else abort it
        Release all locks from all sites this transaction holds. 
    }
```    
