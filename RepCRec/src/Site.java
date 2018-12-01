import java.util.ArrayList;

// This is an instance of a Site
// Contains all the attributes and the required functions

public class Site {
	
	int site_ID = 0;
	String state; // State of the site: Up or Down

	ArrayList<Data> data_items = new ArrayList<Data>();
	
	Site(int site_ID) {
		
		this.site_ID = site_ID;
		addDataItems();
		
	}
	
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

	void initializeSite() {
		
	}

}
