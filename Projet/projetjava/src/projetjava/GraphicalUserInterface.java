package projetjava;

import javax.swing.*;


import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.util.Collections;
import java.util.Vector;


public class GraphicalUserInterface extends JFrame implements ActionListener 
{
	///user command for IP and period (rate) and button to confirm
	private JTextField period;
	private JTextField IP;
	private JTextField nbReadings;
	private JButton sendData;
	private String tmpIP;
	private String tempT;
	private Client theApp;
	private String tmpNbReadings;

	
	public GraphicalUserInterface()
	{
		//Application name 
		this.setTitle("GPU Temperatures from server");
		//Window application size
		this.setSize(1400, 800);
		//Block size of window
		this.setResizable(false);
		
		//JPanel is a group of graphics elements
		JPanel commandWindow = new JPanel();
		
		sendData = new JButton ("Confirm IP Address, the sampling rate, and number of readings");
		
		//Commands for IP Address and gap between value taken and number of readings
		IP = new JTextField (20);
		commandWindow.add(IP);
		
		period = new JTextField (15);
		commandWindow.add(period);
		
		nbReadings = new JTextField (3);
		commandWindow.add(nbReadings);
		
		commandWindow.add(sendData);

		this.add(commandWindow, BorderLayout.NORTH);
		

		sendData.addActionListener(this);

		
		this.setVisible(true);
		
	}
	
	
	public void actionPerformed (ActionEvent e)
	{
		
		if(e.getActionCommand().equals("Confirm IP Address, the sampling rate, and number of readings"))
		{
			this.tmpIP = this.IP.getText();
			//we can't set a IP again if we confirm
			this.IP.setEnabled(false);
			
			this.tempT = this.period.getText();

			
			this.tmpNbReadings = this.nbReadings.getText();

			
			//repaint when the values of the period (rate) and IP Address is taken
			//values send by the user is took and used in paint program
			this.repaint();
		}
		
	}

	
	
	//Draw graph
	public void paint (Graphics graph)
	{
		//constructor of paint called 
		super.paint(graph);
		
		//axes
		//each setColor function set color of the next graphic elements
		graph.setColor(Color.black);
		graph.drawLine(50, 100, 50, 750); //Y
		graph.drawString("Temperature (°C)", 50, 90);
		
		graph.drawLine(50, 750, 1390, 750); //X
		graph.drawString("Time (ms)", 1320, 730);		
		
		//Name of JTextField
		graph.setColor(Color.black);
		graph.drawString("Insert IP Address above", 245, 70);
		graph.drawString("Insert period above (ms)", 505, 70);
		graph.drawString("nb of T           You have to set all the fields to obtain the graph", 670, 70);
		graph.drawString("                  For a better reading of the values -> Advice nb of readings max to 20", 668, 85);
		
		//y axis values
		int pointY=0;
		int spaceY=0;
		int tempY=0;
		for (pointY=1; pointY<=8; pointY++)
		{
			graph.setColor(Color.red);
			graph.drawOval(50, 675 - spaceY, 4, 4);
			tempY = pointY*10;
			graph.drawString(tempY+"°C", 10, 680 - spaceY);
			spaceY=spaceY+75; //75 is the interval between two points 
		}
		
		
		//x axis values
		int pointX=0;
		int spaceX=0;
		//set size of number of readings by the user 
		int nbX=0; // set to 20 readings by default
		Float X = Float.valueOf(this.tmpNbReadings);
		if (X==null)
		{
			nbX=21;
		}
		else 
		{
			nbX = Math.round(X);
		}
		for (pointX=1; pointX<=nbX; pointX++)
		{
			graph.setColor(Color.blue);
			graph.drawOval(115+spaceX, 750, 4, 4);
			graph.drawString(pointX+"T", 110+spaceX, 775);
			spaceX=spaceX+(1300/nbX); //65 is the interval between two points
		}
	
		
		//Vector of temperature
		Vector < Float > vectorTemp = new Vector < Float > (nbX);
		///Add temp points
		int n=1;
		int x=0;
		int intY=0;
		Float y=null;
		///string of the period writen by the user is converted to float
		Float T = Float.valueOf(this.tempT);
		//period in float is converted to int to use it to make pause 
		int pause = Math.round(T);
		String TemperatureString=null;
		//1 to 21 to have 21 readings (rate)
		while (n!=nbX+1) //+1 because it starts from 1 
		{
			//get the IP Address input from Client App
			theApp = new Client(this.tmpIP);
			///get temperature from the client
			Float Temperature = Float.valueOf(this.theApp.getTemp());
			///add Temperature to vector to sort them after
			vectorTemp.add(Temperature);
			TemperatureString = String.valueOf(Temperature); 
			// convert height of th Y axis into degres celsius (80°C is estimated to be the maximum)
			y= 750-((Temperature*600)/80);
			//convert y into int to use it in drawOval function
			intY = Math.round(y);
			graph.setColor(Color.black);
			graph.drawOval(115+x, intY, 4, 4);
			graph.drawString("("+TemperatureString+")", 105+x, intY+20);
			n++;
			x=x+(1300/nbX); //(1300/nbX) is the gap between each period - 1300 is the size of X axis
			//sleep method for each period
			try
			{
			    Thread.sleep(pause);
			}
			catch(InterruptedException ex)
			{
			    Thread.currentThread().interrupt();
			}
			
		}
		
		///sort vector of temp
		Collections.sort(vectorTemp);
		//draw min and max lines
		//convert float into int sup
		int min = Math.round(vectorTemp.get(0));
		int max = Math.round(vectorTemp.get(nbX-1));
		//convert min and max for Y axis
		min = 750-((min*600)/80);
		max = 750-((max*600)/80);
		//draw
		graph.setColor(Color.green);
		graph.drawLine(10, min, 1390, min);
		graph.drawString("MIN = "+vectorTemp.get(0), 1200, 90);
		
		graph.setColor(Color.red);
		graph.drawLine(10, max, 1390, max);
		graph.drawString("MAX = "+vectorTemp.get(nbX-1), 1200, 110);
		
	}
	
	
	
	public static void main (String[] args)
	{
		//GraphicalUserInterface GUIgraph = new GraphicalUserInterface ();
		
	}

}
