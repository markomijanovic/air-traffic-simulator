package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import models.Airport;
import models.Flight;

public class MapPanel extends Panel {
    private List<Airport> airports;
    
    private List<Boolean> visibleAirports;
    private List<Airport> selectedAirports = new ArrayList<>();
    private List<Flight> flights=new ArrayList<>();
    private boolean blinking = false;
    private AppTimer timer;

    private Panel sidePanel;
    private MapCanvas mapCanvas;

    public MapPanel(List<Airport> airports,List<Flight>flights, AppTimer timer) {
        this.airports = airports;
        this.flights=flights;
        this.timer = timer;
        this.visibleAirports = new ArrayList<>();

        for (Airport a : airports)
            visibleAirports.add(true);

        setLayout(new BorderLayout());

        // centralni dio - mapa
        mapCanvas = new MapCanvas();
        add(mapCanvas, BorderLayout.CENTER);

        // desni dio - checkbox lista
        sidePanel = new Panel();
        sidePanel.setLayout(new GridLayout(airports.size(), 1, 5, 5));
        refreshSidePanel();
        add(sidePanel, BorderLayout.EAST);

        // klik na mapu - selektuj / deselektuj više aerodroma
        mapCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Airport clicked = findClickedAirport(e.getX(), e.getY());
                if (clicked != null) {
                    if (selectedAirports.contains(clicked)) {
                        selectedAirports.remove(clicked);
                        if (selectedAirports.isEmpty()) timer.ContinueWork();
                    } else {
                        selectedAirports.add(clicked);
                        timer.pauseTimer();
                    }
                    mapCanvas.repaint();
                }
            }
        });

        // demonska nit za treperenje svih selektovanih
        Thread blinkThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(500);
                    blinking = !blinking;
                    if (!selectedAirports.isEmpty())
                        mapCanvas.repaint();
                }
            } catch (InterruptedException ignored) {}
        });
        blinkThread.setDaemon(true); // automatski se gasi
        blinkThread.start();
    }

    /** 🔹 Popunjava listu checkbox-ova */
    private void refreshSidePanel() {
        sidePanel.removeAll();
        for (int i = 0; i < airports.size(); i++) {
            Airport a = airports.get(i);
            Checkbox cb = new Checkbox(a.getCode() + " (" + a.getX() + ", " + a.getY() + ")", visibleAirports.get(i));
            int index = i;
            cb.addItemListener(e -> {
                visibleAirports.set(index, cb.getState());
                mapCanvas.repaint();
            });
            sidePanel.add(cb);
        }
        sidePanel.validate();
        sidePanel.repaint();
    }

    /** 🔹 Unutrašnja klasa koja crta mapu */
    private class MapCanvas extends Canvas {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            try {
                g.setColor(new Color(180, 210, 255));
                g.fillRect(0, 0, getWidth(), getHeight());

                // ose
                g.setColor(Color.GRAY);
                g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
                g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

                if (airports == null || airports.isEmpty()) {
                    g.setColor(Color.DARK_GRAY);
                    g.drawString("NO AIRPORTS", getWidth() / 2 - 30, getHeight() / 2);
                    return;
                }
                if (flights != null && !flights.isEmpty()) {
                    g.setColor(Color.yellow);
                    double scaleX = getWidth() / 180.0;
                    double scaleY = getHeight() / 180.0;

                    for (Flight f : flights) {
                        Airport from = f.getDep();
                        Airport to = f.getArr();
                        if (from == null || to == null) continue;

                        int x1 = (int) ((from.getX() + 90) * scaleX);
                        int y1 = (int) ((90 - from.getY()) * scaleY);
                        int x2 = (int) ((to.getX() + 90) * scaleX);
                        int y2 = (int) ((90 - to.getY()) * scaleY);

                        g.drawLine(x1, y1, x2, y2);
                    }
                }
                double scaleX = getWidth() / 180.0;
                double scaleY = getHeight() / 180.0;

                for (int i = 0; i < airports.size(); i++) {
                    if (!visibleAirports.get(i)) continue;
                    Airport a = airports.get(i);

                    int x = (int) ((a.getX() + 90) * scaleX);
                    int y = (int) ((90 - a.getY()) * scaleY);

                    if (selectedAirports.contains(a))
                        g.setColor(blinking ? Color.RED : Color.PINK);
                    else
                        g.setColor(Color.LIGHT_GRAY);

                    g.fillRect(x - 5, y - 5, 10, 10);
                    g.setColor(Color.BLACK);
                    g.drawRect(x - 5, y - 5, 10, 10);
                    g.setFont(new Font("Arial", Font.PLAIN, 14));
                    g.drawString(a.getCode(), x + 12, y - 2);
                }

            } catch (Exception ex) {
                g.setColor(Color.RED);
                g.drawString("Error drawing map: " + ex.getMessage(), 10, 20);
            }
        }
    }

    /** 🔹 Pronalazi kliknuti aerodrom */
    private Airport findClickedAirport(int mouseX, int mouseY) {
        if (airports == null || airports.isEmpty()) return null;

        double scaleX = mapCanvas.getWidth() / 180.0;
        double scaleY = mapCanvas.getHeight() / 180.0;

        for (Airport a : airports) {
            int x = (int) ((a.getX() + 90) * scaleX);
            int y = (int) ((90 - a.getY()) * scaleY);
            if (Math.abs(x - mouseX) < 6 && Math.abs(y - mouseY) < 6)
                return a;
        }
        return null;
    }

    /* Ažurira aerodrome i listu */
    public void setAirports(List<Airport> newList) {
        this.airports = newList;
        visibleAirports = new ArrayList<>();
        for (int i = 0; i < airports.size(); i++)
            visibleAirports.add(true);
        refreshSidePanel();
        mapCanvas.repaint();
    }
    
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
        if (mapCanvas != null)
            mapCanvas.repaint();
    }
}
