package fpt.is.bnk.federation;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.StorageId;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Admin 12/1/2025
 *
 **/
public class RemoteUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        UserQueryProvider {

    private static final Logger logger = Logger.getLogger(RemoteUserStorageProvider.class);

    private final KeycloakSession session;
    private final ComponentModel model;
    private final RemoteUserService remoteUserService;

    public RemoteUserStorageProvider(
            KeycloakSession keycloakSession,
            ComponentModel componentModel
    ) {
        this.session = keycloakSession;
        this.model = componentModel;
        this.remoteUserService = new RemoteUserService(componentModel);
        logger.infof("RemoteUserStorageProvider created for model: %s", componentModel.getName());
    }

    @Override
    public boolean supportsCredentialType(String s) {
        return PasswordCredentialModel.PASSWORD.equals(s);
    }

    @Override
    public boolean isConfiguredFor(
            RealmModel realmModel,
            UserModel userModel,
            String s
    ) {
        return supportsCredentialType(s);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        logger.infof("isValid() called for user: %s", userModel.getUsername());

        if (!supportsCredentialType(credentialInput.getType())) {
            logger.warnf("Unsupported credential type: %s", credentialInput.getType());
            return false;
        }

        boolean result = remoteUserService.verifyPassword(
                userModel.getUsername(),
                credentialInput.getChallengeResponse()
        );

        logger.infof("Password validation result for user %s: %s", userModel.getUsername(), result);
        return result;
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String s) {
        logger.infof("getUserByUsername() called with username: %s", s);
        RemoteUser dto = remoteUserService.getUser(s);
        if (dto == null) {
            logger.warnf("User not found: %s", s);
            return null;
        }
        logger.infof("User found, creating adapter for: %s", s);
        return new RemoteUserAdapter(session, realmModel, model, dto);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String s) {
        logger.infof("getUserByEmail() called with email: %s", s);
        RemoteUser dto = remoteUserService.getUserByEmail(s);
        if (dto == null) {
            logger.warnf("User not found by email: %s", s);
            return null;
        }
        logger.infof("User found by email, creating adapter for: %s", s);
        return new RemoteUserAdapter(session, realmModel, model, dto);
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String s) {
        logger.infof("getUserById() called with id: %s", s);
        String externalId = StorageId.externalId(s);
        if (externalId != null) {
            logger.infof("Extracted externalId: %s, looking up by username", externalId);
            return getUserByUsername(realmModel, externalId);
        }
        logger.warnf("Could not extract externalId from: %s", s);
        return null;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return 0;
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, org.keycloak.models.GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }
}
