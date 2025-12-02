package fpt.is.bnk.federation;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin 12/1/2025
 *
 **/
public class RemoteUserStorageProviderFactory
        implements UserStorageProviderFactory<RemoteUserStorageProvider> {

    public static final String PROVIDER_ID = "custom-user-storage";

    @Override
    public RemoteUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new RemoteUserStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "FPT IS Platform - Remote User Provider - Phát triển bởi Huỳnh Đức Phú";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> config = new ArrayList<>();

        ProviderConfigProperty remoteUrl = new ProviderConfigProperty();
        remoteUrl.setName("remoteUrl");
        remoteUrl.setLabel("Remote API Base URL");
        remoteUrl.setType(ProviderConfigProperty.STRING_TYPE);
        remoteUrl.setDefaultValue("http://localhost:8080/api");
        remoteUrl.setHelpText("Base URL backend để Keycloak truy cập user");

        ProviderConfigProperty secret = new ProviderConfigProperty();
        secret.setName("internalSecret");
        secret.setLabel("Internal Secret Key");
        secret.setType(ProviderConfigProperty.STRING_TYPE);
        secret.setHelpText("Secret key để backend xác thực request từ Keycloak");

        config.add(remoteUrl);
        config.add(secret);

        return config;
    }
}
