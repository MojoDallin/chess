package client;

import chess.ChessGame;
import exceptions.ResponseException;
import model.GameData;
import requests.*;
import results.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient
{

    private final ServerFacade server;
    private State curState = State.SIGNED_OUT;
    private final String CROWN = "👑";
    private String authToken;

    public ChessClient(ServerFacade server)
    {
        this.server = server;
    }

    public void signedOutMenu() throws ResponseException
    {
        System.out.println(CROWN + " CS Chess 240: Signed Out " + CROWN);
        System.out.print(SET_TEXT_COLOR_GREEN);
        printHelp();
        System.out.print(RESET_TEXT_COLOR);

        Scanner scanner = new Scanner(System.in);
        while(curState != State.QUIT)
        {
            System.out.print(curState + " >>> ");
            try
            {
                System.out.print(SET_TEXT_COLOR_GREEN);
                parseStatement(scanner.nextLine());
                System.out.print(RESET_TEXT_COLOR);
            } catch (Throwable ex)
            {
                System.out.println(SET_TEXT_COLOR_RED + ex.getMessage() + RESET_TEXT_COLOR);
            }
        }
    }

    private void parseStatement(String statement) throws ResponseException
    {
        String[] params = statement.split(" ");
        statement = params[0].toLowerCase(); // ignore case
        switch(statement)
        {
            case "list" -> listGames();
            case "create" -> createGame(params[1]);
            case "logout" -> logoutUser();
            case "login" -> loginUser(params);
            case "register" -> registerUser(params);
            case "help" -> printHelp();
            case "quit" -> curState = State.QUIT;
            default -> System.out.println("Input '" + statement + "' not recognized! Please try again.");
        }
    }

    private void registerUser(String[] params) throws ResponseException
    {
        if(!isLoggedIn())
        {
            LoginResult result = server.registerUser(new RegisterRequest(params[1], params[2], params[3]));
            curState = State.LOGGED_IN;
            authToken = result.authToken();
        }
        else
        {
            throw new ResponseException(500, "Error: already logged in");
        }
    }

    private void loginUser(String[] params) throws ResponseException
    {
        if(!isLoggedIn())
        {
            LoginResult result = server.loginUser(new LoginRequest(params[1], params[2]));
            curState = State.LOGGED_IN;
            authToken = result.authToken();
        }
        else
        {
            throw new ResponseException(500, "Error: already logged in");
        }
    }

    private void logoutUser() throws ResponseException
    {
        if(isLoggedIn())
        {
           server.logoutUser(new LogoutRequest(authToken));
           authToken = "";
           curState = State.SIGNED_OUT;
        }
        else
        {
            throw new ResponseException(500, "Error: not logged in");
        }
    }

    private void createGame(String name) throws ResponseException
    {
        if(isLoggedIn())
        {
            server.createGame(new GameData(99, null, null, name, new ChessGame()), authToken);
        }
        else
        {
            throw new ResponseException(500, "Error: not logged in");
        }
    }

    private void listGames() throws ResponseException
    {
        if(isLoggedIn())
        {
            ArrayList<GameData> allGames = server.listGames(authToken);
            for(int i = 0; i < allGames.size(); i++)
            {
                System.out.print(SET_TEXT_COLOR_BLUE);
                System.out.println((i + 1) + ": " + allGames.get(i));
                System.out.print(RESET_TEXT_COLOR);
            }
        }
        else
        {
            throw new ResponseException(500, "Error: not logged in");
        }
    }

    private void printHelp()
    {
        if(!isLoggedIn())
        {
            System.out.println("- register <USERNAME> <PASSWORD> <EMAIL>: Create an account to play!");
            System.out.println("- login <USERNAME> <PASSWORD>: Log in to your account!");
        }
        else
        {
            System.out.println("- create <NAME>: Creates a game with the name <NAME>.");
            System.out.println("- list: Lists all the currently available games.");
            System.out.println("- join <ID> [WHITE/BLACK]: Join a game with id <ID> and color [WHITE/BLACK].");
            System.out.println("- observe <ID>: View a game with id <ID>");
            System.out.println("- logout: Logs you out.");
        }
        System.out.println("- quit: Exits the program. Have a good day!");
        System.out.println("- help: Prints these messages. Changes depending on if you're logged in or not.");
    }

private boolean isLoggedIn() { return curState == State.LOGGED_IN; }
}
