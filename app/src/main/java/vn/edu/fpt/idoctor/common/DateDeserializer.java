package vn.edu.fpt.idoctor.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by NamBC on 3/26/2018.
 */

public class DateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String date = element.getAsString();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            System.err.println("Failed to parse Date due to:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}