/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2011.08.10
 */
import java.util.Stack;

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    
    //*************************************************************************
    // keep track of last direction so we can go BACK
    //*************************************************************************
    Stack<String> history = new Stack<>();
    private String lastDirection = "";

    
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    /**************************************************************************************
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room outside, room_a, room_b, room_c, room_d, room_e, room_f, room_g, room_h, room_i, room_j, room_k;
      
        // create the rooms
        outside = new Room("outside the main entrance of the structure");
        room_a = new Room("Room A");
        room_b = new Room("Room B");
        room_c = new Room("Room C");
        room_d = new Room("Room D");
        room_e = new Room("Room E");
        room_f = new Room("Room F");
        room_g = new Room("Room G");
        room_h = new Room("Room H");
        room_i = new Room("Room I");
        room_j = new Room("Room J");
        room_k = new Room("Room K");
        
        // initialise room exits
        outside.setExit("east", room_b);
        outside.setExit("west", room_a);
        outside.setItem("Small black meteorite", 0.5);
        
        room_a.setExit("east", outside);
        room_a.setExit("south", room_d);
        room_a.setItem("gold coin", 0.25);
        room_a.setItem("Rusty battle sword", 12.0);
        
        room_b.setExit("west", outside);
        room_b.setExit("east", room_c);
        room_b.setItem("Dusty book", 2.25);

        room_c.setExit("west", room_b);
        room_c.setExit("south", room_g);
        room_c.setItem("Nasty-looking potion", 0.5);

        room_d.setExit("north", room_a);
        room_d.setExit("south", room_h);
        room_d.setItem("Nepalese Kukri", 1.75);

        room_e.setExit("south", room_i);
        room_e.setItem("IT'S ZUUL! RUN!", 999.0);

        room_f.setExit("east", room_g);
        room_f.setExit("south", room_j);
        room_f.setItem("Giant poisonous scorpion", 4.5);

        room_g.setExit("north", room_c);
        room_g.setExit("west", room_f);
        room_g.setExit("south", room_k);
        room_g.setItem("Roman shield", 10.0);

        room_h.setExit("north", room_d);
        room_h.setExit("east", room_i);
        room_h.setItem("Amulet of Protection", 0.75);

        room_i.setExit("west", room_h);
        room_i.setExit("north", room_e);

        room_j.setExit("north", room_f);
        room_j.setItem("Hazy crystal ball", 4.25);

        room_k.setExit("north", room_g);

        currentRoom = outside;  // start game outside
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;

            case HELP:
                printHelp();
                break;
                
            case LOOK:
                look();
                break;
                
            //***************************************************************
            // process BACK command
            // look at the last direction and go the other way
            //***************************************************************
            case BACK:
                Room nextRoom;
                
                //***********************************************
                // get lastDirection from history Stack
                //***********************************************
                lastDirection = historyPop();
                switch (lastDirection) {
                    case "north":
                        nextRoom = currentRoom.getExit("south");
                        currentRoom = nextRoom;
                    break;
                    case "east":
                        nextRoom = currentRoom.getExit("west");
                        currentRoom = nextRoom;
                    break;
                    case "south":
                        nextRoom = currentRoom.getExit("north");
                        currentRoom = nextRoom;
                    break;
                    case "west":
                        nextRoom = currentRoom.getExit("east");
                        currentRoom = nextRoom;
                    break;
                    // if lastDirection was NOT one of the four above, we have no place to go back to
                    default:
                        System.out.println("This is your starting position so you can't go back.");
                    break;
                }
                System.out.println(currentRoom.getLongDescription());
                break;

            case GO:
                goRoom(command);
                break;
                
            case EAT:
                System.out.println("Yumm! You are no longer hungry.");
                break;    

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:
    
    /***********************************************************************
     * LOOK command
     * return long description of room
     */
    private void look()
    {
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.print("Your command words are: ");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
            
            //*************************************************************
            // remember last direction moved for BACK command
            //*************************************************************
            lastDirection = direction;
            historyPush(lastDirection);
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
    
    //******************************************************************
    // manage direction history in Stack
    // historyPush records the last direction on top of Stack
    // historyPop returns the last direction from the top of Stack
    //******************************************************************
    private void historyPush(String direction) {
            history.push(direction);
        }
        private String historyPop() {
        if(history.empty()) {
            return("");
        }
        else {
            return(history.pop());
        }
    }
    
    public static void main() {
        Game thisGame = new Game();
        thisGame.play();
    }
}
