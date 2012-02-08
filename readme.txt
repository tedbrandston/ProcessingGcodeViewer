ProcessingGCodeViewer - a tool to help you see what's going on in your gcode

Kudos to Noah Levy for starting this project.

ProcessingGCodeViewer is a Java Swing + Processing project intended for use by 
CNCers (I come from a RepRap & descendants background, so it's mostly aimed at
extrusion right now.)

The main goal is to make this easily hackable, because you never know what it
is you're going to need to see until you can't see it.

quick tour:
src/
  gcodeviewer/	 The main classes in the application: our PApplet and our JFrame
    parsers/	 Parsers take Gcode and produce GCodeEvents
    toolpath/	 the GCodeEvent interface and a GCodeEvent collection
      events/	 The GCodeEvents
    utils/	 Useful data structures
    visualizers/ Visualizers take GCodeEvents and draw stuff
  replicatorg/	 This is stuff I ripped straight outta ReplicatorG 
                 (github.com/makerbot/ReplicatorG) with some minor changes