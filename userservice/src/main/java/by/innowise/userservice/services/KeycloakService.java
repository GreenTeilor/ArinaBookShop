package by.innowise.userservice.services;

import by.innowise.userservice.dto.RefreshAndAccessTokenDto;
import by.innowise.userservice.exceptions.UserNotCreatedException;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KeycloakService {
    Optional<UserRepresentation> getUserByUsername(String username);
    UUID createUser(UserRepresentation userRepresentation) throws UserNotCreatedException;
    void assignRealmRolesToUser(UUID userId, List<RoleRepresentation> roles);
    Optional<RoleRepresentation> getRealmRoleRepresentation(String name);
    Optional<RefreshAndAccessTokenDto> getUserTokensByRefreshToken(String refreshToken);
    Optional<RefreshAndAccessTokenDto> getUserTokensByCredentials(String username, String password);
    void logout(UUID userId);
}
