import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by otkachenko on 9/7/17.
 */
/*
    You have pool of resources that could be booked by people (owners)
    Every resource at any particular time could be reserved for one event only.
    Every owner at any particular time could hold reservation for one resource only
    Assuming that reservation system should be implemented in memory and concurently serve many request
    at the same time please design your solution and implement ReservatonSystem interface ensuring application consistency
    and good performance dealing with millions events.
    Please also implement ReservationSystem_Management interface to allow system configuration.

    Example : meeting room booking system in office building
 */
public interface ReservationSystem {
    ReservationTicket reserve(Event event,Resource resource);
    List<Resource> findAvailableResources(Event event);
    List<ReservationTicket> getAllReservationsForOwner(String owner,DateTime startTime,DateTime endTime);
    List<ReservationTicket> getAllReservationsForResource(Resource resource,DateTime startTime,DateTime endTime);
}
