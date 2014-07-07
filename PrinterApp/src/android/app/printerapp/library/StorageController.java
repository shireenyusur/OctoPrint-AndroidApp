package android.app.printerapp.library;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import android.app.printerapp.model.ModelFile;
import android.os.Environment;
import android.util.Log;


/**
 * This class will handle the file storage architecture and retrieval in a static way
 * so every class can access these methods from wherever
 * @author alberto-baeza
 *
 */
public class StorageController {
	
	private static ArrayList<File> mFileList = new ArrayList<File>();
	private static String mCurrentPath;
	
	public StorageController(){
		
		
		//Retrieve normal files
		retrieveFiles( getParentFolder());
		
		//Retrieve trash files
		
	
	}
	
	public static ArrayList<File> getFileList(){
		return mFileList;
	}
	
	public static void retrieveFiles(File path){
		
		/**
		 * CHANGED FILE LOGIC, NOW RETRIEVES FOLDERS INSTEAD OF FILES, AND PARSES
		 * INDIVIDUAL ELEMENTS LATER ON.
		 */
		File[] files = path.listFiles();
		for (File file : files){
			
			if (file.isDirectory()){	
				
				if (isProject(file)){
					
					
					Log.i("OUT","It's also a project " + file.getAbsolutePath());
					
					ModelFile m = new ModelFile(file.getAbsolutePath(), "Internal storage");
					
					//TODO: Move this to the ModelFile code
					m.setPathStl(retrieveFile(file.getAbsolutePath(), "_stl"));	
					m.setPathGcode(retrieveFile(file.getAbsolutePath(), "_gcode"));	
					m.setSnapshot(file.getAbsolutePath() + "/" + m.getName() + ".jpg");
					addToList(m);
					
				} else {
					
					//File folder = new File(file.getAbsolutePath());
					addToList(file);
					
					//Retrieve files for the folder
					//retrieveFiles(new File(file.getAbsolutePath()));
				}
				
				//TODO this will eventually go out
			} else {
				
				
				//ModelFile m = new ModelFile(file.getName(), "Trash storage");
				
				//m.setPathStl(file.getAbsolutePath());	
				//m.setPathGcode(file.getAbsolutePath());	
				addToList(file);
				
			}

		}
		
		
		mCurrentPath = path.toString();
	}
	
		
	//Retrieve main folder or create if doesn't exist
	//TODO: Changed main folder to FILES folder.
	public static File getParentFolder(){
		String parentFolder = Environment.getExternalStorageDirectory().toString();
		File mainFolder = new File(parentFolder + "/PrintManager");
		mainFolder.mkdirs();
		File temp_file = new File(mainFolder.toString());

		return temp_file;
	}
	
	
	//Retrieve certain element from file storage
	public static String retrieveFile(String name, String type){
		
		String result = null;
		
		try {
			File folder = new File(name + "/" + type + "/");
			Log.i("OUT","It's gonna crash searching for " + folder.getAbsolutePath());
			String file = folder.listFiles()[0].getName();
			
			result = folder + "/" + file; 
		
		} catch (ArrayIndexOutOfBoundsException e){
			
		}
		
		
		return result;	
		
	}
	
	public static void reloadFiles(String path){
		
		mFileList.clear();
		retrieveFiles(new File(path));
	}
	
	public static boolean isProject(File file){
		
		FilenameFilter f = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				
				return filename.endsWith("jpg");
			}
		};
		
		if (file.list(f).length > 0) return true;
		else return false;
	}
	
	public static void addToList(File m ){
		mFileList.add(m);
	}

	public static String getCurrentPath(){
		return mCurrentPath;
	}
}
