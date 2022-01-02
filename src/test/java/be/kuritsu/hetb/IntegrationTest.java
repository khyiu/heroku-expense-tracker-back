package be.kuritsu.hetb;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import be.kuritsu.hetb.security.SecurityContextService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @SpyBean
    public SecurityContextService securityContextService;

    @Before
    public void initSecurityContextServiceSpy() {
        doReturn("testUser")
                .when(securityContextService)
                .getAuthenticatedUserName();
    }
}
