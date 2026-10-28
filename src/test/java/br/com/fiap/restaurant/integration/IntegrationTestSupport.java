package br.com.fiap.restaurant.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// One container and one Spring context are shared by every integration test class; tables are wiped after each test.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    protected static final String API = "/api/v1";
    protected static final String PASSWORD = "secret123";
    protected static final String ADDRESS = """
            {"street": "Rua das Palmeiras", "number": "456", "neighborhood": "Jardins",
             "city": "São Paulo", "state": "SP", "zipCode": "01414001"}""";

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("restaurant_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void requireDocker() {
        assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is not available");
    }

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry properties) {
        POSTGRES.start();
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
        properties.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        properties.add("spring.docker.compose.enabled", () -> "false");
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute(
                "TRUNCATE TABLE order_items, orders, item, restaurant_user, restaurant, users RESTART IDENTITY CASCADE");
    }

    protected record Session(long id, String login, String token) {
    }

    protected Session newUser(String login) throws Exception {
        long id = register(login);
        return new Session(id, login, login(login, PASSWORD));
    }

    protected long register(String login) throws Exception {
        MvcResult result = mockMvc.perform(post(API + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "User %1$s", "email": "%1$s@mail.com", "login": "%1$s", "password": "%2$s"}
                                """.formatted(login, PASSWORD)))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    protected String login(String login, String password) throws Exception {
        MvcResult result = mockMvc.perform(post(API + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "%s", "password": "%s"}
                                """.formatted(login, password)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    protected long createRestaurant(Session owner, String cnpj) throws Exception {
        MvcResult result = perform(post(API + "/restaurant"), owner, restaurantJson(cnpj))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    protected String restaurantJson(String cnpj) {
        return """
                {"name": "Cantina Bella Vista", "cnpj": "%s", "kitchenType": "ITALIAN_FOOD",
                 "openingAt": "11:00:00", "closingAt": "23:00:00", "address": %s}
                """.formatted(cnpj, ADDRESS);
    }

    protected long createItem(Session member, long restaurantId, String name) throws Exception {
        MvcResult result = perform(post(API + "/restaurant/" + restaurantId + "/item"), member, """
                {"name": "%s", "description": "Prato da casa", "value": 49.90,
                 "imageUrl": "/images/%s.png", "itemType": "JUST_IN_PERSON"}
                """.formatted(name, name))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }

    protected void addEmployee(Session owner, long restaurantId, long userId) throws Exception {
        perform(post(API + "/restaurant/" + restaurantId + "/employee/" + userId), owner)
                .andExpect(status().isOk());
    }

    protected ResultActions perform(MockHttpServletRequestBuilder request, Session session) throws Exception {
        return perform(request, session, null);
    }

    protected ResultActions perform(MockHttpServletRequestBuilder request, Session session, String body) throws Exception {
        if (session != null) {
            request.header(HttpHeaders.AUTHORIZATION, "Bearer " + session.token());
        }
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        return mockMvc.perform(request);
    }

    protected ResultActions getAs(Session session, String path) throws Exception {
        return perform(get(API + path), session);
    }

    protected ResultActions postAs(Session session, String path, String body) throws Exception {
        return perform(post(API + path), session, body);
    }

    protected ResultActions patchAs(Session session, String path, String body) throws Exception {
        return perform(patch(API + path), session, body);
    }

    protected ResultActions deleteAs(Session session, String path) throws Exception {
        return perform(delete(API + path), session);
    }

    protected static long idOf(MvcResult result) throws Exception {
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}
