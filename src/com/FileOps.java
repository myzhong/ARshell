package com;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileOps {
	
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("hhmmssSSS");
		return sdf.format(new Date());
	}
	
	public void newFileCheck(String path){
		File file = new File(path);
		try {
			if (file.exists()) {
				file.delete();
			}
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	public void wirteFile(String content, String path){
		FileOutputStream f = null;
		try {
            f = new FileOutputStream(path, true);
            f.write(content.getBytes());
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
