package peterstuck.coursewebsitebackend.utils;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;

public class JsonFilter {

    public static MappingJacksonValue filter(Object obj, String filterName, String[] exceptFields) {
        SimpleBeanPropertyFilter simpleBeanPropertyFilter =
                SimpleBeanPropertyFilter.serializeAllExcept(
                        (exceptFields != null ? String.join(" ", exceptFields) : "")
                );

        FilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter(filterName, simpleBeanPropertyFilter);

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(obj);
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

}
