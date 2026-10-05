package gui;

import models.Airport;
import models.Flight;
import logic.FileManager;
import logic.Validator;
import exceptions.FileException;
import exceptions.ValidationException;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AirportPanel extends Panel {
	

    // polja za unos aerodroma
    private TextField nameField, codeField, xField, yField;
    private Button addAirportBtn,loadair,storeair;

    // polja za unos letova
    private Choice fromChoice, toChoice;
    private TextField depTimeField, durationField;
    private Button addFlightBtn,loadfl,storefl;

    // liste podataka
    private final List<Airport> airports = new ArrayList<>();
    private final List<Flight> flights = new ArrayList<>();

    // novi paneli za ispis
    private TextArea airportTextArea;
    private TextArea flightTextArea;

    public AirportPanel() {
        setLayout(new BorderLayout(10, 10));
        
        

        // ---------- GORNJI PANEL: forme ----------
        Panel formPanel = new Panel(new GridLayout(1, 2, 20, 10));
        formPanel.add(createAirportForm());
        formPanel.add(createFlightForm());
        add(formPanel, BorderLayout.NORTH);

        // ---------- DONJI PANEL: prikaz aerodroma i letova ----------
        Panel displayPanel = new Panel(new GridLayout(1, 2, 10, 10));
        displayPanel.setBackground(new Color(230, 235, 245));

        airportTextArea = new TextArea("=== Airports ===\n", 10, 30, TextArea.SCROLLBARS_VERTICAL_ONLY);
        flightTextArea  = new TextArea("=== Flights ===\n", 10, 30, TextArea.SCROLLBARS_VERTICAL_ONLY);

        airportTextArea.setEditable(false);
        flightTextArea.setEditable(false);

        displayPanel.add(airportTextArea);
        displayPanel.add(flightTextArea);
        add(displayPanel, BorderLayout.CENTER);
    }

    // -------------------- Forme --------------------

    private Panel createAirportForm() {
        Panel left = new Panel(new GridLayout(6, 2, 5, 5));
        left.setBackground(new Color(235, 240, 250));

        left.add(new Label("Airport name:"));
        nameField = new TextField();
        left.add(nameField);

        left.add(new Label("Code (3 letters):"));
        codeField = new TextField();
        left.add(codeField);

        left.add(new Label("X coordinate (-90–90):"));
        xField = new TextField();
        left.add(xField);

        left.add(new Label("Y coordinate (-90–90):"));
        yField = new TextField();
        left.add(yField);

        left.add(new Label(""));
        addAirportBtn = new Button("Add Airport");
        left.add(addAirportBtn);
        
        loadair=new Button("Load CSV");
        left.add(loadair);
        storeair=new Button("Save CSV");
        left.add(storeair);
        
        loadair.addActionListener(e->{
			try {
				FileManager.loadAirports("ulaz");
			} catch (FileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        storeair.addActionListener(e->{
			try {
				FileManager.saveAirports(getAirports(), "izlaz");
			} catch (FileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        addAirportBtn.addActionListener(e -> onAddAirport());
        return left;
    }

    private Panel createFlightForm() {
        Panel right = new Panel(new GridLayout(6, 2, 5, 5));
        right.setBackground(new Color(235, 240, 250));

        right.add(new Label("From airport:"));
        fromChoice = new Choice();
        right.add(fromChoice);

        right.add(new Label("To airport:"));
        toChoice = new Choice();
        right.add(toChoice);

        right.add(new Label("Departure time (HH:mm):"));
        depTimeField = new TextField("08:00");
        right.add(depTimeField);

        right.add(new Label("Duration (min):"));
        durationField = new TextField("60");
        right.add(durationField);

        right.add(new Label(""));
        addFlightBtn = new Button("Add Flight");
        right.add(addFlightBtn);

        loadfl=new Button("Load CSV");
        storefl=new Button("Save CSV");
        right.add(loadfl);
        right.add(storefl);
        
        loadfl.addActionListener(e->{
			try {
				FileManager.loadFlights("ulaz1", airports);
			} catch (FileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        storefl.addActionListener(e->{
			try {
				FileManager.saveFlights(flights, "izlaz1");
			} catch (FileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        addFlightBtn.addActionListener(e -> onAddFlight());
        return right;
    }

    // -------------------- Logika --------------------

    private void onAddAirport() {
        try {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim();
            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());

            Validator.validateAirport(new Airport(name, code, x, y), airports);
            Airport a = new Airport(name, code, x, y);
            airports.add(a);
            refreshChoices();
            refreshAirportText();

            // reset
            nameField.setText("");
            codeField.setText("");
            xField.setText("");
            yField.setText("");

        } catch (ValidationException ex) {
            showInfo(ex.getMessage());
        } catch (NumberFormatException nfe) {
            showInfo("Coordinates must be numeric.");
        } catch (Exception ex) {
            showInfo("Error adding airport: " + ex.getMessage());
        }
    }

    private void onAddFlight() {
        try {
            if (airports.size() < 2)
                throw new ValidationException("WARNING","You need at least two airports.");

            Airport from = airports.get(fromChoice.getSelectedIndex());
            Airport to   = airports.get(toChoice.getSelectedIndex());

            LocalTime depTime;
            try {
                depTime = LocalTime.parse(depTimeField.getText().trim());
            } catch (DateTimeParseException dtpe) {
                throw new ValidationException("WARNING","Invalid time format (HH:mm).");
            }

            int duration = Integer.parseInt(durationField.getText().trim());
            if (duration <= 0)
                throw new ValidationException("WARNING","Duration must be positive.");

            Flight f = new Flight(from, to, depTime, duration);
            Validator.validateFlight(f);
            flights.add(f);
            updateFlights();
            //MapPanel.setFlights(flights);
            
            refreshFlightText();

        } catch (ValidationException ex) {
            showInfo(ex.getMessage());
        } catch (NumberFormatException nfe) {
            showInfo("Duration must be a valid integer.");
        } catch (Exception ex) {
            showInfo("Error adding flight: " + ex.getMessage());
        }
    }

    private void refreshChoices() {
        fromChoice.removeAll();
        toChoice.removeAll();
        for (Airport a : airports) {
            fromChoice.add(a.getCode());
            toChoice.add(a.getCode());
        }
    }

    private void refreshAirportText() {
        airportTextArea.setText("=== Airports ===\n");
        for (Airport a : airports)
            airportTextArea.append(a.toString() + "\n");
    }

    private void refreshFlightText() {
        flightTextArea.setText("=== Flights ===\n");
        for (Flight f : flights)
            flightTextArea.append(f.toString() + "\n");
    }

    // -------------------- Dijalog --------------------

    private void showInfo(String msg) {
        Dialog d = new Dialog((Frame) null, "Info", true);
        d.setLayout(new BorderLayout(10, 10));
        d.add(new Label(msg, Label.CENTER), BorderLayout.CENTER);
        Button ok = new Button("OK");
        ok.addActionListener(e -> d.dispose());
        Panel p = new Panel();
        p.add(ok);
        d.add(p, BorderLayout.SOUTH);
        d.setSize(400, 120);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    //-------------------------
    private void updateFlights() {
        // grupišemo po polaznom aerodromu
        for (Airport dep : airports) {
            List<Flight> fromDep = new ArrayList<>();
            for (Flight f : flights)
                if (f.getDep().equals(dep))
                    fromDep.add(f);

            // sortiraj po vremenu
            fromDep.sort(Comparator.comparing(Flight::getDepartureTime));

            // ako ima više od jednog – poslednji novi se pomera
            LocalTime lastTime = null;
            for (Flight f : fromDep) {
                if (lastTime == null) {
                    lastTime = f.getDepartureTime();
                } else {
                    if (!f.getDepartureTime().isAfter(lastTime))
                        f.setDepartureTime(lastTime.plusMinutes(10));
                    lastTime = f.getDepartureTime();
                }
            }
        }
    }


    // -------------------- Getter-i --------------------

    public List<Airport> getAirports() { return airports; }
    public List<Flight> getFlights() { return flights; }
}
