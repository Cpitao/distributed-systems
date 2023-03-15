import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static String getDateTime() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return f.format(date);
    }
}
