package logic;

import java.util.List;

import exceptions.ValidationException;
import models.*;

public class Validator {
	
	//Airport validation
	public static void validateAirport(Airport a,List<Airport> existingAirports) throws ValidationException {
		System.out.println(a.getCode());
		if(a.getName() == null||a.getName().trim().isEmpty() ){
			throw new ValidationException("name", "cannot be empty");
		}
		
		if(a.getCode().length()!=3) {
			throw new ValidationException("code", "must be 3 capital letters");
		}
		for(int i = 0;i < a.getCode().length(); i++) {
			if(a.getCode().charAt(i) < 'A' || a.getCode().charAt(i) > 'Z')
				throw new ValidationException("code", "must be 3 capital letters");
		}
		
		if(existingAirports.stream().anyMatch(t ->t.getCode().equals(a.getCode()) )) {
			throw new ValidationException("code", "airport with this code already exists");
		}
		if(a.getX() < -90 || a.getX() > 90 || a.getY() < -90 || a.getY() > 90) {
            throw new ValidationException("coordinates", "must be within range [-90, 90]");
		}
	}
	//flight validation
	public static void validateFlight(Flight f) throws ValidationException {
		if(f.getDep().equals(f.getArr())) {
			throw new ValidationException("flight", "departure and arrival airports cannot be the same");
		}
		if(f.getFlightDuration()<=0) {
			throw new ValidationException("duration", "must be positive number");
		}
		if(f.getDepartureTime()==null) {
			throw new ValidationException("departureTime", "cannot be null");
		}
	}

}
