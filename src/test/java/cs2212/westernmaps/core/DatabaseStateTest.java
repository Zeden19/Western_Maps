package cs2212.westernmaps.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public final class DatabaseStateTest {
    @Test
    public void testEmptyDatabase() throws IOException, JSONException {
        var database = new DatabaseState(List.of(), List.of(), List.of());
        var databaseJson =
                """
                {
                    "accounts": [],
                    "buildings": [],
                    "pois": []
                }
                """;

        var inputStream = new ByteArrayInputStream(databaseJson.getBytes(StandardCharsets.UTF_8));
        var deserialized = DatabaseState.loadFromStream(inputStream);

        var outputStream = new ByteArrayOutputStream();
        database.saveToStream(outputStream);
        var serialized = outputStream.toString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals(databaseJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(database, deserialized);
    }

    @Test
    public void testSampleDatabaseAccounts() throws IOException, JSONException {
        var database = new DatabaseState(
                List.of(new Account("user", "ESIzRA==", false), new Account("developer", "VWZ3iA==", true)),
                List.of(),
                List.of());
        var databaseJson =
                """
                {
                    "accounts": [
                        {
                            "@id": 1,
                            "username": "user",
                            "passwordHash": "ESIzRA==",
                            "developer": false
                        },
                        {
                            "@id": 2,
                            "username": "developer",
                            "passwordHash": "VWZ3iA==",
                            "developer": true
                        }
                    ],
                    "buildings": [],
                    "pois": []
                }
                """;

        var inputStream = new ByteArrayInputStream(databaseJson.getBytes(StandardCharsets.UTF_8));
        var deserialized = DatabaseState.loadFromStream(inputStream);

        var outputStream = new ByteArrayOutputStream();
        database.saveToStream(outputStream);
        var serialized = outputStream.toString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals(databaseJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(database, deserialized);
    }

    @Test
    public void testSampleDatabaseFull() throws IOException, JSONException {
        var userAccount = new Account("user", "ESIzRA==", false);
        var developerAccount = new Account("developer", "VWZ3iA==", true);

        var floor = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));

        var database = new DatabaseState(
                List.of(userAccount, developerAccount),
                List.of(new Building("Example Building", List.of(floor))),
                List.of(new POI(
                        "Example Restaurant",
                        "A restaurant that has been fabricated for this example.",
                        50,
                        100,
                        Set.of(userAccount),
                        floor,
                        Layer.EATERIES,
                        null)));
        var databaseJson =
                """
                {
                    "accounts": [
                        {
                            "@id": 1,
                            "username": "user",
                            "passwordHash": "ESIzRA==",
                            "developer": false
                        },
                        {
                            "@id": 2,
                            "username": "developer",
                            "passwordHash": "VWZ3iA==",
                            "developer": true
                        }
                    ],
                    "buildings": [
                        {
                            "name": "Example Building",
                            "floors": [
                                {
                                    "@id": 1,
                                    "shortName": "1",
                                    "longName": "First Floor",
                                    "mapPath": "maps/Example Building/First Floor.svg"
                                }
                            ]
                        }
                    ],
                    "pois": [
                        {
                            "name": "Example Restaurant",
                            "description": "A restaurant that has been fabricated for this example.",
                            "x": 50,
                            "y": 100,
                            "favoriteOf": [1],
                            "floor": 1,
                            "layer": "EATERIES"
                        }
                    ]
                }
                """;

        var inputStream = new ByteArrayInputStream(databaseJson.getBytes(StandardCharsets.UTF_8));
        var deserialized = DatabaseState.loadFromStream(inputStream);

        var outputStream = new ByteArrayOutputStream();
        database.saveToStream(outputStream);
        var serialized = outputStream.toString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals(databaseJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(database, deserialized);
    }
}
