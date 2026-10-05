package gui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import models.Airport;

public class MainWindow extends Frame {
	private AppTimer timer;
	
	private CardLayout cardLayout;
    private Panel mainPanel;
	private AirportPanel airportPanel;
	private MapPanel mapPanel;
	private Panel simulationPanel;
	
    public MainWindow() {
        setTitle("Air Traffic Simulator");
        setSize(900, 600);
        setLayout(new BorderLayout());
       
        
        initTimer(); 
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> timer.ContinueWork(),
        	    AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
        initUI();
        initWindow();
        
        
        
        setVisible(true);
        
    }
    private void initUI(){
    	airportPanel = new AirportPanel();
    	mapPanel=new MapPanel(airportPanel.getAirports(),airportPanel.getFlights(),timer);
    	simulationPanel = new SimulationPanel(airportPanel.getFlights());
        
        Panel topBar=new Panel(new FlowLayout(FlowLayout.CENTER,20,10));
        Button airportsBtn = new Button("Airports");
        Button mapBtn = new Button("Map");
        Button simulationBtn = new Button("Simulation");
        
        Font navFont = new Font("Arial", Font.BOLD, 16);
        airportsBtn.setFont(navFont);
        mapBtn.setFont(navFont);
        simulationBtn.setFont(navFont);
        
        topBar.add(airportsBtn);
        topBar.add(mapBtn);
        topBar.add(simulationBtn);
        add(topBar, BorderLayout.NORTH);
        
        //cadrs
        
        cardLayout=new CardLayout();
        mainPanel=new Panel(cardLayout);
        
        mainPanel.add(airportPanel,"Airports");
        mainPanel.add(mapPanel, "Map");
        mainPanel.add(simulationPanel, "Simulation");

        add(mainPanel, BorderLayout.CENTER);
        
        //button action
        
        airportsBtn.addActionListener(e->cardLayout.show(mainPanel, "Airports"));
        mapBtn.addActionListener(e->{
        	List<Airport> airports=airportPanel.getAirports();
        	mapPanel.setAirports(airports);
        	cardLayout.show(mainPanel, "Map");
        	
        });
        simulationBtn.addActionListener(e->cardLayout.show(mainPanel, "Simulation"));
    	
    }
    private void initTimer() {
    	timer = new AppTimer(60,new AppTimer.TimerListener() {
        	
        	private Dialog warningDialog;
        	private Label messageLabel;
			
			@Override
			public void onWarning(int secondsRemaining) {
				EventQueue.invokeLater(()->{
					
					if(warningDialog==null) {
						warningDialog=new Dialog(MainWindow.this,"Inactivity Warning",true);
						warningDialog.setLayout(new BorderLayout(10,10));
						
						messageLabel=new Label("",Label.CENTER);
						warningDialog.add(messageLabel,BorderLayout.CENTER);
						
						Panel bottom=new Panel(new FlowLayout(FlowLayout.CENTER));
						Button continueBtn=new Button("Continue");
						bottom.add(continueBtn);
						warningDialog.add(bottom,BorderLayout.SOUTH);
						
						continueBtn.addActionListener(e->{
							warningDialog.dispose();
							timer.ContinueWork();
						});
						
						warningDialog.addWindowListener(new WindowAdapter() {
						
							@Override public void windowClosing(WindowEvent e) {
		                        warningDialog.dispose();
		                        timer.ContinueWork();
		                    }
						});
						
						warningDialog.setSize(400,150);
						warningDialog.setLocationRelativeTo(MainWindow.this);
					}
					messageLabel.setText("Application will close in " + secondsRemaining + " seconds...");
		            if (!warningDialog.isVisible()) warningDialog.setVisible(true);
					
					
				});
			}
			
			@Override
			public void onExpire() {
				EventQueue.invokeLater(() -> {
		            timer.stopTimer();
		            dispose();
		        });
				
			}
			
			@Override
			public void onContinue() {
				if (warningDialog != null && warningDialog.isVisible())
		            warningDialog.dispose();
		        System.out.println("User continued working, timer reset.");
				
			}
		});
        timer.start();
    }
    private void initWindow() {
    	
    	addWindowListener(new WindowAdapter() {
    		@Override
            public void windowClosing(WindowEvent e) {
    			timer.stopTimer();
                showExitConfirmation();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("Application closed.");
            }
    	
    	});
    }
    private void showExitConfirmation() {
    	Dialog confirmDialog=new Dialog(this,"Exit application",true);
    	
    	confirmDialog.setLayout(new BorderLayout(10,10));
    	
    	Label msg=new Label("Are you sure you want close the app",Label.CENTER);
    	confirmDialog.add(msg,BorderLayout.CENTER);
    	
    	Panel buttPanel=new Panel(new FlowLayout(FlowLayout.CENTER));
    	Button yes=new Button("Yes");
    	Button no=new Button("No");
    	
    	buttPanel.add(yes);
    	buttPanel.add(no);
    	confirmDialog.add(buttPanel,BorderLayout.SOUTH);
    	
    	yes.addActionListener(e->{
    		confirmDialog.dispose();
    		timer.stopTimer();
    		dispose();
    		
    	});
    	
    	no.addActionListener(e->{
    		confirmDialog.dispose();
    	});
    	
    	confirmDialog.addWindowListener(new WindowAdapter() {
		
    		@Override
    		public void windowClosing(WindowEvent e) {
    			confirmDialog.dispose();
    		}
    	});
    	
    	confirmDialog.setSize(300,100);
    	confirmDialog.setResizable(false);
    	confirmDialog.setLocationRelativeTo(this);
    	confirmDialog.setVisible(true);
    }
}

