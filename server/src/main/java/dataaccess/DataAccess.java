package dataaccess;

import exceptions.ResponseException;
import results.*;
import requests.*;

public interface DataAccess {
    // create functions
    LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException;
    // read functions

    // update functions

    // delete functions
}
