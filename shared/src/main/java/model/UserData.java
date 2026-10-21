package model;

import java.util.Objects;

public record UserData(String username, String password, String email)
{

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserData userData = (UserData) o;
        return Objects.equals(username, userData.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
