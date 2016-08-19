package controller;

public class forceLogout extends com.notesIn9.base.PageController {

	private static final long serialVersionUID = 1L;

	
	public boolean pageInit() {
		super.pageInit();
		
		this.addBreadCrumb("Force Logout");
		
	
		return true;
	
	}
	
}
