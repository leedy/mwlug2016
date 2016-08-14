package com.notesIn9.poi;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.ss.usermodel.CellStyle;

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Strings;
import org.openntf.domino.utils.XSPUtil;

import org.openntf.domino.Name;
import org.openntf.domino.Session;
import org.openntf.domino.Database;
import org.openntf.domino.View;
import org.openntf.domino.DocumentCollection;
import org.openntf.domino.Document;

import com.ibm.commons.util.StringUtil;
import javax.servlet.ServletOutputStream;

import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.webapp.XspHttpServletResponse;
import com.notesIn9.model.State;
import com.notesIn9.model.Person;
import com.notesIn9.model.PersonGroup;
import com.notesIn9.model.util.PersonField;

public class ExcelDemo {

	private static final long serialVersionUID = 1L;

	private HSSFWorkbook workbook;
	private HSSFSheet sheet;

	HSSFCreationHelper helper;
	HSSFCellStyle dateStyle;

	private List<String> columnHeads;
	private int columnCount;
	private int rowNumber;

	private boolean history;
	private boolean family;


	private ReportType reportType;
	private String reportKey;
	private String stateId;

	private String reportTitle;
	private long columns;
	EnumSet<PersonField> reportColumns;

	private boolean bottomContents;
	private String currentLocation;
	private boolean bottomContentsCombine;
	
	private HSSFCellStyle _contentsStyle;
	
	private PersonGroup personGroup;
	
	
	

	private boolean useExhibitorFamily = false;

	public static enum ReportType {
		MAIN, JOB, MANIFEST, VERIFICATION, FACILITY;

		public static ReportType getValue(final Object key) {
			try {
				final String ucKey = key.toString().toUpperCase();
				return (StringUtil.isEmpty(ucKey)) ? null : ReportType.valueOf(ucKey);
			} catch (final Exception e) {
				// do nothing
			}

			return null;
		}

	}

	public ExcelDemo() {

	}


	public void init() throws Exception {

		//		

		/**
		 * This report should work against and Exhibitor, Job (current Items),
		 * Job (Historic Items), or Manifest
		 * 
		 * parameters : type : exhibitor, job, manifest, verification id :
		 * unique key that is linked to the type scope : current, history
		 * (mostly for Jobs) columns : bitmask family : true, false - for
		 * exhibitor family
		 * 
		 * 
		 */
		
		if (StringUtil.isEmpty(this.getParam("state"))) {
			// No State field in URL
			// TODO Do something
		} else {
			this.stateId = (this.getParam("state").toUpperCase());
		}

		if (StringUtil.isEmpty(this.getParam("type"))) {
			// No Type field in URL
			// TODO Do something
		} else {
			this.reportType = ReportType.valueOf(this.getParam("type"));
		}

		// If type isn't a valid UPPERCASE ENUM option... then it's going to
		// crash

		this.reportKey = this.stateId;
		this.columns = Long.parseLong(this.getParam("columns"));

		// Create the empty enumset - I ASSUME these are in the "ordinal" order
		this.reportColumns = EnumSet.noneOf(PersonField.class);

		// Now create a loop and add the columns that ARE in the bitmask
		for (final PersonField temp : PersonField.values()) {
			if (temp.isFlagged(columns)) {
				this.reportColumns.add(temp);
			}
		}

		
		

		if ("true".equalsIgnoreCase(this.getParam("bottomContents"))) {
			this.bottomContents = true;
		}
		
		if ("Y".equalsIgnoreCase(this.getParam("combine"))) {
			this.bottomContentsCombine = true;
		}

		switch (this.reportType) {

		case MAIN:
			// do this
			State state = new State(this.stateId);
			state.loadPeople();
			this.personGroup = state.getPeople();
			this.reportTitle = "People in " + this.stateId;
			System.out.println("People COUNT : " + state.getPeople().getCount());
		

			break;
		case JOB:
			// do this
			
			
			
			this.reportTitle =  "Item Tracking Report";
			
			
			
			break;
		case MANIFEST:
			// do this
			break;
		case FACILITY:
			// Facility here
		
			
			break;
			
		default:
			//nothing to do

		}

	}

	public void run() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			this.init();

			// System.out.println("Create Sheet for : " +
			// this.exhibitor.getId());

			this.workbook = new HSSFWorkbook();

			this.sheet = workbook.createSheet(this.reportKey);

			rowNumber = 0;
			buildHeaderRow(rowNumber);
			// addToRowNumber(1);
			// buildBlankRow(rowNumber);
			// buildTestContent();
			this.buildSheetContent();
			// System.out.println("Finished Build Sheet Content");
			// sendAsDownload(sheetName);
			ExternalContext con = facesContext.getExternalContext();
			XspHttpServletResponse response = (XspHttpServletResponse) con.getResponse();
			// Setting response headers for the browser to recognize data
			response.setContentType("application/x-ms-excel");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", -1);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + this.reportKey + ".xls\"");
			ServletOutputStream outStream = (ServletOutputStream) response.getOutputStream();
			this.workbook.write(outStream);
			outStream.flush();
			outStream.close();

		} catch (Exception ex) {
			System.out.println("Error generating dynamic Excel report: " + ex.toString());
			ex.printStackTrace();
		} finally {
			facesContext.responseComplete();
		}
	}

	public HSSFRow buildHeaderRow(int num) {
		HSSFRow row = sheet.createRow(num);
		HSSFFont font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle style = workbook.createCellStyle();

		style.setFont(font);
		HSSFCell cell;

		int colCount = 0;

		// Build the header Row of Columns

		for (PersonField temp : this.reportColumns) {
			cell = row.createCell(colCount++);
			cell.setCellValue(temp.getLabel());
			cell.setCellStyle(style);
			
		}

		// END DEFAULT HEADERS


		// Total number of columns
		this.columnCount = colCount++;

		return row;
	}

	public HSSFRow buildBlankRow(int num) {
		HSSFRow row = sheet.createRow(num);
		// HSSFCreationHelper helper = workbook.getCreationHelper();
		this.helper = this.workbook.getCreationHelper();
		for (int i = 0; i < columnCount; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(helper.createRichTextString(""));
		}
		return row;
	}

	public void buildSheetContent() {
		try {

			int count = 0;
			int colCount = 0;

			Session session = Factory.getSession();

			this.helper = workbook.getCreationHelper();
			this.dateStyle = workbook.createCellStyle();

			HSSFCellStyle wrapStyle = workbook.createCellStyle();
			wrapStyle.setWrapText(true);

			// dateStyle.setDataFormat(helper.createDataFormat().getFormat("MMMM dd, yyyy"));
			this.dateStyle.setDataFormat(helper.createDataFormat().getFormat("mm/dd/yyyy"));

			HSSFRow row;
			HSSFCell cell;
			if (this.personGroup.getCount() < 1) {
				addToRowNumber(1);
				row = sheet.createRow(rowNumber);
				cell = row.createCell(0);
				cell.setCellValue(helper.createRichTextString("-------"));
				cell = row.createCell(1);
				cell.setCellValue(helper.createRichTextString("None"));
				cell = row.createCell(2);
				cell.setCellValue(helper.createRichTextString("-----"));
			} else {
				for (Person person : this.personGroup.getPeople()) {
					addToRowNumber(1);
					count++;

					row = sheet.createRow(rowNumber);
					colCount = 0;
					cell = row.createCell(colCount);

					for (PersonField temp : this.reportColumns) {
						cell = row.createCell(colCount++);
						

						if (temp.getLabel().equalsIgnoreCase(PersonField.ADDINFO.getLabel())) {

							// if Contents then we need to determine if on right or on bottom.

							if (this.bottomContents) {
								// Do nothing here. Since the contents go on the
								// bottom save that for after the loop.

							} else {
								// Contents on Right
								int lineCount = this.addCellMultiLine(person.getAddInfo(), cell);
									
								
								
									if (lineCount > 1) {
										row.setHeightInPoints((lineCount * this.getSheet().getDefaultRowHeightInPoints()));
									} 
								
								
								
								// this.contentsOnRight(row, cell);
								// adjust row height here
							}

						} else {
							// this is NOT a contents column.  Just add it normally.
							// System.out.println("TEMP : " + temp.getLabel());
							this.addCellContent(temp, person, cell);
							
						}
						
						
						

					}

					// Finished processing the item Row. Now If the contents go
					// on the bottom we need to deal with there here.
					if (PersonField.ADDINFO.isFlagged(columns)) {
						// We want to produce the contents
						
						
						if ( this.bottomContents) {
							count = count + this.contentsOnBottom(person);
						} else {
							
							//this.addCellContent(ItemField.CONTENTS_ALL, item, cell);
							//this.contentsOnRight(row, cell);
							
							int lineCount = this.addCellMultiLine(person.getAddInfo(), cell);
							if (lineCount > 1) {
								row.setHeightInPoints((lineCount * this.getSheet().getDefaultRowHeightInPoints()));
							} 
							
							
						}
	
					}
					
				}

			}

			// Autoset the width of the spreadsheet columns based upon the
			// values
			for (int i = 0; i < columnCount; i++) {
				this.sheet.autoSizeColumn(i);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int contentsOnBottom(Person person) {

		// returning the number if lines added from the contents.

		int count = 0;

		if (!Strings.isBlankString(person.getAddInfo())) {
			
			// Only process if there's something in the field.
			HSSFRow row;
			HSSFCell cell;

			addToRowNumber(1);
			row = sheet.createRow(rowNumber);
			cell = row.createCell(3);
			cell.setCellValue("Contents:");

			
			
			// Allow for all contents in 1 cell OR 1 row per line
			if (bottomContentsCombine) {
				
				cell = row.createCell(4);
				int lineCount = this.addCellMultiLine(person.getAddInfo(), cell);
				
				if (lineCount > 1) {
					row.setHeightInPoints((lineCount * this.getSheet().getDefaultRowHeightInPoints()));
				} 
				
			
				
			} else {
				for (String temp : this.getContentLines(person)) {

					cell = row.createCell(4);
					cell.setCellValue(helper.createRichTextString(temp));
					addToRowNumber(1);
					count++;
					row = sheet.createRow(rowNumber);

				}
				
				
			}
			
			
			
			
		}
		
		

		return count;

	}

	



	public void sendAsDownload(String sheetName) {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext con = facesContext.getExternalContext();
			XspHttpServletResponse response = (XspHttpServletResponse) con.getResponse();

			// Setting response headers for the browser to recognize data
			response.setContentType("application/x-ms-excel");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", -1);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + sheetName + ".xlsx\"");
			ServletOutputStream outStream = (ServletOutputStream) response.getOutputStream();
			this.workbook.write(outStream);
			outStream.flush();
			outStream.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return the workbook
	 */
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	/**
	 * @param workbook
	 *            the workbook to set
	 */
	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	/**
	 * @return the sheet
	 */
	public HSSFSheet getSheet() {
		return sheet;
	}

	/**
	 * @param sheet
	 *            the sheet to set
	 */
	public void setSheet(HSSFSheet sheet) {
		this.sheet = sheet;
	}

	/**
	 * @return the columnCount
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * @param columnCount
	 *            the columnCount to set
	 */
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	/**
	 * @return the columnHeads
	 */
	public List<String> getColumnHeads() {
		return columnHeads;
	}

	/**
	 * @return the rowNumber
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	/**
	 * @param rowNumber
	 *            the rowNumber to set
	 */
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * @param num
	 *            the next rowNumber to write
	 */
	public void addToRowNumber(int num) {
		this.rowNumber = rowNumber + num;
	}





	public String getParam(String key) {
		if (!this.getQueryString().containsKey(key)) {
			return "";
		} else {
			return this.getQueryString().get(key);
		}

	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getQueryString() {
		Map<String, String> qs = (Map<String, String>) ExtLibUtil.resolveVariable("param");
		return qs;
	}

	// public boolean isHasColumn(ItemFields fieldname) {
	// return (null == fieldname) ? false :
	// this.getColumns().contains(fieldname);
	// }

	public void addDateCell(final PersonField key, final Person person, HSSFCell cell) {
		if (null == key) {
			return;
		}
		if (!Date.class.equals(key.getType())) {
			return;
		}

		// add the date
		final Object value = person.getValue(key);
		if (null == value) {
			// deal with a null
		}

		if (!(value instanceof Date)) {
			// Something bad happened. Passed the wring value in
		}

		// do whatever you need here

		Date tempDate = (Date) person.getValue(key);
		if (null == tempDate) {
			// cell.setCellValue(null);
			cell.setCellStyle(this.dateStyle);
		} else {
			cell.setCellValue((Date) person.getValue(key));
			cell.setCellStyle(this.dateStyle);
		}

	}
	
	

	public void addStringCell(final PersonField key, final Person person, HSSFCell cell) {
		if (null == key) {
			return;
		}
		if (!String.class.equals(key.getType())) {
			return;
		}
		
	//	System.out.println("KEY 1 : " + person.getValue(key));
		
		cell.setCellValue(helper.createRichTextString(person.getValue(key).toString()));
		
/*//		if (key.equals(ItemField.CONTENTS_ALL)) { 
//			// this IS a contents field
			
			String contents = this.filterCarriageReturns(item.getAddInfo());
			final int count = this.lineCount(contents) + 2;
			
			
			
			
		} else {
			// this is NOT a contents field.
			cell.setCellValue(helper.createRichTextString(CzarUtils.getString(item.getValue(key))));
		}*/
		
		
		
/*
		// Convert the addInfo
		if (key.equals(ItemField.CONTENTS_ALL)) {
			
			//cell.setCellValue(helper.createRichTextString(CzarUtils.getString(item.getAddInfo(true))));
			
		//	String result = this.filterCarriageReturns(item.getAddInfo());
		//	cell.setCellValue(helper.createRichTextString(result));
			
			
			String first = this.filterCarriageReturns(item.getAddInfo());
			
			
			List<String> myList = this.getContentLines(first, "\n");
			StringBuilder sb = new StringBuilder();
			
			for (String temp  : myList) {
				sb.append(temp);
				sb.append("\n");
				
			}
				sb.append("---");
				sb.append("\n");
				sb.append("---");
				sb.append("\n");
				
				
			String s=item.getAddInfo();
			char arr[]=s.toCharArray();
			for(int i=0;i<arr.length;i++){
				sb.append(i + "-(" +  this.CharToASCII(arr[i])   +")  ");
			  //  System.out.println("Data at ["+i+"]="+arr[i]);
			}

			
		//	byte[] ascii = item.getAddInfo().getBytes(); 
		//	String asciiString = Arrays.toString(ascii); 
		//	
		//	for ()
			
		//	System.out.println(asciiString); // print [74, 97, 118, 97]

		//	Read more: http://javarevisited.blogspot.com/2015/07/how-to-convert-string-or-char-to-ascii-example.html#ixzz491PPwNsa
			
			
			cell.setCellValue(helper.createRichTextString(sb.toString()));
			
			
		} else {
			
			cell.setCellValue(helper.createRichTextString(CzarUtils.getString(item.getValue(key))));
		
		}
		
		*/
		
		
	//	CzarUtils.getString(item.getValue(key));

		// value is now a string, do what you need.

	

	}

	public void addDoubleCell(final PersonField key, final Person person, HSSFCell cell) {

		if (null == key) {
			return;
		}

		// This code remarked out because it's coming back as a string for some
		// reason

		// if (!String.class.equals(key.getType())) {
		// System.out.println("String");
		// return;
		// }

		String value = person.getValue(key).toString();

		// value is now a string, do what you need.

		cell.setCellValue(new Double(value));

	}

	public void addCellContent(final PersonField key, final Person person, HSSFCell cell) {
		if (null == key) {
			return;
		}
		
		
		

		if (Date.class.equals(key.getType())) {

			this.addDateCell(key, person, cell);
		} else if (Double.class.equals(key.getType())) {

			this.addDoubleCell(key, person, cell);
		} else if (String.class.equals(key.getType())) {

			this.addStringCell(key, person, cell);
		} else {
			throw new RuntimeException("Unsupported Class: " + key.getType().getName());
		}
	}
	
	/**
	 * @param content
	 * @param cell
	 * @return
	 */
	public int addCellMultiLine(String content, HSSFCell cell) {
		
		
			int lineCount = 1;
			
			if (null == content) {
				return lineCount;
			}
			
			if (Strings.isBlankString(content)) {
				return lineCount;
			}
			
			String temp = this.filterCarriageReturns(content);
			temp = temp.trim();
			
			cell.setCellValue(helper.createRichTextString(temp));
			cell.setCellStyle(this.getContentsStyle());
			
			
			
			
			lineCount = this.getLineCount(temp);
			
			
			
			return lineCount;
		
		
	}
	

	public float computeRowHeightInPoints(int fontSizeInPoints, int numLines) {
		// From :
		// https://github.com/jbrundege/taro/blob/master/src/main/java/taro/spreadsheet/model/SpreadsheetTab.java
		// a crude approximation of what excel does
		float lineHeightInPoints = 1.3f * fontSizeInPoints;
		float rowHeightInPoints = lineHeightInPoints * numLines;
		rowHeightInPoints = Math.round(rowHeightInPoints * 4) / 4f; // round to
		// the
		// nearest
		// 0.25

		// Don't shrink rows to fit the font, only grow them
		float defaultRowHeightInPoints = sheet.getDefaultRowHeightInPoints();
		if (rowHeightInPoints < defaultRowHeightInPoints + 1) {
			rowHeightInPoints = defaultRowHeightInPoints;
		}
		return rowHeightInPoints;
	}
	

	
	private String filterCarriageReturns(final String str) {
		if (Strings.isBlankString(str)) { return ""; }
	
		
		 String result =  str.replaceAll("(\r\n|\n\r|\r|\n\n)", "\n");
		
//		 while (result.endsWith("\n")) {
//			 
//			 result = Strings.leftBack(result, "\n");
//			 
//		 }
		 
		 return result;
		
		// return str.replaceAll("\r", "\n").replaceAll("\n\n", "\n");
		
	}
	
	
	private int getLineCount(String string) {
		
		if (Strings.isBlankString(string)) { return 0; }
		int lines = string.split("\n").length;
		
		return (lines < 1) ? 1 : lines;
		
		
	}
	
	private HSSFCellStyle getContentsStyle() {
		if (null == this._contentsStyle) {
			final HSSFCellStyle style = this.getWorkbook().createCellStyle();
			style.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
			style.setWrapText(true);
			style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			// style.setFillBackgroundColor(HSSFColor.AQUA.index);
			this._contentsStyle = style;
		}

		return this._contentsStyle;

	}


	
	
	public List<String> getContentLines(Person person) {
		List<String> contentLines = new ArrayList<String>();
		String lines[] = person.getAddInfo().split("\\r?\\n");

		for (String temp : lines) {

			contentLines.add(temp);

		}

		return contentLines;
	}
	
	public List<String> getContentLines(String myString, String seperator) {
		List<String> contentLines = new ArrayList<String>();
		String lines[] = myString.split(seperator);

		for (String temp : lines) {

			contentLines.add(temp);

		}

		return contentLines;
	}

	/**
	 * Convert the characters to ASCII value
	 * @param character character
	 * @return ASCII value
	 */
	public static int CharToASCII(final char character){
		return (int)character;
	}
	
	/**
	 * Convert the ASCII value to character
	 * @param ascii ascii value
	 * @return character value
	 */
	public static char ASCIIToChar(final int ascii){
		return (char)ascii;		
	}
}
