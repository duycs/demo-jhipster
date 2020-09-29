package io.github.jhipster.sample.web.rest;

import io.github.jhipster.sample.domain.User;
import io.github.jhipster.sample.security.jwt.JWTFilter;
import io.github.jhipster.sample.security.jwt.TokenProvider;
import io.github.jhipster.sample.service.AuthenticationService;
import io.github.jhipster.sample.service.LdapProvider;
import io.github.jhipster.sample.service.UserService;
import io.github.jhipster.sample.web.rest.vm.LoginVM;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final Logger log = LoggerFactory.getLogger(UserJWTController.class);
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserJWTController(TokenProvider tokenProvider, UserService userService, AuthenticationService authenticationService, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) throws Exception {
        String username = loginVM.getUsername();
        String password = loginVM.getPassword();

//        loginVM.setIsAuthenticationServerLogin(true);
//        loginVM.setServerUrl("ldap.forumsys.com");
//
//        boolean isCheckedLdapLogin = loginVM.getIsAuthenticationServerLogin() != null
//            && loginVM.getIsAuthenticationServerLogin() == true ? true : false;
//
//        // Ldap login
//        if (isCheckedLdapLogin) {
//            User userAuthorized = authenticationService.getUserAuthorized(loginVM.getServerUrl(), username, password);
//            if (userAuthorized == null) {
//                log.error("User unauthorized");
//                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//            }
//
//            username = userAuthorized.getLogin();
//            //password = ldapServer.getPasswordUserDefault();
//            password = "abc@123";
//        }

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }
    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
