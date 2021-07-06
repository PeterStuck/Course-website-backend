package peterstuck.coursewebsitebackend.resources;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestRequestUtils {

    public static ObjectMapper mapper;
    private MockMvc mvc;

    private final Class ITEM_CLASS;
    private final String FILTER_NAME;

    public TestRequestUtils(Class itemClass, MockMvc mvc, String filterName) {
        ITEM_CLASS = itemClass;
        FILTER_NAME = filterName;
        this.mvc = mvc;

        mapper = new ObjectMapper();
    }

    public List<?> makeRequestToGetItems(String path, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response =  mvc.perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        return mapper.readValue(response.getContentAsString(), CollectionsTypeFactory.listOf(ITEM_CLASS));
    }

    public MockHttpServletResponse makeRequestToGetSingleItem(String path, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    public MockHttpServletResponse makePutRequest(String path, Object content, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonFilter.castObjectToJsonString(content, FILTER_NAME, null)))
                .andExpect(expectedStatus)
                .andReturn().getResponse();
    }

    public MockHttpServletResponse makePostRequest(String path, Object content, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonFilter.castObjectToJsonString(content, FILTER_NAME, null)))
                .andExpect(expectedStatus)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    public MockHttpServletResponse makeDeleteRequest(String path, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(delete(path))
                .andExpect(expectedStatus)
                .andReturn().getResponse();
    }

    static class CollectionsTypeFactory {
        static JavaType listOf(Class itemClass) {
            return TypeFactory.defaultInstance().constructCollectionType(List.class, itemClass);
        }
    }
}
