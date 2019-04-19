import java.util.UUID;

public class Reservation implements ReservationTicket {
    private String ticketID;
    private Event event;
    private Resource reservedResource;

    Reservation(Event event_reservation, Resource reserved_resource){
        this.ticketID = String.valueOf(UUID.randomUUID());
        this.event = event_reservation;
        this.reservedResource = reserved_resource;
    }

    @Override
    public String getTicketID() {
        return ticketID;
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public Resource getResource() {
        return reservedResource;
    }
}
