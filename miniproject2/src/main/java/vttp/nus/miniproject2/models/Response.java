package vttp.nus.miniproject2.models;

public class Response {

    private int round;
    private String response;

	public Response(int round2, String responseText) {
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}

    @Override
    public String toString() {
        return "Response{" +
                "round=" + round +
                ", response='" + response + '\'' +
                '}';
    }

}