package desktopapp.write;

import java.io.*;

public class MyTextWriter{
	
	public String spacer = "\",\"";

	public void writeParticipant(int participantID, String conferenceCode, String participantType, String FName, String LName, int chapterNum) throws IOException{
		PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(".\\DAP_Files\\PARTICIPANTS.txt", true)));
		
		outputWriter.println("\"" + participantID + spacer + conferenceCode + spacer + participantType + spacer + FName + spacer + LName + spacer + chapterNum + "\"");
		outputWriter.close();
	}
	
	public void writeType(String typeChar, String description) throws IOException{
		PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(".\\DAP_Files\\TYPE.txt", true)));
		
		outputWriter.println("\"" + typeChar + spacer + description + "\"");
		outputWriter.close();
	}
	
	public void writeWorkshops(int workshopNum, String conferenceCode, String Name, String date, String startTime) throws IOException{
		PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(".\\DAP_Files\\WORKSHOPS.txt", true)));
		
		outputWriter.println("\"" + workshopNum + spacer + conferenceCode + spacer + Name + spacer + date + spacer + startTime + "\"");
		outputWriter.close();
	}
	
	public void writeConferences(String conferenceID, String location, String beginDate, String endDate) throws IOException{
		PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(".\\DAP_Files\\CONFERENCES.txt", true)));
		
		outputWriter.println("\"" + conferenceID + spacer + location + spacer + beginDate + spacer + endDate + "\"");
		outputWriter.close();
	}	
	
	public void writeRegistration(String participantID, int workshopNum) throws IOException{
		PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(".\\DAP_Files\\WKSHP_REGISTRATION.txt", true)));
		
		outputWriter.println("\"" + participantID + spacer + workshopNum + "\"");
		outputWriter.close();
	}
}
