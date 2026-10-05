package logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import exceptions.FileException;
import models.*;

public class FileManager {
	//save airport to CSV
	public static void saveAirports(List<Airport> airports,String file) throws FileException {
		PrintWriter pw=null;
		try {
			pw = new PrintWriter(new FileWriter(file));
			pw.println("Name,Code,X,Y");
			for(Airport a :airports) {
				pw.println(a.getName()+","+a.getCode()+","+a.getX()+","+a.getY());
			}
		}catch (Exception e) {
			throw new FileException(file, "could not save airports: " + e.getMessage());
		}finally {
			if(pw!=null) {
				pw.close();
			}
		}
	}
	//read airports from CSV
	public static List<Airport> loadAirports(String file) throws FileException{
		List<Airport> airports=new ArrayList<Airport>();
		BufferedReader br=null;
		try {
			br=new BufferedReader(new FileReader(file));
			String line =br.readLine(); //skip header
			while((line=br.readLine())!=null) {
				String[] parts=line.split(",");
				if (parts.length != 4) {
                    throw new FileException(file, "file does not contain expected columns (Name, Code, X, Y)");
                }
				airports.add(new Airport(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
			}
		}catch (Exception e) {
			  throw new FileException(file, "error while reading airports: " + e.getMessage());
		}finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
		return airports;
	}
	
	//save flights to CSV
	public static void saveFlights(List<Flight> flights,String file) throws FileException {
		PrintWriter pw = null;
		try {
			pw=new PrintWriter(new FileWriter(file));
			pw.println("Departure,Arrival,Time,Duration");
			for(Flight f:flights) {
				pw.println(f.getDep().getCode()+","+f.getArr().getCode()+","+f.getDepartureTime()+","+f.getFlightDuration());
			}		
		}catch (Exception e) {
			throw new FileException(file,"could not save flights: " + e.getMessage());
		}finally {
			if(pw!=null) {
				pw.close();
			}
		}
	}
	
	//load flight from csv
	public static List<Flight> loadFlights(String file,List<Airport> airports) throws FileException{
		List<Flight> flights=new ArrayList<Flight>();
		BufferedReader br=null;
		try {
			br=new BufferedReader(new FileReader(file));
			String line=br.readLine();
			
			while ((line=br.readLine())!=null) {
				
				String[] parts=line.split(",");
				if (parts.length != 4) {
                    throw new FileException(file, "file does not contain expected columns (Departure, Arrival, Time, Duration)");
                }
				
				Airport departure=null;
				for( Airport a:airports) {
					if(a.getCode().equals(parts[0])) {
						departure=a;
						break;
					}
				}
				if (departure == null) {
	                throw new FileException(file, "Unknown departure airport: " + parts[0]);
		        }
				Airport arrival=null;
				for( Airport a:airports) {
					if(a.getCode().equals(parts[1])) {
						arrival=a;
						break;
					}
				}
				if (arrival == null) {
	                throw new FileException(file, "Unknown departure airport: " + parts[1]);
		        }
				
				LocalTime time=LocalTime.parse(parts[2]);
				int duration= Integer.parseInt(parts[3]);
				
				flights.add(new Flight(departure, arrival, time, duration));
				
				
			}
		} catch (Exception e) {
			throw new FileException(file, "error while reading flights: " + e.getMessage());
		}finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
		return flights;
	}
		
		
		
		
		
		
		
		
	

}
