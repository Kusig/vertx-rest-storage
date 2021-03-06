package org.swisspush.reststorage.util;

import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Utility class to configure the RestStorageModule.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class ModuleConfiguration {

    public enum StorageType {
        filesystem, redis
    }

    private String             root                          = "."                       ;
    private StorageType        storageType                   = StorageType.filesystem    ;
    private int                port                          = 8989                      ;
    private String             prefix                        = ""                        ;
    private String             storageAddress                = "resource-storage"        ;
    private Map<String,String> editorConfig                  = null                      ;
    private String             redisHost                     = "localhost"               ;
    private int                redisPort                     = 6379                      ;
    private String             redisAuth                     = null                      ;
    private String             expirablePrefix               = "rest-storage:expirable"  ;
    private String             resourcesPrefix               = "rest-storage:resources"  ;
    private String             collectionsPrefix             = "rest-storage:collections";
    private String             deltaResourcesPrefix          = "delta:resources"         ;
    private String             deltaEtagsPrefix              = "delta:etags"             ;
    private long               resourceCleanupAmount         = 100_000L                  ;
    private String             lockPrefix                    = "rest-storage:locks"      ;
    private boolean            confirmCollectionDelete       = false                     ;
    private boolean            rejectStorageWriteOnLowMemory = false                     ;
    private long               freeMemoryCheckIntervalMs     = 60_000L                   ;
    private boolean            return200onDeleteNonExisting  = false                     ;

    public ModuleConfiguration root(String root) {
        this.root = root;
        return this;
    }

    public ModuleConfiguration storageType(StorageType storageType) {
        this.storageType = storageType;
        return this;
    }

    public ModuleConfiguration storageTypeFromString(String storageType) throws IllegalArgumentException {
        this.storageType = StorageType.valueOf(storageType); // let it throw IAEx in case of unknown string value
        return this;
    }

    public ModuleConfiguration port(int port) {
        this.port = port;
        return this;
    }

    public ModuleConfiguration prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ModuleConfiguration storageAddress(String storageAddress) {
        this.storageAddress = storageAddress;
        return this;
    }

    public ModuleConfiguration editorConfig(Map<String,String> editorConfig) {
        this.editorConfig = editorConfig;
        return this;
    }

    public ModuleConfiguration redisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    public ModuleConfiguration redisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    public ModuleConfiguration redisAuth(String redisAuth) {
        this.redisAuth = redisAuth;
        return this;
    }

    public ModuleConfiguration expirablePrefix(String expirablePrefix) {
        this.expirablePrefix = expirablePrefix;
        return this;
    }

    public ModuleConfiguration resourcesPrefix(String resourcesPrefix) {
        this.resourcesPrefix = resourcesPrefix;
        return this;
    }

    public ModuleConfiguration collectionsPrefix(String collectionsPrefix) {
        this.collectionsPrefix = collectionsPrefix;
        return this;
    }

    public ModuleConfiguration deltaResourcesPrefix(String deltaResourcesPrefix) {
        this.deltaResourcesPrefix = deltaResourcesPrefix;
        return this;
    }

    public ModuleConfiguration deltaEtagsPrefix(String deltaEtagsPrefix) {
        this.deltaEtagsPrefix = deltaEtagsPrefix;
        return this;
    }

    public ModuleConfiguration resourceCleanupAmount(long resourceCleanupAmount) {
        this.resourceCleanupAmount = resourceCleanupAmount;
        return this;
    }

    public ModuleConfiguration lockPrefix(String lockPrefix) {
        this.lockPrefix = lockPrefix;
        return this;
    }

    public ModuleConfiguration confirmCollectionDelete(boolean confirmCollectionDelete) {
        this.confirmCollectionDelete = confirmCollectionDelete;
        return this;
    }

    public ModuleConfiguration rejectStorageWriteOnLowMemory(boolean rejectStorageWriteOnLowMemory) {
        this.rejectStorageWriteOnLowMemory = rejectStorageWriteOnLowMemory;
        return this;
    }

    public ModuleConfiguration freeMemoryCheckIntervalMs(long freeMemoryCheckIntervalMs) {
        this.freeMemoryCheckIntervalMs = freeMemoryCheckIntervalMs;
        return this;
    }

    public ModuleConfiguration return200onDeleteNonExisting(boolean deleteNonExistingReturn200) {
        this.return200onDeleteNonExisting = deleteNonExistingReturn200;
        return this;
    }



    public String getRoot() {
        return root;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public int getPort() {
        return port;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getStorageAddress() {
        return storageAddress;
    }

    public Map<String, String> getEditorConfig() {
        return editorConfig;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisAuth() {
        return redisAuth;
    }

    public String getExpirablePrefix() {
        return expirablePrefix;
    }

    public String getResourcesPrefix() {
        return resourcesPrefix;
    }

    public String getCollectionsPrefix() {
        return collectionsPrefix;
    }

    public String getDeltaResourcesPrefix() {
        return deltaResourcesPrefix;
    }

    public String getDeltaEtagsPrefix() {
        return deltaEtagsPrefix;
    }

    public long getResourceCleanupAmount() {
        return resourceCleanupAmount;
    }

    public String getLockPrefix() { return lockPrefix; }

    public boolean isConfirmCollectionDelete() { return confirmCollectionDelete; }

    public boolean isRejectStorageWriteOnLowMemory() { return rejectStorageWriteOnLowMemory; }

    public long getFreeMemoryCheckIntervalMs() { return freeMemoryCheckIntervalMs; }

    public boolean isReturn200onDeleteNonExisting() {
        return return200onDeleteNonExisting;
    }

    public JsonObject asJsonObject(){
        return JsonObject.mapFrom(this);
    }

    public static ModuleConfiguration fromJsonObject(JsonObject json){
        ModuleConfiguration mc = json.mapTo(ModuleConfiguration.class);
        return mc;
    }

    @Override
    public String toString() {
        return asJsonObject().toString();
    }

}