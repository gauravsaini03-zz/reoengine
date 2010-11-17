package uRen;

import java.beans.XMLEncoder;
import java.io.BufferedWriter;
import java.io.FileWriter;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class XMLSerializer {

	public String Serialize(Object obj, String fileName) {
		// Create output stream.
		ByteOutputStream bos = new ByteOutputStream();
		//FileOutputStream fos = new FileOutputStream("c:\\temp\\foo.xml");
		// Create XML encoder.
		XMLEncoder xenc = new XMLEncoder(bos);
		// Write object.
		xenc.writeObject(obj);
		xenc.flush();
		String xmlOut = bos.toString();
		Logger.Log(xmlOut);
		
		if ( fileName != "" && fileName.length() > 0) {
			try {
				BufferedWriter fout = new BufferedWriter(new FileWriter(fileName));
				fout.write(xmlOut);
				fout.close();
			}
			catch (Exception ex) {
				
			}
		}
		
		return xmlOut;
	}	
}
