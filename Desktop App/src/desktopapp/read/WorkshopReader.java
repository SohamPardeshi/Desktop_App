package desktopapp.read;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/** Used to read "Workshops.txt"*/
public class WorkshopReader {
		//int workshopNum, String conferenceCode, String Name, String date, String startTime
	
	private static Scanner reader;
	private final static int workshopNum = 0;
	private final static int conferenceCode = 1;
	private final static int Name = 2;
	private final static int Date = 3;
	private final static int StartTime = 4;
	private static ConferenceReader conferenceReader = new ConferenceReader();
	
	
	public static String[] getLine(int numLine) throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\TEXTCODE\\WORKSHOPS.txt")));

		int count = 1;
		
		String[] lineArray = null;
		while(reader.hasNextLine()){
			String currentLine = reader.nextLine();
			currentLine = currentLine.substring(1, currentLine.length() - 1);
			if(count == numLine){
				lineArray = currentLine.split("\",\"");
				break;
			}
			count++;
		}
		reader.close();

		return lineArray;
	}
	public static int getNumOfLines() throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\TEXTCODE\\WORKSHOPS.txt")));
		
		int count = 1;
		while(reader.hasNextLine()){
			String line = reader.nextLine();
            		count++;
		}
		
		return count;
	}
	
	public int getLineOfID(String num) throws FileNotFoundException{
		for(int i = 1; i < WorkshopReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[workshopNum].equals(num)){
				return i;
			}
		}
		return -1;

	}
	public ArrayList<Integer> getLinesOfConference(String conferenceCode) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < WorkshopReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.conferenceCode].equals(conferenceCode)){
				allLines.add(i);
			}
		}
		return allLines;

	}
	public int getLineOfName(String name) throws FileNotFoundException{
		for(int i = 1; i < WorkshopReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[Name].equals(name)){
				return i;
			}
		}
		return -1;

	}
	public ArrayList<Integer> getLinesOfDate(String date) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < WorkshopReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.Date].equals(date)){
				allLines.add(i);
			}
		}
		return allLines;

	}
	public ArrayList<Integer> getLinesOfStartTime(String timeFormat) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < WorkshopReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.StartTime].equals(timeFormat)){
				allLines.add(i);
			}
		}
		return allLines;

	}

	public int getWorkshopNum(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return Integer.parseInt(line[workshopNum]);
	}
	public String getConferenceCode(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[conferenceCode];
	}
	public String getDate(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[Date];
	}
	public String getStartTime(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[StartTime];
	}
	public String getName(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[Name];
	}
        public int getMaxID() throws FileNotFoundException{
		int maxID = 0;
		for(int i = 1; i < getNumOfLines(); i++){
			if(getWorkshopNum(i) > maxID){
				maxID = getWorkshopNum(i);
			}
		}
		return maxID;
	}

	public ArrayList<String> getAllWorkshops(){
		ArrayList<String> array = new ArrayList<String>();
		try {
			for(int i = 1; i < getNumOfLines(); i++){
				array.add(""+getName(i));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return array;
	}
	public Object[][] getWorkshopData() throws FileNotFoundException {
		Object[][] array = new Object[getNumOfLines()][5];
                int n = WorkshopReader.getNumOfLines();
                for(int i = 1; i < n; i++){
			for(int j = 0; j < 4; j++){

				if(j == 0){
					array[i][j] = getName(i);
				}
				else if (j == 1){
					array[i][j] = getConferenceCode(i);

				}
				else if (j == 2){
					array[i][j] = getDate(i);	
				}
				else if (j == 3){
					array[i][j] = getStartTime(i);
				}
				
			}
		}
		return array;
	}
           public static void removeLine(int lineNum) throws IOException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\TEXTCODE\\WORKSHOPS.txt")));
		ArrayList<String> array = new ArrayList<String>();

		int count = 1;
		while(reader.hasNextLine()){
			String temp = reader.nextLine();
			if(count != lineNum){
				array.add(temp);
			}
			count++;
		}	
		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(".\\TEXTCODE\\WORKSHOPS.txt"));
		for(int i = 0; i < array.size() ; i++){
			writer.write(array.get(i));
                        if(i != array.size())
			writer.newLine();
		}
		writer.close();


	}

}
