package desktopapp.read;

import java.io.*;
import java.util.*;


public class ParticipantReader {
	//String participantID     String conferenceCode     String participantType    String FName     String LName     int chapterNum

	private static Scanner reader;
	private final int participantID = 0;
	private final int conferenceCode = 1;
	private final int participantType = 2;
	private final int FName = 3;
	private final int LName = 4;
	private final int chapterNum = 5;

	private ConferenceReader conferenceReader = new ConferenceReader();


	public static String[] getLine(int numLine) throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\DAP_Files\\PARTICIPANTS.txt")));

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
		reader = new Scanner(new BufferedReader(new FileReader(".\\DAP_Files\\PARTICIPANTS.txt")));

		int count = 1;
		while(reader.hasNextLine()){
                        String line = reader.nextLine();
			count++;

		}

		return count;
	}

	public int getLineOfID(String IDNumber) throws FileNotFoundException{
		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[participantID].equals(IDNumber)){
				return i;
			}
		}
		return -1;

	}
	public ArrayList<Integer> getLinesOfConference(String conferenceCode) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.conferenceCode].equals(conferenceCode)){
				allLines.add(i);
			}
		}
		return allLines;

	}
	public ArrayList<Integer> getLinesOfType(String type) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.participantType].equals(type)){
				allLines.add(i);
			}
		}
		return allLines;

	}
	public ArrayList<Integer> getLinesOfFName(String FName) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.FName].equals(FName)){
				allLines.add(i);
			}
		}
		return allLines;


	}	
	public ArrayList<Integer> getLinesOfLName(String LName) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.LName].equals(LName)){
				allLines.add(i);
			}
		}
		return allLines;

	}
	public ArrayList<Integer> getLinesOfChapterNum(String chapterNum) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.chapterNum].equals(chapterNum)){
				allLines.add(i);
			}
		}
		return allLines;


	}

	public int getID(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return Integer.parseInt(line[participantID]);
	}
	public String getConferenceCode(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[conferenceCode];
	}
	public String getType(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[participantType];
	}
	public String getFName(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[FName];
	}
	public String getLName(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[LName];
	}
	public int getChapterNum(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return Integer.parseInt(line[chapterNum]);
	}

	public int getMaxID() throws FileNotFoundException{
		int maxID = 0;
		for(int i = 1; i < getNumOfLines(); i++){
			if(getID(i) > maxID){
				maxID = getID(i);
			}
		}
		return maxID;
	}
	public Object[][] getAllParticipants() throws FileNotFoundException {
		Object[][] array = new Object[getNumOfLines()][6];

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			for(int j = 0; j < 6; j ++){

				if(j == 0){
					array[i][j] = getID(i);
                                 }
				else if (j == 1){
					array[i][j] = getFName(i);	
				}
				else if (j == 2){
					array[i][j] = getLName(i);	
				}
				else if (j == 3){
					array[i][j] = getType(i);
				}
				else if (j == 4){
					int conferenceCodeLine = conferenceReader.getLineOfID(getConferenceCode(i));
					array[i][j] = conferenceReader.getLocation(conferenceCodeLine);
				}
				else if(j == 5){
					array[i][j]= getChapterNum(i);
				}
			}
		}

		return array;
	}
	public static void removeLine(int lineNum) throws IOException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\DAP_Files\\PARTICIPANTS.txt")));
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
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(".\\DAP_Files\\PARTICIPANTS.txt"));
		for(int i = 0; i < array.size() ; i++){
			writer.write(array.get(i));
			writer.newLine();
		}
		writer.close();
		

	}
}
