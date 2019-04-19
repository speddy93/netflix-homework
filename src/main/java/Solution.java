import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solution implements ReservationSystem, ReservationSystem_Management{
    HashMap<String, Resource> resources = new HashMap<>();
    List<ReservationTicket> reservations = new ArrayList<>();

    public Solution(){}

    @Override
    public synchronized ReservationTicket reserve(Event event, Resource resource) {
        ReservationTicket reservationTicket = null;
        validateInterval(event.getStartTime(),event.getEndTime(),"Event "+event.getName());
        if(resources.containsKey(resource.getId())) {
            if (getReservationsForOwner(event.getOwner(), event.getStartTime(), event.getEndTime(),false).size() == 0) {
                if (getReservationsForResources(resource, event.getStartTime(), event.getEndTime(),false).size() == 0) {
                    reservationTicket = new Reservation(event, resource);
                    reservations.add(reservationTicket);
                }
            }
        }
        return reservationTicket;
    }

    private void validateInterval(DateTime startTime, DateTime endTime, String event) {
        if(startTime.isAfter(endTime)){
            throw new RuntimeException(event+" does not include a valid timeframe: Start time is after end time");
        }
        else if(startTime.isEqual(endTime)){
            throw new RuntimeException(event+" does not include a valid timeframe: Start time is the same as End time");
        }
    }


    @Override
    public List<Resource> findAvailableResources(Event event) {
        validateInterval(event.getStartTime(),event.getEndTime(), "Event "+event.getName());
        List<Resource> availableResources = new ArrayList<>();
        if(getAllReservationsForOwner(event.getOwner(),event.getStartTime(),event.getEndTime()).size()==0){
            //Find remaining resources based on interval
            for(String resource: resources.keySet()){
                if(getAllReservationsForResource(resources.get(resource), event.getStartTime(), event.getEndTime()).size() == 0){
                    availableResources.add(resources.get(resource));
                }
            }
        }
        return availableResources;
    }

    @Override
    public List<ReservationTicket> getAllReservationsForOwner(String owner, DateTime startTime, DateTime endTime) {
        return getReservationsForOwner(owner, startTime, endTime,true);
    }

    //In the case that Resrvations get large - we dont always need all the elements - small potential improvement
    public List<ReservationTicket> getReservationsForOwner(String owner, DateTime startTime, DateTime endTime, boolean all) {
        validateInterval(startTime,endTime,"Search Range");
        List<ReservationTicket> ownerReservations = new ArrayList<>();
        Interval interval = new Interval(startTime, endTime);
        for(ReservationTicket ticket: reservations){
            if(owner.equals(ticket.getEvent().getOwner())){
                Interval eventInterval = new Interval(ticket.getEvent().getStartTime(), ticket.getEvent().getEndTime());
                if(intervalOverlaps(interval,eventInterval)){
                    ownerReservations.add(ticket);
                    if(!all){
                        break;
                    }
                }
            }
        }
        return ownerReservations;
    }

    @Override
    public List<ReservationTicket> getAllReservationsForResource(Resource resource, DateTime startTime, DateTime endTime) {
        return getReservationsForResources(resource,startTime,endTime,true);
    }

    //In the case that Resrvations get large - we dont always need all the elements - small potential improvement
    public List<ReservationTicket> getReservationsForResources(Resource resource, DateTime startTime, DateTime endTime, boolean all) {
        validateInterval(startTime,endTime,"Search Range");
        List<ReservationTicket> resourceReservations = new ArrayList<>();
        Interval interval = new Interval(startTime, endTime);
        for(ReservationTicket reservation: reservations){
            //TODO replace with an equals implementation
            if(resource.getId().equals(reservation.getResource().getId())){
                Interval eventInterval = new Interval(reservation.getEvent().getStartTime(),reservation.getEvent().getEndTime());
                if(intervalOverlaps(interval, eventInterval)){
                    resourceReservations.add(reservation);
                    if(!all){
                        break;
                    }
                }
            }
        }
        return resourceReservations;
    }

    @Override
    public synchronized boolean addResourceIfNotExists(Resource resource) {
        if(resources.containsKey(resource.getId())){
            return false;
        }
        resources.put(resource.getId(),resource);
        return true;
    }

    //All reservations - that could start before or end after but that are active during some time in between?
    public boolean intervalOverlaps(Interval one, Interval two){
        if(one.overlap(two) != null){
            return true;
        }
        return false;
    }
}
