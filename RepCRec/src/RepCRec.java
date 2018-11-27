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
		// MOdify the file path here
		//String file_path = "E:/Ecplise/RepCRec/test_scripts/test_script_" + scanner.nextInt() + ".txt";
		String file_path = "E:/Ecplise/RepCRec/test_scripts/test_script_1.txt";
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
		
		for(int i = 0; i < input.size(); i ++) {
		
			String operation = input.get(i).substring(0, input.indexOf('(')).toUpperCase();
			
			int transaction_ID = 0;
			
			switch(operation) {
				
			case "BEGIN":
				break;
				
			case "BEGINRO":
				break;
				
			case "R":
				break;
				
			case "W":
				break;
				
			case "FAIL":
				break;
				
			case "RECOVER":
				break;
				
			case "END":
				break;
				
			case "DUMP":
				break;
				
			default:
				System.out.println("Invalid operation. Please check input file");
				break;
			
			}
			
		}
		
		

		scanner.close();
		
	}

}
