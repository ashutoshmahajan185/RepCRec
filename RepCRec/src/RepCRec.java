import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class RepCRec {

	public static void main(String[] args) throws FileNotFoundException {

		System.out.println("Replicated Concurrency Control and Recovery Simulation");
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Enter the test_script name/number");
		// Modify the file path here
		//String file_path = "E:/Ecplise/RepCRec/test_scripts/test_script_" + scanner.nextInt() + ".txt";
		String file_path = "E:/Ecplise/RepCRec/test_scripts/test_script_3.txt";
		FileReader file_reader = new FileReader(file_path);
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
		System.out.println("This is the file content");
		for(int i = 0; i < input.size(); i ++) {
			System.out.println(input.get(i));
		}
		
		TransactionManager transaction_manager = new TransactionManager();
		
		int timer = 0; // Advances with each newline or each new instruction
		for(int i = 0; i < input.size(); i ++) {
		
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
				break;
				
			case "R":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(',')));
				data_item = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('x') + 1, current_instruction.indexOf(')')));
				break;
				
			case "W":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(',')));
				data_item = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('x') + 1, current_instruction.lastIndexOf(',')));
				write_value = Integer.parseInt(current_instruction.substring(current_instruction.lastIndexOf(',') + 1, current_instruction.lastIndexOf(')')));
				break;
				
			case "FAIL":
				site_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('(') + 1, current_instruction.indexOf(')')));
				break;
				
			case "RECOVER":
				site_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('(') + 1, current_instruction.indexOf(')')));
				break;
				
			case "END":
				transaction_ID = Integer.parseInt(current_instruction.substring(current_instruction.indexOf('T') + 1, current_instruction.indexOf(')')));
				break;
				
			case "DUMP":
				System.out.println("Dump operation");
				break;
				
			default:
				System.out.println("Invalid operation. Please check input file");
				break;
			
			}
			
			System.out.println("Operation: " + operation);
			System.out.println("Transaction: " + transaction_ID);
			System.out.println("Data Item: " + data_item);
			System.out.println("Site: " + site_ID);
			
		}
		
		

		scanner.close();
		
	}

}
