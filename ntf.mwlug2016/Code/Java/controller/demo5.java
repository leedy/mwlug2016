package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.openntf.domino.utils.Strings;

import com.notesIn9.model.util.PersonField;


public class demo5 extends com.notesIn9.base.PageController {


	private static final long serialVersionUID = 1L;
	
	private Set<String> optionalFields;  
	private List<String> selectedFields;
	List<String> defaultColumns;

	public demo5() {
		
		this.optionalFields = new TreeSet();
		this.selectedFields = new ArrayList<String>();
		
		
		for (PersonField temp : PersonField.values()) {
			this.optionalFields.add(temp.getLabel());
		}
		
		
		// Create the empty enumset - I ASSUME these are in the "ordinal" order
		this.defaultColumns = new ArrayList<String>();

		// Now create a loop and add the columns that ARE in the bitmask
		for (final PersonField personfield : PersonField.values()) {
			if (personfield.isFlagged(PersonField.getStandardFields())) {
				this.defaultColumns.add(personfield.getLabel());
			}
		}
		
	}
	
	public List<String> getDefaultColumns() {
		return this.defaultColumns;
		
	}
	
	public void setDefaultColumns(List<String> defaultColumns) {
		this.defaultColumns = defaultColumns;
	}

	public Set<String> getOptionalFields() {
		return optionalFields;
	}

	public void setOptionalFields(Set<String> optionalFields) {
		this.optionalFields = optionalFields;
	}

	public Object getSelectedFields() {
		return this.selectedFields;
	}

	public void setSelectedFields(Object inputMulti) {
		this.selectedFields = Strings.getVectorizedStrings(inputMulti);
	}
	
	public String getIndividualReport() {

		this.console("BitMask : ");
		this.printSelectedFields();

		
	//	return "/.ibmxspres/domino/inventory/reports.nsf/excel_Inventory.xsp?type=EXHIBITOR&id=" + this.getId() + "&columns=" + this.getBitMask();
		return  "/xExcelDownload.xsp?state=PA&type=MAIN&columns=" + this.getBitMask();

	}
	
	public long getBitMask() {
		
		long mask = 0;

		PersonField myEnum = null;
		if (null == this.selectedFields) {
			this.console("Selected Fields = null");
			return PersonField.getStandardFields();
		} else {
			
			for (String str : this.selectedFields) {

				myEnum = PersonField.getValue(str);
				mask = mask + myEnum.getFlag();
			
			}

			this.console("getBitMask() : " + PersonField.getStandardFields() + mask);
			return PersonField.getStandardFields() + mask;
			
		}
		
	}
	
	public void validateMask(long mask) {
		
		for (PersonField field : PersonField.values()) {
			
			if (field.isFlagged(mask)) {
				System.out.println(field.name() + " is Inside Mask.  BitMask : " + field.getFlag() );
			}
			
		}
			
	}
	
	public void printSelectedFields() {
		

		long result = this.getBitMask();
		this.console("printSelectedFields : " + result);
		
		this.validateMask(result);
	}
	
}
