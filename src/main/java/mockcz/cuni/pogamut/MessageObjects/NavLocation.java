

package mockcz.cuni.pogamut.MessageObjects;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * NavPoint or location. Allows the two to be treated the same.
 * @author Jacob Schrum
 */
public class NavLocation {
    NavPoint nav;
    Location loc;

    public NavLocation(NavPoint nav){
        this.nav = nav;
        this.loc = null;
    }

    public NavLocation(double x, double y, double z){
        this(new Location(x,y,z));
    }

    public NavLocation(Location loc){
        this.loc = loc;
        this.nav = null;
    }

    public Location getLocation(){
        return (nav == null ? loc : nav.getLocation());
    }

    public String getStringId(){
        return (loc == null ? nav.getId().getStringId() : loc.toString());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof NavLocation){
            NavLocation nl = (NavLocation) o;
            return nl.getStringId().equals(getStringId());
        }
        return false;
    }
}
