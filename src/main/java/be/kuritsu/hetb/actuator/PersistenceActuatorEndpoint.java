package be.kuritsu.hetb.actuator;

import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "persistent-storage")
public class PersistenceActuatorEndpoint {

    public enum StorageType {
        DB
    }

    private final Map<StorageType, String> storageInfo;

    public PersistenceActuatorEndpoint(Map<StorageType, String> storageInfo) {
        this.storageInfo = storageInfo;
    }

    @ReadOperation()
    public Map<StorageType, String> persistentStorageInfo() {
        return storageInfo;
    }
}
