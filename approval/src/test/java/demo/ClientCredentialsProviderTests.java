package demo;

import org.springframework.boot.test.SpringApplicationConfiguration;

import com.example.Application;

import sparklr.common.AbstractClientCredentialsProviderTests;

/**
 * @author Dave Syer
 */
@SpringApplicationConfiguration(classes=Application.class)
public class ClientCredentialsProviderTests extends AbstractClientCredentialsProviderTests {


}
