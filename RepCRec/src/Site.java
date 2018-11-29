import java.util.ArrayList;

// This is an instance of a Site
// Contains all the attributes and the required functions

public class Site {
	
	int site_ID = 0;
	String state; // State of the site: Up or Down

	ArrayList<Data> data_item = new ArrayList<Data>();
	
	Site(int site_ID) {
		
		this.site_ID = site_ID;
		addDataItems();
		
	}
	
	void addDataItems() {
		
		for(int i = 1; i <= 10; i ++) {
			data_item.add(new Data(i * 2));
		}
		
	}

	void initializeSite() {
		
	}

}
