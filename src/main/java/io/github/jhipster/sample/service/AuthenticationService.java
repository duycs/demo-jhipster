package io.github.jhipster.sample.service;

import com.google.common.base.Strings;
import io.github.jhipster.sample.domain.User;
import io.github.jhipster.sample.domain.User_;
import io.github.jhipster.sample.service.dto.AuthenticationServerDTO;
import io.github.jhipster.sample.service.dto.LdapServers;
import io.github.jhipster.sample.service.dto.UserDTO;
import io.github.jhipster.sample.web.rest.UserJWTController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private UserService userService;
    private LdapProvider ldapProvider;

    public AuthenticationService(UserService userService, LdapProvider ldapProvider) {
        this.userService = userService;
        this.ldapProvider = ldapProvider;
    }

    public List<AuthenticationServerDTO> getAuthenticationServerDTO() throws Exception {
        List<AuthenticationServerDTO> authenticationServerDTOS = ldapProvider.getAuthenticationServers();
        if (authenticationServerDTOS == null)
            return new ArrayList<>();

        return authenticationServerDTOS;
    }

    public User getUserAuthorized(String serverUrl, String username, String password) throws Exception {
        LdapServers.Server ldapServer = ldapProvider.getServerByUrl(serverUrl);
        if (ldapServer == null || !Boolean.parseBoolean(ldapServer.getEnable())) {
            log.error("Server is not enable");
            return null;
        }

        Hashtable<String, String> userAttributes = null;
        String userEmailLdap = "";

        // Check username unique is CN or email or...at userDnPattern setting

        userAttributes = ldapProvider.getUserMappingAttribute(ldapServer, username, password);
        if (userAttributes == null) {
            log.error("User login don't have attribute");
            return null;
        }

//         Check user email ldap existing then authorized
        userEmailLdap = userAttributes.get(User_.email.getName());
        if (Strings.isNullOrEmpty(userEmailLdap)) {
            log.error("User login don't have an email or email invalid ", userEmailLdap);
            return null;
        }

        Optional<User> userExisting = userService.findOneByEmail(userEmailLdap);
        if (userExisting.isPresent())
            return userExisting.get();

        //Optional<User> userExisting = userService.findOneByLogin(username);
//        if(userExisting.isPresent())
//            return userExisting.get();

        // Create new user ldap with default password, don't store origin password
        AuthenticationServerDTO authenticationServerDTO = ldapProvider.getAuthenticationServer(ldapServer.getUrl());
        User userCreated = userService.createNewAuthenticatedUser(authenticationServerDTO, userAttributes, ldapServer.getPasswordUserDefault());
        return userCreated;
    }

    public String getPasswordUserDefault(String serverUrl) {
        LdapServers.Server ldapServer = ldapProvider.getServerByUrl(serverUrl);
        return ldapServer.getPasswordUserDefault();
    }
}
