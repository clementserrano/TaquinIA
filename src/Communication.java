import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Communication extends HashMap<String, List<Message>> {

    public synchronized List<Message> getAndClear(String nom) {
        List<Message> res = new ArrayList<>(this.get(nom));
        this.put(nom, new ArrayList<>());
        return res;
    }
}
