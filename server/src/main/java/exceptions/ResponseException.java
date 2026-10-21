package exceptions;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception
{
    private final int id;
    public ResponseException(int id, String message)
    {
        super(message);
        this.id = id;
    }

    public int id()
    {
        return id;
    }

    public String toJson()
    {
        return new Gson().toJson(Map.of("message", getMessage()));
    }
}
