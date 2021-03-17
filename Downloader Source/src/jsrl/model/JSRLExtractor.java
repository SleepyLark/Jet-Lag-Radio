package jsrl.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import jsrl.controller.Controller;
import jsrl.model.LucarIO.ChooseOption;

public class JSRLExtractor
{

	private Controller app;
	private String html;
	private String stations;
	private String[][] stationsArray;
	private LucarIO io;
	

	public JSRLExtractor(Controller app)
	{
		this.app = app;

		io = new LucarIO(app);

		html = "";
	}

	public void getHTMLfromFile()
	{
		try
		{
			File htmlFile = new File(io.fileChooser(ChooseOption.FILE, "Choose HTML"));
			Scanner reader = new Scanner(htmlFile);
			Scanner loader = new Scanner(htmlFile);
			int maxLineSize = 0;
			int counter = 0;

			app.debug("Loading file...");
			while (loader.hasNext())
			{
				maxLineSize++;
				loader.nextLine();
			}

			while (reader.hasNext())
			{
				counter++;
				html += reader.nextLine() + "\n";
				app.debug(((Math.round(((double) counter) / (maxLineSize) * 100)) + "%"));
			}

			loader.close();
			reader.close();
			app.debug("Done!");
		}
		catch (Exception e)
		{
			app.errorManager(e);
		}

		if (!(findTag("!DOCTYPE", true) || findTag("html", true)))
			app.errorManager("This is not an HTML file");

	}

	/**
	 * Copies HTML source code from URL and saves it as a String (probably not the best way to read it since said String may be really big in memory)
	 * @param urlLink
	 */
	public void getHTMLfromURL(String urlLink)
	{
		try
		{
			html = "";
			URL link = new URL(urlLink);
			BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream()));

			app.debug("Extracting HTML...");
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				html += inputLine + "\n";
			}

			in.close();
			app.debug("Done!");
		}
		catch (IOException e)
		{
			app.errorManager("Unable to open link.  Make sure the site is still online or that you have internet connection");
		}
	}

	public boolean findTag(String tag, boolean noEndTag)
	{
		return !(getTag(tag, noEndTag, html) == null);
	}

	public String getTag(String tag, boolean noEndTag)
	{
		return getTag(tag, noEndTag, html);
	}

	/**
	 * Helper methods, basically a modified version of String.contains() and String.substring().  
	 * Looks for the given HTML tag and returns the section of code either inbetween or after said tag
	 * @param tag
	 * @param noEndTag
	 * @param block
	 * @return
	 */
	private String getTag(String tag, boolean noEndTag, String block)
	{
		app.debug("-------FINDTAG START!------");
		String codeBlock = null;
		tag = "<" + tag.trim();
		String endTag = "</" + tag.substring(1) + ">";
		if (noEndTag)
			endTag = ">";

		Scanner reader = new Scanner(block);
		String currentLine = "";

		while (reader.hasNext())
		{
			currentLine = reader.nextLine();

			if (currentLine.contains(tag))
			{

				currentLine = currentLine.substring(currentLine.indexOf(tag));
				codeBlock += currentLine + "\n";
			}
		}
		reader.close();

		return codeBlock;
	}

	public String findQuoteParameter(String block, String keyword)
	{
		String result = "";
		if (block.contains(keyword))
		{
			result = block.substring(block.indexOf(keyword) + keyword.length() + 1, block.indexOf("\"></script>"));
		}
		app.debug(result);
		return result;
	}
	
	/**
	 * The method that actually scraps the stations information from the HTML code.
	 */
	public void getStations()
	{
		//All station's songs are saved in the same format as a "~list.js" file
		String firstChunk = html.substring(html.indexOf("<script src=\"radio/stations/bumps/~list.js\"></script>"));
		stations = firstChunk.substring(0,firstChunk.indexOf("<!--===================================-->"));
		String list = "";
		int maxStations = 0;
		
		//Scrap the HTML section that contains the stations and save each instance of "~list.js" as a separate String to use later for reference
		Scanner read = new Scanner(stations);
		while (read.hasNextLine())
		{
			String current = read.nextLine();
			if(!current.isEmpty())
			{
				list += findQuoteParameter(current, "<script src=")+"\n";
				maxStations++;
			}
		}
		
		
		stations = list;
		app.debug(stations);
		
		//Read String that contains listing of station's "~list.js" and save them into an array after modifying the reference link
		read = new Scanner(stations);
		stationsArray = new String [maxStations][3];// Creates a 2D array with each row for every station listed 
													// and the columns being: Name, Associated URL Link, and a Reference link to "~link.js"
		
		for (int row = 0; row < stationsArray.length; row++)
		{
			String listJS = "https://jetsetradio.live/"+ read.nextLine();
			String name = listJS.substring("https://jetsetradio.live/radio/stations/".length(),listJS.lastIndexOf("/"));
			String link = listJS.substring(0,listJS.indexOf(name))+name+"/";
			stationsArray[row][0] = name;
			stationsArray[row][1] =link;
			stationsArray[row][2] =  listJS;
			app.debug(stationsArray[row][1]);
			
		}
		
		ArrayList<String> playlist = new ArrayList<String>();
		io.fileChooser(ChooseOption.DIRECTORY, "Pick a directory to save all files to."); //Pick path to save all files to
		//Loop through every station...
		for(int index = 0; index < stationsArray.length; index++)
		{
			
			//Get the filenames from the current station's "~list.js"
			playlist = getPlaylist(stationsArray[index][2]);
			//Header String for each file
			String csv = stationsArray[index][0] +",\n";
			String nameCSV = "artist,title,\n";
			String nameTxt = "";
			String m3u = "#EXTM3U\n";
			
			//For each filename found...
			for(int current = 0; current < playlist.size(); current++)
			{
				String songLink = playlist.get(current);
				try 
				{
					//Encode filename so it can be properly opened in a browser, replaces "+" with "%20" because it doesn't read spaces without it being encoded
					songLink = URLEncoder.encode(songLink, StandardCharsets.UTF_8.name()).replace("+","%20");
				}
				catch (UnsupportedEncodingException ex)
				{
					//idk if this will actually happen, but the method requires a catch condition regardless.
					app.errorManager("Unable to encode "+songLink+".  Try loading it in a web browser or manually encoding it");
				}
				
				
				csv += "\""+stationsArray[index][1]+songLink+".mp3\",\n"; //Example: "https://jetsetradio.live/radio/stations/classic/B.B.%20Rights%20-%20Funky%20Radio.mp3"
				m3u += "#EXTINF:0,"+playlist.get(current)+"\n" + 
						"#EXTVLCOPT:network-caching=1000\n" + //No clue how important this is, was on VLC when I tried testing converting songs
						stationsArray[index][1]+songLink+".mp3\n";
				
				//The filename for the songs are usually formated as "Artist - Song Title", this extracts that information and stores each part as a csv cell
				nameCSV += "\""+playlist.get(current).replace(" - ", ",")+"\",\n";
				nameTxt += playlist.get(current)+"\n";
						
				
			}
			
			//Call IO to write each String as their own file
			io.saveString(csv,index+" - "+ stationsArray[index][0], ".csv", false);
			io.saveString(m3u, index +" - " + stationsArray[index][0], ".m3u", false);
			io.saveString(nameCSV, index+" - "+stationsArray[index][0], "-playlist.csv",false);
			io.saveString(nameTxt, index+" - "+stationsArray[index][0], "-playlist.txt",false);
			
		}
		
		
	}
	
	/**
	 * Extracts and converts the filename through the given "~list.js" link
	 * @param list Direct link to the station's "~list.js" file
	 * @return an ArrayList of String that has the original filename listed in the array
	 */
	private ArrayList<String> getPlaylist(String list)
	{
		ArrayList<String> playlist = new ArrayList<String>();
		
		this.getHTMLfromURL(list); //dump source code into String
		String songs = "";
		boolean isBumps = false;
		
		if(list.contains("bumps")) //bumps use a different format for its array
		{
			 songs = html.substring(html.indexOf("bumpsArray[bumpsArray.length] = \""));
			 isBumps = true;
		}
		else
		{
		 songs = html.substring(html.indexOf("this[stationName+'_tracks'][this[stationName+'_tracks'].length] = \""));
		}
		
		Scanner read = new Scanner(songs);
		
		//Go through the each line in the String and remove unneeded parts so only the filename is left
		//Afterwards, add filename of song to ArrayList
		while (read.hasNextLine())
		{
			String current = read.nextLine();
			if(!current.isEmpty())
			{
				if(isBumps)
					playlist.add(current.replace("bumpsArray[bumpsArray.length] = \"","").replace("\";",""));
				else
				playlist.add(current.replace("this[stationName+'_tracks'][this[stationName+'_tracks'].length] = \"","").replace("\";",""));
			}
		}
		
		app.debug(playlist.toString());
		
		read.close();
		
		return playlist;
	}
}
