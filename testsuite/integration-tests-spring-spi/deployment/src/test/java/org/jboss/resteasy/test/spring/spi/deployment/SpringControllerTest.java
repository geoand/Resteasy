package org.jboss.resteasy.test.spring.spi.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.spring.spi.deployment.resource.TestController;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@RunWith(Arquillian.class)
@RunAsClient
public class SpringControllerTest {

    static Client client;
    private static final String DEPLOYMENT_NAME = "springdep";

    @Before
    public void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    @Test
    public void verifySimplestGet() {
        WebTarget target = client.target(getBaseURL() + TestController.TEST_CONTROLLER_PATH);
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "hello world", str);
    }

    @Test
    public void verifySimplestGetWithQueryParam() {
        WebTarget target = client.target(getBaseURL() + TestController.TEST_CONTROLLER_PATH + "?name=people");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "hello people", str);
    }


    @Test
    public void verifyJsonGetWithPathParamAndGettingMapping() {
        verifyGetJson("json");
    }

    @Test
    public void verifyJsonOnRequestMappingGetWithPathParamAndRequestMapping() {
        verifyGetJson("json2");
    }

    private void verifyGetJson(final String path) {
        WebTarget target = client.target(getBaseURL() + TestController.TEST_CONTROLLER_PATH + "/" + path + "/dummy");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("dummy"));
    }

    @Test
    public void verifyJsonPostWithPostMapping() {
        verifyPostJson("json");
    }

    @Test
    public void verifyJsonPostWithRequestMapping() {
        verifyPostJson("json2");
    }

    private void verifyPostJson(final String path) {
        WebTarget target = client.target(getBaseURL() + TestController.TEST_CONTROLLER_PATH + "/" + path);
        Response response = target.request().post(Entity.entity("{\"message\": \"hi\"}", MediaType.APPLICATION_JSON));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("hi"));
    }


    private String getBaseURL() {
        return PortProviderUtil.generateURL("/", DEPLOYMENT_NAME);
    }

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_NAME + ".war");
        archive.addAsWebInfResource(SpringControllerTest.class.getPackage(), "web.xml", "web.xml");
        archive.addAsManifestResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml");


        TestUtilSpring.addSpringLibraries(archive);
        archive.as(ZipExporter.class).exportTo(new File("target", DEPLOYMENT_NAME + ".war"), true);
        return TestUtil.finishContainerPrepare(archive, null, TestController.class);
    }

}
