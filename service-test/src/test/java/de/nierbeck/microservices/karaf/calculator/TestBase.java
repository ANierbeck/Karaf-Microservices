package de.nierbeck.microservices.karaf.calculator;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.MavenUtils.asInProject;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.karaf.features.BootFinished;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.junit.Before;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBase {

	static final Long COMMAND_TIMEOUT = 30000L;
	static final Long SERVICE_TIMEOUT = 30000L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	protected BundleContext bc;

	@Inject
	protected FeaturesService featuresService;

	@Inject
	protected SessionFactory sessionFactory;

	/**
	 * To make sure the tests run only when the boot features are fully
	 * installed
	 */
	@Inject
	protected BootFinished bootFinished;

	private ExecutorService executor = Executors.newCachedThreadPool();

	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private PrintStream printStream = new PrintStream(byteArrayOutputStream);
	private PrintStream errStream = new PrintStream(byteArrayOutputStream);
	private Session session;

	public Option[] configBase() {
		return options(
				karafDistributionConfiguration().frameworkUrl(mvnKarafDist())
						.unpackDirectory(new File("target/paxexam/unpack/")).useDeployFolder(false),
				logLevel(LogLevel.INFO), keepRuntimeFolder(), features(karafStandardFeature(), "scr"),
				configureConsole().ignoreLocalConsole(), junitBundles());
	}

	private MavenArtifactUrlReference mvnKarafDist() {
		return maven().groupId("de.nierbeck.microservices.karaf.tools").artifactId("Karaf-Service-Runtime")
				.type("tar.gz").version(asInProject());
	}

	private MavenUrlReference karafStandardFeature() {
		return maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features")
				.version(asInProject());
	}

	@Before
	public void setUpITestBase() throws Exception {
		int count = 0;
		logger.info("Waited for Cassandra service to appear: " + Integer.toString(count * 500));

		session = sessionFactory.create(System.in, printStream, errStream);
	}

	protected String executeCommand(final String command) throws IOException {
		byteArrayOutputStream.flush();
		byteArrayOutputStream.reset();

		String response;
		FutureTask<String> commandFuture = new FutureTask<String>(new Callable<String>() {
			public String call() {
				try {
					System.err.println(command);
					session.execute(command);
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
				printStream.flush();
				errStream.flush();
				return byteArrayOutputStream.toString();
			}
		});

		try {
			executor.submit(commandFuture);
			response = commandFuture.get(10000L, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			response = "SHELL COMMAND TIMED OUT: ";
		}

		System.err.println(response);

		return response;
	}

    protected <T> T getOsgiService(Class<T> type, long timeout) {
        return getOsgiService(type, null, timeout);
    }

    protected <T> T getOsgiService(Class<T> type) {
        return getOsgiService(type, null, SERVICE_TIMEOUT);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> T getOsgiService(Class<T> type, String filter, long timeout) {
		ServiceTracker tracker = null;
		try {
			String flt;
			if (filter != null) {
				if (filter.startsWith("(")) {
					flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")" + filter + ")";
				} else {
					flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")(" + filter + "))";
				}
			} else {
				flt = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";
			}
			Filter osgiFilter = FrameworkUtil.createFilter(flt);
			tracker = new ServiceTracker(bc, osgiFilter, null);
			tracker.open(true);
			// Note that the tracker is not closed to keep the reference
			// This is buggy, as the service reference may change i think
			Object svc = type.cast(tracker.waitForService(timeout));
			if (svc == null) {
				Dictionary dic = bc.getBundle().getHeaders();
				System.err.println("Test bundle headers: " + explode(dic));

				for (ServiceReference ref : asCollection(bc.getAllServiceReferences(null, null))) {
					System.err.println("ServiceReference: " + ref);
				}

				for (ServiceReference ref : asCollection(bc.getAllServiceReferences(null, flt))) {
					System.err.println("Filtered ServiceReference: " + ref);
				}

				logger.error("Gave up waiting for service " + flt);
				return null;
			}
			return type.cast(svc);
		} catch (InvalidSyntaxException e) {
			throw new IllegalArgumentException("Invalid filter", e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Explode the dictionary into a ,-delimited list of key=value pairs
	 */
	@SuppressWarnings("rawtypes")
	private static String explode(Dictionary dictionary) {
		Enumeration keys = dictionary.keys();
		StringBuffer result = new StringBuffer();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			result.append(String.format("%s=%s", key, dictionary.get(key)));
			if (keys.hasMoreElements()) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	/**
	 * Provides an iterable collection of references, even if the original array
	 * is null
	 */
	@SuppressWarnings("rawtypes")
	private static Collection<ServiceReference> asCollection(ServiceReference[] references) {
		return references != null ? Arrays.asList(references) : Collections.<ServiceReference> emptyList();
	}

}
