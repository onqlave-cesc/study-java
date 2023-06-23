package onqlave_key_manager.services;

import onqlave_key_manager.types.KeyID;

import java.util.UUID;

public interface IDService {
    public String newStringID();
    public KeyID newKeyID() throws Exception;
}

class IdService implements IDService {
    private final CPRNGService randomService;


    public IdService(CPRNGService randomService) {
        this.randomService = randomService;
    }

    @Override
    public String newStringID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public KeyID newKeyID() throws Exception {
        return new KeyID(randomService.getRandomInt());
    }
}