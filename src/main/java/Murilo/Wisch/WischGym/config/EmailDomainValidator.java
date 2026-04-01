package Murilo.Wisch.WischGym.config;

import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Component
public class EmailDomainValidator {

    public boolean isDominioValido(String email) {
        if (email == null || !email.contains("@")) return false;

        String dominio = email.substring(email.indexOf("@") + 1);

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes("dns:/" + dominio, new String[]{"MX"});
            Attribute mx = attrs.get("MX");

            return mx != null && mx.size() > 0;

        } catch (NamingException e) {
            return false;
        }
    }
}
