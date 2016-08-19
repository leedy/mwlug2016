package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.ViewEntryCollection;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Strings;
import org.openntf.domino.utils.XSPUtil;

import com.ibm.commons.util.StringUtil;

public class Messages extends com.notesIn9.base.PageController {

	
	private String hero;
	private Map<String, String> dcUniverse;
	private Map<String, String> marvelUniverse;
	
	
	public boolean pageInit() {
		super.pageInit();
		
		this.addBreadCrumb("Messages Demo");
	
		this.dcUniverse = new HashMap<String, String>();
		this.dcUniverse.put("Oliver Queen", "Green Arrow");
		this.dcUniverse.put("Barry Allen", "Flash");
		this.dcUniverse.put("Bruce Wayne", "Batman");
		this.dcUniverse.put("Clark Kent", "Superman");
		
		this.marvelUniverse = new HashMap<String, String>();
		this.marvelUniverse.put("Tony Stark", "Iron Man");
		this.marvelUniverse.put("Matt Murdock", "DareDevil");
		this.marvelUniverse.put("Janet Van Dyne", "Wasp");
		this.marvelUniverse.put("Bruce Banner", "Hulk");
	
		
		return true;
	}
	
	
	public List<SelectItem> getBoth() {
		// http://stackoverflow.com/questions/21532641/populating-selectitems-of-the-combobox-label-value-using-a-managed-bean
		// From Mark Leusink
		
		SelectItem option = new SelectItem();
		option.setLabel("Choose Someone");
		option.setValue("");
		
		
		
		 List<SelectItem> groupedOptions = new ArrayList<SelectItem>();

		 
		 groupedOptions.add(option);
		 
		    SelectItemGroup group1 = new SelectItemGroup("DC");

		    SelectItem[] dc = new SelectItem[4];
		    
		    int count = 0;
		    for(Entry<String, String> entry : this.dcUniverse.entrySet()) {
		    	dc[count] = new SelectItem(entry.getKey(), entry.getValue());
		    	count++;
		    	
		    //    String key = entry.getKey();
		    //    String value = entry.getValue();

		        // do what you have to do here
		        // In your case, an other loop.
		    }
		    
		    group1.setSelectItems(dc);

		    groupedOptions.add(group1);

		    SelectItemGroup group2 = new SelectItemGroup("Marvel");
		    
		    SelectItem[] marvel = new SelectItem[4];
		    count = 0;
		    
		    for(Entry<String, String> entry : this.marvelUniverse.entrySet()) {
		    	marvel[count] = new SelectItem(entry.getKey(), entry.getValue());
		    	count++;
		    	
		    }
		    
		    
		    group2.setSelectItems(marvel);
		    groupedOptions.add(group2);
		    
		    
		    
		    
		    return groupedOptions;
	}
	
	private SelectItem addItem(String name) {
		SelectItem option = new SelectItem();
		option.setLabel(name);
		option.setValue(name);
		return option;
	}


	public String getHero() {
		return hero;
	}


	public void setHero(String hero) {
		this.hero = hero;
		
		if (StringUtil.isNotEmpty(hero)) {
			if (this.dcUniverse.containsKey(hero)) {
				this.getMessage().setError();
			} else {
				this.getMessage().setSuccess();
			}
			 
			this.getMessage().appendMessage(this.hero);
		}
	}
	
	
	
}
