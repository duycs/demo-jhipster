package io.github.jhipster.sample.service.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "ldap-servers-list")
@Configuration
@Component
@Data
public class LdapServers {
    private List<Server> configurations;

    public List<Server> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Server> configurations) {
        this.configurations = configurations;
    }

    @Data
    public static class Server {
        private String enable;
        private String name;
        private String url;
        private int port;
        private int timeout;
        private List<String> baseDn;
        private List<String> userDn;
        private String prefixMailIsUserDn;
        private String authenticationType;
        private String emailUserMapping;
        private String firstNameUserMapping;
        private String lastNameUserMapping;
        private String passwordUserDefault;

        private String masterUserDn;
        private String masterUserDnValue;
        private String masterUserPassword;
        private String masterBaseDn;

        public String getEnable() {
            return enable;
        }

        public void setEnable(String enable) {
            this.enable = enable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public List<String> getBaseDn() {
            return baseDn;
        }

        public void setBaseDn(List<String> baseDn) {
            this.baseDn = baseDn;
        }

        public List<String> getUserDn() {
            return userDn;
        }

        public void setUserDn(List<String> userDn) {
            this.userDn = userDn;
        }

        public String getPrefixMailIsUserDn() {
            return prefixMailIsUserDn;
        }

        public void setPrefixMailIsUserDn(String prefixMailIsUserDn) {
            this.prefixMailIsUserDn = prefixMailIsUserDn;
        }

        public String getAuthenticationType() {
            return authenticationType;
        }

        public void setAuthenticationType(String authenticationType) {
            this.authenticationType = authenticationType;
        }

        public String getEmailUserMapping() {
            return emailUserMapping;
        }

        public void setEmailUserMapping(String emailUserMapping) {
            this.emailUserMapping = emailUserMapping;
        }

        public String getFirstNameUserMapping() {
            return firstNameUserMapping;
        }

        public void setFirstNameUserMapping(String firstNameUserMapping) {
            this.firstNameUserMapping = firstNameUserMapping;
        }

        public String getLastNameUserMapping() {
            return lastNameUserMapping;
        }

        public void setLastNameUserMapping(String lastNameUserMapping) {
            this.lastNameUserMapping = lastNameUserMapping;
        }

        public String getPasswordUserDefault() {
            return passwordUserDefault;
        }

        public void setPasswordUserDefault(String passwordUserDefault) {
            this.passwordUserDefault = passwordUserDefault;
        }

        public String getMasterUserDn() {
            return masterUserDn;
        }

        public void setMasterUserDn(String masterUserDn) {
            this.masterUserDn = masterUserDn;
        }

        public String getMasterUserDnValue() {
            return masterUserDnValue;
        }

        public void setMasterUserDnValue(String masterUserDnValue) {
            this.masterUserDnValue = masterUserDnValue;
        }

        public String getMasterUserPassword() {
            return masterUserPassword;
        }

        public void setMasterUserPassword(String masterUserPassword) {
            this.masterUserPassword = masterUserPassword;
        }

        public String getMasterBaseDn() {
            return masterBaseDn;
        }

        public void setMasterBaseDn(String masterBaseDn) {
            this.masterBaseDn = masterBaseDn;
        }
    }
}
