package models;

import java.time.LocalTime;

public class Flight {
	private Airport dep,arr;
	private LocalTime departureTime;
	private int flightDuration;
	
	public Flight(Airport a1,Airport a2,LocalTime dt,int dur) {
		dep=a1;
		arr=a2;
		departureTime=dt;
		flightDuration=dur;
	}

	public Airport getDep() {return dep;}
	public Airport getArr() {return arr;}
	public LocalTime getDepartureTime() {return departureTime;}
	public int getFlightDuration() {return flightDuration;}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(dep.getCode()).append(" -> ").append(arr.getCode()).append(" at ").
		append(departureTime).append(" (").append(flightDuration).append(" min)");
		return sb.toString();
	}
	public static void main(String[] args) {
		Airport a1=new Airport("Surcing","BGD", 45, 45);
		Airport a2=new Airport("Cilipi","DUB", 12, 12);
		Flight f=new Flight(a1, a2, LocalTime.of(12, 53), 35);
		System.out.println(f.toString());
	}
	public void setDepartureTime(LocalTime t) { this.departureTime = t; }

}
