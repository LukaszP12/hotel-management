package pl.piwowarski.model.room;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(unique = true, nullable = false)
    private String roomNumber;

    @NotBlank(message = "Room name is required")
    @Column(unique = true, nullable = false)
    private String roomName;

    @NotNull(message = "Room capacity is required")
    @Min(value = 1, message = "Room capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity;

    @NotBlank(message = "Room type is required")
    @Column(nullable = false)
    private RoomType roomType;  // e.g., "Single", "Double", "Suite"

    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private boolean available = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.CLEAN;

    // --- Constructors ---
    public Room() {
    }

    public Room(RoomType roomType, double price, boolean available) {
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

    public void setName(String name) {
        this.roomName = name;
    }

    public String getRoomName() {
        return roomName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setStatus(RoomStatus newStatus) {
        this.status = newStatus;
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
