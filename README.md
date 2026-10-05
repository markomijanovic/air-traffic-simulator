# Air Traffic Simulator

A Java AWT desktop simulation of airports and flights, with CSV input, validation, a map view, airport panels, and a simulation timer.

The source matches all 15 Java files in the local `srcfinal.zip` submission. Other local simulator copies contain different GUI implementations.

## Build and run

Requires JDK 11 or later. From PowerShell:

```powershell
New-Item -ItemType Directory -Force build | Out-Null
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d build $sources
java -cp build main.App
```

Import airport and flight CSV files through the application menu. Small supplied CSV files are in `examples/`; use the application validation messages to check file compatibility.

## Structure

- `models/`: airports and flights.
- `logic/`: file loading and validation.
- `gui/`: AWT windows, panels, map, and timer.
- `exceptions/`: application, validation, and simulation errors.

Runtime behavior and the GUI still need an interactive smoke test.

## Context

Educational project by Marko Mijanovic, University of Belgrade, School of Electrical Engineering. Course scaffolding and supplied assets remain part of the project; this preparation does not grant a new license to third-party material.

## Preparation validation

- javac all sources: passed.
