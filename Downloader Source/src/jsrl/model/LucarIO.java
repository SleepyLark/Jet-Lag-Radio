package jsrl.model;

import java.io.*;
import java.util.zip.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jsrl.controller.Controller;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is an IO handler that I've used for other smaller projects, so most of the methods aren't
 * used or necessarily needed for this one
 * @author Skylark
 *
 */
public class LucarIO
{
	private Controller app;
	private String currentOS;
	private String startPath;
	private String recentPath;

	public enum ChooseOption
	{
		FILE, DIRECTORY;
	}

	public LucarIO(Controller app)
	{
		this.app = app;

		currentOS = System.getProperty("os.name");
		startPath = System.getProperty("user.dir");
		
		recentPath = startPath;
		//recentPath = "C:\\Users\\<USER_NAME>\\Downloads"; //Rename to home directory
	}

	private String getTime()
	{
		Calendar date = Calendar.getInstance();
		return date.get(Calendar.MONTH) + "-" + date.get(Calendar.DAY_OF_MONTH) + "-" + date.get(Calendar.YEAR);
	}

	/**
	 * Saves given String as a text file
	 * @param textToSave Text that should be written to file
	 * @param name Name of the file that should be saved
	 * @param extension The ending extension that the file should have that is written
	 * @param addDate Appends the file name to add date created (useful for preventing existing files from being overwritten).
	 */
	public void saveString(String textToSave, String name, String extension, boolean addDate)
	{
		try
		{
			String path = recentPath;
			String time = "";
			if(addDate)
				time = " "+getTime();
			File temp = new File(path + "/"+name+time+extension);
			Scanner reader = new Scanner(textToSave);
			PrintWriter output = new PrintWriter(temp);

			while (reader.hasNext())
			{
				output.println(reader.nextLine());
			}

			output.close();
			reader.close();

		}
		catch (IOException error)
		{
			//app.errorHandler(error);
		}

	}

	public void setRecentPath(String path)
	{
		if (doesExists(path))
			recentPath = path;
	}

	/**
	 * A method that calls JFileChooser to pick a path
	 * @param choice If it should show files or only directories
	 * @param message Text to be displayed on the window
	 * @return Path to given destination
	 */
	public String fileChooser(ChooseOption choice, String message)
	{
		String path = null;
		int result = -99;
		JFileChooser explore = new JFileChooser(recentPath);
		if (message != null)
			explore.setDialogTitle(message);

		if (choice == ChooseOption.FILE)
		{
			result = explore.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				path = explore.getSelectedFile().getAbsolutePath();
				recentPath = explore.getCurrentDirectory().getPath();
			}
		}
		else if (choice == ChooseOption.DIRECTORY)
		{
			explore.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			result = explore.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				path = explore.getSelectedFile().getAbsolutePath();
				recentPath = explore.getCurrentDirectory().getPath();
				System.out.println(path);
			}
		}

		return path;
	}

	public boolean doesExists(String path)
	{
		boolean exists = false;

		File temp = new File(path);

		exists = temp.exists();

		return exists;
	}

	public boolean isEmpty(File folder)
	{
		boolean empty = false;

		if (folder.isDirectory())
		{
			if (folder.list().length < 0)
			{
				empty = true;
			}
		}

		return empty;
	}

	public boolean isHidden(File filename)
	{
		boolean hidden = false;

		if (filename.getName().startsWith("."))
		{
			hidden = true;
		}

		return hidden;
	}
	
	public String listArrayList(ArrayList<File> arrayList)
	{
		String print = "";
		for(int index = 0; index < arrayList.size(); index++)
		{
			print+=arrayList.get(index)+"\n";
		}
		return print;
	}

	public String listEverything(String directory, boolean showFolder)
	{
		File folder = new File(directory);
		String print = "";

		if (folder.isDirectory())
		{
			for (File current : folder.listFiles())
			{
				if (current.isFile())
				{
					if (!(this.isHidden(current)))
					{
						print += current.getName() + "\n";
					}
				}
				else if (current.isDirectory())
				{
					if (showFolder)
					{
						print += "--- " + current.getName() + " ---\n";
					}
					if (!(this.isEmpty(current)))
					{
						print += listEverything(current.getPath(), showFolder);
					}
					else
					{
						print += "[No file found]\n";
					}
				}
			}
		}

		return print;
	}

	public ArrayList<String> listDirectory(String directory)
	{
		File folder = new File(directory);
		ArrayList<String> list = new ArrayList<String>();

		if (folder.isDirectory())
		{
			for (File current : folder.listFiles())
			{
				if (current.isFile())
				{
					list.add(current.getName());
				}
				else if (current.isDirectory())
				{
					list.add(current.getName() + File.separator);
				}
			}
		}

		if (list.isEmpty())
		{
			list.add("No files found");
		}

		return list;
	}

	public ArrayList<String> listFilesAsString(String directory)
	{
		File folder = new File(directory);
		ArrayList<String> list = new ArrayList<String>();

		if (folder.isDirectory())
		{
			for (File current : folder.listFiles())
			{
				if (current.isFile())
				{
					list.add(current.getName());
				}
			}
		}

		if (list.isEmpty())
		{
			list.add("No files found");
		}

		return list;
	}

	private ArrayList<File> getListOfFiles(String directory, boolean recursiveSearch)
	{
		File folder = new File(directory);
		ArrayList<File> list = new ArrayList<File>();

		
		if (folder.isDirectory())
		{
			for (File current : folder.listFiles())
			{
				if (current.isFile())
				{
					list.add(current);
				}
				if (current.isDirectory() && recursiveSearch)
				{
					list.addAll(getListOfFiles(current.getPath(), recursiveSearch));
				}
			}
		}
		return list;
	}
	

	public ArrayList<String> getListOfFolders(String directory)
	{
		File folder = new File(directory);
		ArrayList<String> list = new ArrayList<String>();

		if (folder.isDirectory())
		{
			for (File current : folder.listFiles())
			{
				if (current.isDirectory())
				{
					list.add(current.getName() + File.separator);
				}
			}
		}

		if (list.isEmpty())
		{
			list.add("No folders found");
		}

		return list;
	}

	public String getReadableName(String path)
	{
		int startIndex = 0;
		int endIndex = path.lastIndexOf(".");
		if (path.contains(File.separator))
		{
			startIndex = path.lastIndexOf(File.separator) + 1;
		}
		if (endIndex < 0)
		{
			endIndex = path.length();
		}

		return path.substring(startIndex, endIndex);

	}
	
	public String getReadableName(File temp)
	{
		return getReadableName(temp.getPath());
	}
	
	
	public String getSize(String path)
	{
		File temp = new File(path);
		return getSize(temp);
	}
	
	public String getSize(File fileToCheck)
	{
		
		long filesize = fileToCheck.length();
		String size = "";
		
		if(filesize >= 1024)
		{
			if(filesize >= 1048576)
			{
				if(filesize >= 1073741824)
				{
					size = toGigaBytes(filesize) + " GB";
				}
				else
				{
					size = toMegaBytes(filesize) + " MB";
				}
			}
			else
			{
				size = toKiloBytes(filesize) + " KB";
			}
		}
		else
		{
			size = filesize + " bytes";
		}
		
		return size;
	}
	
	public long toKiloBytes(long fileBytes)
	{
		return fileBytes / 1024;
	}
	
	public long toMegaBytes(long fileBytes)
	{
	
		return toKiloBytes(fileBytes)/ 1024;
	}
	
	public long toGigaBytes(long fileBytes)
	{
		return toMegaBytes(fileBytes)/1024;
	}
	

	
	public String removeExtension(String path, String extension)
	{
		File temp = new File(path);

		String list = "";
		try
		{
			Scanner read = new Scanner(temp);
			while (read.hasNext())
			{
				String current = read.nextLine();
				if (current.contains(extension))
				{
					current = current.substring(0, current.indexOf(extension));
				}
				list += current + "\n";
			}
			read.close();
		}
		catch (IOException error)
		{

		}

		return list;
	}



	public String getExtension(String path)
	{
		int startIndex = path.lastIndexOf(".");
		String end = "No Extension";
		if (startIndex > 0)
		{
			end = path.substring(startIndex);
		}

		return end;
	}

	public long getLastModified(File currentFile)
	{
		return currentFile.lastModified();

	}

	public long getLastModified(String path)
	{
		long time = -99;
		if (doesExists(path))
		{
			time = getLastModified(new File(path));
		}

		return time;
	}
}
