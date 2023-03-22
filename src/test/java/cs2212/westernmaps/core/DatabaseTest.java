package cs2212.westernmaps.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public final class DatabaseTest {
    @Test
    public void testEmptyDatabase() throws IOException, JSONException {
        var database = new Database(List.of(), List.of(), List.of());
        var databaseJson =
                """
                {
                    "accounts": [],
                    "buildings": [],
                    "pois": []
                }
                """;

        var inputStream = new ByteArrayInputStream(databaseJson.getBytes(StandardCharsets.UTF_8));
        var deserialized = Database.loadFromStream(inputStream);

        var outputStream = new ByteArrayOutputStream();
        database.saveToStream(outputStream);
        var serialized = outputStream.toString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals(databaseJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(database, deserialized);
    }

    @Test
    public void testSampleDatabaseAccounts() throws IOException, JSONException {
        var database = new Database(
                List.of(
                        new Account("user", new byte[] {0x11, 0x22, 0x33, 0x44}, false),
                        new Account("developer", new byte[] {0x55, 0x66, 0x77, (byte) 0x88}, true)),
                List.of(),
                List.of());
        var databaseJson =
                """
                {
                    "accounts": [
                        {
                            "username": "user",
                            "passwordHash": "ESIzRA==",
                            "developer": false
                        },
                        {
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
        var deserialized = Database.loadFromStream(inputStream);

        var outputStream = new ByteArrayOutputStream();
        database.saveToStream(outputStream);
        var serialized = outputStream.toString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals(databaseJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(database, deserialized);
    }

    @Test
    public void testSampleDatabaseFull() throws IOException, JSONException {
        var floor = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));

        var database = new Database(
                List.of(
                        new Account("user", new byte[] {0x11, 0x22, 0x33, 0x44}, false),
                        new Account("developer", new byte[] {0x55, 0x66, 0x77, (byte) 0x88}, true)),
                List.of(new Building("Example Building", List.of(floor))),
                List.of(new POI(
                        "Example Restaurant",
                        "A restaurant that has been fabricated for this example.",
                        50,
                        100,
                        true,
                        floor,
                        Layer.EATERIES)));
        var databaseJson =
                """
                {
                    "accounts": [
                        {
                            "username": "user",
                            "passwordHash": "ESIzRA==",
                            "developer": false
                        },
                        {
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
                            "favorite": true,
                            "floor": 1,
                            "layer": "EATERIES"
                        }
                    ]
                }
                """;

        var inputStream = new ByteArrayInputStream(databaseJson.getBytes(StandardCharsets.UTF_8));
        var deserialized = Database.loadFromStream(inputStream);

        var outputStream = new ByteArrayOutputStream();
        database.saveToStream(outputStream);
        var serialized = outputStream.toString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals(databaseJson, serialized, JSONCompareMode.STRICT);
        Assertions.assertEquals(database, deserialized);
    }
}
