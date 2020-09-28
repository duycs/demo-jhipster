package io.github.jhipster.sample.service.dto;

public class AuthenticationServerDTO {
    private String name;
    private String host;
    private int port;
    private String url;
    private String description;
    private String authenticationCategory;
    private String authenticationType;
    private Boolean isEnable;
    private String userEmailMapping;
    private String userFirstNameMapping;
    private String userLastNameMapping;

    public AuthenticationServerDTO(String name, String host, int port, String url, String description,
                                   String authenticationCategory, String authenticationType, Boolean isEnable,
                                   String userEmailMapping, String userFirstNameMapping, String userLastNameMapping) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.url = url;
        this.description = description;
        this.authenticationCategory = authenticationCategory;
        this.authenticationType = authenticationType;
        this.isEnable = isEnable;
        this.userEmailMapping = userEmailMapping;
        this.userFirstNameMapping = userFirstNameMapping;
        this.userLastNameMapping = userLastNameMapping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationCategory() {
        return authenticationCategory;
    }

    public void setAuthenticationCategory(String authenticationCategory) {
        this.authenticationCategory = authenticationCategory;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean enable) {
        isEnable = enable;
    }

    public String getUserEmailMapping() {
        return userEmailMapping;
    }

    public void setUserEmailMapping(String userEmailMapping) {
        this.userEmailMapping = userEmailMapping;
    }

    public String getUserFirstNameMapping() {
        return userFirstNameMapping;
    }

    public void setUserFirstNameMapping(String userFirstNameMapping) {
        this.userFirstNameMapping = userFirstNameMapping;
    }

    public String getUserLastNameMapping() {
        return userLastNameMapping;
    }

    public void setUserLastNameMapping(String userLastNameMapping) {
        this.userLastNameMapping = userLastNameMapping;
    }

    @Override
    public String toString() {
        return "AuthenticationServerDTO{" +
            "name='" + name + '\'' +
            ", host='" + host + '\'' +
            ", port=" + port +
            ", url='" + url + '\'' +
            ", description='" + description + '\'' +
            ", authenticationCategory='" + authenticationCategory + '\'' +
            ", authenticationType='" + authenticationType + '\'' +
            ", isEnable=" + isEnable +
            '}';
    }
}
