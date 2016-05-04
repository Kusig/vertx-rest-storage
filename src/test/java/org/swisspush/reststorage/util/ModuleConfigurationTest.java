package org.swisspush.reststorage.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.swisspush.reststorage.util.ModuleConfiguration.*;

/**
 * Tests for {@link ModuleConfiguration} class.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class ModuleConfigurationTest {

    @Test
    public void testDefaultConfiguration(TestContext testContext) {
        ModuleConfiguration config = new ModuleConfiguration();
        testContext.assertEquals(config.getRoot(), ".");
        testContext.assertEquals(config.getStorageType(), StorageType.filesystem);
        testContext.assertEquals(config.getPort(), 8989);
        testContext.assertEquals(config.getPrefix(), "");
        testContext.assertEquals(config.getStorageAddress(), "resource-storage");
        testContext.assertNull(config.getEditorConfig());
        testContext.assertEquals(config.getRedisHost(), "localhost");
        testContext.assertEquals(config.getRedisPort(), 6379);
        testContext.assertEquals(config.getExpirablePrefix(), "rest-storage:expirable");
        testContext.assertEquals(config.getResourcesPrefix(), "rest-storage:resources");
        testContext.assertEquals(config.getCollectionsPrefix(), "rest-storage:collections");
        testContext.assertEquals(config.getDeltaResourcesPrefix(), "delta:resources");
        testContext.assertEquals(config.getDeltaEtagsPrefix(), "delta:etags");
        testContext.assertEquals(config.getResourceCleanupAmount(), 100000L);
        testContext.assertEquals(config.getLockPrefix(), "rest-storage:locks");
    }

    @Test
    public void testOverrideConfiguration(TestContext testContext){
        ModuleConfiguration config = with()
                .redisHost("anotherhost")
                .redisPort(1234)
                .editorConfig(new JsonObject().put("myKey", "myValue"))
                .build();

        // default values
        testContext.assertEquals(config.getRoot(), ".");
        testContext.assertEquals(config.getStorageType(), StorageType.filesystem);
        testContext.assertEquals(config.getPort(), 8989);
        testContext.assertEquals(config.getPrefix(), "");
        testContext.assertEquals(config.getStorageAddress(), "resource-storage");
        testContext.assertEquals(config.getExpirablePrefix(), "rest-storage:expirable");
        testContext.assertEquals(config.getResourcesPrefix(), "rest-storage:resources");
        testContext.assertEquals(config.getCollectionsPrefix(), "rest-storage:collections");
        testContext.assertEquals(config.getDeltaResourcesPrefix(), "delta:resources");
        testContext.assertEquals(config.getDeltaEtagsPrefix(), "delta:etags");
        testContext.assertEquals(config.getResourceCleanupAmount(), 100000L);
        testContext.assertEquals(config.getLockPrefix(), "rest-storage:locks");


            // overriden values
        testContext.assertEquals(config.getRedisHost(), "anotherhost");
        testContext.assertEquals(config.getRedisPort(), 1234);
        testContext.assertNotNull(config.getEditorConfig());
        testContext.assertTrue(config.getEditorConfig().containsKey("myKey"));
        testContext.assertEquals(config.getEditorConfig().getString("myKey"), "myValue");
    }

    @Test
    public void testGetDefaultAsJsonObject(TestContext testContext){
        ModuleConfiguration config = new ModuleConfiguration();
        JsonObject json = config.asJsonObject();

        testContext.assertEquals(json.getString(PROP_ROOT), ".");
        testContext.assertEquals(json.getString(PROP_STORAGE_TYPE), StorageType.filesystem.name());
        testContext.assertEquals(json.getInteger(PROP_PORT), 8989);
        testContext.assertEquals(json.getString(PROP_PREFIX), "");
        testContext.assertEquals(json.getString(PROP_STORAGE_ADDRESS), "resource-storage");
        testContext.assertNull(json.getJsonObject(PROP_EDITOR_CONFIG));
        testContext.assertEquals(json.getString(PROP_REDIS_HOST), "localhost");
        testContext.assertEquals(json.getInteger(PROP_REDIS_PORT), 6379);
        testContext.assertEquals(json.getString(PROP_EXP_PREFIX), "rest-storage:expirable");
        testContext.assertEquals(json.getString(PROP_RES_PREFIX), "rest-storage:resources");
        testContext.assertEquals(json.getString(PROP_COL_PREFIX), "rest-storage:collections");
        testContext.assertEquals(json.getString(PROP_DELTA_RES_PREFIX), "delta:resources");
        testContext.assertEquals(json.getString(PROP_DELTA_ETAGS_PREFIX), "delta:etags");
        testContext.assertEquals(json.getLong(PROP_RES_CLEANUP_AMMOUNT), 100000L);
        testContext.assertEquals(json.getString(PROP_LOCK_PREFIX), "rest-storage:locks");
    }

    @Test
    public void testGetOverridenAsJsonObject(TestContext testContext){

        ModuleConfiguration config = with()
                .redisHost("anotherhost")
                .redisPort(1234)
                .editorConfig(new JsonObject().put("myKey", "myValue"))
                .build();

        JsonObject json = config.asJsonObject();

        // default values
        testContext.assertEquals(json.getString(PROP_ROOT), ".");
        testContext.assertEquals(json.getString(PROP_STORAGE_TYPE), StorageType.filesystem.name());
        testContext.assertEquals(json.getInteger(PROP_PORT), 8989);
        testContext.assertEquals(json.getString(PROP_PREFIX), "");
        testContext.assertEquals(json.getString(PROP_STORAGE_ADDRESS), "resource-storage");
        testContext.assertEquals(json.getString(PROP_EXP_PREFIX), "rest-storage:expirable");
        testContext.assertEquals(json.getString(PROP_RES_PREFIX), "rest-storage:resources");
        testContext.assertEquals(json.getString(PROP_COL_PREFIX), "rest-storage:collections");
        testContext.assertEquals(json.getString(PROP_DELTA_RES_PREFIX), "delta:resources");
        testContext.assertEquals(json.getString(PROP_DELTA_ETAGS_PREFIX), "delta:etags");
        testContext.assertEquals(json.getLong(PROP_RES_CLEANUP_AMMOUNT), 100000L);
        testContext.assertEquals(json.getString(PROP_LOCK_PREFIX), "rest-storage:locks");


        // overriden values
        testContext.assertEquals(json.getString(PROP_REDIS_HOST), "anotherhost");
        testContext.assertEquals(json.getInteger(PROP_REDIS_PORT), 1234);

        testContext.assertNotNull(json.getJsonObject(PROP_EDITOR_CONFIG));
        testContext.assertTrue(json.getJsonObject(PROP_EDITOR_CONFIG).containsKey("myKey"));
        testContext.assertEquals(json.getJsonObject(PROP_EDITOR_CONFIG).getString("myKey"), "myValue");
    }

    @Test
    public void testGetDefaultFromJsonObject(TestContext testContext){
        JsonObject json  = new ModuleConfiguration().asJsonObject();
        ModuleConfiguration config = fromJsonObject(json);

        testContext.assertEquals(config.getRoot(), ".");
        testContext.assertEquals(config.getStorageType(), StorageType.filesystem);
        testContext.assertEquals(config.getPort(), 8989);
        testContext.assertEquals(config.getPrefix(), "");
        testContext.assertEquals(config.getStorageAddress(), "resource-storage");
        testContext.assertNull(config.getEditorConfig());
        testContext.assertEquals(config.getRedisHost(), "localhost");
        testContext.assertEquals(config.getRedisPort(), 6379);
        testContext.assertEquals(config.getExpirablePrefix(), "rest-storage:expirable");
        testContext.assertEquals(config.getResourcesPrefix(), "rest-storage:resources");
        testContext.assertEquals(config.getCollectionsPrefix(), "rest-storage:collections");
        testContext.assertEquals(config.getDeltaResourcesPrefix(), "delta:resources");
        testContext.assertEquals(config.getDeltaEtagsPrefix(), "delta:etags");
        testContext.assertEquals(config.getResourceCleanupAmount(), 100000L);
        testContext.assertEquals(config.getLockPrefix(), "rest-storage:locks");

    }

    @Test
    public void testGetOverridenFromJsonObject(TestContext testContext){

        JsonObject json = new JsonObject();
        json.put(PROP_ROOT, "newroot");
        json.put(PROP_STORAGE_TYPE, "redis");
        json.put(PROP_PORT, 1234);
        json.put(PROP_PREFIX, "newprefix");
        json.put(PROP_STORAGE_ADDRESS, "newStorageAddress");
        json.put(PROP_EDITOR_CONFIG, new JsonObject().put("myKey", "myValue"));
        json.put(PROP_REDIS_HOST, "newredishost");
        json.put(PROP_REDIS_PORT, 4321);
        json.put(PROP_EXP_PREFIX, "newExpirablePrefix");
        json.put(PROP_RES_PREFIX, "newResourcesPrefix");
        json.put(PROP_COL_PREFIX, "newCollectionsPrefix");
        json.put(PROP_DELTA_RES_PREFIX, "newDeltaResourcesPrefix");
        json.put(PROP_DELTA_ETAGS_PREFIX, "newDeltaEtagsPrefix");
        json.put(PROP_RES_CLEANUP_AMMOUNT, 999L);
        json.put(PROP_LOCK_PREFIX, "newLockPrefix");

        ModuleConfiguration config = fromJsonObject(json);
        testContext.assertEquals(config.getRoot(), "newroot");
        testContext.assertEquals(config.getStorageType(), StorageType.redis);
        testContext.assertEquals(config.getPort(), 1234);
        testContext.assertEquals(config.getPrefix(), "newprefix");
        testContext.assertEquals(config.getStorageAddress(), "newStorageAddress");

        testContext.assertNotNull(config.getEditorConfig());
        testContext.assertTrue(config.getEditorConfig().containsKey("myKey"));
        testContext.assertEquals(config.getEditorConfig().getString("myKey"), "myValue");

        testContext.assertEquals(config.getRedisHost(), "newredishost");
        testContext.assertEquals(config.getRedisPort(), 4321);
        testContext.assertEquals(config.getExpirablePrefix(), "newExpirablePrefix");
        testContext.assertEquals(config.getResourcesPrefix(), "newResourcesPrefix");
        testContext.assertEquals(config.getCollectionsPrefix(), "newCollectionsPrefix");
        testContext.assertEquals(config.getDeltaResourcesPrefix(), "newDeltaResourcesPrefix");
        testContext.assertEquals(config.getDeltaEtagsPrefix(), "newDeltaEtagsPrefix");
        testContext.assertEquals(config.getResourceCleanupAmount(), 999L);
        testContext.assertEquals(config.getLockPrefix(), "newLockPrefix");
    }
}