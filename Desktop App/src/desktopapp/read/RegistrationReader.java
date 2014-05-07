package desktopapp.read;

import java.io.*;
import java.util.*;

/** Used to read "WKSHP_Registration.txt"*/
public class RegistrationReader {
	//String conferenceID           String location         String beginDate       String endDate
	
	private static Scanner reader;
	private final int participantID = 0;
	private final int workshopNum = 1;
        private final ParticipantReader participantReader = new ParticipantReader();
        private final WorkshopReader workshopReader  = new WorkshopReader();
	
	public static String[] getLine(int numLine) throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\TEXTCODE\\WKSHP_REGISTRATION.txt")));

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
		reader = new Scanner(new BufferedReader(new FileReader(".\\TEXTCODE\\WKSHP_REGISTRATION.txt")));
		
		int count = 1;
		while(reader.hasNextLine()){
			String line = reader.nextLine();
			count++;
		}
		
		return count;
	}

	public ArrayList<Integer> getLinesOfID(String participantID) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.participantID].equals(participantID)){
				allLines.add(i);
			}
		}
		return allLines;

	}
	public ArrayList<Integer> getLinesOfWorkshop(String workshopNum) throws FileNotFoundException{

		ArrayList<Integer> allLines = new ArrayList<Integer>();

		for(int i = 1; i < ParticipantReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[this.workshopNum].equals(workshopNum)){
				allLines.add(i);
			}
		}
		return allLines;

	}
	public String getID(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[this.participantID];
	}
	public String getWorkshopNum(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[this.workshopNum];
	}
        public Object[][] getRegData() throws FileNotFoundException {
		Object[][] array = new Object[getNumOfLines()][3];

		for(int i = 1; i < RegistrationReader.getNumOfLines(); i++){
			for(int j = 0; j < 3; j ++){

				if(j == 0){
                                        String userFirst = participantReader.getFName(participantReader.getLineOfID(getID(i)));
                                        System.out.println(participantReader.getLineOfID(getID(i)));
					array[i][j] = userFirst;
				}
                                else if(j == 1){
                                        String userLast = participantReader.getLName(participantReader.getLineOfID(getID(i)));
                                        array[i][j] = userLast;
                                }
				else if (j == 2){
                                        String workshop = workshopReader.getName(workshopReader.getLineOfID(getWorkshopNum(i)));
					array[i][j] = workshop;
                                }
				
			}
            }
		
		return array;
            }
	}

