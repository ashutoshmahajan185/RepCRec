import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RepCRec {

	public static void main(String[] args) {

		System.out.println("Replicated Concurrency Control and Recovery Simulation\n");

		Scanner scanner = new Scanner(System.in);

		//System.out.println("Enter the test_script name/number");
		// Modify the file path here
		//String file_path = "E:/Ecplise/RepCRec/test_scripts/test_script_" + scanner.nextInt() + ".txt";
		//String file_path = "../test_scripts/test_script_18.txt";
		String file_name = new String(args[0]);
		String file_path = "../test_scripts/" + file_name;
		//String file_path = "/Users/tushar/eclipse-workspace/RepCRec/RepCRec/test_scyripts/test_script_3_5.txt";
		FileReader file_reader = null;
		try {
			file_reader = new FileReader(file_path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader buffered_reader = new BufferedReader(file_reader);
		// Dump for input
		ArrayList<String> input = new ArrayList<String>();
		try {
			String read_line;
			while((read_line = buffered_reader.readLine()) != null) {
				// Handling the comments
				if(read_line.length() == 0 || read_line.substring(0,2).equals("//"))
					continue;
				// Adding contents of file to ArrayList for better handling
				// It still ensures that we are not looking ahead into the input 
				input.add(read_line.replaceAll("\\s", ""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Single instance of a transaction manager. This instance never fails.
		TransactionManager transaction_manager = new TransactionManager();

		int timer = 0; // Advances with each newline or each new instruction
		for(int i = 0; i < input.size(); i ++) {

			timer ++; // Reading a new line. 
			String current_instruction = input.get(i);
			String operation = current_instruction.substring(0, current_instruction.indexOf('(')).toUpperCase();

			int transaction_ID = 0;
			int site_ID = 0;
			int data_item = 0;
			int write_value = 0;

			switch(operation) {
			
			case "BEGIN":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(')')));
				transaction_manager.beginTransaction(transaction_ID, timer);
				break;

			case "BEGINRO":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(')')));
				transaction_manager.beginTransaction(transaction_ID, timer);
				transaction_manager.transactions.get(transaction_ID - 1).setreadOnly();
				transaction_manager.createSnapshot(transaction_ID - 1);
				break;

			case "R":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(',')));
				data_item = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('x') + 1, current_instruction.indexOf(')')));
				transaction_manager.graph.addEdge("T" + transaction_ID, "x" + data_item);
				Instruction I = new Instruction(data_item,transaction_ID);
				transaction_manager.addInstruction(transaction_ID - 1, I);
				transaction_manager.processInstruction(I, transaction_ID - 1, false);
				break;

			case "W":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(',')));
				data_item = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('x') + 1, current_instruction.lastIndexOf(',')));
				write_value = Integer.parseInt(current_instruction.substring(current_instruction.lastIndexOf(',') + 1, current_instruction.lastIndexOf(')')));
				System.out.println("Transaction " + "T" + transaction_ID + "  Data " + "x" + data_item);
				transaction_manager.graph.addEdge("T" + transaction_ID, "x" + data_item);
				Instruction I_write = new Instruction(data_item,transaction_ID,write_value);
				transaction_manager.addInstruction(transaction_ID - 1, I_write);
				transaction_manager.processInstruction(I_write,transaction_ID - 1,false);
				break;

			case "FAIL":
				site_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('(') + 1, current_instruction.indexOf(')')));
				transaction_manager.failSite(site_ID);
				break;

			case "RECOVER":
				site_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('(') + 1, current_instruction.indexOf(')')));
				transaction_manager.recoverSite(site_ID);
				break;

			case "END":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(')')));
				transaction_manager.endTransaction(transaction_ID - 1);
				break;

			case "DUMP":
				/*String dump_parameter = current_instruction.substring(current_instruction.indexOf('(') + 1, current_instruction.indexOf(')'));
				if(dump_parameter.length() == 0)
					transaction_manager.dump();
				if(dump_parameter.length() == 1)
					transaction_manager.dump(Integer.parseInt(dump_parameter));
				if(dump_parameter.length() == 2)
					transaction_manager.dump(dump_parameter);*/
				break;

			default:
				System.out.println("Invalid operation. Please check input file");
				break;

			}
			//System.out.println(transaction_manager.transactions);

			System.out.println("Operation: " + operation + " Transaction: T" + transaction_ID);
			//System.out.println("Transaction: " + transaction_ID);
			//System.out.println("Data Item: " + data_item);
			//System.out.println("Site: " + site_ID);
			


		}
		//		for(int i = 0; i < 10; i ++) {
		//			
		//			Site current_site = transaction_manager.sites.get(i);
		//			System.out.print("\nSite " + current_site.site_ID);
		//			System.out.println(" --> Data Items at this site: ");
		//			ArrayList<Data> data_items_onsite = current_site.data_items;
		//			for(int j = 0; j < data_items_onsite.size(); j ++) {
		//				System.out.println("X" + data_items_onsite.get(j).data_index + ": " + data_items_onsite.get(j).data_value);
		//			}
		//		}


		//System.out.println(transaction_manager.sites);
		scanner.close();

	}

}