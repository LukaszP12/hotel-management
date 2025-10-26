package pl.piwowarski.model;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(unique = true, nullable = false)
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    @Column(nullable = false)
    private RoomType roomType;  // e.g., "Single", "Double", "Suite"

    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private boolean available = true;

    // --- Constructors ---
    public Room() {
    }

    public Room(Long id, RoomType roomType, double price, boolean available) {
        this.id = id;
        this.roomType = roomType;
        this.price = price;
        this.available = available;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getType() {
        return roomType;
    }

    public void setType(RoomType type) {
        this.roomType = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // --- Helper Methods ---
    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", type='" + roomType.name() + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }
}
