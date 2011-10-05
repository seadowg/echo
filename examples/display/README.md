## display

This is just a small example of how Events can be used to easily create fluid, simple GUI code. As with all the examples
to build and run do:

    buildr
    buildr start
    
## What is it?

Display will show a JFrame with one button. When the button is pressed some stuff will be printed to the command line. 

In the code you will see that there is a custom 'Button' class. This class extends JButton and EventSource. When it is
instantiated it creates an ActionListener (Swing casualties: stop grimacing; it'll be over soon). This is then used to
make the Button 'occur' (we think of this as a press). 

The 'Display' file holds the main method. Here it simply creates Button and assigns an edge (the print out) to it via 'each' (and also creates the JFrame).
    
    