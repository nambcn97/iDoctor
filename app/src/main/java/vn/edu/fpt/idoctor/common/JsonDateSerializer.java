//package vn.edu.fpt.idoctor.common;
//
///**
// * Created by NamBC on 3/26/2018.
// */
//
//
//@Component
//public class JsonDateSerializer extends JsonSerializer<Date>{
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
//    @Override
//    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
//            throws IOException, JsonProcessingException {
//        String formattedDate = dateFormat.format(date);
//        gen.writeString(formattedDate);
//    }
//}