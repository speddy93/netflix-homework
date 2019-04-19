import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by otkachenko on 02/02/18.
 */
public class TestReservation_Sample {
    public static final String TEST_RESOURCE_ID="Test Resource";
    public static final String TEST_RESOURCE_ID2="Test Resource 2";
    public static final String TEST_Owner="Test Owner";
    public static final String TEST_Owner2="Test Owner 2";
    ReservationSystem robin=null;
    Resource Resource1;
    Resource Resource2;

    @Before
    public void init() {
        // uncoment below and
        // instantiate your class here
        robin = new Solution();
        ReservationSystem_Management management = (ReservationSystem_Management) robin;
        management.addResourceIfNotExists(Resource1= new Resource() {

            @Override public String getId() {
                return TEST_RESOURCE_ID;
            }
        });
        (management).addResourceIfNotExists(Resource2 =new Resource() {

            @Override public String getId() {
                return TEST_RESOURCE_ID2;
            }
        });
    }
    @Test
    public void TestSingleReservationCase1() {

        robin.reserve(new Event() {

            @Override public String getName() {
                return "TestSingleReservationCase1";
            }

            @Override public DateTime getStartTime() {
                return new DateTime("2017-01-01");
            }

            @Override public DateTime getEndTime() {
                return new DateTime("2017-02-01");
            }

            @Override public String getOwner() {
                return TEST_Owner;
            }
        }, new Resource() {

            @Override public String getId() {
                return TEST_RESOURCE_ID;
            }
        });
        assertEquals(1,robin.getAllReservationsForResource(Resource1,new DateTime("2016-01-01"),new DateTime("2018-01-01")).size());
        assertEquals(0,robin.getAllReservationsForResource(Resource1,new DateTime("2018-01-01"),new DateTime("2018-02-01")).size());

        assertEquals(1,robin.getAllReservationsForOwner(TEST_Owner ,new DateTime("2016-01-01"),new DateTime("2018-02-01")).size());
        assertEquals(0,robin.getAllReservationsForOwner(TEST_Owner ,new DateTime("2018-01-01"),new DateTime("2018-02-01")).size());

        assertEquals(0,robin.getAllReservationsForOwner("Another One" ,new DateTime("2016-01-01"),new DateTime("2018-02-01")).size());
        assertEquals(1,robin.getAllReservationsForResource(new Resource() {
            @Override
            public String getId () {
                return TEST_RESOURCE_ID;
            }
        }, new DateTime("2016-01-01"), new DateTime("2018-01-01")).size());
    }

    @Test
    public void TestMultipleReservationCase() {
        List<Event> eventList = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();
        ReservationSystem_Management management =(ReservationSystem_Management)robin;

        resources.add(generateResource("basketball"));
        resources.add(generateResource("footbal"));
        resources.add(generateResource("clown"));
        resources.add(generateResource("pipe"));
        resources.add(generateResource("balloon"));
        resources.add(generateResource("test"));
        resources.forEach(management::addResourceIfNotExists);

        eventList.add(generateNewEvent("basketballParty","2018-01-02","2018-01-03",TEST_Owner));
        eventList.add(generateNewEvent("basketballParty","2018-01-03","2018-01-04",TEST_Owner));
        eventList.add(generateNewEvent("footballParty","2018-01-02T10","2018-01-02T11",TEST_Owner2));
        eventList.add(generateNewEvent("clown","2017-01-01T10","2017-01-01T11",TEST_Owner2));
        eventList.add(generateNewEvent("clown2","2017-01-01T10","2017-01-01T12",TEST_Owner));
        eventList.add(generateNewEvent("clown2","2017-01-01","2017-01-01",TEST_Owner));

        robin.reserve(eventList.get(0),resources.get(0));
        robin.reserve(eventList.get(1),resources.get(0));
        robin.reserve(eventList.get(2),resources.get(1));
        robin.reserve(eventList.get(3),resources.get(2));
        robin.reserve(eventList.get(4),resources.get(2));

        assertEquals(2,robin.getAllReservationsForOwner(TEST_Owner,new DateTime("2017-01-01"),new DateTime("2018-02-01")).size());
        assertEquals(2,robin.getAllReservationsForOwner(TEST_Owner2,new DateTime("2017-01-01"),new DateTime("2018-02-01")).size());
    }

    public Event generateNewEvent(final String name, final String start, final String end, final String owner){
        return new Event() {

            @Override public String getName() {
                return name;
            }

            @Override public DateTime getStartTime() {
                return new DateTime(start);
            }

            @Override public DateTime getEndTime() {
                return new DateTime(end);
            }

            @Override public String getOwner() {
                return owner;
            }
        };
    }

    public Resource generateResource(final String id){
        return new Resource() {
            @Override
            public String getId() {
                return id;
            }
        };
    }

}

