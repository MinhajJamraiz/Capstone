package application;

// Simple Location class for warehouse slots
public class Location {
   // private double x;
   // private double y;
   // private double z;
    private String id; // optional
    private boolean is_loaded = false;
    public Location(String id) { this.id = id; }
    public String getId() { return id; }
    public boolean getLoaded()
    {
    	return is_loaded;
    }
    public boolean setLoaded(boolean val)
    {
    	this.is_loaded = val;
    	return is_loaded;
    }
    
    //@Override
    /*public String toString() {
        return id != null ? id : "(" + x + "," + y + "," + z + ")";
    }*/
}
