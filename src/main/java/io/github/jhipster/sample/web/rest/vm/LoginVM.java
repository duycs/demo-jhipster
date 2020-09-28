package io.github.jhipster.sample.web.rest.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * View Model object for storing a user's credentials.
 */
public class LoginVM {

    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @NotNull
    @Size(min = 4, max = 100)
    private String password;

    private Boolean rememberMe;

    private Boolean isAuthenticationServerLogin;
    private String serverUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public Boolean getIsAuthenticationServerLogin() {
        return isAuthenticationServerLogin;
    }

    public void setIsAuthenticationServerLogin(Boolean authenticationServerLogin) {
        isAuthenticationServerLogin = authenticationServerLogin;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoginVM{" +
            "username='" + username + '\'' +
            ", rememberMe=" + rememberMe +
            '}';
    }
}
