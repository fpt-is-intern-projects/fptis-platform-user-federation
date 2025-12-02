package fpt.is.bnk.federation;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.StorageId;
import org.jboss.logging.Logger;

import java.util.stream.Stream;

/**
 * Admin 12/1/2025
 *
 **/
public class RemoteUserAdapter extends AbstractUserAdapterFederatedStorage {

    private static final Logger logger = Logger.getLogger(RemoteUserAdapter.class);

    private final RemoteUser remoteUser;
    private final String federatedId;

    public RemoteUserAdapter(
            KeycloakSession session,
            RealmModel realm,
            ComponentModel storageProviderModel,
            RemoteUser remoteUser
    ) {
        super(session, realm, storageProviderModel);
        this.remoteUser = remoteUser;
        this.federatedId = StorageId.keycloakId(storageProviderModel, remoteUser.getUsername());
        logger.infof("RemoteUserAdapter created for user: %s, federatedId: %s",
                remoteUser.getUsername(), federatedId);
    }

    @Override
    public String getId() {
        logger.infof("getId() called, returning: %s", federatedId);
        return federatedId;
    }

    @Override
    public String getUsername() {
        logger.infof("getUsername() called, returning: %s", remoteUser.getUsername());
        return remoteUser.getUsername();
    }

    @Override
    public String getFirstName() {
        return remoteUser.getFirstName();
    }

    @Override
    public String getLastName() {
        return remoteUser.getLastName();
    }

    @Override
    public String getEmail() {
        return remoteUser.getEmail();
    }

    @Override
    public boolean isEnabled() {
        logger.infof("isEnabled() called for user %s, returning: %s",
                remoteUser.getUsername(), remoteUser.getActive());
        return remoteUser.getActive();
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public Long getCreatedTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public String getFederationLink() {
        return storageProviderModel.getId();
    }

    @Override
    public String getServiceAccountClientLink() {
        return null;
    }

    @Override
    public Stream<String> getRequiredActionsStream() {
        logger.infof("getRequiredActionsStream() called for user %s", remoteUser.getUsername());
        return Stream.empty();
    }

    @Override
    public void addRequiredAction(String action) {
    }

    @Override
    public void removeRequiredAction(String action) {
    }

    @Override
    public void setUsername(String s) {

    }
}
