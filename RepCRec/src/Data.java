// Represents an instance of a data item
// Data Items initialized site-wise
public class Data {
	
	int data_index;
	int data_value;
	
	// Constructor for even indexed data items
	Data(int data_index) {
		data_value = 10 * data_index;
	}
	
	// Method for even indexed data item
	public void initializeDataItem() {
		data_value = 10 * data_index;
	}
	/*
	Integer x1 = 10;
	Integer x2 = 20;
	Integer x3 = 30;
	Integer x4 = 40;
	Integer x5 = 50;
	Integer x6 = 60;
	Integer x7 = 70;
	Integer x8 = 80;
	Integer x9 = 90;
	Integer x10 = 100;
	Integer x11 = 110;
	Integer x12 = 120;
	Integer x13 = 130;
	Integer x14 = 140;
	Integer x15 = 150;
	Integer x16 = 160;
	Integer x17 = 170;
	Integer x18 = 180;
	Integer x19 = 190;
	Integer x20 = 200;
	*/
	
}
