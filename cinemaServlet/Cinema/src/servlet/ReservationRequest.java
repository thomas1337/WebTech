package servlet;

public class ReservationRequest {

	String name;
	String email;
	int[] seats;
	int movieId;
	public ReservationRequest(String name, String email, int[] seats,
			int movieId) {
		super();
		this.name = name;
		this.email = email;
		this.seats = seats;
		this.movieId = movieId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int[] getSeats() {
		return seats;
	}
	public void setSeats(int[] seats) {
		this.seats = seats;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	
	
	
}
