package cs2212.westernmaps.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import javax.annotation.Nullable;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public final class AccountTest {
    private static @Nullable ObjectMapper objectMapper = null;

    @BeforeAll
    public static void beforeAll() {
        objectMapper = Database.createObjectMapper();
    }

    @Test
    public void testSampleUserAccount() throws JsonProcessingException, JSONException {
        var mapper = Objects.requireNonNull(objectMapper);

        // A real password hash will be longer, but this will do for the test.
        var account = new Account("user", new byte[] {0x11, 0x22, 0x33, 0x44}, false);
        var accountJson =
                """
                {
                    "username": "user",
                    "passwordHash": "ESIzRA==",
                    "developer": false
                }
                """;

        var serialized = mapper.writeValueAsString(account);
        var deserialized = mapper.readValue(accountJson, Account.class);

        JSONAssert.assertEquals(accountJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(account, deserialized);
    }

    @Test
    public void testSampleDeveloperAccount() throws JsonProcessingException, JSONException {
        var mapper = Objects.requireNonNull(objectMapper);

        // A real password hash will be longer, but this will do for the test.
        var account = new Account("developer", new byte[] {0x55, 0x66, 0x77, (byte) 0x88}, true);
        var accountJson =
                """
                {
                    "username": "developer",
                    "passwordHash": "VWZ3iA==",
                    "developer": true
                }
                """;

        var serialized = mapper.writeValueAsString(account);
        var deserialized = mapper.readValue(accountJson, Account.class);

        JSONAssert.assertEquals(accountJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(account, deserialized);
    }
}
