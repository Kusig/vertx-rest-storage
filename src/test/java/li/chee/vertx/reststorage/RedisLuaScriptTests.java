package li.chee.vertx.reststorage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisLuaScriptTests {

    Jedis jedis = null;

    private final static String prefixResources = "rest-storage:resources";
    private final static String prefixCollections = "rest-storage:collections";

    @Before
    public void connect() {
        jedis = new Jedis("localhost");
    }

    @After
    public void disconnnect() {
        jedis.flushAll();
        jedis.close();
    }

    @Test
    public void putResourcePathDepthIs3() {

        // ACT
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");

        // ASSERT
        assertThat("server", equalTo(jedis.zrangeByScore("rest-storage:collections:nemo", 0d, 9999999999999d).iterator().next()));
        assertThat("test", equalTo(jedis.zrangeByScore("rest-storage:collections:nemo:server", 0d, 9999999999999d).iterator().next()));
        assertThat("test1", equalTo(jedis.zrangeByScore("rest-storage:collections:nemo:server:test", 0d, 9999999999999d).iterator().next()));
        assertThat("test2", equalTo(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test1", 0d, 9999999999999d).iterator().next()));
        assertThat("{\"content\": \"test/test1/test2\"}", equalTo(jedis.get("rest-storage:resources:nemo:server:test:test1:test2")));
    }

    @Test
    public void putResourcePathDepthIs3WithSiblings() {

        // ACT
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");
        evalScriptPut(":nemo:server:test:test11", "{\"content\": \"test/test11\"}");

        // ASSERT
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo", 0d, 9999999999999d).iterator().next(), equalTo("server"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server", 0d, 9999999999999d).iterator().next(), equalTo("test"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test", 0d, 9999999999999d).iterator().next(), equalTo("test1"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test1", 0d, 9999999999999d).iterator().next(), equalTo("test2"));
        assertThat(jedis.get("rest-storage:resources:nemo:server:test:test1:test2"), equalTo("{\"content\": \"test/test1/test2\"}"));
    }

    @Test
    public void getResourcePathDepthIs3() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");

        // ACT
        String value = (String) evalScriptGet(":nemo:server:test:test1:test2");

        // ASSERT
        assertThat("{\"content\": \"test/test1/test2\"}", equalTo(value));
    }

    @Test
    public void getCollectionPathDepthIs3ChildIsResource() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");

        // ACT
        @SuppressWarnings("unchecked")
        List<String> values = (List<String>) evalScriptGet(":nemo:server:test:test1");

        // ASSERT
        assertThat(values.get(0), equalTo("test2"));
    }

    @Test
    public void getCollectionPathDepthIs3HasOtherCollection() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");

        // ACT
        @SuppressWarnings("unchecked")
        List<String> values = (List<String>) evalScriptGet(":nemo:server:test");

        // ASSERT
        assertThat(values.get(0), equalTo("test1:"));
    }

    @Test
    public void deleteResource2BranchesDeleteOnRootNode() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");
        evalScriptPut(":nemo:server:test:test11:test22", "{\"content\": \"test/test1/test2\"}");

        // ACT
        evalScriptDel(":nemo");

        // ASSERT
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test1", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test11:test2"), equalTo(false));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test11", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test11:test22"), equalTo(false));
    }

    @Test
    public void deleteResource2BranchesDeleteOnForkNode() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");
        evalScriptPut(":nemo:server:test:test11:test22", "{\"content\": \"test/test1/test2\"}");

        // ACT
        evalScriptDel(":nemo:server:test");

        // ASSERT
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test1", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test11:test2"), equalTo(false));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test11", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test11:test22"), equalTo(false));
    }

    @Test
    public void deleteResource2BranchesDeleteOneLevelAboveBranch() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");
        evalScriptPut(":nemo:server:test:test11:test22", "{\"content\": \"test/test1/test2\"}");

        // ACT
        evalScriptDel(":nemo:server:test:test1");

        // ASSERT
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo", 0d, 9999999999999d).iterator().next(), equalTo("server"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server", 0d, 9999999999999d).iterator().next(), equalTo("test"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test", 0d, 9999999999999d).iterator().next(), equalTo("test11"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test11", 0d, 9999999999999d).iterator().next(), equalTo("test22"));
        assertThat(jedis.get("rest-storage:resources:nemo:server:test:test11:test22"), equalTo("{\"content\": \"test/test1/test2\"}"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test1", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test1:test2"), equalTo(false));
    }

    @Test
    public void deleteResource2BranchesDeleteOnOneResource() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");
        evalScriptPut(":nemo:server:test:test11:test22", "{\"content\": \"test/test1/test2\"}");

        // ACT
        evalScriptDel(":nemo:server:test:test1:test2");

        // ASSERT
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo", 0d, 9999999999999d).iterator().next(), equalTo("server"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server", 0d, 9999999999999d).iterator().next(), equalTo("test"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test", 0d, 9999999999999d).iterator().next(), equalTo("test11"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test11", 0d, 9999999999999d).iterator().next(), equalTo("test22"));
        assertThat(jedis.get("rest-storage:resources:nemo:server:test:test11:test22"), equalTo("{\"content\": \"test/test1/test2\"}"));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test1", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test11:test2"), equalTo(false));
    }

    @Test
    public void deleteResource2BranchesDeleteOnBothResources() {

        // ARRANGE
        evalScriptPut(":nemo:server:test:test1:test2", "{\"content\": \"test/test1/test2\"}");
        evalScriptPut(":nemo:server:test:test11:test22", "{\"content\": \"test/test1/test2\"}");

        // ACT
        evalScriptDel(":nemo:server:test:test1:test2");
        evalScriptDel(":nemo:server:test:test11:test22");

        // ASSERT
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test1", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test11:test2"), equalTo(false));
        assertThat(jedis.zrangeByScore("rest-storage:collections:nemo:server:test:test11", 0d, 9999999999999d).size(), equalTo(0));
        assertThat(jedis.exists("rest-storage:resources:nemo:server:test:test11:test22"), equalTo(false));
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "serial" })
    private void evalScriptPut(final String resourceName1, final String resourceValue1) {
        String putScript = readScript("put.lua");
        jedis.eval(putScript, new ArrayList() {
            {
                add(resourceName1);
            }
        }, new ArrayList() {
            {
                add(prefixResources);
                add(prefixCollections);
                add("false");
                add("9999999999999");
                add(resourceValue1);
            }
        }
                );
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "serial" })
    private Object evalScriptGet(final String resourceName1) {
        String getScript = readScript("get.lua");
        return jedis.eval(getScript, new ArrayList() {
            {
                add(resourceName1);
            }
        }, new ArrayList() {
            {
                add(prefixResources);
                add(prefixCollections);
                add(String.valueOf(System.currentTimeMillis()));
                add("9999999999999");
            }
        }
                );
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "serial" })
    private void evalScriptDel(final String resourceName) {
        String putScript = readScript("del.lua");
        jedis.eval(putScript, new ArrayList() {
            {
                add(resourceName);
            }
        }, new ArrayList() {
            {
                add(prefixResources);
                add(prefixCollections);
            }
        }
                );
    }

    private String readScript(String scriptFileName) {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(scriptFileName)));
        StringBuilder sb;
        try {
            sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        return sb.toString();
    }
}
