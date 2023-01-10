import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Product {

	public static final int MINIMUM_QUANTITY = 1;

	// name is the unique id
	private int id;
	private Item item;
	private LocalDate date; // when was it bought or added to Warehouse; read-only will be set in constructor
	private int quantity; // read-only, set in constructor, defaults to 1
	private WarehouseDSC.SECTION section;

	// constructor
	public Product(int id, Item item, LocalDate date, int quantity, WarehouseDSC.SECTION section) throws Exception {
		if (id < 1) 
			throw new Exception("[ERROR] id value cannot be less than 1");
		if (item == null) 
			throw new Exception("[ERROR] Item cannot be null value");
		if (section == null) 
			throw new Exception("[ERROR] Section cannot be null value");		
		if (quantity < MINIMUM_QUANTITY) 
			throw new Exception("[ERROR] Quantity value cannot be less than 1");

		this.id = id;
		this.item = item;
		this.date = date != null ? date : LocalDate.now();
		this.quantity = quantity;
		this.section = section;
	}

	// constructor
	public Product(int id, Item item, LocalDate date, WarehouseDSC.SECTION section) throws Exception {
		this(id, item, date, MINIMUM_QUANTITY, section);
	}

	// constructor
	public Product(int id, Item item, int quantity, WarehouseDSC.SECTION section) throws Exception {
		this(id, item, LocalDate.now(), quantity, section);
	}

	// constructor
	public Product(int id, Item item, WarehouseDSC.SECTION section) throws Exception {
		this(id, item, LocalDate.now(), MINIMUM_QUANTITY, section);
	}	

	public int getId() {
		return this.id;
	}

	public Item getItem() {
		return this.item;
	}

	public String getItemName() {
		return this.item.getName();
	}

	public LocalDate getDate() {
		return this.date;
	}

	public String getDateStr() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(WarehouseDSC.DATE_FORMAT);

		return this.date.format(dtf);
	}

	public String getDaysAgo() {
		return WarehouseDSC.calcDaysAgoStr(date);
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void updateQuantity() throws Exception {
		if (this.quantity > MINIMUM_QUANTITY)
			this.quantity--;
		else
			throw new Exception("[ERROR] Quantity value cannot be less than 1");
	}

	public WarehouseDSC.SECTION getSection() {
		return this.section;
	}


	public String toString() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(WarehouseDSC.DATE_FORMAT);
		String daysAgo = WarehouseDSC.calcDaysAgoStr(date);

		return "[ id: " + this.id
			+ ", item: " + this.item.getName() + (item.canExpire() ? " (EXP)":"")
			+ ", date: " + this.date.format(dtf) + " (" + daysAgo + ")"
			+ ", quantity: " + this.quantity
			+ ", section: " + this.section
			+ " ]";
	}

	// To perform some quick tests
	public static void main(String [] args) throws Exception {
		Item i1 = new Item("Milk", false);
		System.out.println(i1);

		Item i2 = new Item("Fish", true);
		System.out.println(i2);

		Product g1 = new Product(1, i1, WarehouseDSC.SECTION.COOLING);
		System.out.println(g1);

		Product g2 = new Product(1, i2, 10, WarehouseDSC.SECTION.COOLING);
		System.out.println(g2);	
	}	
}