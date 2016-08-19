package com.notesIn9.base;

import java.util.List;
import java.util.Map;

import org.openntf.domino.Session;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

import com.ibm.xsp.acl.RedirectSignal;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.notesIn9.xsp.PageMessage;
import com.notesIn9.xsp.BreadCrumb;


public class PageController extends com.notesIn9.base.AbstractObject {

	private static final long serialVersionUID = 1L;

	private List<String> userGroups;
	private List<String> userRoles;
	private String userName;

	public PageController() {

	}
	
	public void beforePageLoad() {
		// Note if you use Jesse Gallagher's frostillicus framework
		// you can bind to the beforePageLoad() even which is rather handy
		// I don't believe that's an option if 
		// you do the no dependency panel binding controller.
		
		
	}

	protected boolean pageInit() {

		Session session = Factory.getSession(SessionType.CURRENT);
		this.userName = AbstractObject.getXSPContext().getUser().getCommonName();
		this.loadUserData();

		return true;

	}
	
	protected void loadUserData() {

		// We're storing the groups just to have
		this.userGroups = AbstractObject.getXSPContext().getUser().getGroups();
		this.userRoles = AbstractObject.getXSPContext().getUser().getRoles();

	}

	public PageMessage getMessage() {
		return PageMessage.getPageMessage();
	}
	
	// Just a handy method if you want to redirect as user to another page
	// from inside Java.
	public void redirectToPage(final String pageName) {
		// pageName = "/myPage.xsp"

		try {
			// You'd think this would end all Java processing but that's NOT
			// what happens
			// It looks like the Java code will finish and only then will the
			// redirection happen.

			final String entryPage = AbstractObject.getXSPContext().getUrl()
					.getPath()
					+ AbstractObject.getXSPContext().getUrl().getQueryString();

			AbstractObject.getXSPContext().redirectToPage(pageName);

		} catch (final RedirectSignal rs) {
			// Ignoring this error. Useless!
			// Everything's fine. This just prevents crap from polluting the
			// console.
		}

	}



	public boolean isGroupMember(String groupName) {

		//If you don't want to store the groups this is another way to get the info
		return AbstractObject.getXSPContext().getUser().getGroups().contains(
				groupName);

		// if (this.userGroups.contains(groupName)) {
		// return true;
		// } else {
		// return false;
		// }

	}

	public boolean hasRole(String roleName) {

		return AbstractObject.getXSPContext().getUser().getRoles().contains(
				roleName);

		// if (this.userRoles.contains(roleName)) {
		// return true;
		// } else {
		// return false;
		// }

	}
	
	public String getParam(final String key) {
		if (!this.getQueryString().containsKey(key)) {
			return null;
		} else {
			String temp = this.getQueryString().get(key);
			String sanitize = temp.replaceAll("\\#\\{[^\\}]+\\}", "");
			
			return sanitize;
		}

	}


	@SuppressWarnings("unchecked")
	public Map<String, String> getQueryString() {
		final Map<String, String> qs = (Map<String, String>) ExtLibUtil.resolveVariable("param");
		return qs;
	}

public void addBreadCrumb(String displayText) {
		
		BreadCrumb bc = (BreadCrumb) ExtLibUtil.resolveVariable("Breadcrumb");
		
		String currentPage = AbstractObject.getXSPContext().getUrl().getPath() + AbstractObject.getXSPContext().getUrl().getQueryString();
//		this.console("Page : " + AbstractObject.getXSPContext().getUrl().getPath());
//		this.console("QS : " + AbstractObject.getXSPContext().getUrl().getQueryString());
		this.console(currentPage);
		bc.addLink(displayText, currentPage);
		
	}
}
