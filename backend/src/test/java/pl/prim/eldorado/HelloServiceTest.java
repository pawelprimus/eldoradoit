package pl.prim.eldorado;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloServiceTest {

    @Mock
    private RestClient restClient;  // Mock the RestClient

    @Mock
    private ObjectMapper objectMapper;  // Mock ObjectMapper


    @Autowired
    private WebClient.Builder webClientBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void testApply_ShouldReturnResponse_WhenRequestIsValid() {


    }

}
