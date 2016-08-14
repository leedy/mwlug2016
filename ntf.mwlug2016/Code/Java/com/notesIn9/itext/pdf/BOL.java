package com.notesIn9.itext.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;

import lotus.domino.NotesException;

import org.openntf.domino.Database;
import org.openntf.domino.DateTime;
import org.openntf.domino.Document;
import org.openntf.domino.DocumentCollection;
import org.openntf.domino.EmbeddedObject;
import org.openntf.domino.Item;
import org.openntf.domino.MIMEEntity;
import org.openntf.domino.Name;
import org.openntf.domino.RichTextItem;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.ViewEntryCollection;
import org.openntf.domino.utils.CollectionUtils;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Strings;
import org.openntf.domino.utils.XSPUtil;

import sun.misc.IOUtils;




import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.webapp.XspHttpServletResponse;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Jpeg;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfConcatenate;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class BOL {
	
	// NOTE : 
	// Much of this code was adapted from code by Declan Lynch
	// With additional modifications by Devin Olson.
	// I've left in many unused constants and some unneeded code 
	// The original Declan code combined several PDF's into 1 big report.
	

	
	private static final String CONTENT_TYPE = "application/pdf";
	// Margins For Full Report
	private final static float BOTTOM_MARGIN = 50f;
	private final static float LEFT_MARGIN = 20f;
	private final static float RIGHT_MARGIN = 20f;
	private final static float TOP_MARGIN = 70f;
	// Margins For Single Note Report
	private final static float SINGLE_BOTTOM_MARGIN = 50f;
	private final static float SINGLE_LEFT_MARGIN = 20f;
	private final static float SINGLE_RIGHT_MARGIN = 20f;
	private final static float SINGLE_TOP_MARGIN = 150f;
	private ByteArrayOutputStream coverBAOS_;
	private ByteArrayOutputStream noteBAOS_;
	private ByteArrayOutputStream attachBAOS_;
	private ByteArrayOutputStream skippedBAOS_;
	private ByteArrayOutputStream stampedBAOS_;
	private ByteArrayOutputStream unstampedBAOS_;
	private ByteArrayOutputStream finalBAOS_;
	private com.itextpdf.text.Document notePDF_;
	private com.itextpdf.text.Document skippedPDF_;
	private PdfWriter Pdfwriter_;
	private PdfWriter skippedWriter_;
	private PdfConcatenate attachWriter_;
	private transient Database database_;

	
	
	private Map<String, String> params_;
	
	
	
	private boolean alternate;
	
	// These are for the example.  Normally you'd read from a document
	private Date shipDate;
	private String exhibitorName;
	private String jobNumber;
	private String manifest;
	private String trailerNumber;
	private String driverName;
	private String driverPhone ;
	private String carrier;
	private String tracking;
	private String notes;
	private String thirdPartyAddress;
	private String terms;
	
	
	
	
	public BOL() {
		alternate = false;  // not really needed since it defaults to false
		
	}
	
	public void doReport() throws DocumentException, IOException, Exception {
		
		Map<String, String> param = (Map<String, String>) ExtLibUtil.resolveVariable("param");
		
		// just a little example of how to get to url parameters
		if (param.containsKey("other")) {
			this.alternate = true;
		}
		
		
		this.buildFullCoverPage();
		this.sendToClient(this.getCoverBAOS());
		
	}
	
	
	private void setVariables1() {
		
		this.shipDate = new Date();
		this.exhibitorName = "NotesIn9";
		this.jobNumber = "9";
		this.manifest = "54-899";
		this.trailerNumber = "123";
		this.driverName = "David Leedy";
		this.driverPhone = "555-9999";
		this.carrier = "We will Ship your Pants";
		this.tracking = "03-3454228";
		this.notes = "Anti Zombie weapons.  Handle with Care";
		this.terms = "prepaid";
		
		
	}
	
	private void setVariables2() {
		this.shipDate = new Date();
		this.exhibitorName = "Marky Roden";
		this.jobNumber = "12";
		this.manifest = "67-352";
		this.trailerNumber = "123";
		this.driverName = "Mickey Mouse";
		this.driverPhone = "555-9999";
		this.carrier = "We will Ship your Pants";
		this.tracking = "03-3454228";
		this.notes = "Exhibitor needs a truckload of mis matched socks ASAP!";
		this.terms = "prepaid";
		
	}
	
	
	private void buildFullCoverPage() throws DocumentException, IOException {
		
		final PdfReader pdfCoverTemplate = this.getCoverTemplateFull();
		final PdfStamper pdfCoverStamper = new PdfStamper(pdfCoverTemplate, this.getCoverBAOS());
		pdfCoverStamper.setFormFlattening(true);
		// pdfCoverStamper.getAcroFields().setField("exhibitorNameTop",applicationScope.job.EXHIBITNAME);	
		
//		this.setJobField(pdfCoverStamper, "boothSize", job.getBoothSize());
		
		if (this.alternate) {
			this.setVariables2();
		} else { 
			this.setVariables1();
		}
		
		
		pdfCoverStamper.getAcroFields().setField("date", this.formatDate(this.shipDate));
		pdfCoverStamper.getAcroFields().setField("exhibitorName",this.exhibitorName);
		pdfCoverStamper.getAcroFields().setField("jobNumber", this.jobNumber);
		pdfCoverStamper.getAcroFields().setField("manifest", this.manifest);
		pdfCoverStamper.getAcroFields().setField("trailerNumber", this.trailerNumber );
		pdfCoverStamper.getAcroFields().setField("driverName", this.driverName );
		pdfCoverStamper.getAcroFields().setField("driverPhone", this.driverPhone );
		pdfCoverStamper.getAcroFields().setField("carrier", this.carrier );
		pdfCoverStamper.getAcroFields().setField("tracking", this.tracking );
		pdfCoverStamper.getAcroFields().setField("notes",this.notes);
		pdfCoverStamper.getAcroFields().setField("thirdPartyAddress", this.thirdPartyAddress  );
		
		pdfCoverStamper.getAcroFields().setField("terms", this.terms);
		
		//pdfCoverStamper.getAcroFields().setField("destination", manifest.getDestAddressLabel());
		
		

		String newLine = "\n";
		
		String shipFrom = "Main Street USA" + newLine;
		shipFrom += "Orlando, FL, 12345";
		
		pdfCoverStamper.getAcroFields().setField("origin", shipFrom);
		
	
		// String Builder is better then the above method
		StringBuilder destination = new StringBuilder();
		destination.append("9 ScreenCast Lane");
		destination.append("\n");
		destination.append("City, State, Zipcode");
		
		
		pdfCoverStamper.getAcroFields().setField("destination", destination.toString());
		
		
		pdfCoverStamper.getAcroFields().setField("qty1", "5");
		pdfCoverStamper.getAcroFields().setField("itemType1", "SKID");
		pdfCoverStamper.getAcroFields().setField("weight1", "150");
		
		pdfCoverStamper.getAcroFields().setField("qtyTotals", "5" );
		pdfCoverStamper.getAcroFields().setField("weightTotals", "150");
		
		
		
		pdfCoverStamper.close();
		pdfCoverTemplate.close();
	}
	
	private String addLine(String value) {
		
		String newLine = "\n";
		String temp = "";
		
		if (Strings.isBlankString(value)) {
			// do nothing
		} else {
			temp = value + newLine;
		}
	
		return temp;
	}
	
	
	
	/**
	 * Gets the cover template full.
	 * 
	 * @return the cover template full
	 * @throws IOException
	 */
	public PdfReader getCoverTemplateFull() throws IOException {
		this.printDebug("COMS Report : Getting Full Cover Page");
		final InputStream inStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("BOL-MWLUG.pdf");
		final PdfReader result = new PdfReader(inStream);
		return result;
	}
	
	/**
	 * Gets the cover baos.
	 * 
	 * @return the cover baos
	 */
	private ByteArrayOutputStream getCoverBAOS() {
		if (this.coverBAOS_ == null) {
			this.coverBAOS_ = new ByteArrayOutputStream();
		}
		return this.coverBAOS_;
	}

	
	
	private static String formatDate(final Date date) {
		if (date == null) { return ""; }
		final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		return format.format(date);
	}
	
	
	/**
	 * Prints the.
	 * 
	 * @param message
	 *            the message
	 */
	private void printDebug(final String message) {
		
			System.out.println(message);
		
	}
	
	
	/**
	 * Send to client.
	 * 
	 * @param finalBAOS
	 *            the final baos
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws NotesException
	 */
	private void sendToClient(final ByteArrayOutputStream finalBAOS) throws IOException {
		this.printDebug("COMS BOL : Sending Final PDF To Web Browser");
		// Open the servlet Response
		final FacesContext context = FacesContext.getCurrentInstance();
		final ExternalContext con = context.getExternalContext();
		final XspHttpServletResponse response = (XspHttpServletResponse) con.getResponse();
		// Setup The File Name
		
		response.setHeader("Content-Disposition", "attachment;filename=" + "TEST-BOL.pdf");
		
		// setting response headers for browser to recognize data
		response.setContentType(BOL.CONTENT_TYPE);
		// response.setHeader("Pragma", "public");
		// response.setHeader("Cache-Control", "max-age=0");
		response.setDateHeader("Expires", -1);
		// response.setContentLength(finalBAOS.size());
		// response.addIntHeader("Accept-Header", finalBAOS.size());
		// get a handle on the actual output stream and write the stampedBAOS
		final ServletOutputStream servletOutputStream = response.getOutputStream();
		finalBAOS.writeTo(servletOutputStream);
		servletOutputStream.flush();
		servletOutputStream.close();
		// Close the renderer and return the document to the browser
		context.responseComplete();
		this.closeAllStreams();
	}
	
	/**
	 * Close all streams.
	 */
	private void closeAllStreams() {
		this.printDebug("COMS Report : Clean Up : Closing All Streams");
		if (this.attachBAOS_ != null) {
			try {
				this.attachBAOS_.flush();
			} catch (final Throwable t) {
			}
			try {
				this.attachBAOS_.close();
			} catch (final Throwable t) {
			}
		}
		if (this.coverBAOS_ != null) {
			try {
				this.coverBAOS_.flush();
			} catch (final Throwable t) {
			}
			try {
				this.coverBAOS_.close();
			} catch (final Throwable t) {
			}
		}
		if (this.skippedBAOS_ != null) {
			try {
				this.skippedBAOS_.flush();
			} catch (final Throwable t) {
			}
			try {
				this.skippedBAOS_.close();
			} catch (final Throwable t) {
			}
		}
		if (this.stampedBAOS_ != null) {
			try {
				this.stampedBAOS_.flush();
			} catch (final Throwable t) {
			}
			try {
				this.stampedBAOS_.close();
			} catch (final Throwable t) {
			}
		}
		if (this.noteBAOS_ != null) {
			try {
				this.noteBAOS_.flush();
			} catch (final Throwable t) {
			}
			try {
				this.noteBAOS_.close();
			} catch (final Throwable t) {
			}
		}
		if (this.finalBAOS_ != null) {
			try {
				this.finalBAOS_.flush();
			} catch (final Throwable t) {
			}
			try {
				this.finalBAOS_.close();
			} catch (final Throwable t) {
			}
		}
		if (this.unstampedBAOS_ != null) {
			try {
				this.unstampedBAOS_.flush();
			} catch (final Throwable t) {
			}
			try {
				this.unstampedBAOS_.close();
			} catch (final Throwable t) {
			}
		}
	}
	

	
	@SuppressWarnings("unchecked")
	private Map<String, String> getParams() {
		if (this.params_ == null) {
			this.params_ = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		}
		return this.params_;
	}
	
	
}
