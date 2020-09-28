package io.github.jhipster.sample.service;

import com.google.common.base.Strings;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.jhipster.sample.domain.User_;
import io.github.jhipster.sample.service.dto.AuthenticationServerDTO;
import io.github.jhipster.sample.service.dto.LdapServers;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LdapProvider {
    private final Logger log = LoggerFactory.getLogger(LdapProvider.class);

    @Autowired
    private LdapServers ldapServers;

    private final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private final String LDAP_TIMEOUT = "com.sun.jndi.ldap.read.timeout";
    private final String LDAP_CTX_FLOW = "follow";
    private final String LDAP_CTX_IGNORE = "ignore";

    // Some key mapper user ldap
    public static final String LDAP = "LDAP";
    public static final String BASE_DN = "baseDn";
    public static final String USER_DN = "userDn";
    public static final String USER_DN_VALUE = "userDnValue";
    public static final String PRINCIPAL_DN = "principalDn";
    public static final String OU = "ou";
    public static final String O = "o";
    public static final String CN = "cn";
    public static final String SN = "sn";

    public LdapProvider() {
    }

    public List<AuthenticationServerDTO> getAuthenticationServers() throws Exception {
        try {
            List<AuthenticationServerDTO> authServers = new ArrayList<>();
            List<LdapServers.Server> servers = ldapServers.getConfigurations();
            if (servers == null)
                return new ArrayList<>();

            for (LdapServers.Server server : servers) {
                if (!Boolean.parseBoolean(server.getEnable()))
                    continue;

                AuthenticationServerDTO authenticationServerDTO = new AuthenticationServerDTO(server.getName(), server.getUrl(),
                    server.getPort(), server.getUrl(), server.getName(), LDAP, server.getAuthenticationType(), true,
                    server.getEmailUserMapping(), server.getFirstNameUserMapping(), server.getLastNameUserMapping());
                authServers.add(authenticationServerDTO);
            }
            return authServers;
        }catch (Exception ex){
            log.error("getAuthenticationServers, " + ex.getMessage());
            throw new Exception();
        }
    }

    public AuthenticationServerDTO getAuthenticationServer(String url) throws Exception {
        List<AuthenticationServerDTO> authServers = getAuthenticationServers();
        AuthenticationServerDTO authenticationServerDTO = authServers.stream().filter(x -> x.getUrl().toLowerCase().equals(url.toLowerCase())).collect(Collectors.toList()).get(0);
        return authenticationServerDTO;
    }

    public LdapServers.Server getServerByUrl(String url) {
        List<LdapServers.Server> servers = ldapServers.getConfigurations();
        if (servers == null)
            return null;

        LdapServers.Server server = servers.stream().filter(x -> x.getUrl().equals(url)).collect(Collectors.toList()).get(0);
        return server;
    }

    // get userDN valid with setting scope of base DN, have account authentication first or not
    public Hashtable<String, String> getUserDnValid(LdapServers.Server server, String username, String password) {
        String masterUserDn = server.getMasterUserDn();
        String masterIdentifier = server.getMasterUserDnValue();
        String masterUserPassword = server.getMasterUserPassword();
        String masterBaseDn = server.getMasterBaseDn();

        // Not setting master account for searching then search all base DN and user DN
        boolean isNotSettingMasterAccount = Strings.isNullOrEmpty(masterUserDn) || Strings.isNullOrEmpty(masterIdentifier)
            || Strings.isNullOrEmpty(masterUserPassword) || Strings.isNullOrEmpty(masterBaseDn);
        if (isNotSettingMasterAccount) {
            List<Hashtable<String, String>> usersDnValid = getValidDns(server, username, password);
            if (usersDnValid == null || usersDnValid.size() == 0) {
                log.error("getUserDnValid, don't existing any user login");
                return null;
            }

            // return fist user DN valid
            return usersDnValid.get(0);
        }

        // Have setting master account for searching then focus search each base DN with group condition userDN
        Hashtable<String, String> userDnValid = null;
        List<String> baseDns = server.getBaseDn();
        List<String> userDns = server.getUserDn();

//        boolean prefixMailIsUserDn = Boolean.parseBoolean(server.getPrefixMailIsUserDn());
//        boolean usernameIsEmail = StringUtil.isValidEmailAddress(username);
//        String identifier = getIdentifier(prefixMailIsUserDn, usernameIsEmail, username);
        String identifier = username;

        // Or condition searching example: (|(CN=ldap15)(givenName=ldap15))
        StringBuilder searchFilterBuilder = new StringBuilder();
        searchFilterBuilder.append("(|");
        for (String userDn : userDns) {
            String condition = String.format("(%s=%s)", userDn, identifier);
            searchFilterBuilder.append(condition);
        }
        searchFilterBuilder.append(")");

        for (String baseDn : baseDns) {
            try {
                Hashtable<String, String> userDnValidInBaseDn = getUserDnValidByMasterUser(server, masterUserDn, masterIdentifier,
                    masterUserPassword, masterBaseDn,
                    baseDn, searchFilterBuilder.toString(), username);

                // return fist user DN valid with principle
                if (userDnValidInBaseDn == null)
                    continue;
                else return userDnValidInBaseDn;
            } catch (Exception ex) {
                continue;
            }
        }

        log.error("getUserDnValid, don't existing any user login with username: " + username);

        // If don't existing any user login
        return null;
    }

    public Hashtable<String, String> getUserDnValidByMasterUser(LdapServers.Server server,
                                                                String masterUserDn, String masterIdentifier,
                                                                String masterUserPassword, String masterBaseDn,
                                                                String baseDn, String searchUserDnCondition, String username) throws Exception {
        // If have master account setting
        // the master account authentication successful then search user quickly
        Hashtable<String, String> userDnExisting = null;
        Hashtable<String, String> masterEnv = new Hashtable<>();
        String masterPrinciple = String.format("%s=%s,%s", masterUserDn, masterIdentifier, masterBaseDn);
        String ldapUrls = String.format("%s:%s", server.getUrl(), server.getPort());

        masterEnv.put(Context.PROVIDER_URL, ldapUrls);
        masterEnv.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY);
        masterEnv.put(LDAP_TIMEOUT, String.valueOf(server.getTimeout()));
        masterEnv.put(Context.SECURITY_AUTHENTICATION, server.getAuthenticationType());
        masterEnv.put(Context.SECURITY_CREDENTIALS, masterUserPassword);
        masterEnv.put(Context.SECURITY_PRINCIPAL, masterPrinciple);
        // Set the referral property to "follow" referrals automatically
//        masterEnv.put(Context.REFERRAL, LDAP_CTX_FLOW);
        masterEnv.put(Context.REFERRAL, LDAP_CTX_IGNORE);

        try {
            DirContext ctx = new InitialDirContext(masterEnv);
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration res = ctx.search(baseDn, searchUserDnCondition, sc);
            while (res.hasMore()) {
                userDnExisting = new Hashtable<>();
                SearchResult s = (SearchResult) res.next();
                String dn = s.getNameInNamespace();
                String user = dn.split(",")[0];
                String userDn = user.split("=")[0];
                String userDnValue = user.split("=")[1];
                String baseDnToTreeUser = dn.replace(user + ",", "");
                userDnExisting.put(USER_DN, userDn);
                userDnExisting.put(USER_DN_VALUE, userDnValue);
                userDnExisting.put(BASE_DN, baseDnToTreeUser);
                userDnExisting.put(PRINCIPAL_DN, dn);
                // Work around REFERRAL attribute of LDAP server, if don't have cause UnknownHostException, so always first return if existing dn
                ctx.close();
                return userDnExisting;
            }
            ctx.close();
            return userDnExisting;
        } catch (NamingException ex) {
            log.error("getUserDnValidByMasterUser NamingException trace: " + ExceptionUtils.getStackTrace(ex));
            if (ex.getCause() !=null && ex.getCause().getMessage().contains("Connection timed out")) {
                log.error("Connection timeout: " + ex.getCause().getMessage());
            }
            throw new AuthenticationException();
        } catch (Exception ex){
            log.error("getUserDnValidByMasterUser Exception" + ex.getMessage());
            throw new Exception();
        }
    }

    public List<Hashtable<String, String>> getValidDns(LdapServers.Server server, String username, String password) {
        if (server.getBaseDn() == null || server.getUserDn() == null)
            return null;

        List<Hashtable<String, String>> dnValidList = new ArrayList<>();
        List<String> baseDns = server.getBaseDn();
        List<String> userDns = server.getUserDn();
        String principal = null;
        for (String baseDn : baseDns) {
            for (String userDn : userDns) {
                try {
                    principal = String.format("%s=%s,%s", userDn, username, baseDn);
                    if (getUserLdapAttribute(server, baseDn, userDn, username, principal, password) == null)
                        continue;
                } catch (Exception ex) {
                    continue;
                }
                Hashtable<String, String> dnValid = new Hashtable<>();
                dnValid.put(USER_DN, userDn);
                dnValid.put(BASE_DN, baseDn);
                dnValid.put(PRINCIPAL_DN, principal);
                dnValidList.add(dnValid);

                // return find first valid
                return dnValidList;
            }
        }
        return dnValidList;
    }

    // Username is DN or user account
    public Hashtable<String, String> getUserMappingAttribute(LdapServers.Server server,
                                                             String username, String password) {
        if (!Boolean.parseBoolean(server.getEnable())) {
            log.error("getUserMappingAttribute, LDAP is not enable, username: " + username);
            return null;
        }

        Hashtable<String, String> dnValid = getUserDnValid(server, username, password);
        if (dnValid == null) {
            log.error("getUserMappingAttribute, User login have DN invalid, username: " + username);
            return null;
        }

        Hashtable<String, String> userAttributes = new Hashtable<>();
        String emailUserMapping = server.getEmailUserMapping();
        String firstNameUserMapping = server.getFirstNameUserMapping();
        String lastNameUserMapping = server.getLastNameUserMapping();
        String baseDnValid = dnValid.get(BASE_DN);
        String userDnValid = dnValid.get(USER_DN);
        String userDnValidValue = dnValid.get(USER_DN_VALUE);
        String principal = dnValid.get(PRINCIPAL_DN);
        try {
            Attributes attributes = getUserLdapAttribute(server, baseDnValid, userDnValid, userDnValidValue, principal, password);

            if (attributes == null) {
                log.error("getUserMappingAttribute, username: " + username + ", DN principal: " + principal);
                log.error("getUserMappingAttribute, User don't have attribute");
                return null;
            }

            // Mapping attributes user ldap to user akabot
            userAttributes.put(User_.login.getName(), username);

            if (attributes.get(emailUserMapping) != null)
                userAttributes.put(User_.email.getName(), attributes.get(emailUserMapping).get().toString());

            if (attributes.get(firstNameUserMapping) != null)
                userAttributes.put(User_.firstName.getName(), attributes.get(firstNameUserMapping).get().toString());

            if (attributes.get(lastNameUserMapping) != null)
                userAttributes.put(User_.lastName.getName(), attributes.get(lastNameUserMapping).get().toString());

            return userAttributes;
        } catch (Exception ex) {
            log.error("getUserMappingAttribute Exception, username: " + username + ", DN principal: " + principal);
            return null;
        }
    }

    private Attributes getUserLdapAttribute(LdapServers.Server server, String baseDn, String userDn,
                                            String userDnValue, String principal, String password) throws Exception {
        Attributes attributes = null;

        // If prefixMailIsUserDn setting true then get only prefix mail as userDn
        //boolean prefixMailIsUserDn = Boolean.parseBoolean(server.getPrefixMailIsUserDn());
        //boolean usernameIsEmail = StringUtil.isValidEmailAddress(userDnValue);
        //String identifier = getIdentifier(prefixMailIsUserDn, usernameIsEmail, userDnValue);
        String identifier = userDnValue;

        Hashtable<String, String> env = new Hashtable<>();
        String ldapUrls = String.format("%s:%s", server.getUrl(), server.getPort());
//        String principal = String.format("%s=%s,%s", userDn, identifier, baseDn);
        String emailUserMapping = server.getEmailUserMapping();
        String firstNameUserMapping = server.getFirstNameUserMapping();
        String lastNameUserMapping = server.getLastNameUserMapping();

//         TODO: handle type of authentication password?
//        if(authenticationType.equals(AuthenticationType.SASL))

        // Set environment naming for searching
        env.put(Context.PROVIDER_URL, ldapUrls);
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY);
        env.put(LDAP_TIMEOUT, String.valueOf(server.getTimeout()));
        env.put(Context.SECURITY_AUTHENTICATION, server.getAuthenticationType());
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.SECURITY_PRINCIPAL, principal);

        try {
            DirContext ctx = new InitialDirContext(env);
            String[] attributeFilter = {emailUserMapping, firstNameUserMapping, lastNameUserMapping, OU, O, CN, SN};
            SearchControls sc = new SearchControls();
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchFilter = String.format("(%s=%s)", userDn, identifier);
            NamingEnumeration<SearchResult> results = ctx.search(baseDn, searchFilter, sc);
            if (results.hasMore()) {
                SearchResult result = results.next();
                attributes = result.getAttributes();
            }
            ctx.close();

            if(attributes == null)
                return null;

//            if (attributes.get(emailUserMapping) == null) {
//                log.error("getUserLdapAttribute, User LDAP don't have an email");
//                throw new AuthenticationException();
//            } else if (!StringUtil.isValidEmailAddress(attributes.get(emailUserMapping).get().toString())) {
//                log.error("getUserLdapAttribute, User LDAP has an email invalid");
//                throw new AuthenticationException();
//            }

            // Check valid email if user dn is email
//            if (prefixMailIsUserDn && usernameIsEmail) {
//                // Don't support an user have many of the same email attributes, ex: mail:prefixName@gmail.com, mail: prefixName@outlook.com
//                if (!attributes.get(emailUserMapping).get().toString().toLowerCase().equals(userDnValue.toLowerCase())) {
//                    log.error("getUserLdapAttribute, User LDAP have email invalid, the email not math with login email");
//                    throw new AuthenticationException();
//                }
//            }

            return attributes;
        } catch (AuthenticationNotSupportedException ex) {
            log.error("getUserLdapAttribute AuthenticationNotSupportedException" + ex.getMessage());
            throw new AuthenticationException();
        } catch (AuthenticationException ex) {
            log.error("getUserLdapAttribute AuthenticationException" + ex.getMessage());
            throw new AuthenticationException();
        } catch (NamingException ex) {
            // Include timeout, refused exception
            log.error("getUserLdapAttribute NamingException" + ex.getMessage());
            if (ex.getCause() !=null && ex.getCause().getMessage().contains("Connection timed out")) {
                log.error("Connection timeout: " + ex.getCause().getMessage());
            }
            throw new AuthenticationException();
        }catch (Exception ex){
            log.error("getUserLdapAttribute Exception " + ex.getMessage());
            throw new Exception();
        }
    }

    private String getIdentifier(boolean prefixMailIsUserDn, boolean usernameIsEmail, String username) {
        String identifier = username;
        // If prefixMailIsUserDn setting true then get only prefix mail as userDn
        if (prefixMailIsUserDn && usernameIsEmail) {
            int index = username.indexOf('@');
            if (index > 0)
                identifier = username.substring(0, index);
        }

        return identifier;
    }
}
