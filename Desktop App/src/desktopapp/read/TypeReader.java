package desktopapp.read;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/** Used to read "Type.txt"*/
public class TypeReader {
	
	private static Scanner reader;
	private final static int TYPE_CHAR = 0;
	private final static int DESCRIPTION = 1;
	
	public static String[] getLine(int numLine) throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\TEXTCODE\\TYPE.txt")));

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
	public static int getNumOfLines() throws FileNotFoundException{
		reader = new Scanner(new BufferedReader(new FileReader(".\\TEXTCODE\\TYPE.txt")));

		int count = 1;
		while(reader.hasNextLine()){
			String line = reader.nextLine();
			count++;
		}

		return count;
	}

	
	public int getLineOfChar(String typeChar) throws FileNotFoundException{
		for(int i = 1; i < ConferenceReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[TYPE_CHAR].equals(typeChar)){
				return i;
			}
		}
		return -1;
			
	}
	public int getLineOfDescription(String descr) throws FileNotFoundException{
		for(int i = 1; i < ConferenceReader.getNumOfLines(); i++){
			String [] lineArray= getLine(i);
			if(lineArray[DESCRIPTION].equals(descr)){
				return i;
			}
		}
		return -1;
}
	
	public String getTypeChar(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[TYPE_CHAR];
	}
	public String getDescription(int lineNum) throws FileNotFoundException{
		String[] line = getLine(lineNum);

		return line[DESCRIPTION];
	}

	public String[] getAllTypes() throws FileNotFoundException{
		ArrayList<String> arrayList = new ArrayList<String>();
		for(int i = 1; i < getNumOfLines(); i++){
			arrayList.add(getDescription(i));
		}
		
		String[] array = new String[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++ ){
			array[i] = arrayList.get(i);
		}
		return array;
	}
}