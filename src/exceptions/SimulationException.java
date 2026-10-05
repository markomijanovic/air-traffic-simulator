package exceptions;

public class SimulationException extends AppException {

	private final String flightCode;

    public SimulationException(String flightCode, String message) {
        super(Severity.CRITICAL,"Simulation error for flight '" + flightCode + "': " + message );
        this.flightCode = flightCode;
    }

    public String getFlightCode() {
        return flightCode;
    }

}
