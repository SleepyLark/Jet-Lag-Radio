package jsrl.controller;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import jsrl.model.LucarIO;
import jsrl.model.JSRLExtractor;



public class Controller
{
	private JSRLExtractor reader;
	private LucarIO io;
	private Scanner scan;
	private boolean debug;

	public Controller()
	{
		reader = new JSRLExtractor(this);
		io = new LucarIO(this);
		//ui = new AppFrame(this);
		
		debug = true;
		
	}

	public void start()
	{
		reader.getHTMLfromURL("https://jetsetradio.live/");
		reader.getStations();
	}
	
	public void sendURL(String url)
	{
		
	}
	public void errorManager(Exception problem)
	{
		JOptionPane.showMessageDialog(null, problem.getMessage(), "[ERROR]: " + problem.getCause().getMessage(), JOptionPane.ERROR_MESSAGE);
	}

	public void errorManager(String message)
	{
		JOptionPane.showMessageDialog(null, message, "[ERROR] ", JOptionPane.ERROR_MESSAGE);
	}
	
	
	public void print(String word)
	{
		System.out.println(word);
	}

	/**
	 * print something to console for quick sanity checks
	 * @param word
	 */
	public void debug(String word)
	{
		if(debug)
		System.out.println(word);
	}
}
