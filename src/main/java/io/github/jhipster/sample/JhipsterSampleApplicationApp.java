package io.github.jhipster.sample;

import io.github.jhipster.sample.config.ApplicationProperties;

import io.github.jhipster.config.DefaultProfileUtil;
import io.github.jhipster.config.JHipsterConstants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class JhipsterSampleApplicationApp {

    private static final Logger log = LoggerFactory.getLogger(JhipsterSampleApplicationApp.class);

    private final Environment env;

    public JhipsterSampleApplicationApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes jhipsterSampleApplication.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JhipsterSampleApplicationApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
        //ldapTest();
    }

    private static void ldapTest(){
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        String principalName = "cn=read-only-admin,dc=example,dc=com";
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ldap.forumsys.com:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, principalName);
        env.put(Context.SECURITY_CREDENTIALS, "password");

        try {
            DirContext authContext = new InitialDirContext(env);
            // user is authenticated
            System.out.println("USER IS AUTHETICATED");

            //
            SearchControls searchCtls = new SearchControls();
            // Specify the attributes to return
            String returnedAtts[] = { "cn"};
            searchCtls.setReturningAttributes(returnedAtts);
            // Specify the search scope
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // specify the LDAP search filter
            String searchFilter = "(&(cn=read-only-admin))";
            // Specify the Base for the search
            String searchBase = "dc=example,dc=com";
            // initialize counter to total the results
            int totalResults = 0;
            // Search for objects using the filter
            NamingEnumeration<SearchResult> answer = authContext.search(searchBase, searchFilter, searchCtls);
            // Loop through the search results
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                totalResults++;
            }
            System.out.println("Total results: " + totalResults);
            authContext.close();

        } catch (AuthenticationException ex) {
            // Authentication failed
            System.out.println("AUTH FAILED : " + ex);

        } catch (NamingException ex) {
            ex.printStackTrace();
        }
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }
}
