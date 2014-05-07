package desktopapp.read;

import java.io.*;
import java.util.*;


public class ConferenceReader {
	//String conferenceID           String location         String beginDate       String endDate

	private static Scanner reader;
	private final int ID = 0;
	private final int LOCATION = 1; 
	private final int BEGIN_DATE = 2;
	private final int END_DATE = 3;

	/*
	*	@param int lineNumber
	*	This method removes the line Number from the Conference Text File
	*/
	public static String[] getLine(int numLine) throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\DAP_Files\\CONFERENCES.txt")));

		int count = 1;

		String[] lineArray = null;
		boolean go = true;
		while(reader.hasNextLine() && go){
			String currentLine = reader.nextLine();			
			currentLine = currentLine.substring(1, currentLine.length() - 1);
			if(count == numLine){
				lineArray = currentLine.split("\",\"");
				go = false;
			}
			count++;
		}
		reader.close();

		return lineArray;
	}
	/*
	*	Returns the number of lines in the Conference Text File
	*/
	public static int getNumOfLines() throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\DAP_Files\\CONFERENCES.txt")));

		int count = 1;
		while(reader.hasNextLine()){
			String line = reader.nextLine();
			count++;
		}

		return count;
	}
	/*
	*	@param 	conference ID 
	*	Returns the first line containing
	*/
	public int getLineOfID(String conferenceID) throws FileNotFoundException{
		for(int i = 1; i < ConferenceReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[ID].equals(conferenceID)){
				return i;
			}
		}
		return -1;
	}

	public int getLineOfLocation(String location) throws FileNotFoundException{
		for(int i = 1; i < ConferenceReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[LOCATION].equals(location)){
				return i;
			}
		}
		return -1;

	}

	public String getConferenceID(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[ID];
	}
	public String getLocation(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[LOCATION];
	}
	public String getEndDate(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[BEGIN_DATE];
	}
	public String getBeginDate(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[END_DATE];
	}

	public String[] getAllConf() throws FileNotFoundException{
		ArrayList<String> arrayList = new ArrayList<String>();
		for(int i = 1; i < getNumOfLines(); i++){
			arrayList.add(getLocation(i));
		}

		String[] array = new String[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++ ){
			array[i] = arrayList.get(i);
		}
		return array;
	}
        public String[] getConfWithAll() throws FileNotFoundException{
		ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add("All...");
		for(int i = 1; i < getNumOfLines(); i++){
			arrayList.add(getLocation(i));
		}

		String[] array = new String[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++ ){
			array[i] = arrayList.get(i);
		}
		return array;
	}
	public Object[][] getConfData() throws FileNotFoundException {
		Object[][] array = new Object[getNumOfLines()][5];

		for(int i = 1; i < ConferenceReader.getNumOfLines(); i++){
			for(int j = 0; j < 4; j ++){

				if(j == 1){
					array[i][j] = getLocation(i);
				}
                                else if(j == 0){
                                        array[i][j] = getConferenceID(i);
                                }
				else if (j == 3){
					array[i][j] = getBeginDate(i);	
				}
				else if (j == 2){
					array[i][j] = getEndDate(i);	
				}
			}
		}
		return array;
	}

	public int getMaxID() throws FileNotFoundException{
		int maxID = 0;
		for(int i = 1; i < getNumOfLines(); i++){
			int num = Integer.parseInt(getConferenceID(i).substring(2));
			if(num > maxID){
				maxID = num;
			}
		}
		return maxID;
	}
	public static void removeLine(int lineNum) throws IOException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\DAP_Files\\CONFERENCES.txt")));
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
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(".\\DAP_Files\\CONFERENCES.txt"));
		for(int i = 0; i < array.size() ; i++){
			System.out.println(array.get(i) + "\n");
			writer.write(array.get(i));
			writer.newLine();
		}
		writer.close();
		

	}
}