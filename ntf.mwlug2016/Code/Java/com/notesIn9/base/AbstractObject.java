package com.notesIn9.base;

import java.io.Serializable;
import java.util.List;

import com.ibm.xsp.acl.RedirectSignal;
import javax.faces.context.FacesContext;

import org.openntf.domino.Session;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.XSPUtil;

import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class AbstractObject implements Serializable {

	private static final long serialVersionUID = 1L;
	



	protected void console(final String debugText) {

		// add logic here to not print on production servers.

		System.out.println(this.getClass().getName() + " : " + debugText);
	}

	protected void print(String msg) {
		// Just another method incase someone wants to use print() instead.

		this.console(msg);

	}

	

	// borrowed from Tim Tripcony's JSFUtil
	public static XSPContext getXSPContext() {
		return XSPContext.getXSPContext(getFacesContext());
	}

	// borrowed from Tim Tripcony's JSFUtil
	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

}
