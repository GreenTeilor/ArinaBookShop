package by.innowise.userservice.services.impl;

import by.innowise.userservice.dto.RefreshAndAccessTokenDto;
import by.innowise.userservice.exceptions.Messages;
import by.innowise.userservice.exceptions.UserNotCreatedException;
import by.innowise.userservice.services.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.NotAuthorizedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;
    private final RestTemplate restTemplate;

    @Value("${keycloak.uri}")
    private String keycloakURI;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.clientId}")
    private String keycloakClientId;

    @Value("${keycloak.clientSecret}")
    private String keycloakClientSecret;

    @Override
    public Optional<UserRepresentation> getUserByUsername(String username) {
        return keycloak.realm(keycloakRealm).users().search(username).stream().filter(u ->
                u.getUsername().equals(username)).findAny();
    }

    @Override
    public UUID createUser(UserRepresentation userRepresentation) throws UserNotCreatedException {
        keycloak.realm(keycloakRealm).users().create(userRepresentation);
        return UUID.fromString(getUserByUsername(userRepresentation.getUsername()).
                orElseThrow(() -> new UserNotCreatedException(Messages.USER_NOT_CREATED)).getId());
    }

    @Override
    public void assignRealmRolesToUser(UUID userId, List<RoleRepresentation> roles) {
        keycloak.realm(keycloakRealm).users().get(userId.toString()).roles().realmLevel().add(roles);
    }

    @Override
    public Optional<RoleRepresentation> getRealmRoleRepresentation(String name) {
        return keycloak.realm(keycloakRealm).roles().list().stream().filter(r ->
                r.getName().equals(name)).findAny();
    }

    @Override
    public Optional<RefreshAndAccessTokenDto> getUserTokensByRefreshToken(String refreshToken) {
        //Desired functionality is not supported
        //by java keycloak client,
        //so use RestTemplate here
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);
        map.add("client_id", keycloakClientId);
        map.add("client_secret", keycloakClientSecret);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        try {
            return Optional.ofNullable(restTemplate.postForObject(
                    keycloakURI + "/realms/" + keycloakRealm + "/protocol/openid-connect/token",
                    entity, RefreshAndAccessTokenDto.class));
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RefreshAndAccessTokenDto> getUserTokensByCredentials(String username, String password) {
        TokenManager tokenManager = Keycloak.getInstance(keycloakURI, keycloakRealm, username, password,
                keycloakClientId, keycloakClientSecret).tokenManager();
        try {
            String accessToken = tokenManager.getAccessTokenString();
            String refreshToken = tokenManager.refreshToken().getRefreshToken();
            return Optional.ofNullable(RefreshAndAccessTokenDto.builder().
                    refreshToken(refreshToken).
                    accessToken(accessToken).build());
        } catch (NotAuthorizedException e) {
            return Optional.empty();
        }
    }

    @Override
    public void logout(UUID userId) {
        keycloak.realm(keycloakRealm).users().get(userId.toString()).logout();
    }
}
