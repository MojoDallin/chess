package client;

import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient
{

    private final ServerFacade server;
    private State curState = State.SIGNED_OUT;
    private final String CROWN = "👑";

    public ChessClient(int port)
    {
        server = new ServerFacade(port);
    }

    public void signedOutMenu()
    {
        System.out.println(CROWN + " CS Chess 240: Signed Out " + CROWN);
        System.out.print(SET_TEXT_COLOR_GREEN);
        printHelp();
        System.out.print(RESET_TEXT_COLOR);

        Scanner scanner = new Scanner(System.in);
        while(curState != State.QUIT)
        {
            System.out.print(curState + " >>> ");
            System.out.print(SET_TEXT_COLOR_GREEN);
            parseStatement(scanner.nextLine());
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    private void parseStatement(String statement)
    {
        switch(statement)
        {
            case "help" -> printHelp();
            case "quit" -> curState = State.QUIT;
            default -> System.out.println("Input '" + statement + "' not recognized! Please try again.");
        }
    }

    private void printHelp()
    {
        if(curState == State.SIGNED_OUT)
        {
            System.out.println("- register <USERNAME> <PASSWORD> <EMAIL>: Create an account to play!");
            System.out.println("- login <USERNAME> <PASSWORD>: Log in to your account!");
        }
        else
        {

        }
        System.out.println("- quit: Exits the program. Have a good day!");
        System.out.println("- help: Prints these messages. Changes depending on if you're logged in or not.");
    }
}
