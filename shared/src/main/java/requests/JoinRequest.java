package requests;

import model.JoinData;

public record JoinRequest(JoinData data, String username) {
}
