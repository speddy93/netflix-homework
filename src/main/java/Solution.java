import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution implements ReservationSystem, ReservationSystem_Management {
    private Map<String, Resource> resources = new ConcurrentHashMap<>();
    private volatile List<ReservationTicket> reservations = new ArrayList<>();

    public Solution(){}

    //3 approaches to the locking work
    //1. Create a lock object - and any method that does updates would take a hold of the lock so that things are not read when things are being written
    //2. Create a volatile object with syncronization to prevent read and write access at the same time
    //3. syncronization at the variable instead of method

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

    @Override
    public List<Resource> findAvailableResources(Event event) {
        validateInterval(event.getStartTime(),event.getEndTime(), "Event "+event.getName());
        if(getReservationsForOwner(event.getOwner(),event.getStartTime(),event.getEndTime(),false).size()==0){
            //Find remaining resources based on interval
            return resources.keySet().parallelStream()
                    .map(resources::get)
                    .filter(resource -> getReservationsForResources(resource,event.getStartTime(),event.getEndTime(),false).size()==0)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<ReservationTicket> getAllReservationsForOwner(String owner, DateTime startTime, DateTime endTime) {
        return getReservationsForOwner(owner, startTime, endTime,true);
    }

    private List<ReservationTicket> getReservationsForOwner(String owner, DateTime startTime, DateTime endTime, boolean all) {
        validateInterval(startTime,endTime,"Search Range");
        Interval interval = new Interval(startTime, endTime);

        Stream<ReservationTicket> reservationTicketStream = reservations.parallelStream().filter(reservationTicket ->
                reservationTicket.getEvent().getOwner().equals(owner) &&
                        intervalOverlaps(interval,
                                new Interval(reservationTicket.getEvent().getStartTime(),reservationTicket.getEvent().getEndTime())));

        if(all){
            return reservationTicketStream.collect(Collectors.toList());
        }
        else{
            Optional<ReservationTicket> item =reservationTicketStream.findFirst();
            if(item.isPresent()){
                return Arrays.asList(item.get());
            }
            return Collections.emptyList();
        }
    }

    @Override
    public List<ReservationTicket> getAllReservationsForResource(Resource resource, DateTime startTime, DateTime endTime) {
        return getReservationsForResources(resource,startTime,endTime,true);
    }

    private List<ReservationTicket> getReservationsForResources(Resource resource, DateTime startTime, DateTime endTime, boolean all) {
        validateInterval(startTime,endTime,"Search Range");
        Interval interval = new Interval(startTime, endTime);
        Stream<ReservationTicket> reservationTicketStream = reservations.parallelStream().filter(reservationTicket ->
                reservationTicket.getResource().getId().equals(resource.getId()) &&
                        intervalOverlaps(interval,
                                new Interval(reservationTicket.getEvent().getStartTime(),reservationTicket.getEvent().getEndTime())));
        if(all){
            return reservationTicketStream.collect(Collectors.toList());
        }
        else{
            Optional<ReservationTicket> item =reservationTicketStream.findFirst();
            if(item.isPresent()){
                return Arrays.asList(item.get());
            }
            return Collections.emptyList();
        }
    }

    @Override
    public boolean addResourceIfNotExists(Resource resource) {
        synchronized (resources){
            if(resources.containsKey(resource.getId())){
                return false;
            }
            resources.put(resource.getId(),resource);
            return true;
        }
    }

    //All reservations - that could start before or end after but that are active during some time in between?
    private boolean intervalOverlaps(Interval one, Interval two){
        if(one.overlap(two) != null){
            return true;
        }
        return false;
    }

    private void validateInterval(DateTime startTime, DateTime endTime, String event) {
        if(startTime.isAfter(endTime)){
            throw new RuntimeException(event+" does not include a valid timeframe: Start time is after end time");
        }
        else if(startTime.isEqual(endTime)){
            throw new RuntimeException(event+" does not include a valid timeframe: Start time is the same as End time");
        }
    }

}
