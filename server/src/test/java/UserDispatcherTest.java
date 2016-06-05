import com.game.server.userservice.UserDispatcher;
import com.game.server.userservice.UserObject;
import com.game.server.userservice.UserService;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import protocol.Packet.JsonPacketFactory;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Created by Lee on 2016-06-02.
 */
public class UserDispatcherTest {
    /* for database */
    private static final String JDBC_DRIVER = org.h2.Driver.class.getName();
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";


    @BeforeClass
    public static void createSchema() throws Exception {
        RunScript.execute(JDBC_URL, USER, PASSWORD, "server/src/test/resources/schema.sql", Charset.forName("UTF-8"), false);
    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = readDataSet();
        cleanlyInsert(dataSet);
    }

    private IDataSet readDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new File("server/src/test/resources/dataset.xml"));
    }

    private void cleanlyInsert(IDataSet dataSet) throws Exception {
        IDatabaseTester databaseTester = new JdbcDatabaseTester(
                "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    private Connection getConnection() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(JDBC_URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);

        return dataSource.getConnection();
    }


    /* test start here */
    @Test
    public void joinTest() {
        // service and dispatcher
        UserService userService = new UserService();
        UserDispatcher userDispatcher = new UserDispatcher(userService);
        assertNotNull(userService);
        assertNotNull(userDispatcher);

        // json packet factory
        JsonPacketFactory packetFactory = new JsonPacketFactory();
        assertNotNull(packetFactory);

        // json request
        JSONObject request = packetFactory.join("admin3", "1234", "일지매", 1);
        assertNotNull(request);

    }
    @Test
    public void loginTest() {
        // service and dispatcher
        UserService userService = new UserService();
        UserDispatcher userDispatcher = new UserDispatcher(userService);
        assertNotNull(userService);
        assertNotNull(userDispatcher);

        // json packet factory
        JsonPacketFactory packetFactory = new JsonPacketFactory();
        assertNotNull(packetFactory);

        // json request
        JSONObject request = packetFactory.login("admin", "1234");
        assertNotNull(request);

        // Some channel
        EmbeddedChannel testChannel = new EmbeddedChannel();
        assertNotNull(testChannel);

        String user_id = (String) request.get("user_id");
        String user_pw = (String) request.get("user_pw");
        assertEquals("admin", user_id);
        assertEquals("1234", user_pw);

        try {
            userDispatcher.login(getConnection(), testChannel, request);

            UserObject testUser = userDispatcher.getUser();
            assertEquals(1, testUser.getUuid());
            assertEquals(1, testUser.getLevel());
            assertEquals(1, testUser.getJobId());
            assertEquals("홍길동", testUser.getName());
            assertEquals(1, testUser.getMapId());
            assertEquals(100, testUser.getX());
            assertEquals(100, testUser.getY());

            String notifyData = (String) testChannel.readOutbound();
            JSONObject notifyPacket = (JSONObject) JSONValue.parse(notifyData);
            assertEquals("notify", notifyPacket.get("type"));
            assertEquals("login success", notifyPacket.get("content"));

            String characterData = (String) testChannel.readOutbound();
            JSONObject characterPacket = (JSONObject) JSONValue.parse(characterData);
            assertEquals("character", characterPacket.get("type"));
            assertEquals((long)1, characterPacket.get("id"));
            assertEquals("홍길동", characterPacket.get("name"));
            assertEquals((long)1, characterPacket.get("job_id"));
            assertEquals((long)1, characterPacket.get("level"));

            String moveData = (String) testChannel.readOutbound();
            JSONObject movePacket = (JSONObject) JSONValue.parse(moveData);
            assertEquals("move", movePacket.get("type"));
            assertEquals((long)1, movePacket.get("id"));
            assertEquals((long)1, movePacket.get("dest_map_id"));
            assertEquals((long)100, movePacket.get("dest_x"));
            assertEquals((long)100, movePacket.get("dest_y"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void dbView() {
        try {
            String sql = "SELECT * FROM USERS;";
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int uuid = rs.getInt("ID");
                String user_id = rs.getString("USER_ID");
                String user_pw = rs.getString("USER_PW");
                String user_name = rs.getString("USER_NAME");
                System.out.println(uuid);
                System.out.println(user_id);
                System.out.println(user_pw);
                System.out.println(user_name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}