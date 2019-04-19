import org.joda.time.DateTime;

/**
 * Created by otkachenko on 9/7/17.
 */
public interface Event {
    String getName();
    DateTime getStartTime();
    DateTime getEndTime();
    String getOwner();
}
