import org.json.JSONException;
import org.json.JSONObject;

public class SecureMessage {
    private static final String  TAG = "SecureMessage";
    private String m; // message
    private String p; // password
    private String s; // salt
    private String i; // iv
    

    public SecureMessage(String m, String p, String s, String i) {
        super();
        this.m = m;
        this.p = p;
        this.s = s;
        this.i = i;
    }

    @Override
    public String toString() {
        return toJSON();
    }

    public String toJSON() {
        try {
            JSONObject obj=  new JSONObject();
            obj.put("m",getM());
            obj.put("p", getP());
            obj.put("s", getS());
            obj.put("i", getI());
            return obj.toString();
        } catch (JSONException e) {
            //Log.e(TAG,e.getMessage());
            return null;
        }
    }

    public String getM() {
        return m;
    }
    public void setM(String m) {
        this.m = m;
    }
    public String getP() {
        return p;
    }
    public void setP(String p) {
        this.p = p;
    }
    public String getS() {
        return s;
    }
    public void setS(String s) {
        this.s = s;
    }
    public String getI() {
        return i;
    }
    public void setI(String i) {
        this.i = i;
    }

}
