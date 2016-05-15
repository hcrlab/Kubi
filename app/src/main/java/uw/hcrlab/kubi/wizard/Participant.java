package uw.hcrlab.kubi.wizard;

/**
 * Created by Alexander on 5/14/2016.
 */
public class Participant {
    private String key;

    private String id;

    public Participant(String key, String id) {
        this.key = key;
        this.id = id;
    }

    public void setKey(String k) {
        key = k;
    }

    public String getKey() {
        return key;
    }

    public void setId(String i) {
        id = i;
    }

    public String getId() {
        return id;
    }
}
