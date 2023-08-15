package org.smartregister.fhir.structuremaptool;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPExample {

	public static void main(String[] args) {
		String file = "/home/ona-kigamba/Downloads/gzip_binary.txt";
		String gzipFile = "/home/ona-kigamba/Downloads/gzip_binary.txt.gz";
		String newFile = "/home/ona-kigamba/Downloads/new_gzip_binary.txt";

		compressGzipFile(file, gzipFile);

		decompressGzipFile(gzipFile, newFile);

	}

	private static void decompressGzipFile(String gzipFile, String newFile) {
		try {
			FileInputStream fis = new FileInputStream(gzipFile);
			GZIPInputStream gis = new GZIPInputStream(fis);
			FileOutputStream fos = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int len;
			while((len = gis.read(buffer)) != -1){
				fos.write(buffer, 0, len);
			}
			//close resources
			fos.close();
			gis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void compressGzipFile(String file, String gzipFile) {
		try {
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(gzipFile);
			GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
			byte[] buffer = new byte[1024];
			int len;
			while((len=fis.read(buffer)) != -1){
				gzipOS.write(buffer, 0, len);
			}
			//close resources
			gzipOS.close();
			fos.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
