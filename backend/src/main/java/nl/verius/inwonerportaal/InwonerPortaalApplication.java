package nl.verius.inwonerportaal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Inwonerportaal BFF (Backend-for-Frontend).
 *
 * <p>Moderne herbouw van het X-Works "klantenportaal". De koppeling met X-Works loopt
 * uitsluitend via de anti-corruption layer ({@link nl.verius.inwonerportaal.acl.XworksClient}),
 * zodat de domeinlogica, validatie, sleutelgeneratie en concurrency in X-Works/Uniface blijven
 * en de gemeente de door de burger ingevoerde zaken automatisch in X-Works ziet.
 *
 * <p>Standaardprofiel {@code stub} vangt die ACL af met in-memory testdata.
 */
@SpringBootApplication
public class InwonerPortaalApplication {
    public static void main(String[] args) {
        SpringApplication.run(InwonerPortaalApplication.class, args);
    }
}
