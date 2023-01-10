import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.awt.Button;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.io.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;


//MUMINOV FAYZ 19897760 2022-CSE3OAD(SY-2) 

public class WarehouseFX extends Application {

	// used as ChoiceBox value for filter
	public enum FILTER_COLUMNS {
		ITEM,
		SECTION,
		BOUGHT_DAYS_AGO
	};
	
	// the data source controller
	private WarehouseDSC warehouseDSC;
	

	public void init() throws Exception {
		// creating an instance of the data source controller to be used
		// in this application
		warehouseDSC = new WarehouseDSC();
		try {
			WarehouseDSC.connect();
		}
		catch (Exception e)
		{
			System.out.println("Error... try to connect again " + e.toString());
		}
		// NOTE: that database connect method throws exception
		 
	}

	public void start(Stage stage) throws Exception {
		build(stage);
		stage.setTitle("This is the Warehouse stage ");
		stage.show();
		
		Thread.currentThread().setUncaughtExceptionHandler((thread,exception) -> {
			System.out.println("Opps Error: " + exception);
		});
		

		/*  this method is the start method for your 
		  application set application title show the stage
		 * currentThread uncaught exception handler
		 */
	}

	public void build(Stage stage) throws Exception {

		// Create table data (an observable list of objects)
		ObservableList<Product> tableData = FXCollections.observableArrayList();

		// Define table columns
		TableColumn<Product, String> idColumn = new TableColumn<Product, String>("Id");
		TableColumn<Product, String> itemNameColumn = new TableColumn<Product, String>("Item");
		TableColumn<Product, Integer> quantityColumn = new TableColumn<Product, Integer>("QTY");
		TableColumn<Product, String> sectionColumn = new TableColumn<Product, String>("Section");
		TableColumn<Product, String> daysAgoColumn = new TableColumn<Product, String>("Bought");
		
		idColumn.setCellValueFactory( new PropertyValueFactory<Product,Integer>("id"));
		itemNameColumn.setCellValueFactory( new PropertyValueFactory<Product, String>("ItemName"));
		quantityColumn.setCellValueFactory( new PropertyValueFactory<Product, Integer>("Quantity"));
		sectionColumn.setCellValueFactory( new PropertyValueFactory<Product, String>("Section"));
		daysAgoColumn.setCellValueFactory( new PropertyValueFactory<Product, String>("DaysAgo"));
		
		/*
		  for each column defined, call their setCellValueFactory method 
		  using an instance of PropertyValueFactory
		 */


		// Create the table view and add table columns to it
		TableView<Product> tableView = new TableView<Product>();
		
		tableView.getColumns().add(idColumn);
		tableView.getColumns().add(itemNameColumn);
		tableView.getColumns().add(quantityColumn);
		tableView.getColumns().add(sectionColumn);
		tableView.getColumns().add(daysAgoColumn);

		// add table columns to the table view create above
		 
		//	Attach table data to the table view
		//tableView.setItems(tableData);
		tableView.setMinWidth(600);
		tableView.setMaxWidth(600);
		quantityColumn.setMinWidth(50);

		/* 
		 set minimum and maximum width to the table view and each columns
		 */


		/*
		 call data source controller get all products method to add
		 all products to table data observable list
		 filter container - part 1
		  add all filter related UI elements you identified
		 */
	

		// =====================================================
		// ADD the remaining UI elements
		// NOTE: the order of the following TODO items can be 
		// 		 changed to satisfy your UI implementation goals
		// =====================================================
		// top filter elements
			TextField filterTextField = new TextField();
			Label filterLabel = new Label("Filter By");
			ChoiceBox<String> filterchoice = new ChoiceBox<>();
			filterchoice.getItems().addAll("ITEM","SECTION","BOUGHT DAYS AGO");
			filterchoice.setValue("ITEM");

			CheckBox filtercb = new CheckBox();
			filtercb.setDisable(true);
			Label checkBoxLabel = new Label ("Show Expire Only");
			checkBoxLabel.setDisable(true);
			HBox topRow = new HBox(filterTextField,filterLabel,filterchoice,filtercb,checkBoxLabel);
			topRow.setSpacing(5);
		

		/*
		 filter container - part 2:
		 addListener to the "Filter By" ChoiceBox to clear the filter
		 text field vlaue and to enable the "Show Expire Only" CheckBox
		 if "BOUGHT_DAYS_AGO" is selected
		 */
			//listener when filter by check box is selected
			filterchoice.getSelectionModel().selectedItemProperty().addListener((observableValue,oldValue,newValue) -> {
				filterTextField.clear();
				filterTextField.requestFocus();
				if (newValue.equalsIgnoreCase("BOUGHT DAYS AGO"))
				{
					checkBoxLabel.setDisable(false);
					filtercb.setDisable(false);
				}
				else
				{
					checkBoxLabel.setDisable(true);
					filtercb.setDisable(true);

				}
			
			});
		/* 
		 filter container - part 2:
		 addListener to the "Filter By" ChoiceBox to clear and set focus 
		 to the filter text field and to enable the "Show Expire Only" 
		 CheckBox if "BOUGHT_DAYS_AGO" is selected
		 
		 setOnAction on the "Show Expire Only" Checkbox to clear and 
		 set focus to the filter text field
		 */
			
			filtercb.setOnAction(event -> {
				filterTextField.clear();
				filterTextField.requestFocus();
			});

		/* 
		 filter container - part 3:
		 create a filtered list
		 create a sorted list from the filtered list
		 bind comparators of sorted list with that of table view
		 set items of table view to be sorted list
		 set a change listener to text field to set the filter predicate
		 of filtered list
		 */	
			FilteredList<Product> filterList = new FilteredList<>(tableData, p -> true);
			SortedList<Product> sortedList = new SortedList<>(filterList);
			sortedList.comparatorProperty().bind(tableView.comparatorProperty());
			tableView.setItems(sortedList);




			filterTextField.textProperty().addListener((observableValue,oldValue,newValue) ->
					{

						filterList.setPredicate(grocerydata ->{
								try{
						{
							if (newValue == null || newValue.isEmpty()) {

								return true;
							}

							String filterString = newValue.toUpperCase();
							if (filterchoice.getValue().equalsIgnoreCase("item"))
							{
								if (productdata.getItemName().toUpperCase().contains(filterString))
								{
									return true;
								} else
									{
									return false;
								    }
							}
							else if (filterchoice.getValue().equalsIgnoreCase("section"))
							{
								if(productdata.getSection().toString().contains(filterString)) {
									return true;
								}
								else
								{
									return false;
								}
							}
							else if (filterchoice.getValue().equalsIgnoreCase("Bought days ago"))
							{
								int daysAgo = productdata.getDaysAgo().equalsIgnoreCase("today")? 0:
										Integer.parseInt(productdata.getDaysAgo().substring(0,productdata.getDaysAgo().indexOf(" ")));
								if(filtercb.isSelected())
								{
									if(daysAgo >= Integer.parseInt(filterTextField.getText()) && productdata.getItem().canExpire())
									{
										return true;
									}
									else
									{
										return false;
									}


								}
								else
								{
									if(daysAgo >= Integer.parseInt(filterTextField.getText()))
									{
										return true;
									}
									else
									{
										return false;
									}
								}

							}
							else
							{
								return false;
							}


						}}
								catch(Exception e)
						{
							return false;
						}});
					});



		/* 
		 * ACTION buttons: ADD, UPDATE ONE, DELETE
		 * - ADD button sets the add UI elements to visible;
		 *   NOTE: the add input controls and container may have to be
		 * 		   defined before these action controls & container(s)
		 * - UPDATE ONE and DELETE buttons action need to check if a
		 *   table view row has been selected first before doing their
		 *   action; hint: should you also use an Alert confirmation?
		 */		
			//buttons
			Button addBtn = new Button("Add");
			Button updateBtn = new Button("Update");
			Button deletBtn = new Button("Delete");
			HBox buttonBox = new HBox (addBtn,updateBtn,deletBtn);
			buttonBox.setSpacing(5);

			//Add controls
			//Labels for Add items
			Label itemLabel = new Label("Item");
			Label sectionLabel = new Label("Section");
			Label quantityLabel = new Label ("Quantity");

			ComboBox<Item> itemAdd = new ComboBox<Item>();
			itemAdd.getItems().addAll(warehouseDSC.getAllItems());

			ChoiceBox<WarehouseDSC.SECTION> sectionAdd = new ChoiceBox<>();
			sectionAdd.getItems().addAll(WarehouseDSC.SECTION.values());

			Button clearBtn = new Button("Clear");
			Button saveBtn = new Button("Save");

			TextField quantityAdd = new TextField();
			GridPane addGrid = new GridPane();
			addGrid.setPadding(new Insets(10,10,10,10));
			addGrid.setVgap(5);
			addGrid.setHgap(5);
			addGrid.add(itemLabel,0,0);
			addGrid.add(itemAdd,0,1);
			addGrid.add(sectionLabel,1,0);
			addGrid.add(sectionAdd,1,1);
			addGrid.add(quantityLabel,2,0);
			addGrid.add(quantityAdd,2,1);


			HBox saveBox = new HBox (clearBtn,saveBtn);
			saveBox.setSpacing(5);
			saveBox.setAlignment(Pos.CENTER);

			VBox hiddenAddBox = new VBox(addGrid,saveBox);
			hiddenAddBox.setVisible(false);
			addBtn.setOnAction(e -> hiddenAddBox.setVisible(true));

	        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
	        infoAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	        Alert errorAlert = new Alert (Alert.AlertType.ERROR);
			errorAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			saveBtn.setOnAction(e->
			{
				try {
					if ((itemAdd.getValue().getName()!= null) && sectionAdd.getValue()!=null)
					{
						System.out.println(quantityAdd.getText()!=null);
						if(!quantityAdd.getText().isEmpty()){
							confirmationAlert.setContentText("Would you like to add the item ?");
							Optional<ButtonType> result = confirmationAlert.showAndWait();
							if (result.isPresent()) {
								if (result.get() == ButtonType.OK) {
									warehouseDSC.addProduct(itemAdd.getValue().getName(), Integer.parseInt(quantityAdd.getText()), sectionAdd.getValue());
									tableData.clear();
									tableData.addAll(warehouseDSC.getAllProducts());
									infoAlert.setContentText("Item successfully added to the Warehouse");
									infoAlert.showAndWait();
									itemAdd.getSelectionModel().clearSelection();
									sectionAdd.getSelectionModel().clearSelection();
									quantityAdd.clear();
									tableView.getSelectionModel().clearSelection();
									hiddenAddBox.setVisible(false);
								}

							}
						}
						else
						{
							errorAlert.setContentText("Quantity cannot be empty");
							errorAlert.showAndWait();
						}
					}
					else
					{
						errorAlert.setContentText("Section cannot be empty ");
						errorAlert.showAndWait();
					}

				}
				catch (NullPointerException empty)
				{
					errorAlert.setHeaderText(empty.toString());
					errorAlert.setContentText("Item Value Cannot be empty ");
					errorAlert.showAndWait();

				}
				catch (NumberFormatException n)
				{
					errorAlert.setHeaderText(n.toString());
					errorAlert.setContentText(" Add the quantity number");
					errorAlert.showAndWait();

				}
				catch(Exception ex)
				{
					errorAlert.setContentText(ex.toString());
					errorAlert.showAndWait();
				}
			});

			//clear button
			clearBtn.setOnAction(e-> {
				itemAdd.getSelectionModel().clearSelection();
				sectionAdd.getSelectionModel().clearSelection();
				quantityAdd.clear();
			});

			//update button

			updateBtn.setOnAction(e ->
			{
				hiddenAddBox.setVisible(false);
				Product p = tableView.getSelectionModel().getSelectedItem();
				if (g !=null) {
					try {
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Would you like to update the selected Product?");
						Optional<ButtonType> result = alert.showAndWait();
						if (result.isPresent()) {
							if (result.get() == ButtonType.OK) {
								warehouseDSC.useProduct(p.getId());
								tableData.clear();
								tableData.addAll(warehouseDSC.getAllProducts());
								infoAlert.setContentText(" Item Id " + p.getId() + ": " + p.getItemName() + " quantity is reduced from " + p.getQuantity() + " to " +(p.getQuantity()-1) );
								infoAlert.showAndWait();
								tableView.getSelectionModel().clearSelection();
							}
						}

					} catch (Exception exception) {
						errorAlert.setContentText(exception.toString());
						errorAlert.showAndWait();

					}
				}
				else
				{
					errorAlert.setContentText("Please select a row from table to make a change");
					errorAlert.showAndWait();
				}

			});

			// delete Button

			deletBtn.setOnAction(e ->
			{
				hiddenAddBox.setVisible(false);
				Product p = tableView.getSelectionModel().getSelectedItem();
				if ( p !=null) {
					try {
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Would ypu like to delete selectef product?");
						Optional<ButtonType> result = alert.showAndWait();
						if (result.isPresent()) {
							if (result.get() == ButtonType.OK) {
								warehouseDSC.removePeoduct(p.getId());
								tableData.clear();
								tableData.addAll(warehouseDSC.getAllProducts());
								infoAlert.setContentText("Item Id " +p.getId() +": " + p.getItemName() + " is removed from the warehouse " );
								infoAlert.showAndWait();
								tableView.getSelectionModel().clearSelection();
							}
						}
					}
					catch (Exception exception)
					{
						errorAlert.setContentText(exception.toString());
						errorAlert.showAndWait();

					}
				}
				else
				{
					errorAlert.setContentText("Please select a row from table to delete");
					 errorAlert.showAndWait();

				}


			});
		/* TODO 2-13 - TO COMPLETE ****************************************
		 * add input controls and container(s)
		 * - Item will list item data from the data source controller list
		 *   all items method
		 * - Section will list all sections defined in the data source
		 *   controller SECTION enum
		 * - Quantity: a texf field, self descriptive
		 * - CANCEL button: clears all input controls
		 * - SAVE button: sends the new product information to the data source
		 *   controller add product method; be mindful of exceptions when any
		 *   or all of the input controls are empty upon SAVE button click
		 */	

		// =====================================================================
		// SET UP the Stage
		// =====================================================================
		// Create scene and set stage
		VBox root = new VBox(topRow,tableView,buttonBox,hiddenAddBox);
		root.setSpacing(5);
		root.setPadding(new Insets(5,5,5,10));

		/* 
		 add all your containers, controls to the root
		 */		

		root.setStyle(
			"-fx-font-size: 20;" +
			"-fx-alignment: center;"
		);

		Scene scene = new Scene(root);
		stage.setScene(scene);
	}

	public void stop() throws Exception {
		public void stop() throws Exception {
			try
			{
				WarehouseDSC.disconnect();
			}
			catch (Exception exception)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText(exception.toString());
				Optional<ButtonType> result = alert.showAndWait();
			}

		/* 
		  call the data source controller database disconnect method
		  NOTE: that database disconnect method throws exception
		 */				
	}	
}
