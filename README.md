# Air Traffic Simulator

A Java AWT desktop simulation of airports and flights, with CSV input, validation, a map view, airport panels, and a simulation timer.

## Build and run

Requires JDK 11 or later. From PowerShell:

```powershell
New-Item -ItemType Directory -Force build | Out-Null
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d build $sources
java -cp build main.App
```

Import `examples/airports.csv` first, then `examples/flights.csv` through the application menu. Flights refer to the three-letter airport codes loaded in the first step.

### CSV format

| File | Header | Example row |
|---|---|---|
| Airports | `Name,Code,X,Y` | `Alpha Airport,AAA,-40,20` |
| Flights | `Departure,Arrival,Time,Duration` | `AAA,BBB,08:00,60` |

Use uppercase three-letter codes, integer coordinates between -90 and 90, `HH:mm` departure times, and positive durations in minutes. The parser expects a header and simple comma-separated fields; quoted fields containing commas are unsupported.

## Structure

- `models/`: airports and flights.
- `logic/`: file loading and validation.
- `gui/`: AWT windows, panels, map, and timer.
- `exceptions/`: application, validation, and simulation errors.

Runtime behavior and the GUI still need an interactive smoke test.

## Context

Educational project by [Marko Mijanovic](https://github.com/markomijanovic), University of Belgrade, School of Electrical Engineering. Supplied course scaffolding and assets retain their original licensing terms.

## Verification

- All Java sources compile with JDK 11.
- Interactive AWT behavior has not been verified.
