package gui;

import models.Airport;
import models.Flight;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimulationPanel extends Panel {

    private List<Flight> flights;
    private List<Plane> planes = new ArrayList<>();
    private List<Flight> startedFlights = new ArrayList<>(); //da znamo koji su već poletjeli

    private boolean running = false;
    private boolean paused = false;

    private Button startBtn, pauseBtn, resetBtn;
    private Label clockLabel, statusLabel;

    private LocalTime simTime = LocalTime.of(0, 0); // simulacijski sat
    private static final int REAL_STEP_MS = 200;    // svaka 0.2 sekunde
    private static final int SIM_INCREMENT_MIN = 2; // +2 min po iteraciji

    public SimulationPanel(List<Flight> flights) {
        this.flights = flights;
        setLayout(new BorderLayout());

        // -------- kontrole --------
        Panel controls = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        startBtn = new Button("Start");
        pauseBtn = new Button("Pause");
        resetBtn = new Button("Reset");
        clockLabel = new Label("Time: 00:00");
        statusLabel = new Label("Status: Ready");

        controls.add(startBtn);
        controls.add(pauseBtn);
        controls.add(resetBtn);
        controls.add(clockLabel);
        add(controls, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> startSimulation());
        pauseBtn.addActionListener(e -> pauseSimulation());
        resetBtn.addActionListener(e -> resetSimulation());
    }

    // ---------------- START SIMULATION ----------------
    private void startSimulation() {
        if (running) return;
        if (flights == null || flights.isEmpty()) {
            statusLabel.setText("Status: No flights to simulate");
            return;
        }

        running = true;
        paused = false;
        simTime = LocalTime.of(0, 0);
        planes.clear();
        startedFlights.clear();
        statusLabel.setText("Status: Running");

        Thread simThread = new Thread(() -> {
            while (running) {
                if (!paused) {
                    // 1️ ažuriraj simulacijsko vreme
                    simTime = simTime.plusMinutes(SIM_INCREMENT_MIN);
                    clockLabel.setText("Time: " + simTime);

                    // 2️ poleti novi avioni samo jednom
                    for (Flight f : flights) {
                        if (!startedFlights.contains(f) && !simTime.isBefore(f.getDepartureTime())) {
                            planes.add(new Plane(f));
                            startedFlights.add(f);
                            System.out.println("Flight " + f.getDep().getCode() + "→" +
                                    f.getArr().getCode() + " took off at " + simTime);
                        }
                    }

                    // 3️ ažuriraj pozicije postojećih aviona
                    Iterator<Plane> it = planes.iterator();
                    while (it.hasNext()) {
                        Plane p = it.next();
                        p.update(simTime);
                        if (p.hasLanded()) {
                            System.out.println("Flight " + p.getFlight().getDep().getCode() + "→" +
                                    p.getFlight().getArr().getCode() + " landed at " + simTime);
                            it.remove();
                        }
                    }

                    repaint();

                    // 4️ proveri da li je sve završeno
                    boolean allStarted = startedFlights.size() == flights.size();
                    boolean allLanded = planes.isEmpty();
                    if (allStarted && allLanded) {
                        running = false;
                        statusLabel.setText("Status: Simulation finished");
                        System.out.println("All flights completed!");
                    }
                }

                try { Thread.sleep(REAL_STEP_MS); } catch (InterruptedException ignored) {}
            }
        });

        simThread.setDaemon(true);
        simThread.start();
    }

    private void pauseSimulation() {
        if (!running) return;
        paused = !paused;
        pauseBtn.setLabel(paused ? "Resume" : "Pause");
        statusLabel.setText(paused ? "Status: Paused" : "Status: Running");
    }

    private void resetSimulation() {
        running = false;
        paused = false;
        planes.clear();
        startedFlights.clear();
        simTime = LocalTime.of(0, 0);
        clockLabel.setText("Time: 00:00");
        statusLabel.setText("Status: Ready");
        repaint();
    }

    // ---------------- RENDER ----------------
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(180, 210, 255));
        g.fillRect(0, 0, getWidth(), getHeight());

        double scaleX = getWidth() / 180.0;
        double scaleY = getHeight() / 180.0;

        // ose
        g.setColor(Color.GRAY);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        // rute
        if (flights != null) {
            g.setColor(Color.DARK_GRAY);
            for (Flight f : flights) {
                Airport d = f.getDep(), a = f.getArr();
                int x1 = (int) ((d.getX() + 90) * scaleX);
                int y1 = (int) ((90 - d.getY()) * scaleY);
                int x2 = (int) ((a.getX() + 90) * scaleX);
                int y2 = (int) ((90 - a.getY()) * scaleY);
                g.drawLine(x1, y1, x2, y2);
                g.fillRect(x1 - 3, y1 - 3, 6, 6);
                g.fillRect(x2 - 3, y2 - 3, 6, 6);
            }
        }

        // avioni
        for (Plane p : planes)
            p.draw(g, scaleX, scaleY);
    }

    // ---------------- PLANE CLASS ----------------
    private static class Plane {
        private final Flight flight;
        private final LocalTime departureTime;
        private final int duration;

        private boolean flying = false;
        private boolean landed = false;

        private double x, y; // trenutne koordinate
        private double progress = 0.0;
        private int depX, depY, arrX, arrY;

        public Plane(Flight f) {
            this.flight = f;
            this.departureTime = f.getDepartureTime();
            this.duration = f.getFlightDuration();
        }

        public Flight getFlight() { return flight; }

        public void update(LocalTime simTime) {
            if (landed) return;

            if (!flying && !simTime.isBefore(departureTime)) {
                flying = true;
                Airport d = flight.getDep();
                Airport a = flight.getArr();
                depX = d.getX();
                depY = d.getY();
                arrX = a.getX();
                arrY = a.getY();
            }

            if (flying) {
                progress += (SimulationPanel.SIM_INCREMENT_MIN / (double) duration);
                if (progress >= 1.0) {
                    progress = 1.0;
                    landed = true;
                }

                // izračunaj poziciju
                double dx = arrX - depX;
                double dy = arrY - depY;
                x = depX + dx * progress;
                y = depY + dy * progress;
            }
        }

        public boolean hasLanded() { return landed; }

        public void draw(Graphics g, double scaleX, double scaleY) {
            if (!flying) return;
            int px = (int) ((x + 90) * scaleX);
            int py = (int) ((90 - y) * scaleY);
            g.setColor(Color.BLUE);
            g.fillOval(px - 5, py - 5, 10, 10);
        }
    }
}
