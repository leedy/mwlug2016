package com.notesIn9.util;



import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.openntf.domino.utils.Factory;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.EmbeddedObject;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;
import lotus.domino.View;
//import org.openntf.domino.utils.Factory;
//import org.openntf.domino.utils.Factory.SessionType;


import com.google.common.io.Files;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoUtils;

import javaxt.io.Image;


public class FileImporter implements Serializable {


	private static final long serialVersionUID = 1L;
	
	
	private String fullType;
	private String smallType;
	private String fileName;
	private String externalKey;
	private String fileKey;
	private long currentSize;
	private String filePath;
	
	
	public FileImporter() throws NotesException {
		
		this.currentSize = 0;
		
		
		this.filePath = this.getSession(true).getCurrentDatabase().getFilePath();
		
	}
	
	
	public void processUploads(String token)  throws NotesException {
		
		System.out.println("FileVault processing : " + token);
		
		Session session = this.getSession(true);
		session.setConvertMime(false);
		Database db = session.getCurrentDatabase();
		View view = db.getView("Files");
		DocumentCollection collect = view.getAllDocumentsByKey(token, true);
		
		List<String> fileList = null;
		
		
		String[] inputFormats = Image.InputFormats;
		List<String> formats = Arrays.asList(inputFormats);
		
		
		for (String x : formats) {
			System.out.println(x);
		}
		
	
		String fullType ="image/jpeg";
		String[] typeArray = fullType.split("/");
	
		String smallType = typeArray[1].toUpperCase();
		System.out.println("TYPE : " + smallType );
	
		if ("a".equalsIgnoreCase("a")) {
			return;
		}
		
		Document doc = collect.getFirstDocument();
		
		
		Document tmpDoc = null;
		
		while (doc != null) {
			System.out.println("Processing Loop");
			tmpDoc = collect.getNextDocument(doc);
			
			// Get the actual MIME Type
			this.fullType = doc.getItemValueString("type");
			
			// Get the fileName
			this.fileName = doc.getItemValueString("fileName");
//			System.out.println("FileName : " + this.fileName);
			
			this.smallType = Files.getFileExtension(this.fileName).toUpperCase();
//			System.out.println("Small Type : " + this.smallType);
			
			//if (formats.contains(this.smallType)) {
			if (this.smallType.equalsIgnoreCase("JPG")  || this.smallType.equalsIgnoreCase("jpeg")) {
				// This is an Image
//				System.out.println("Document has an Image");
				this.processImage(doc);
				
			
				doc.remove(true);
				
			} else {
				// This is NOT an image
				System.out.println("Document does NOT have an Image");
				
				this.processFile(doc, token);
				
				doc.remove(true);
				
			}
			
			this.smallType = "";
			this.fileName = "";
			this.fullType = "";
			this.currentSize = 0;
			
			System.out.println("FileVault END PROCESSING : " + token);
			doc.recycle();
			doc = tmpDoc;
			
		}
		
	}
	
	private void processImage(Document doc)  throws NotesException {
		
	//	List<String> fileList = null;
	//	fileList = this.getFileNames(doc, "attachment");
	//	String fileName = fileList.get(0);
		
		
		EmbeddedObject eoImage = doc.getAttachment(this.fileName);
//		System.out.println("IMAGE FileSize : " + eoImage.getFileSize());
//		System.out.println("IMAGE FileName : " + eoImage.getName());
		
		this.currentSize =  eoImage.getFileSize();
		
		Image image = new Image(eoImage.getInputStream());
//		System.out.println("Height : " + image.getHeight());
		
		Session session = this.getSession(false);
		// String imageKey = session.getUnique();
		this.fileKey = String.valueOf(session.evaluate("@Unique").get(0));
		String externalKey = doc.getItemValueString("uploadToken");
		
		
		// Rotate the Image
		// But calling rotate() and NOT adding a value it will rotate based on the EXIF data
		this.printExif(image);
	//	image.rotate();
	//	this.printExif(image);
		
		
		// Process ORIGINAL Image
		Document newDoc = this.getNewImageDocument(fileKey, externalKey, this.filePath);
		
		
		this.attachImage(newDoc, image.getBufferedImage(), fileName, this.smallType);
		newDoc.save();
		
		// Large = Fixed Height of 600px
		// Small = Fixed Height of 240px
		
//		System.out.println("PROCESSING LARGE IMAGE");
		image.setHeight(600);
		newDoc = this.getNewImageDocument(fileKey, externalKey, this.filePath);
		this.attachImage(newDoc, image.getBufferedImage(), fileName, this.smallType);
		newDoc.save();
		
	
//		System.out.println("PROCESSING SMALL IMAGE");
		image.setHeight(240);
		newDoc = this.getNewImageDocument(fileKey, externalKey, this.filePath);
		this.attachImage(newDoc, image.getBufferedImage(), fileName, this.smallType);
		newDoc.save();
		
//		System.out.println("PROCESSING META Document");
		this.createImageMetaDocument(fileName, fileKey, externalKey, "Catalog", doc);
		
		
	}
	
	
	
	
	
	
	
	
	private void attachImage(Document imageDoc, BufferedImage image, String fileName, String fileExtension)  throws NotesException {
		// Thanks to:  http://www.keithstric.com/A55BAC/keithstric.nsf/default.xsp?documentId=B69A5A4E460E3D4185257A360051B9B8


		
//		System.out.println("BufferedImage Height : " + image.getHeight());
//		System.out.println("BufferedImage Width : " + image.getWidth() );
		
		
		
		InputStream is = null;
		Stream stream = null;
		// System.out.println("attaching Image");
		
		try {
			
			
			MIMEEntity mime = imageDoc.createMIMEEntity("attachment"); // This is the NotesFieldName
			MIMEHeader header = mime.createHeader("Content-Disposition");
			header.setHeaderVal("attachment; filename=\"" + fileName + "\"");
			header = mime.createHeader("Content-ID");
			header.setHeaderVal(fileName);
			
			byte[] imgByteArray = getImgByteArray(image, fileExtension);
			is = new ByteArrayInputStream(imgByteArray);
			
			
			//  Stream stream = NotesContext.getCurrent().getCurrentSession().createStream();
			Session session = this.getSession(false);
			session.setConvertMime(false);
				
			stream = session.createStream();

			stream.setContents(is);
			
//			System.out.println("STREAM BYTES : " + stream.getBytes());
			
			mime.setContentFromBytes(stream, "image/" + fileExtension, mime.ENC_IDENTITY_BINARY);
			
			
		} catch (Exception e) {
			
			System.out.println("***");
			System.out.println("3catch. : Problem!!");
			System.out.println("***");
			
			e.printStackTrace();
		} finally {
			
			// Added by Dave
			imageDoc.closeMIMEEntities(); 
			
			if (null != stream) {
				stream.close(); // closing Stream
			}
			
		
			
			if (null != is) {
				try {
					is.close();
//					System.out.println("***");
//					System.out.println("3g. : is close");
//					System.out.println("***");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("***");
					System.out.println("3h. : is exception");
					System.out.println("***");
					e.printStackTrace();
				}
			}
			// End Dave Added code
			
			
		}
	}

	private static byte[] getImgByteArray(BufferedImage buffImg, String imageExtension) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buffImg, imageExtension, baos);
			byte[] imgByteArray = baos.toByteArray();
			return imgByteArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Pass in true to get an elevated (sessionAsSigner) session or false for a normal user session.
	 * 
	 * Note: This method does NOT change the state of the object.  It ignores the "elevated" setting.
	 * 
	 * @param force
	 * 			true for elevated session
	 * @return
	 * 			NotesSession
	 */
	protected Session getSession(boolean force) {
		
/*		if (force) {
			// return XSPUtil.getCurrentSessionAsSigner();
			return Factory.getSession(SessionType.SIGNER);
		} else {
			return Factory.getSession(SessionType.CURRENT);
		}*/
		
		
		return  ExtLibUtil.getCurrentSessionAsSigner();
		
	}
	
	private List<String> getFileNames(final Document document, final String itemname) {
		try {
			
			
			if (null == document) { throw new IllegalArgumentException("Document is null"); }
			if (null == itemname) { throw new IllegalArgumentException("Item Name is null"); }

			if (document.hasItem(itemname)) {
				
				final lotus.domino.Item item = document.getFirstItem(itemname);
				
				if ((null != item) && (item.getType() == lotus.domino.Item.RICHTEXT)) {
					final RichTextItem rti = (RichTextItem) document.getFirstItem(itemname);
					final Vector<EmbeddedObject> objects = rti.getEmbeddedObjects();
					if ((null != objects) && !objects.isEmpty()) {
						final List<String> result = new ArrayList<String>();
						for (final EmbeddedObject eo : objects) {
							if (eo.getType() == lotus.domino.EmbeddedObject.EMBED_ATTACHMENT) {
								result.add(eo.getName());
							}
						}
				
						return result;
					}
				}
			}

		} catch (final Exception e) {
			// don't do anything for now
		}

		return null;
	} 
	
//	private void getLocations() {
//		filePath = new HashMap<Location, String>();
//		
//		filePath.put(Location.Info, "fileVault\\fileVault-Info.nsf");
//		filePath.put(Location.Small, "fileVault\\fileVault-Small.nsf");
//		filePath.put(Location.Large, "fileVault\\fileVault-Large.nsf");
//		filePath.put(Location.Original, "fileVault\\fileVault-Original.nsf");
//		filePath.put(Location.File,"fileVault\\fileVault-File.nsf");
//	}
	
private Document getNewImageDocument(String fileKey, String externalKey, String dbPath) throws NotesException {
		
		
		Session session = this.getSession(false);
		session.setConvertMime(false);
		Database db = session.getCurrentDatabase();
		Database newDB = session.getDatabase(db.getServer(), dbPath);
		Document newDoc = newDB.createDocument();
		newDoc.replaceItemValue("form", "file");
		newDoc.replaceItemValue("externalKey", externalKey);
		newDoc.replaceItemValue("fileKey", fileKey);
		
		return newDoc;
	}

	
	
	public void createImageMetaDocument( String picName,String picKey, String externalKey, String imageType, Document doc) throws NotesException {
		
		Session session = this.getSession(false);
		Database db = session.getCurrentDatabase();
		Database infoDB = session.getDatabase(db.getServer(), this.filePath);
		Document infoDoc = infoDB.createDocument();
		
		
		Document newDoc = infoDB.createDocument();
		newDoc.replaceItemValue("form", "fileInfo");
		newDoc.replaceItemValue("externalKey", externalKey);
		newDoc.replaceItemValue("fileKey", picKey);
		newDoc.replaceItemValue("category", imageType);
		newDoc.replaceItemValue("fileName", picName);
		newDoc.replaceItemValue("fileType", doc.getItemValueString("type"));
//		newDoc.replaceItemValue("pathOriginal", filePath.get(this.filePath);
//		newDoc.replaceItemValue("pathLarge", filePath.get(this.filePath);
//		newDoc.replaceItemValue("pathSmall",filePath.get(Location.Small));
		// Set attachment type to I for "Image"
		newDoc.replaceItemValue("attachmentType", "I");
		
		
		
		// System.out.println("FILE TYPE : " + doc.getItemValueString("type"));
		if ("".equals(doc.getItemValueString("type"))) {
			
			newDoc.replaceItemValue("fileType", "image/jpeg");
		} else {
			newDoc.replaceItemValue("fileType", doc.getItemValueString("type"));
		}
		
		if (doc.hasItem("size")) {
			newDoc.replaceItemValue("fileSize", doc.getItemValue("size"));
		} else {
			newDoc.replaceItemValue("fileSize", this.currentSize);
		}
		
		
	
		newDoc.save();
		
	}
	
	public boolean processFile(Document doc, String externalKey) throws NotesException {
		
		boolean result;
	
	
		Session session = this.getSession(false);
		String fileKey = this.getUnique();
		session.setConvertMime(false);
		Database db = session.getCurrentDatabase();
		// Process Info
		
		Database infoDB = session.getDatabase(db.getServer(), this.filePath);
		Document infoDoc = infoDB.createDocument();
		infoDoc.replaceItemValue("form", "fileInfo");
		infoDoc.replaceItemValue("externalKey", externalKey);
		infoDoc.replaceItemValue("fileKey", fileKey);
		
		String fileName;
		String fileType;
		
		fileName = doc.getItemValueString("fileName");
		infoDoc.replaceItemValue("fileName", fileName);
		
		fileType = fileName.substring(fileName.length() - 3);
		infoDoc.replaceItemValue("type", this.smallType);
		
		infoDoc.replaceItemValue("fileType", doc.getItemValueString("type"));
		infoDoc.replaceItemValue("fileSize", doc.getItemValue("size"));
		infoDoc.replaceItemValue("category", "File");
		infoDoc.replaceItemValue("storagePath", this.filePath);
		
		
		result = infoDoc.save();
		if (!result) {
			// the save failed for some reason
			return result;
		}
		
		// Process Attachment
		Database fileDB = session.getDatabase(db.getServer(), this.filePath);
		Document fileDoc = fileDB.createDocument();
		fileDoc.replaceItemValue("form", "file");
		fileDoc.replaceItemValue("externalKey", externalKey);
		fileDoc.replaceItemValue("fileKey", fileKey);
		
		// System.out.println("FILE NAME : " + doc.getItemValueString("fileName"));
		
		// Extract Attachment and Add To Attachment Document
		
		
		List<EmbeddedObject> docEO = this.getFileAttachments(doc, "attachment");
		
		for (EmbeddedObject eoPDF : docEO) {
			// Extract Attachment and Add To Attachment Document
			InputStream attachInputStream = eoPDF.getInputStream();
			Stream attachStream = session.createStream();
			attachStream.setContents(attachInputStream);
			
			MIMEEntity attachField = fileDoc.createMIMEEntity("attachment");
			MIMEHeader attachHeader = attachField.createHeader("content-disposition");
			attachHeader.setHeaderVal("attachment;filename=\"" + eoPDF.getName() + "\"");
			attachField.setContentFromBytes(attachStream, this.smallType , MIMEEntity.ENC_IDENTITY_BINARY);
			
		}
		
//		var uploadField:NotesRichTextItem = uploadDoc.createRichTextItem("attachment");
//		uploadField.embedObject(1454, "", correctedFile.getAbsolutePath(), null);
//		uploadDoc.save(true);
		
		
		
		
	//	RichTextItem source = doc.getFirstItem("attachment");
	//	source.copyItemToDocument(fileDoc);
		
	//	RichTextItem test = fileDoc.createRichTextItem("attachment");
	
		
		
	//	RichTextItem rtItem = (RichTextItem) doc.getFirstItem("attachment");
		
	//	rtItem.copyItemToDocument(fileDoc);
		
		
		
		return fileDoc.save();
	}

	private String getUnique() throws NotesException {
		
		Session session = this.getSession(false);
		return String.valueOf(session.evaluate("@Unique").get(0));
	}
	
	public List<EmbeddedObject> getFileAttachments(final Document document, final String itemname) {
		try {
			
			
			if (null == document) { throw new IllegalArgumentException("Document is null"); }
			if (null == itemname) { throw new IllegalArgumentException("Item Name is null"); }

			if (document.hasItem(itemname)) {
				
				final lotus.domino.Item item = document.getFirstItem(itemname);
				
				if ((null != item) && (item.getType() == lotus.domino.Item.RICHTEXT)) {
					final RichTextItem rti = (RichTextItem) document.getFirstItem(itemname);
					final Vector<EmbeddedObject> objects = rti.getEmbeddedObjects();
					if ((null != objects) && !objects.isEmpty()) {
						final List<EmbeddedObject> result = new ArrayList<EmbeddedObject>();
						for (final EmbeddedObject eo : objects) {
							if (eo.getType() == lotus.domino.EmbeddedObject.EMBED_ATTACHMENT) {
								result.add(eo);
							}
						}
				
						return result;
					}
				}
			}

		} catch (final Exception e) {
			// don't do anything for now
		}

		return null;
	} 
	
	
	private void printExif(Image image) {
		 java.util.HashMap<Integer, Object> exif = image.getExifTags();
		   
		//Print Camera Info
//		  System.out.println("EXIF Fields: " + exif.size());
//		  System.out.println("-----------------------------");
//		  System.out.println("Date: " + exif.get(0x0132)); //0x9003       
//		  System.out.println("Camera: " + exif.get(0x0110));
//		  System.out.println("Manufacturer: " + exif.get(0x010F));
//		  System.out.println("Focal Length: " + exif.get(0x920A));
//		  System.out.println("F-Stop: " + exif.get(0x829D));
//		  System.out.println("Exposure Time (1 / Shutter Speed): " + exif.get(0x829A));
//		  System.out.println("ISO Speed Ratings: " + exif.get(0x8827));
//		  System.out.println("Shutter Speed Value (APEX): " + exif.get(0x9201));
//		  System.out.println("Shutter Speed (Exposure Time): " + exif.get(0x9201));
//		  System.out.println("Aperture Value (APEX): " + exif.get(0x9202));
//		  System.out.println("Color Space: " + exif.get(0xA001));
		   
		//Print Image Orientation
		  try{
		      int orientation = (Integer) exif.get(0x0112);
		      String desc = "";
		      switch (orientation) {
		          case 1: desc = "Top, left side (Horizontal / normal)"; break;
		          case 2: desc = "Top, right side (Mirror horizontal)"; break;
		          case 3: desc = "Bottom, right side (Rotate 180)"; break;
		          case 4: desc = "Bottom, left side (Mirror vertical)"; break;
		          case 5: desc = "Left side, top (Mirror horizontal and rotate 270 CW)"; break;
		          case 6: desc = "Right side, top (Rotate 90 CW)"; break;
		          case 7: desc = "Right side, bottom (Mirror horizontal and rotate 90 CW)"; break;
		          case 8: desc = "Left side, bottom (Rotate 270 CW)"; break;
		      }
		      System.out.println("Orientation: " + orientation + " -- " + desc);
		  }
		  catch(Exception e){
		  }
		   
		 
		//Print GPS Information
		  double[] coord = image.getGPSCoordinate();
		  if (coord!=null){
		      System.out.println("GPS Coordinate: " + coord[0] + ", " + coord[1]);
		      System.out.println("GPS Datum: " + image.getGPSDatum());
		  }
	}
	
}
