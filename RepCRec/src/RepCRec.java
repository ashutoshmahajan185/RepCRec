import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class RepCRec {

	public static void main(String[] args) throws FileNotFoundException {

		System.out.println("Replicated Concurrency Control and Recovery Simulation");
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Enter the test_script name/number");
		// MOdify the file path here
		String file_path = "E:/Ecplise/RepCRec/test_scripts/test_script_" + scanner.nextInt() + ".txt";
		FileReader file_reader = new FileReader(file_path);
		BufferedReader buffered_reader = new BufferedReader(file_reader);
		try {
			String read_line;
			while((read_line = buffered_reader.readLine()) != null) {
				// Handling the comments
				if(read_line.length() == 0 || read_line.substring(0,2).equals("//"))
					continue;
				// 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
	}

}
