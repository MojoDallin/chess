package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exceptions.ResponseException;
import model.GameData;
import model.JoinData;
import requests.*;
import results.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Objects;
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
            case "observe" -> observeGame(params[1]);
            case "join" -> joinGame(params[2], params[1]); // subtract 1 to account for zero-indexing
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

    private void joinGame(String color, String id) throws ResponseException
    {
        if(isLoggedIn())
        {
            try
            {
                Integer.parseInt(id);
            }
            catch (Exception ex)
            {
                throw new ResponseException(500, "Error: invalid game ID " + id);
            }
            color = color.toLowerCase(); // ignore case
            if(!Objects.equals(color, "white") && !Objects.equals(color, "black"))
            {
                throw new ResponseException(500, "Error: invalid color " + color);
            }
            if(server.listGames(authToken).size() < Integer.parseInt(id) || Integer.parseInt(id) < 1)
            {
                throw new ResponseException(500, "Error: game ID out of bounds");
            }
            boolean wasSuccessful = server.joinGame(new JoinData(color.toUpperCase(), Integer.parseInt(id)), authToken);
            if(wasSuccessful)
            {
                drawChessBoard(server.listGames(authToken).get(Integer.parseInt(id) - 1).game(), color);
            }
        }
        else
        {
            throw new ResponseException(500, "Error: not logged in");
        }
    }

    private void observeGame(String id) throws ResponseException
    {
        GameData data = server.listGames(authToken).get(Integer.parseInt(id) - 1);
        drawChessBoard(data.game(), "white"); // default is from white's perspective
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

    private void drawChessBoard(ChessGame boardData, String color)
    {
        ChessBoard board = boardData.getBoard();
        String[] colorSequences = {SET_TEXT_COLOR_BLUE, SET_TEXT_COLOR_RED};
        char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        byte[] numbers = {8, 7, 6, 5, 4, 3, 2, 1};
        if(Objects.equals(color, "black"))
        {
            colorSequences[0] = SET_TEXT_COLOR_RED; // colors/letters/numbers change depending on perspective
            colorSequences[1] = SET_TEXT_COLOR_BLUE;
            letters = new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
            numbers = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        }
        for(int i = 0; i < 10; i++)
        {
            if(i == 0 || i == 9)
            {
                System.out.print("    ");
            }
            for(int j = 0; j < 10; j++)
            {
                int extraSpaces = 2;
                if(i == 0 || i == 9)
                {
                    if(j > 0 && j < 9)
                    {
                        System.out.print(letters[j - 1]);
                    }
                }
                else
                {
                    if(j > 0 && j < 9)
                    {
                        ChessPiece piece = board.getPositionAt(i, j).getCurrentPiece();
                        if((j + i) % 2 == 1)
                        {
                            System.out.print(SET_BG_COLOR_BLACK);
                        }
                        else
                        {
                            System.out.print(SET_BG_COLOR_WHITE);
                        }
                        if(piece != null)
                        {
                            System.out.print(SET_TEXT_BOLD);
                            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                            {
                                System.out.print(colorSequences[0]);
                            }
                            else
                            {
                                System.out.print(colorSequences[1]);
                            }
                            System.out.print(" ");
                            extraSpaces--;
                            String pieceString = piece.toString().toUpperCase();
                            System.out.print(pieceString);
                            System.out.print(RESET_TEXT_BOLD_FAINT);
                            System.out.print(SET_TEXT_COLOR_GREEN);
                        }
                        else
                        {
                            System.out.print(" ");
                        }
                    }
                    else
                    {
                        System.out.print(RESET_BG_COLOR);
                        System.out.print("  ");
                        System.out.print(numbers[i - 1]);
                    }
                }
                for(int k = 0; k < extraSpaces; k++)
                {
                    System.out.print(" ");
                }
            }
            System.out.println(); // newline
        }
    }

private boolean isLoggedIn() { return curState == State.LOGGED_IN; }
}
