package be.kuritsu.hetb;

import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import be.kuritsu.hetb.security.SecurityContextService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @SpyBean
    public SecurityContextService securityContextService;

    @BeforeEach
    public void initSecurityContextServiceSpy() {
        doReturn("testUser")
                .when(securityContextService)
                .getAuthenticatedUserName();
    }
}
