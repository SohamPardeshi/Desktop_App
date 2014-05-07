/*
 * DesktopView.java
 */
package desktopapp;

import desktopapp.read.*;
import desktopapp.write.*;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 * The application's main frame used to generate the GUI and dynamically load all data into RAM.
 */
public class DesktopView extends FrameView {

    private ConferenceReader conferenceReader = new ConferenceReader();
    private ParticipantReader participantReader = new ParticipantReader();
    private TypeReader typeReader = new TypeReader();
    private WorkshopReader workshopReader = new WorkshopReader();
    private RegistrationReader registrationReader = new RegistrationReader();
    private MyTextWriter writer = new MyTextWriter();
    private boolean participantClickAllowed = true;
    private boolean conferenceClickAllowed = true;
    private boolean workshopClickAllowed = true;
    int row = 0;
    int column = 0;

    public int createPartID() {
        int maxID = 0;
        try {
            maxID = participantReader.getMaxID();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return maxID + 1;
    }

    public int createWorkID() {
        int maxID = 0;
        try {
            maxID = workshopReader.getMaxID();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return maxID + 1;
    }

    public String createConfID() {
        int maxID = 0;
        try {
            maxID = conferenceReader.getMaxID();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "ca" + (maxID + 1);
    }

    public static void createNecessaryFiles() throws IOException {
        File path = new File(".\\TEXTCODE");

        File confFile = new File(".\\TEXTCODE\\CONFERENCES.txt");
        File partFile = new File(".\\TEXTCODE\\PARTICIPANTS.txt");
        File typeFile = new File(".\\TEXTCODE\\TYPE.txt");
        File workFile = new File(".\\TEXTCODE\\WORKSHOPS.txt");
        File regFile = new File(".\\TEXTCODE\\WKSHP_REGISTRATION.txt");

        System.out.println(confFile.getAbsolutePath());
        if (!path.exists()) {
            path.mkdirs();
        }

        if (!confFile.exists()) {
            confFile.createNewFile();
        }
        if (!partFile.exists()) {
            partFile.createNewFile();
        }
        if (!typeFile.exists()) {
            typeFile.createNewFile();
        }
        if (!workFile.exists()) {
            workFile.createNewFile();
        }
        if (!regFile.exists()) {
            regFile.createNewFile();
        }

    }

    public String[] getYears() {
        String[] temp = new String[26];
        temp[0] = "Year";
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = 0; i < 25; i++) {
            temp[i + 1] = "" + currentYear;
            currentYear++;
        }
        return temp;

    }

    public DesktopView(SingleFrameApplication app) {
        super(app);

        try {
            createNecessaryFiles();
        } catch (IOException ex) {
            Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
        }

        initComponents();
        String[] tempType = null;
        String[] tempConference = null;
        String[] tempYear = getYears();
        String[] tempWorkshop = null;

        String[] participantColumn = {"ID", "First", "Last", "Type", "Conference", "Chapter"};
        String[] conferenceColumn = {"ID", "Location", "Start Date", "End Date"};
        String[] workshopColumn = {"Name", "Conference", "Date", "Start Time"};
        Object[][] conferenceData = null;
        Object[][] participantData = null;
        Object[][] workshopData = null;
        try {
            tempType = typeReader.getAllTypes();
            tempConference = conferenceReader.getAllConf();
            participantData = participantReader.getAllParticipants();
            conferenceData = conferenceReader.getConfData();
            workshopData = workshopReader.getWorkshopData();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
        }

        registerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < regScrollPane.getComponentCount(); i++) {
                    JCheckBox temp = (JCheckBox) regScrollPane.getComponent(i);
                    String tempText = temp.getText();
                    tempText = tempText.substring(0, tempText.indexOf("   "));
                    tempText = tempText.trim();
                    if (temp.isSelected()) {
                        int n = 1;
                        try {
                            n = workshopReader.getLineOfID(tempText);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        if(n != -1)
                            try {
                                writer.writeRegistration((createPartID() - 1) + "", n);
                            } catch (IOException ex) {
                                Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        else
                            System.out.println("Registration Failed... No Workshop Found");
                            
                    }
                }
                selectPanelBox.setSelectedIndex(0);
                participantScrollPane.setVisible(true);
                regScrollPane.setVisible(false);
                registerButton.setVisible(false);
                
                    participantClickAllowed = true;
                    participantTable.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent e) {
                                if (participantClickAllowed) {
                                    row = participantTable.rowAtPoint(e.getPoint());
                                    column = participantTable.columnAtPoint(e.getPoint());

                                    participantIDBox.setText("" + (Integer) participantTable.getModel().getValueAt(row, 0));
                                    participantFirstBox.setText((String) participantTable.getModel().getValueAt(row, 1));
                                    participantLastBox.setText((String) participantTable.getModel().getValueAt(row, 2));
                                    participantTypeBox.setSelectedItem((String) participantTable.getModel().getValueAt(row, 3));
                                    participantConferenceBox.setSelectedItem((String) participantTable.getModel().getValueAt(row, 4));
                                    participantChapterBox.setText("" + (Integer) participantTable.getModel().getValueAt(row, 5));
                                }
                            }
                        });
                    participantTable.requestFocus();
            }
        });

        DefaultComboBoxModel typeModel = new DefaultComboBoxModel(tempType);
        participantTypeBox.setModel(typeModel);

        DefaultComboBoxModel conferenceModel = new DefaultComboBoxModel(tempConference);
        participantConferenceBox.setModel(conferenceModel);
        workshopConferenceBox.setModel(conferenceModel);

        DefaultComboBoxModel yearModel = new DefaultComboBoxModel(tempYear);
        conferenceSYear.setModel(yearModel);
        conferenceEYear.setModel(yearModel);
        workshopYear.setModel(yearModel);

        participantSearchCombo.setModel(new DefaultComboBoxModel(participantColumn));
        final DefaultTableModel participantTableModel = new DefaultTableModel(participantData, participantColumn) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        participantTable.setModel(participantTableModel);
        participantFirstBox.setEnabled(false);
        participantLastBox.setEnabled(false);
        participantConferenceBox.setEnabled(false);
        participantTypeBox.setEnabled(false);
        participantIDBox.setEnabled(false);
        participantChapterBox.setEnabled(false);
        participantSave.setEnabled(false);

        conferenceState.setEnabled(false);
        conferenceCity.setEnabled(false);
        conferenceSDay.setEnabled(false);
        conferenceSMonth.setEnabled(false);
        conferenceSYear.setEnabled(false);
        conferenceEDay.setEnabled(false);
        conferenceEMonth.setEnabled(false);
        conferenceEYear.setEnabled(false);
        conferenceSave.setEnabled(false);

        workshopNameBox.setEnabled(false);
        workshopPeriod.setEnabled(false);
        workshopMinute.setEnabled(false);
        workshopHour.setEnabled(false);
        workshopYear.setEnabled(false);
        workshopMonth.setEnabled(false);
        workshopDay.setEnabled(false);
        workshopConferenceBox.setEnabled(false);
        workshopSave.setEnabled(false);

        participantAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                participantSave.setEnabled(true);

                participantFirstBox.setEnabled(true);
                participantLastBox.setEnabled(true);
                participantConferenceBox.setEnabled(true);
                participantTypeBox.setEnabled(true);
                participantChapterBox.setEnabled(true);

                participantFirstBox.setText("");
                participantLastBox.setText("");
                participantConferenceBox.setSelectedIndex(0);
                participantTypeBox.setSelectedIndex(0);
                participantIDBox.setText(createPartID() + "");
                participantChapterBox.setText("");

                participantClickAllowed = false;

                participantRemove.setEnabled(false);
                participantEdit.setText("Cancel");

            }
        });
        participantSave.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (participantRemove.isEnabled() == false && participantEdit.getText().equals("Cancel")) {
                    if (!participantFirstBox.getText().replaceAll(" ", "").equals("")
                            && !participantLastBox.getText().replaceAll(" ", "").equals("")
                            && !participantIDBox.getText().replaceAll(" ", "").equals("")
                            && !participantChapterBox.getText().replaceAll(" ", "").equals("")) {

                        try {
                            String newUserConference = conferenceReader.getConferenceID(conferenceReader.getLineOfLocation(participantConferenceBox.getSelectedItem().toString()));
                            writer.writeParticipant(createPartID(), newUserConference, participantTypeBox.getSelectedItem().toString(), participantFirstBox.getText(), participantLastBox.getText(), Integer.parseInt(participantChapterBox.getText()));
                            Object[] newRow = {createPartID() - 1 + "", participantFirstBox.getText(), participantLastBox.getText(), participantTypeBox.getSelectedItem().toString(), participantConferenceBox.getSelectedItem().toString(), participantChapterBox.getText()};
                            participantTableModel.addRow(newRow);
                            participantTable.validate();
                        } catch (IOException ex) {
                            Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        int n = JOptionPane.showConfirmDialog(null, "Would you like to Register this New User for Workshops?", "Register Participants", JOptionPane.YES_NO_OPTION);
                        if (n == JOptionPane.YES_OPTION) {
                            participantRegistration.setVisible(true);
                            participantScrollPane.setVisible(false);
                            String conference = (String) participantConferenceBox.getSelectedItem();
                            for (int i = 1; i < workshopTable.getRowCount(); i++) {
                                if (conference.equals((String) workshopTable.getValueAt(i, 1))) {
                                    JCheckBox temp = new JCheckBox((String) workshopTable.getValueAt(i, 0) + "     " + (String) workshopTable.getValueAt(i, 3));
                                    regScrollPane.add(temp);
                                }
                            }
                            if (regScrollPane.getComponentCount() == 0) {
                                text.setText("Sorry, there are no workshops :( ");
                            } else {
                                text.setText("Select Workshops");
                            }
                            participantRegistration.repaint();
                            participantRegistration.revalidate();
                        }

                        participantTable.setRowSelectionInterval(participantTable.getRowCount() - 1, participantTable.getRowCount() - 1);
                        participantFirstBox.setText("");
                        participantLastBox.setText("");
                        participantConferenceBox.setSelectedIndex(0);
                        participantTypeBox.setSelectedIndex(0);
                        participantIDBox.setText(createPartID() + "");
                        participantChapterBox.setText("");

                        participantFirstBox.setEnabled(false);
                        participantLastBox.setEnabled(false);
                        participantConferenceBox.setEnabled(false);
                        participantTypeBox.setEnabled(false);
                        participantIDBox.setEnabled(false);
                        participantChapterBox.setEnabled(false);
                        participantRemove.setEnabled(true);
                        participantSave.setEnabled(false);

                    } else {
                        int n = JOptionPane.showConfirmDialog(null, "Oh no! One of the fields was left blank or is an invalid input!");
                    }
                } else {
                    String newUserConference = null;
                    try {
                        newUserConference = conferenceReader.getConferenceID(conferenceReader.getLineOfLocation(participantConferenceBox.getSelectedItem().toString()));
                        writer.writeParticipant(Integer.parseInt(participantIDBox.getText()), newUserConference, participantTypeBox.getSelectedItem().toString(), participantFirstBox.getText(), participantLastBox.getText(), Integer.parseInt(participantChapterBox.getText()));
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        participantReader.removeLine(participantReader.getLineOfID(participantTableModel.getValueAt(row, 0).toString()));
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    Object[] newRow = {participantIDBox.getText(), participantFirstBox.getText(), participantLastBox.getText(), participantTypeBox.getSelectedItem().toString(), participantConferenceBox.getSelectedItem().toString(), participantChapterBox.getText()};
                    participantTableModel.removeRow(row);
                    participantTableModel.insertRow(row, newRow);
                    participantTable.validate(); 

                    participantRemove.setEnabled(true);
                    participantSave.setEnabled(false);
                    participantFirstBox.setText("");
                    participantLastBox.setText("");
                    participantConferenceBox.setSelectedIndex(0);
                    participantTypeBox.setSelectedIndex(0);
                    participantIDBox.setText(createPartID() + "");
                    participantChapterBox.setText("");

                    participantRemove.setEnabled(true);
                    participantAdd.setEnabled(true);
                    participantSave.setEnabled(false);

                }
            }
        });
        participantRegistration.setVisible(false);
        participantScrollPane.setVisible(true);
        participantEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (participantEdit.getText().equals("Cancel")) {
                    participantClickAllowed = true;
                    participantEdit.setText("Edit");
                    participantRemove.setEnabled(true);
                    participantSave.setEnabled(false);

                    participantIDBox.setText("" + (Integer) participantTable.getModel().getValueAt(row, 0));
                    participantFirstBox.setText((String) participantTable.getModel().getValueAt(row, 1));
                    participantLastBox.setText((String) participantTable.getModel().getValueAt(row, 2));
                    participantTypeBox.setSelectedItem((String) participantTable.getModel().getValueAt(row, 3));
                    participantConferenceBox.setSelectedItem((String) participantTable.getModel().getValueAt(row, 4));
                    participantChapterBox.setText("" + (Integer) participantTable.getModel().getValueAt(row, 5));

                    participantFirstBox.setEnabled(false);
                    participantLastBox.setEnabled(false);
                    participantConferenceBox.setEnabled(false);
                    participantTypeBox.setEnabled(false);
                    participantIDBox.setEnabled(false);
                    participantChapterBox.setEnabled(false);

                } else if (participantEdit.getText().equals("Edit")) {
                    participantSave.setEnabled(true);
                    participantRemove.setEnabled(false);
                    participantAdd.setEnabled(false);

                    participantFirstBox.setEnabled(true);
                    participantLastBox.setEnabled(true);
                    participantConferenceBox.setEnabled(true);
                    participantTypeBox.setEnabled(true);
                    participantChapterBox.setEnabled(true);

                }
            }

        });
        participantRemove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete: \n" + participantFirstBox.getText() + " " + participantLastBox.getText() + "?", "Delete", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    try {
                        participantReader.removeLine(participantReader.getLineOfID(participantTableModel.getValueAt(row, 0).toString()));
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    participantTableModel.removeRow(row);
                    
                    
                    participantIDBox.setText("");
                    participantFirstBox.setText("");
                    participantLastBox.setText("");
                    participantTypeBox.setSelectedIndex(0);
                    participantConferenceBox.setSelectedIndex(0);
                    participantChapterBox.setText("");
                }
            }
        });
        participantTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (participantClickAllowed) {
                    row = participantTable.rowAtPoint(e.getPoint());
                    column = participantTable.columnAtPoint(e.getPoint());

                    participantIDBox.setText("" + (Integer) participantTable.getModel().getValueAt(row, 0));
                    participantFirstBox.setText((String) participantTable.getModel().getValueAt(row, 1));
                    participantLastBox.setText((String) participantTable.getModel().getValueAt(row, 2));
                    participantTypeBox.setSelectedItem((String) participantTable.getModel().getValueAt(row, 3));
                    participantConferenceBox.setSelectedItem((String) participantTable.getModel().getValueAt(row, 4));
                    participantChapterBox.setText("" + (Integer) participantTable.getModel().getValueAt(row, 5));
                }
            }
        });

        final DefaultTableModel conferenceTableModel = new DefaultTableModel(conferenceData, conferenceColumn) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        conferenceCity.setEnabled(false);
        conferenceState.setEnabled(false);
        conferenceTable.setModel(conferenceTableModel);
        conferenceTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (conferenceClickAllowed) {
                    row = conferenceTable.rowAtPoint(e.getPoint());
                    column = conferenceTable.columnAtPoint(e.getPoint());
                    String location = (String) conferenceTable.getValueAt(row, 1);
                    String sDate = (String) conferenceTable.getValueAt(row, 2);
                    String eDate = (String) conferenceTable.getValueAt(row, 3);

                    conferenceCity.setText(location.substring(0, location.indexOf(", ")));
                    conferenceState.setText(location.substring(location.indexOf(", ") + 2));

                    conferenceSDay.setSelectedIndex(Integer.parseInt(sDate.substring(sDate.indexOf("-") + 1, sDate.lastIndexOf("-"))));
                    conferenceSMonth.setSelectedItem(sDate.substring(0, sDate.indexOf("-")));
                    conferenceSYear.setSelectedItem(sDate.substring(sDate.lastIndexOf("-") + 1));

                    conferenceEDay.setSelectedIndex(Integer.parseInt(eDate.substring(eDate.indexOf("-") + 1, eDate.lastIndexOf("-"))));
                    conferenceEMonth.setSelectedItem(eDate.substring(0, eDate.indexOf("-")));
                    conferenceEYear.setSelectedItem(eDate.substring(eDate.lastIndexOf("-") + 1));

                }
            }
        });

        final DefaultTableModel workshopTableModel = new DefaultTableModel(workshopData, workshopColumn) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        conferenceAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                conferenceState.setText("");
                conferenceCity.setText("");
                conferenceEDay.setSelectedIndex(0);
                conferenceEMonth.setSelectedIndex(0);
                conferenceEYear.setSelectedIndex(0);
                conferenceSDay.setSelectedIndex(0);
                conferenceSMonth.setSelectedIndex(0);
                conferenceSYear.setSelectedIndex(0);

                conferenceCity.setEnabled(true);
                conferenceState.setEnabled(true);
                conferenceSDay.setEnabled(true);
                conferenceSMonth.setEnabled(true);
                conferenceSYear.setEnabled(true);
                conferenceEDay.setEnabled(true);
                conferenceEMonth.setEnabled(true);
                conferenceEYear.setEnabled(true);

                conferenceRemove.setEnabled(false);
                conferenceEdit.setText("Cancel");
                conferenceSave.setEnabled(true);

            }
        });
        conferenceRemove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete: \n" + conferenceCity.getText() + ", " + conferenceState.getText() + "?", "Delete", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    try {
                        conferenceReader.removeLine(conferenceReader.getLineOfLocation(conferenceTableModel.getValueAt(row, 1).toString()));
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    conferenceTableModel.removeRow(row);
                }
            }
        });
        conferenceEdit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (conferenceEdit.getText().equals("Cancel")) {
                    conferenceCity.setEnabled(false);
                    conferenceState.setEnabled(false);
                    conferenceSDay.setEnabled(false);
                    conferenceSMonth.setEnabled(false);
                    conferenceSYear.setEnabled(false);
                    conferenceEDay.setEnabled(false);
                    conferenceEMonth.setEnabled(false);
                    conferenceEYear.setEnabled(false);

                    String location = (String) conferenceTable.getValueAt(row, 1);
                    String sDate = (String) conferenceTable.getValueAt(row, 2);
                    String eDate = (String) conferenceTable.getValueAt(row, 3);
                    conferenceCity.setText("");
                    conferenceState.setText("");

                    conferenceSDay.setSelectedIndex(0);
                    conferenceSMonth.setSelectedIndex(0);
                    conferenceSYear.setSelectedIndex(0);

                    conferenceEDay.setSelectedIndex(0);
                    conferenceEMonth.setSelectedIndex(0);
                    conferenceEYear.setSelectedItem(0);

                    conferenceClickAllowed = true;
                    conferenceEdit.setText("Edit");
                    conferenceRemove.setEnabled(true);

                } else if (conferenceEdit.getText().equals("Edit")) {

                    conferenceCity.setEnabled(true);
                    conferenceState.setEnabled(true);
                    conferenceSDay.setEnabled(true);
                    conferenceSMonth.setEnabled(true);
                    conferenceSYear.setEnabled(true);
                    conferenceEDay.setEnabled(true);
                    conferenceEMonth.setEnabled(true);
                    conferenceEYear.setEnabled(true);

                    conferenceRemove.setEnabled(false);
                    conferenceSave.setEnabled(true);
                    conferenceAdd.setEnabled(false);

                }
            }
        });
        conferenceSave.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (conferenceRemove.isEnabled() == false && conferenceEdit.getText().equals("Cancel")) {
                    try {
                        Object[] newConfRow = {createConfID(), conferenceCity + ", " + conferenceState, conferenceSMonth.getSelectedItem() + "-" + conferenceSDay.getSelectedIndex() + "-" + (String) conferenceSYear.getSelectedItem(), (String) conferenceEMonth.getSelectedItem() + "-" + conferenceEDay.getSelectedIndex() + "-" + (String) conferenceEYear.getSelectedItem()};
                        conferenceTableModel.removeRow(row);
                        conferenceTableModel.insertRow(row, newConfRow);
                        conferenceTableModel.addRow(newConfRow);
                        writer.writeConferences(createConfID(), conferenceCity + ", " + conferenceState, conferenceSMonth.getSelectedIndex() + "-" + conferenceSDay.getSelectedIndex() + "-" + (String) conferenceSYear.getSelectedItem(), (String) conferenceEMonth.getSelectedItem() + "-" + conferenceEDay.getSelectedIndex() + "-" + (String) conferenceEYear.getSelectedItem());
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    conferenceCity.setText("");
                    conferenceState.setText("");
                    conferenceSDay.setSelectedIndex(0);
                    conferenceSMonth.setSelectedIndex(0);
                    conferenceSYear.setSelectedItem(0);
                    conferenceEDay.setSelectedIndex(0);
                    conferenceEMonth.setSelectedIndex(0);
                    conferenceEYear.setSelectedItem(0);

                    conferenceState.setEnabled(false);
                    conferenceCity.setEnabled(false);
                    conferenceSDay.setEnabled(false);
                    conferenceSMonth.setEnabled(false);
                    conferenceSYear.setEnabled(false);
                    conferenceEDay.setEnabled(false);
                    conferenceEMonth.setEnabled(false);
                    conferenceEYear.setEnabled(false);
                    conferenceSave.setEnabled(false);

                    conferenceEdit.setText("Edit");
                    conferenceRemove.setEnabled(true);
                    conferenceSave.setEnabled(false);
                    conferenceClickAllowed = true;

                } else {
                    try {

                        writer.writeConferences((String) conferenceTable.getValueAt(row, 0), conferenceCity.getText() + ", " + conferenceState.getText(), conferenceSMonth.getSelectedItem() + "-" + conferenceSDay.getSelectedIndex() + "-" + (String) conferenceSYear.getSelectedItem(), (String) conferenceEMonth.getSelectedItem() + "-" + conferenceEDay.getSelectedIndex() + "-" + (String) conferenceEYear.getSelectedItem());
                        conferenceReader.removeLine(conferenceReader.getLineOfID((String) conferenceTable.getValueAt(row, 0)));
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Object[] newRow = {(String) conferenceTable.getValueAt(row, 0), conferenceCity.getText() + ", " + conferenceState.getText(), conferenceSMonth.getSelectedItem() + "-" + conferenceSDay.getSelectedIndex() + "-" + (String) conferenceSYear.getSelectedItem(), (String) conferenceEMonth.getSelectedItem() + "-" + conferenceEDay.getSelectedIndex() + "-" + (String) conferenceEYear.getSelectedItem()};
                    conferenceTableModel.removeRow(row);
                    conferenceTableModel.insertRow(row, newRow);
                    conferenceTable.validate();

                    conferenceAdd.setEnabled(true);
                    conferenceCity.setText("");
                    conferenceState.setText("");
                    conferenceSDay.setSelectedIndex(0);
                    conferenceSMonth.setSelectedIndex(0);
                    conferenceSYear.setSelectedItem(0);
                    conferenceEDay.setSelectedIndex(0);
                    conferenceEMonth.setSelectedIndex(0);
                    conferenceEYear.setSelectedItem(0);

                    conferenceState.setEnabled(false);
                    conferenceCity.setEnabled(false);
                    conferenceSDay.setEnabled(false);
                    conferenceSMonth.setEnabled(false);
                    conferenceSYear.setEnabled(false);
                    conferenceEDay.setEnabled(false);
                    conferenceEMonth.setEnabled(false);
                    conferenceEYear.setEnabled(false);
                    conferenceSave.setEnabled(false);

                    conferenceEdit.setText("Edit");
                    conferenceRemove.setEnabled(true);
                    conferenceSave.setEnabled(false);
                    conferenceClickAllowed = true;

                }
            }
        });

        workshopTable.setModel(workshopTableModel);
        workshopTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (workshopClickAllowed) {
                    row = workshopTable.rowAtPoint(e.getPoint());
                    column = workshopTable.columnAtPoint(e.getPoint());

                    String date = (String) workshopTable.getModel().getValueAt(row, 2);
                    String time = (String) workshopTable.getModel().getValueAt(row, 3);

                    workshopNameBox.setText((String) workshopTable.getModel().getValueAt(row, 0));
                    workshopConferenceBox.setSelectedItem((String) workshopTable.getModel().getValueAt(row, 1));

                    workshopMonth.setSelectedItem(date.substring(0, date.indexOf("-")));
                    workshopDay.setSelectedIndex(Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-"))));
                    workshopYear.setSelectedItem(date.substring(date.lastIndexOf("-") + 1));

                    workshopHour.setSelectedIndex(Integer.parseInt(time.substring(0, time.indexOf(":"))));
                    workshopMinute.setSelectedIndex(Integer.parseInt(time.substring(time.indexOf(":") + 1, time.indexOf(" "))));
                    workshopPeriod.setSelectedItem(time.substring(time.indexOf(" ") + 1));

                }
            }
        });

        workshopDay.setEnabled(false);
        workshopMonth.setEnabled(false);
        workshopYear.setEnabled(false);
        workshopHour.setEnabled(false);
        workshopMinute.setEnabled(false);
        workshopPeriod.setEnabled(false);
        workshopSave.setEnabled(false);
        regScrollPane.setLayout(new GridLayout(0, 1));
        workshopAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                workshopNameBox.setText("");
                workshopConferenceBox.setSelectedIndex(0);
                workshopDay.setSelectedIndex(0);
                workshopMonth.setSelectedIndex(0);
                workshopYear.setSelectedIndex(0);
                workshopHour.setSelectedIndex(0);
                workshopMinute.setSelectedIndex(0);
                workshopPeriod.setSelectedIndex(0);

                workshopDay.setEnabled(true);
                workshopMonth.setEnabled(true);
                workshopYear.setEnabled(true);
                workshopHour.setEnabled(true);
                workshopMinute.setEnabled(true);
                workshopPeriod.setEnabled(true);
                workshopConferenceBox.setEnabled(true);
                workshopNameBox.setEnabled(true);

                workshopRemove.setEnabled(false);
                workshopEdit.setText("Cancel");
                workshopSave.setEnabled(true);
                workshopClickAllowed = false;
            }
        });
        workshopRemove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete: \n" + workshopNameBox.getText() + "?", "Delete", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    try {
                        workshopReader.removeLine(workshopReader.getLineOfName(workshopTableModel.getValueAt(row, 0).toString()));
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    workshopTableModel.removeRow(row);
                }
            }
        });
        workshopEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (workshopEdit.getText().equals("Cancel")) {
                    String date = (String) workshopTable.getModel().getValueAt(row, 2);
                    String time = (String) workshopTable.getModel().getValueAt(row, 3);

                    workshopNameBox.setText((String) workshopTable.getModel().getValueAt(row, 0));
                    workshopConferenceBox.setSelectedItem((String) workshopTable.getModel().getValueAt(row, 1));

                    workshopMonth.setSelectedIndex(0);
                    workshopDay.setSelectedIndex(0);
                    workshopYear.setSelectedIndex(0);

                    workshopHour.setSelectedIndex(0);
                    workshopMinute.setSelectedIndex(0);
                    workshopPeriod.setSelectedItem(0);
                    workshopSave.setEnabled(false);
                    workshopRemove.setEnabled(true);
                    workshopClickAllowed = true;
                } else {
                    workshopNameBox.setEnabled(true);
                    workshopConferenceBox.setEnabled(true);
                    workshopDay.setEnabled(true);
                    workshopMonth.setEnabled(true);
                    workshopYear.setEnabled(true);
                    workshopHour.setEnabled(true);
                    workshopMinute.setEnabled(true);
                    workshopPeriod.setEnabled(true);
                    workshopSave.setEnabled(true);
                    workshopClickAllowed = false;

                    workshopRemove.setEnabled(false);
                    workshopAdd.setEnabled(false);
                }
            }
        });
        workshopSave.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (workshopRemove.isEnabled() == false && workshopEdit.getText().equals("Cancel")) {
                    String workshopConfVal = (String) workshopConferenceBox.getSelectedItem();
                    String workshopDate = (String) workshopMonth.getSelectedItem() + "-" + (String) workshopDay.getSelectedItem() + "-" + (String) workshopYear.getSelectedItem();
                    String workshopTime = (String) workshopHour.getSelectedItem() + ":" + (String) workshopMinute.getSelectedItem() + " " + (String) workshopPeriod.getSelectedItem();
                    try {
                        Object[] newConfRow = {workshopNameBox.getText(), workshopConfVal, workshopDate, workshopTime};
                        workshopTableModel.addRow(newConfRow);

                        writer.writeWorkshops(createWorkID(), workshopConfVal, workshopNameBox.getText(), workshopDate, workshopTime);
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    workshopNameBox.setText("");
                    workshopConferenceBox.setSelectedIndex(0);
                    workshopDay.setSelectedIndex(0);
                    workshopMonth.setSelectedIndex(0);
                    workshopYear.setSelectedIndex(0);
                    workshopHour.setSelectedIndex(0);
                    workshopMinute.setSelectedIndex(0);
                    workshopPeriod.setSelectedIndex(0);

                    workshopNameBox.setEnabled(false);
                    workshopConferenceBox.setEnabled(false);
                    workshopDay.setEnabled(false);
                    workshopMonth.setEnabled(false);
                    workshopYear.setEnabled(false);
                    workshopHour.setEnabled(false);
                    workshopMinute.setEnabled(false);
                    workshopPeriod.setEnabled(false);

                    workshopEdit.setText("Edit");
                    workshopRemove.setEnabled(true);
                    workshopSave.setEnabled(false);

                    workshopNameBox.setEnabled(false);
                    workshopConferenceBox.setEnabled(false);
                    workshopDay.setEnabled(false);
                    workshopMonth.setEnabled(false);
                    workshopYear.setEnabled(false);
                    workshopHour.setEnabled(false);
                    workshopMinute.setEnabled(false);
                    workshopPeriod.setEnabled(false);
                    workshopSave.setEnabled(false);

                    workshopClickAllowed = true;
                } else {
                    try {

                        writer.writeWorkshops(createWorkID(), (String) workshopConferenceBox.getSelectedItem(), workshopNameBox.getText(), (String) workshopMonth.getSelectedItem() + "-" + (String) workshopDay.getSelectedItem() + "-" + (String) workshopYear.getSelectedItem(), (String) workshopHour.getSelectedItem() + ":" + (String) workshopMinute.getSelectedItem() + " " + (String) workshopPeriod.getSelectedItem());
                        workshopReader.removeLine(workshopReader.getLineOfName(workshopTableModel.getValueAt(row, 0).toString()));
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String workshopConfVal = (String) workshopConferenceBox.getSelectedItem();
                    String workshopDate = (String) workshopMonth.getSelectedItem() + "-" + (String) workshopDay.getSelectedItem() + "-" + (String) workshopYear.getSelectedItem();
                    String workshopTime = (String) workshopHour.getSelectedItem() + ":" + (String) workshopMinute.getSelectedItem() + " " + (String) workshopPeriod.getSelectedItem();

                    Object[] newRow = {workshopNameBox.getText(), workshopConfVal, workshopDate, workshopTime};
                    workshopTableModel.removeRow(row);
                    workshopTableModel.insertRow(row, newRow);
                    workshopTable.validate();

                    workshopRemove.setEnabled(true);
                    workshopSave.setEnabled(false);
                    workshopClickAllowed = true;
                    workshopAdd.setEnabled(true);
                }

            }
        });

        participantSearchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String[] tempColumn = {"ID", "First", "Last", "Type", "Conference", "Chapter"};
                Object[][] tempData = null;
                try {
                    tempData = participantReader.getAllParticipants();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                }

                DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn);
                String search = String.valueOf(participantSearchBox.getText());
                int searchColumn = participantSearchCombo.getSelectedIndex();
                if (!search.replace(" ", "").equals("")) {
                    for (int i = 1; i < tempModel.getRowCount(); i++) {

                        if (searchColumn == 0 || searchColumn == 5) {
                            int valueAt = (Integer) tempModel.getValueAt(i, searchColumn);
                            if (Integer.parseInt(search) != valueAt) {
                                tempModel.removeRow(i);
                            }
                        } else {
                            String valueAt = (String) tempModel.getValueAt(i, searchColumn);
                            if (!valueAt.contains(search)) {
                                tempModel.removeRow(i);
                            }
                        }
                    }

                }
                participantTable.setModel(tempModel);
                participantSearchBox.setText("");

            }
        });
        conferenceSearchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String[] tempColumn = {"ID", "Location", "Start Date", "End Date"};
                Object[][] tempData = null;
                try {
                    tempData = conferenceReader.getConfData();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                }

                DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn);
                String search = String.valueOf(conferenceSearchBox.getText());
                if (!search.replace(" ", "").equals("")) {
                    for (int i = 1; i < tempModel.getRowCount(); i++) {
                        String valueAt = (String) tempModel.getValueAt(i, 0);
                        if (!valueAt.contains(search)) {
                            tempModel.removeRow(i);
                        }
                    }

                }
                conferenceTable.setModel(tempModel);
                conferenceSearchBox.setText("");

            }
        });
        workshopSearchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String[] tempColumn = {"Name", "Conference", "Date", "Start Time"};
                Object[][] tempData = null;
                try {
                    tempData = workshopReader.getWorkshopData();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                }

                DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn);
                String search = String.valueOf(workshopSearchBox.getText());
                if (!search.replace(" ", "").equals("")) {
                    for (int i = 1; i < tempModel.getRowCount(); i++) {
                        String valueAt = (String) tempModel.getValueAt(i, 0);
                        if (!valueAt.contains(search)) {
                            tempModel.removeRow(i);
                        }
                    }

                }
                workshopTable.setModel(tempModel);
                workshopSearchBox.setText("");

            }
        });

        confBox.setVisible(false);
        reportConferenceSelect.setVisible(false);
        reportSortOrder.setVisible(false);
        orderBox.setVisible(false);

        reportSortBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = reportSortBox.getSelectedIndex();
                if (n == 0) {
                    orderBox.setVisible(false);
                    confBox.setVisible(false);
                    reportConferenceSelect.setVisible(false);
                    reportSortOrder.setVisible(false);

                } else if (n == 1) {
                    try {
                        DefaultComboBoxModel tempModel = new DefaultComboBoxModel(conferenceReader.getConfWithAll());
                        reportConferenceSelect.setModel(tempModel);

                        DefaultTableModel tempModel2 = new DefaultTableModel(participantReader.getAllParticipants(), new String[]{"ID", "Conference", "Type", "First", "Last", "Chapter"}) {

                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }

                        };

                        reportTable.setModel(tempModel2);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    orderBox.setVisible(true);
                    reportSortOrder.setVisible(true);
                    confBox.setVisible(true);
                    reportConferenceSelect.setVisible(true);

                } else if (n == 2) {
                    orderBox.setVisible(false);
                    confBox.setVisible(false);
                    reportConferenceSelect.setVisible(false);
                    reportSortOrder.setVisible(false);
                    String[] tempColumn = {"First", "Last", "Conference"};
                    Object[][] tempData = null;
                    try {
                        tempData = registrationReader.getRegData();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn) {

                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }

                    };

                    reportTable.setModel(tempModel);

                }
            }
        });

        reportTable.setModel(participantTableModel);
        reportConferenceSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (reportConferenceSelect.getSelectedIndex() == 0) {
                    String[] tempColumn = {"ID", "First", "Last", "Type", "Conference", "Chapter"};
                    Object[][] tempData = null;
                    try {
                        tempData = participantReader.getAllParticipants();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn) {

                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }

                    };

                    reportTable.setModel(tempModel);
                } else {
                    String[] tempColumn = {"ID", "First", "Last", "Type", "Conference", "Chapter"};
                    Object[][] tempData = null;
                    try {
                        tempData = participantReader.getAllParticipants();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn) {

                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }

                    };

                    String n = (String) reportConferenceSelect.getSelectedItem();

                    for (int i = tempModel.getRowCount() - 1; i >= 1; i--) {
                        String p = (String) tempModel.getValueAt(i, 4);
                        if (!p.equals(n)) {
                            tempModel.removeRow(i);
                        }
                    }

                    reportTable.setModel(tempModel);
                }
            }
        });
        final Comparator<Object> noChapter = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Object[] left = (Object[]) o1;
                Object[] right = (Object[]) o2;

                String leftType = (String) left[3];
                String leftLast = (String) left[2];

                String rightType = (String) right[3];
                String rightLast = (String) right[2];

                System.out.println(leftType + "   " + rightType);
                if (leftType == null) {
                    leftType = "";
                }
                if (rightType == null) {
                    rightType = "";
                }
                if (leftLast == null) {
                    leftLast = "";
                }
                if (rightLast == null) {
                    rightLast = "";
                }

                int r = leftType.compareTo(rightType);
                if (r == 0) {
                    r = leftLast.compareTo(rightLast);
                }
                return r;
            }

        };
        final Comparator<Object> yesChapter = new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                Object[] left = (Object[]) o1;
                Object[] right = (Object[]) o2;

                String leftType = (String) left[3];
                String leftLast = (String) left[2];
                Integer leftChapter = Integer.parseInt("" + left[5]);

                String rightType = (String) right[3];
                String rightLast = (String) right[2];
                Integer rightChapter = Integer.parseInt("" + right[5]);

                System.out.println(leftType + "   " + rightType);
                if (leftType == null) {
                    leftType = "";
                }
                if (rightType == null) {
                    rightType = "";
                }
                if (leftLast == null) {
                    leftLast = "";
                }
                if (rightLast == null) {
                    rightLast = "";
                }
                if (leftChapter == null) {
                    leftChapter = 0;
                }
                if (rightChapter == null) {
                    rightChapter = 0;
                }

                if (leftChapter > rightChapter) {
                    return 1;
                } else if (rightChapter > leftChapter) {
                    return -1;
                }
                int r = leftLast.compareTo(rightLast);
                if (r != 0) {
                    return r;
                }
                r = leftType.compareTo(rightType);
                if (r != 0) {
                    return r;
                }
                return 1;
            }

        };
        reportSortOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = reportSortOrder.getSelectedIndex();
                if (n == 0) {
                    String[] tempColumn = {"ID", "First", "Last", "Type", "Conference", "Chapter"};
                    Object[][] tempData = new Object[reportTable.getRowCount()][5];
                    for (int i = 1; i <= reportTable.getRowCount() - 1; i++) {
                        for (int j = 0; j < 6; j++) {
                            tempData[i][j] = reportTable.getValueAt(i, j);
                        }
                    }
                    Arrays.sort(tempData, noChapter);
                    DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn);

                    reportTable.setModel(tempModel);

                } else if (n == 1) {
                    String[] tempColumn = {"ID", "First", "Last", "Type", "Conference", "Chapter"};
                    Object[][] tempData = new Object[reportTable.getRowCount()][5];
                    for (int i = 1; i <= reportTable.getRowCount() - 1; i++) {
                        for (int j = 0; j < 6; j++) {
                            tempData[i][j] = reportTable.getValueAt(i, j);
                        }
                    }
                    Arrays.sort(tempData, yesChapter);
                    DefaultTableModel tempModel = new DefaultTableModel(tempData, tempColumn);

                    reportTable.setModel(tempModel);
                }
            }
        });

        int hour = Calendar.getInstance().HOUR;
        if (hour > 12) {
            hour -= 12;
        }
        DateFormat dateFormat = new SimpleDateFormat(hour + ":mm");
        DateFormat secondFormat = new SimpleDateFormat(" :ss");
        DateFormat timeStamp = new SimpleDateFormat(" aa");
        Date date = new Date();
        timeLabel.setText(dateFormat.format(date));
        secondLabel.setText(secondFormat.format(date));
        stampLabel.setText(timeStamp.format(date));
        Timer timer = new Timer(100, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int hour = Calendar.getInstance().HOUR;
                if (hour > 12) {
                    hour -= 12;
                }
                DateFormat dateFormat = new SimpleDateFormat(hour + ":mm");
                DateFormat secondFormat = new SimpleDateFormat(" :ss");
                DateFormat timeStamp = new SimpleDateFormat(" aa");

                Date date = new Date();
                timeLabel.setText(dateFormat.format(date));
                secondLabel.setText(secondFormat.format(date));
                stampLabel.setText(timeStamp.format(date));
            }
        });
        timer.setInitialDelay(1600);
        timer.start();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DesktopApp.getApplication().getMainFrame();
            aboutBox = new DesktopAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DesktopApp.getApplication().show(aboutBox);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        menuPanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        selectPanelBox = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        stampLabel = new javax.swing.JLabel();
        secondLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        cardHolder = new javax.swing.JPanel();
        homePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        homeConference = new javax.swing.JButton();
        homeWorkshop = new javax.swing.JButton();
        homeReports = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        homeParticipant = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        participantStart = new javax.swing.JPanel();
        participantSearchBox = new javax.swing.JTextField();
        participantSearchButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        participantLastBox = new javax.swing.JTextField();
        participantChapterBox = new javax.swing.JTextField();
        participantConferenceBox = new javax.swing.JComboBox();
        participantTypeBox = new javax.swing.JComboBox();
        participantFirstBox = new javax.swing.JTextField();
        participantIDBox = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        participantSearchCombo = new javax.swing.JComboBox();
        participantRemove = new javax.swing.JButton();
        participantEdit = new javax.swing.JButton();
        participantSave = new javax.swing.JButton();
        participantAdd = new javax.swing.JButton();
        participantCardPanel = new javax.swing.JPanel();
        participantScrollPane = new javax.swing.JScrollPane();
        participantTable = new javax.swing.JTable();
        participantRegistration = new javax.swing.JPanel();
        text = new javax.swing.JLabel();
        registerButton = new javax.swing.JButton();
        regScrollPane = new javax.swing.JPanel();
        conferenceStart = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        conferenceTable = new javax.swing.JTable();
        participantStart1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        participantTable1 = new javax.swing.JTable();
        conferenceSearchBox = new javax.swing.JTextField();
        conferenceSearchButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        conferenceState = new javax.swing.JTextField();
        conferenceEDay = new javax.swing.JComboBox();
        conferenceEMonth = new javax.swing.JComboBox();
        conferenceCity = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        conferenceEYear = new javax.swing.JComboBox();
        conferenceSMonth = new javax.swing.JComboBox();
        conferenceSDay = new javax.swing.JComboBox();
        conferenceSYear = new javax.swing.JComboBox();
        conferenceAdd = new javax.swing.JButton();
        conferenceRemove = new javax.swing.JButton();
        conferenceEdit = new javax.swing.JButton();
        conferenceSave = new javax.swing.JButton();
        reportStart = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        reportSortBox = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        reportConferenceSelect = new javax.swing.JComboBox();
        confBox = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        reportTable = new javax.swing.JTable();
        reportSortOrder = new javax.swing.JComboBox();
        orderBox = new javax.swing.JLabel();
        workshopStart = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        workshopTable = new javax.swing.JTable();
        participantStart2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        participantTable2 = new javax.swing.JTable();
        workshopSearchBox = new javax.swing.JTextField();
        workshopSearchButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        workshopMonth = new javax.swing.JComboBox();
        workshopDay = new javax.swing.JComboBox();
        workshopYear = new javax.swing.JComboBox();
        workshopConferenceBox = new javax.swing.JComboBox();
        workshopHour = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        workshopMinute = new javax.swing.JComboBox();
        workshopPeriod = new javax.swing.JComboBox();
        workshopRemove = new javax.swing.JButton();
        workshopEdit = new javax.swing.JButton();
        workshopSave = new javax.swing.JButton();
        workshopAdd = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        workshopNameBox = new javax.swing.JTextField();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(desktopapp.DesktopApp.class).getContext().getResourceMap(DesktopView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        menuPanel.setBackground(resourceMap.getColor("menuPanel.background")); // NOI18N
        menuPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        menuPanel.setName("menuPanel"); // NOI18N
        menuPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(desktopapp.DesktopApp.class).getContext().getActionMap(DesktopView.class, this);
        jButton2.setAction(actionMap.get("addHomeIcon")); // NOI18N
        jButton2.setBackground(resourceMap.getColor("jButton2.background")); // NOI18N
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setContentAreaFilled(false);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
                jButton2AncestorRemoved(evt);
            }
        });
        menuPanel.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 42));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setName("jSeparator1"); // NOI18N
        menuPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 7, -1, 30));

        selectPanelBox.setBackground(resourceMap.getColor("selectPanelBox.background")); // NOI18N
        selectPanelBox.setFont(resourceMap.getFont("selectPanelBox.font")); // NOI18N
        selectPanelBox.setForeground(resourceMap.getColor("selectPanelBox.foreground")); // NOI18N
        selectPanelBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Home", "Participants", "Conferences", "Workshops", "Reports" }));
        selectPanelBox.setName("selectPanelBox"); // NOI18N
        selectPanelBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPanelBoxActionPerformed(evt);
            }
        });
        menuPanel.add(selectPanelBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 6, 140, 30));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N
        menuPanel.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 7, -1, 30));

        stampLabel.setFont(resourceMap.getFont("stampLabel.font")); // NOI18N
        stampLabel.setForeground(resourceMap.getColor("stampLabel.foreground")); // NOI18N
        stampLabel.setText(resourceMap.getString("stampLabel.text")); // NOI18N
        stampLabel.setName("stampLabel"); // NOI18N
        menuPanel.add(stampLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 40, 30));

        secondLabel.setFont(resourceMap.getFont("secondLabel.font")); // NOI18N
        secondLabel.setForeground(resourceMap.getColor("secondLabel.foreground")); // NOI18N
        secondLabel.setText(resourceMap.getString("secondLabel.text")); // NOI18N
        secondLabel.setName("secondLabel"); // NOI18N
        menuPanel.add(secondLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 40, 20));

        timeLabel.setFont(resourceMap.getFont("timeLabel.font")); // NOI18N
        timeLabel.setForeground(resourceMap.getColor("timeLabel.foreground")); // NOI18N
        timeLabel.setText(resourceMap.getString("timeLabel.text")); // NOI18N
        timeLabel.setName("timeLabel"); // NOI18N
        menuPanel.add(timeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, -1, -1));

        mainPanel.add(menuPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, -1));

        cardHolder.setBackground(resourceMap.getColor("cardHolder.background")); // NOI18N
        cardHolder.setName("cardHolder"); // NOI18N
        cardHolder.setLayout(new java.awt.CardLayout());

        homePanel.setBackground(resourceMap.getColor("homePanel.background")); // NOI18N
        homePanel.setName("homePanel"); // NOI18N
        homePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        homePanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, -1, -1));

        homeConference.setAction(actionMap.get("addConferenceIcon")); // NOI18N
        homeConference.setBackground(resourceMap.getColor("homeConference.background")); // NOI18N
        homeConference.setName("homeConference"); // NOI18N
        homeConference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeConferenceActionPerformed(evt);
            }
        });
        homePanel.add(homeConference, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, 170, 210));

        homeWorkshop.setAction(actionMap.get("addWorkshopIcon")); // NOI18N
        homeWorkshop.setBackground(resourceMap.getColor("homeWorkshop.background")); // NOI18N
        homeWorkshop.setName("homeWorkshop"); // NOI18N
        homeWorkshop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeWorkshopActionPerformed(evt);
            }
        });
        homePanel.add(homeWorkshop, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 100, 170, 210));

        homeReports.setAction(actionMap.get("createReportIcon")); // NOI18N
        homeReports.setBackground(resourceMap.getColor("homeReports.background")); // NOI18N
        homeReports.setName("homeReports"); // NOI18N
        homeReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeReportsActionPerformed(evt);
            }
        });
        homePanel.add(homeReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 100, 170, 210));

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        homePanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 330, -1, -1));

        homeParticipant.setAction(actionMap.get("createParticipantIcon")); // NOI18N
        homeParticipant.setBackground(resourceMap.getColor("homeParticipant.background")); // NOI18N
        homeParticipant.setName("homeParticipant"); // NOI18N
        homeParticipant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeParticipantActionPerformed(evt);
            }
        });
        homePanel.add(homeParticipant, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, 170, 210));

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        homePanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 330, -1, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        homePanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 330, -1, -1));

        cardHolder.add(homePanel, "card2");

        participantStart.setBackground(resourceMap.getColor("participantStart.background")); // NOI18N
        participantStart.setName("participantStart"); // NOI18N
        participantStart.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        participantSearchBox.setBackground(resourceMap.getColor("participantSearchBox.background")); // NOI18N
        participantSearchBox.setFont(resourceMap.getFont("participantSearchBox.font")); // NOI18N
        participantSearchBox.setText(resourceMap.getString("participantSearchBox.text")); // NOI18N
        participantSearchBox.setToolTipText(resourceMap.getString("participantSearchBox.toolTipText")); // NOI18N
        participantSearchBox.setName("participantSearchBox"); // NOI18N
        participantStart.add(participantSearchBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, 250, 36));

        participantSearchButton.setAction(actionMap.get("createSearchIcon")); // NOI18N
        participantSearchButton.setBorderPainted(false);
        participantSearchButton.setContentAreaFilled(false);
        participantSearchButton.setName("participantSearchButton"); // NOI18N
        participantStart.add(participantSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 30, 45, -1));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        participantStart.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, -1, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        participantStart.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 110, -1, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        participantStart.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, -1, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        participantStart.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 260, -1, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        participantStart.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, -1, -1));

        jLabel10.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        participantStart.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, -1, -1));

        participantLastBox.setBackground(resourceMap.getColor("participantLastBox.background")); // NOI18N
        participantLastBox.setText(resourceMap.getString("participantLastBox.text")); // NOI18N
        participantLastBox.setName("participantLastBox"); // NOI18N
        participantStart.add(participantLastBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, 150, 30));

        participantChapterBox.setBackground(resourceMap.getColor("participantChapterBox.background")); // NOI18N
        participantChapterBox.setText(resourceMap.getString("participantChapterBox.text")); // NOI18N
        participantChapterBox.setName("participantChapterBox"); // NOI18N
        participantStart.add(participantChapterBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 210, 150, 30));

        participantConferenceBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        participantConferenceBox.setName("participantConferenceBox"); // NOI18N
        participantStart.add(participantConferenceBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 280, 140, 30));

        participantTypeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        participantTypeBox.setName("participantTypeBox"); // NOI18N
        participantStart.add(participantTypeBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 280, 120, 30));

        participantFirstBox.setBackground(resourceMap.getColor("participantFirstBox.background")); // NOI18N
        participantFirstBox.setText(resourceMap.getString("participantFirstBox.text")); // NOI18N
        participantFirstBox.setName("participantFirstBox"); // NOI18N
        participantStart.add(participantFirstBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, 150, 30));

        participantIDBox.setBackground(resourceMap.getColor("participantIDBox.background")); // NOI18N
        participantIDBox.setText(resourceMap.getString("participantIDBox.text")); // NOI18N
        participantIDBox.setName("participantIDBox"); // NOI18N
        participantStart.add(participantIDBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 210, 150, 30));

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        participantStart.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        jSeparator3.setName("jSeparator3"); // NOI18N
        participantStart.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 160, 20));

        participantSearchCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ID", "First", "Last", "Type", "Conference", "Chapter" }));
        participantSearchCombo.setSelectedItem(1);
        participantSearchCombo.setName("participantSearchCombo"); // NOI18N
        participantStart.add(participantSearchCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 40, 90, 30));

        participantRemove.setAction(actionMap.get("createRemoveIcon")); // NOI18N
        participantRemove.setFont(resourceMap.getFont("participantSave.font")); // NOI18N
        participantRemove.setText(resourceMap.getString("participantRemove.text")); // NOI18N
        participantRemove.setName("participantRemove"); // NOI18N
        participantStart.add(participantRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 380, 100, 40));

        participantEdit.setAction(actionMap.get("createEditIcon")); // NOI18N
        participantEdit.setFont(resourceMap.getFont("participantSave.font")); // NOI18N
        participantEdit.setText(resourceMap.getString("participantEdit.text")); // NOI18N
        participantEdit.setName("participantEdit"); // NOI18N
        participantStart.add(participantEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 380, 100, 40));

        participantSave.setAction(actionMap.get("createSaveIcon")); // NOI18N
        participantSave.setFont(resourceMap.getFont("participantSave.font")); // NOI18N
        participantSave.setText(resourceMap.getString("participantSave.text")); // NOI18N
        participantSave.setName("participantSave"); // NOI18N
        participantStart.add(participantSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 380, 100, 40));

        participantAdd.setAction(actionMap.get("createAddIcon")); // NOI18N
        participantAdd.setFont(resourceMap.getFont("participantSave.font")); // NOI18N
        participantAdd.setText(resourceMap.getString("participantAdd.text")); // NOI18N
        participantAdd.setName("participantAdd"); // NOI18N
        participantStart.add(participantAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 90, 40));

        participantCardPanel.setBackground(resourceMap.getColor("participantCardPanel.background")); // NOI18N
        participantCardPanel.setName("participantCardPanel"); // NOI18N
        participantCardPanel.setLayout(new java.awt.CardLayout());

        participantScrollPane.setName("participantScrollPane"); // NOI18N

        participantTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First", "Last", "Type", "Conference", "Chapter"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        participantTable.setName("participantTable"); // NOI18N
        participantScrollPane.setViewportView(participantTable);
        participantTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        participantTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        participantTable.getColumnModel().getColumn(5).setPreferredWidth(30);

        participantCardPanel.add(participantScrollPane, "card2");

        participantRegistration.setBackground(resourceMap.getColor("participantRegistration.background")); // NOI18N
        participantRegistration.setName("participantRegistration"); // NOI18N

        text.setFont(resourceMap.getFont("text.font")); // NOI18N
        text.setText(resourceMap.getString("text.text")); // NOI18N
        text.setName("text"); // NOI18N

        registerButton.setFont(resourceMap.getFont("registerButton.font")); // NOI18N
        registerButton.setText(resourceMap.getString("registerButton.text")); // NOI18N
        registerButton.setName("registerButton"); // NOI18N

        regScrollPane.setName("regScrollPane"); // NOI18N
        regScrollPane.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout participantRegistrationLayout = new javax.swing.GroupLayout(participantRegistration);
        participantRegistration.setLayout(participantRegistrationLayout);
        participantRegistrationLayout.setHorizontalGroup(
            participantRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(participantRegistrationLayout.createSequentialGroup()
                .addGroup(participantRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(participantRegistrationLayout.createSequentialGroup()
                        .addGap(193, 193, 193)
                        .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(participantRegistrationLayout.createSequentialGroup()
                        .addGap(183, 183, 183)
                        .addComponent(text))
                    .addGroup(participantRegistrationLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(regScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        participantRegistrationLayout.setVerticalGroup(
            participantRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(participantRegistrationLayout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addComponent(text)
                .addGap(18, 18, 18)
                .addComponent(regScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(registerButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addContainerGap())
        );

        participantCardPanel.add(participantRegistration, "card3");

        participantStart.add(participantCardPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 90, 520, 330));

        cardHolder.add(participantStart, "card3");

        conferenceStart.setBackground(resourceMap.getColor("conferenceStart.background")); // NOI18N
        conferenceStart.setName("conferenceStart"); // NOI18N
        conferenceStart.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        conferenceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Location", "Start Date", "End Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        conferenceTable.setName("conferenceTable"); // NOI18N
        jScrollPane2.setViewportView(conferenceTable);
        conferenceTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        conferenceTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title3")); // NOI18N
        conferenceTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        conferenceTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title0")); // NOI18N
        conferenceTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        conferenceTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title1")); // NOI18N
        conferenceTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        conferenceTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title2")); // NOI18N

        conferenceStart.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 500, 310));

        participantStart1.setBackground(resourceMap.getColor("participantStart1.background")); // NOI18N
        participantStart1.setName("participantStart1"); // NOI18N
        participantStart1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        participantTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First", "Last", "Type", "Conference", "Chapter"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        participantTable1.setName("participantTable1"); // NOI18N
        jScrollPane3.setViewportView(participantTable1);
        participantTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        participantTable1.getColumnModel().getColumn(3).setPreferredWidth(40);
        participantTable1.getColumnModel().getColumn(5).setPreferredWidth(30);

        participantStart1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 499, 312));

        conferenceSearchBox.setBackground(resourceMap.getColor("conferenceSearchBox.background")); // NOI18N
        conferenceSearchBox.setFont(resourceMap.getFont("conferenceSearchBox.font")); // NOI18N
        conferenceSearchBox.setToolTipText(resourceMap.getString("conferenceSearchBox.toolTipText")); // NOI18N
        conferenceSearchBox.setName("conferenceSearchBox"); // NOI18N
        participantStart1.add(conferenceSearchBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 40, 252, 36));

        conferenceSearchButton.setAction(actionMap.get("createSearchIcon")); // NOI18N
        conferenceSearchButton.setBorderPainted(false);
        conferenceSearchButton.setContentAreaFilled(false);
        conferenceSearchButton.setName("conferenceSearchButton"); // NOI18N
        participantStart1.add(conferenceSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 30, 45, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        participantStart1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, -1, -1));

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        participantStart1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 110, -1, -1));

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        participantStart1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, -1, -1));

        jLabel17.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        participantStart1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, -1, -1));

        conferenceState.setBackground(resourceMap.getColor("conferenceState.background")); // NOI18N
        conferenceState.setName("conferenceState"); // NOI18N
        participantStart1.add(conferenceState, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, 150, 30));

        conferenceEDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Day...", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        conferenceEDay.setName("conferenceEDay"); // NOI18N
        participantStart1.add(conferenceEDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 280, 60, 30));

        conferenceEMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Month...", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        conferenceEMonth.setName("conferenceEMonth"); // NOI18N
        participantStart1.add(conferenceEMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, 110, 30));

        conferenceCity.setBackground(resourceMap.getColor("conferenceCity.background")); // NOI18N
        conferenceCity.setName("conferenceCity"); // NOI18N
        participantStart1.add(conferenceCity, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, 150, 30));

        jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        participantStart1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, -1, -1));

        jSeparator4.setName("jSeparator4"); // NOI18N
        participantStart1.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 160, 20));

        conferenceEYear.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Year...", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));
        conferenceEYear.setName("conferenceEYear"); // NOI18N
        participantStart1.add(conferenceEYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 280, 80, 30));

        conferenceSMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Month...", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        conferenceSMonth.setName("conferenceSMonth"); // NOI18N
        participantStart1.add(conferenceSMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, 110, 30));

        conferenceSDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Day...", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        conferenceSDay.setName("conferenceSDay"); // NOI18N
        participantStart1.add(conferenceSDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 210, 60, 30));

        conferenceSYear.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Year...", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));
        conferenceSYear.setName("conferenceSYear"); // NOI18N
        participantStart1.add(conferenceSYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 210, 80, 30));

        conferenceAdd.setAction(actionMap.get("createAddIcon")); // NOI18N
        conferenceAdd.setFont(resourceMap.getFont("conferenceAdd.font")); // NOI18N
        conferenceAdd.setText(resourceMap.getString("conferenceAdd.text")); // NOI18N
        conferenceAdd.setName("conferenceAdd"); // NOI18N
        participantStart1.add(conferenceAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 90, 40));

        conferenceRemove.setAction(actionMap.get("createRemoveIcon")); // NOI18N
        conferenceRemove.setFont(resourceMap.getFont("conferenceRemove.font")); // NOI18N
        conferenceRemove.setText(resourceMap.getString("conferenceRemove.text")); // NOI18N
        conferenceRemove.setName("conferenceRemove"); // NOI18N
        participantStart1.add(conferenceRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 380, 100, 40));

        conferenceEdit.setAction(actionMap.get("createEditIcon")); // NOI18N
        conferenceEdit.setFont(resourceMap.getFont("conferenceEdit.font")); // NOI18N
        conferenceEdit.setText(resourceMap.getString("conferenceEdit.text")); // NOI18N
        conferenceEdit.setName("conferenceEdit"); // NOI18N
        participantStart1.add(conferenceEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 380, 100, 40));

        conferenceSave.setAction(actionMap.get("createSaveIcon")); // NOI18N
        conferenceSave.setFont(resourceMap.getFont("conferenceSave.font")); // NOI18N
        conferenceSave.setText(resourceMap.getString("conferenceSave.text")); // NOI18N
        conferenceSave.setName("conferenceSave"); // NOI18N
        participantStart1.add(conferenceSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 380, 100, 40));

        conferenceStart.add(participantStart1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, 430));

        cardHolder.add(conferenceStart, "card4");

        reportStart.setBackground(resourceMap.getColor("reportStart.background")); // NOI18N
        reportStart.setName("reportStart"); // NOI18N

        jButton3.setAction(actionMap.get("createPrinterIcon")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        reportSortBox.setFont(resourceMap.getFont("reportSortBox.font")); // NOI18N
        reportSortBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select Sort Style...", "Conference", "Registration" }));
        reportSortBox.setName("reportSortBox"); // NOI18N

        jLabel23.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        reportConferenceSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Conferences Go Here..." }));
        reportConferenceSelect.setName("reportConferenceSelect"); // NOI18N

        confBox.setFont(resourceMap.getFont("confBox.font")); // NOI18N
        confBox.setText(resourceMap.getString("confBox.text")); // NOI18N
        confBox.setName("confBox"); // NOI18N

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        reportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First", "Last", "Type", "Conference", "Chapter"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        reportTable.setName("reportTable"); // NOI18N
        jScrollPane10.setViewportView(reportTable);
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        reportTable.getColumnModel().getColumn(5).setPreferredWidth(30);

        reportSortOrder.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Type, Last Name", "Chapter, Type, Last Name" }));
        reportSortOrder.setName("reportSortOrder"); // NOI18N

        orderBox.setFont(resourceMap.getFont("orderBox.font")); // NOI18N
        orderBox.setText(resourceMap.getString("orderBox.text")); // NOI18N
        orderBox.setName("orderBox"); // NOI18N

        javax.swing.GroupLayout reportStartLayout = new javax.swing.GroupLayout(reportStart);
        reportStart.setLayout(reportStartLayout);
        reportStartLayout.setHorizontalGroup(
            reportStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportStartLayout.createSequentialGroup()
                .addGroup(reportStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportStartLayout.createSequentialGroup()
                        .addGroup(reportStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(reportStartLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(jLabel23))
                            .addGroup(reportStartLayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addGroup(reportStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(confBox)
                                    .addComponent(reportSortBox, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(reportStartLayout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(orderBox)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportStartLayout.createSequentialGroup()
                        .addContainerGap(129, Short.MAX_VALUE)
                        .addGroup(reportStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(reportSortOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reportConferenceSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(112, 112, 112)))
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportStartLayout.createSequentialGroup()
                .addContainerGap(915, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        reportStartLayout.setVerticalGroup(
            reportStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportStartLayout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reportSortBox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(orderBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportSortOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(confBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportConferenceSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(97, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportStartLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        cardHolder.add(reportStart, "card6");

        workshopStart.setBackground(resourceMap.getColor("workshopStart.background")); // NOI18N
        workshopStart.setName("workshopStart"); // NOI18N
        workshopStart.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        workshopTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Conference", "Date", "Start Time"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        workshopTable.setName("workshopTable"); // NOI18N
        jScrollPane4.setViewportView(workshopTable);
        workshopTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        workshopTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title3")); // NOI18N
        workshopTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        workshopTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title0")); // NOI18N
        workshopTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        workshopTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title1")); // NOI18N
        workshopTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        workshopTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("conferenceTable.columnModel.title2")); // NOI18N
        workshopTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("workshopTable.columnModel.title4")); // NOI18N

        workshopStart.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 500, 310));

        participantStart2.setBackground(resourceMap.getColor("participantStart2.background")); // NOI18N
        participantStart2.setName("participantStart2"); // NOI18N
        participantStart2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        participantTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First", "Last", "Type", "Conference", "Chapter"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        participantTable2.setName("participantTable2"); // NOI18N
        jScrollPane5.setViewportView(participantTable2);
        participantTable2.getColumnModel().getColumn(0).setPreferredWidth(20);
        participantTable2.getColumnModel().getColumn(3).setPreferredWidth(40);
        participantTable2.getColumnModel().getColumn(5).setPreferredWidth(30);

        participantStart2.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 499, 312));

        workshopSearchBox.setBackground(resourceMap.getColor("workshopSearchBox.background")); // NOI18N
        workshopSearchBox.setFont(resourceMap.getFont("workshopSearchBox.font")); // NOI18N
        workshopSearchBox.setToolTipText(resourceMap.getString("workshopSearchBox.toolTipText")); // NOI18N
        workshopSearchBox.setName("workshopSearchBox"); // NOI18N
        participantStart2.add(workshopSearchBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 40, 252, 36));

        workshopSearchButton.setAction(actionMap.get("createSearchIcon")); // NOI18N
        workshopSearchButton.setBorderPainted(false);
        workshopSearchButton.setContentAreaFilled(false);
        workshopSearchButton.setName("workshopSearchButton"); // NOI18N
        participantStart2.add(workshopSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 30, 45, -1));

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        participantStart2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, -1, -1));

        jLabel19.setFont(resourceMap.getFont("jLabel19.font")); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        participantStart2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 290, -1, -1));

        jLabel20.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N
        participantStart2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, -1, -1));

        jLabel21.setFont(resourceMap.getFont("jLabel21.font")); // NOI18N
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N
        participantStart2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        jSeparator5.setName("jSeparator5"); // NOI18N
        participantStart2.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 160, 20));

        workshopMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Month...", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        workshopMonth.setName("workshopMonth"); // NOI18N
        participantStart2.add(workshopMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, 110, 30));

        workshopDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Day...", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        workshopDay.setName("workshopDay"); // NOI18N
        participantStart2.add(workshopDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 240, 60, 30));

        workshopYear.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Year...", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));
        workshopYear.setName("workshopYear"); // NOI18N
        participantStart2.add(workshopYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 240, 80, 30));

        workshopConferenceBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        workshopConferenceBox.setName("workshopConferenceBox"); // NOI18N
        participantStart2.add(workshopConferenceBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 170, 160, 30));

        workshopHour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Hour", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        workshopHour.setName("workshopHour"); // NOI18N
        participantStart2.add(workshopHour, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 320, 60, 30));

        jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        participantStart2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 310, -1, 40));

        workshopMinute.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Day", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        workshopMinute.setName("workshopMinute"); // NOI18N
        participantStart2.add(workshopMinute, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 320, 60, 30));

        workshopPeriod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " Period", "AM", "PM" }));
        workshopPeriod.setName("workshopPeriod"); // NOI18N
        participantStart2.add(workshopPeriod, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 320, 60, 30));

        workshopRemove.setAction(actionMap.get("createRemoveIcon")); // NOI18N
        workshopRemove.setFont(resourceMap.getFont("conferenceRemove.font")); // NOI18N
        workshopRemove.setText(resourceMap.getString("workshopRemove.text")); // NOI18N
        workshopRemove.setName("workshopRemove"); // NOI18N
        participantStart2.add(workshopRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 380, 100, 40));

        workshopEdit.setAction(actionMap.get("createEditIcon")); // NOI18N
        workshopEdit.setFont(resourceMap.getFont("workshopAdd.font")); // NOI18N
        workshopEdit.setText(resourceMap.getString("workshopEdit.text")); // NOI18N
        workshopEdit.setName("workshopEdit"); // NOI18N
        participantStart2.add(workshopEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 380, 100, 40));

        workshopSave.setAction(actionMap.get("createSaveIcon")); // NOI18N
        workshopSave.setFont(resourceMap.getFont("workshopAdd.font")); // NOI18N
        workshopSave.setText(resourceMap.getString("workshopSave.text")); // NOI18N
        workshopSave.setName("workshopSave"); // NOI18N
        participantStart2.add(workshopSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 380, 110, 40));

        workshopAdd.setAction(actionMap.get("createAddIcon")); // NOI18N
        workshopAdd.setFont(resourceMap.getFont("workshopAdd.font")); // NOI18N
        workshopAdd.setText(resourceMap.getString("workshopAdd.text")); // NOI18N
        workshopAdd.setName("workshopAdd"); // NOI18N
        participantStart2.add(workshopAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 90, 40));

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N
        participantStart2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, -1, -1));

        workshopNameBox.setBackground(resourceMap.getColor("workshopNameBox.background")); // NOI18N
        workshopNameBox.setFont(resourceMap.getFont("workshopNameBox.font")); // NOI18N
        workshopNameBox.setText(resourceMap.getString("workshopNameBox.text")); // NOI18N
        workshopNameBox.setName("workshopNameBox"); // NOI18N
        participantStart2.add(workshopNameBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 150, 30));

        workshopStart.add(participantStart2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, 430));

        cardHolder.add(workshopStart, "card5");

        mainPanel.add(cardHolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 1020, 430));

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1020, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 850, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        setComponent(mainPanel);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2AncestorRemoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jButton2AncestorRemoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2AncestorRemoved

    private void homeParticipantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeParticipantActionPerformed
        selectPanelBox.setSelectedIndex(1);
    }//GEN-LAST:event_homeParticipantActionPerformed

    private void homeConferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeConferenceActionPerformed
        selectPanelBox.setSelectedIndex(2);
    }//GEN-LAST:event_homeConferenceActionPerformed

    private void homeWorkshopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeWorkshopActionPerformed
        selectPanelBox.setSelectedIndex(3);
    }//GEN-LAST:event_homeWorkshopActionPerformed

    private void homeReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeReportsActionPerformed
        selectPanelBox.setSelectedIndex(4);
    }//GEN-LAST:event_homeReportsActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        selectPanelBox.setSelectedIndex(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void selectPanelBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPanelBoxActionPerformed
        int n = selectPanelBox.getSelectedIndex() + 2;
        switchTo("card" + n);
    }//GEN-LAST:event_selectPanelBoxActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            reportTable.print();
        } catch (PrinterException ex) {
            Logger.getLogger(DesktopView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    public void switchTo(String name) {
        ((CardLayout) cardHolder.getLayout()).show(cardHolder, name);
        cardHolder.requestFocus();
    }

    @Action
    public void addHomeIcon() {
    }

    @Action
    public void addConferenceIcon() {
    }

    @Action
    public void addWorkshopIcon() {
    }

    @Action
    public void createReportIcon() {
    }

    @Action
    public void createParticipantIcon() {
    }

    @Action
    public void createSearchIcon() {
    }

    @Action
    public void createAddIcon() {
    }

    @Action
    public void createRemoveIcon() {
    }

    @Action
    public void createPrinterIcon() {
    }

    @Action
    public void createLargePrinterIcon() {
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardHolder;
    private javax.swing.JLabel confBox;
    private javax.swing.JButton conferenceAdd;
    private javax.swing.JTextField conferenceCity;
    private javax.swing.JComboBox conferenceEDay;
    private javax.swing.JComboBox conferenceEMonth;
    private javax.swing.JComboBox conferenceEYear;
    private javax.swing.JButton conferenceEdit;
    private javax.swing.JButton conferenceRemove;
    private javax.swing.JComboBox conferenceSDay;
    private javax.swing.JComboBox conferenceSMonth;
    private javax.swing.JComboBox conferenceSYear;
    private javax.swing.JButton conferenceSave;
    private javax.swing.JTextField conferenceSearchBox;
    private javax.swing.JButton conferenceSearchButton;
    private javax.swing.JPanel conferenceStart;
    private javax.swing.JTextField conferenceState;
    private javax.swing.JTable conferenceTable;
    private javax.swing.JButton homeConference;
    private javax.swing.JPanel homePanel;
    private javax.swing.JButton homeParticipant;
    private javax.swing.JButton homeReports;
    private javax.swing.JButton homeWorkshop;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JLabel orderBox;
    private javax.swing.JButton participantAdd;
    private javax.swing.JPanel participantCardPanel;
    private javax.swing.JTextField participantChapterBox;
    private javax.swing.JComboBox participantConferenceBox;
    private javax.swing.JButton participantEdit;
    private javax.swing.JTextField participantFirstBox;
    private javax.swing.JTextField participantIDBox;
    private javax.swing.JTextField participantLastBox;
    private javax.swing.JPanel participantRegistration;
    private javax.swing.JButton participantRemove;
    private javax.swing.JButton participantSave;
    private javax.swing.JScrollPane participantScrollPane;
    private javax.swing.JTextField participantSearchBox;
    private javax.swing.JButton participantSearchButton;
    private javax.swing.JComboBox participantSearchCombo;
    private javax.swing.JPanel participantStart;
    private javax.swing.JPanel participantStart1;
    private javax.swing.JPanel participantStart2;
    private javax.swing.JTable participantTable;
    private javax.swing.JTable participantTable1;
    private javax.swing.JTable participantTable2;
    private javax.swing.JComboBox participantTypeBox;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel regScrollPane;
    private javax.swing.JButton registerButton;
    private javax.swing.JComboBox reportConferenceSelect;
    private javax.swing.JComboBox reportSortBox;
    private javax.swing.JComboBox reportSortOrder;
    private javax.swing.JPanel reportStart;
    private javax.swing.JTable reportTable;
    private javax.swing.JLabel secondLabel;
    private javax.swing.JComboBox selectPanelBox;
    private javax.swing.JLabel stampLabel;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel text;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JButton workshopAdd;
    private javax.swing.JComboBox workshopConferenceBox;
    private javax.swing.JComboBox workshopDay;
    private javax.swing.JButton workshopEdit;
    private javax.swing.JComboBox workshopHour;
    private javax.swing.JComboBox workshopMinute;
    private javax.swing.JComboBox workshopMonth;
    private javax.swing.JTextField workshopNameBox;
    private javax.swing.JComboBox workshopPeriod;
    private javax.swing.JButton workshopRemove;
    private javax.swing.JButton workshopSave;
    private javax.swing.JTextField workshopSearchBox;
    private javax.swing.JButton workshopSearchButton;
    private javax.swing.JPanel workshopStart;
    private javax.swing.JTable workshopTable;
    private javax.swing.JComboBox workshopYear;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    @Action
    public void createPlusIcon() {
    }

    @Action
    public void createEditIcon() {
    }

    @Action
    public void createSaveIcon() {
    }
}

class Printer implements Printable, ActionListener {

    public int print(Graphics g, PageFormat pf, int page) throws
            PrinterException {

        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        g.drawString("Hello World!", 100, 100);

        return PAGE_EXISTS;
    }

    public void actionPerformed(ActionEvent e) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {

            }
        }
    }
}
