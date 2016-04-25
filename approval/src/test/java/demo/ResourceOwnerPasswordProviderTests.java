package demo;

import org.springframework.boot.test.SpringApplicationConfiguration;

import com.example.Application;

import sparklr.common.AbstractResourceOwnerPasswordProviderTests;

/**
 * @author Dave Syer
 */
@SpringApplicationConfiguration(classes=Application.class)
public class ResourceOwnerPasswordProviderTests extends AbstractResourceOwnerPasswordProviderTests {

}
