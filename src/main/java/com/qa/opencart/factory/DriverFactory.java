package com.qa.opencart.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.qa.opencart.errors.AppErrors;
import com.qa.opencart.exception.FrameworkException;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Single Responsibility principle says that DriverFactory is responsible for
 * initializing the driver for creating the driver & we are moving the driver
 * from one location to another location. Here we are not initializing the
 * driver again & again, only one driver is coming to the basetest, from
 * basetest to the LoginPage & LP is initializing its own private driver & using
 * it in other page actions & from the LoginPagetest we are keep calling the
 * page actions method.
 * 
 * @author vk1622
 *
 */

public class DriverFactory {

	public WebDriver driver;
	public Properties prop;

	private static final Logger LOG = Logger.getLogger(DriverFactory.class);

	// ThreadLocal is used to get the copy of driver.
	// Thread local says that we will give you a local copy for each & every thread
	// that will help you in parallel testing

	public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();

	public static String highlight;
	public OptionsManager optionsManager;

	/**
	 * this method is used to init the driver on the basis of given browser name
	 * @param browserName
	 * @return this will return the driver instance
	 */
	public WebDriver initDriver(Properties prop) {
		String browserName = prop.getProperty("browser").toLowerCase();
		System.out.println("browser name is : " + browserName);
		LOG.info("browser name is : " + browserName);
		
		highlight = prop.getProperty("highlight").trim();
		optionsManager = new OptionsManager(prop);

		if (browserName.equals("chrome")) {
			
			if(Boolean.parseBoolean(prop.getProperty("remote"))) {
				
				// Remote run:
				init_remoteDriver("chrome");
			}
			else {
				
				//Local run:
				WebDriverManager.chromedriver().setup();
				//driver = new ChromeDriver();
				tlDriver.set(new ChromeDriver(optionsManager.getChromeOptions()));
				
			}	
			
		} 
		
		else if (browserName.equals("firefox")) {

			if(Boolean.parseBoolean(prop.getProperty("remote"))) {
				
				// Remote run:
				init_remoteDriver("firefox");
			}
			else {
				
				//Local run:
				WebDriverManager.firefoxdriver().setup();
				tlDriver.set(new FirefoxDriver(optionsManager.getFirefoxOptions()));
				
			}
		}
		
		else if (browserName.equals("edge")) {
			

			if(Boolean.parseBoolean(prop.getProperty("remote"))) {
				
				// Remote run:
				init_remoteDriver("edge");
			}
			else {
				
				//Local run:
				WebDriverManager.edgedriver().setup();
				tlDriver.set(new EdgeDriver(optionsManager.getEdgeOptions()));
				
			}
		}
		
		else if (browserName.equals("safari")) {
			//only local execution -- docker doesnot support safari
			tlDriver.set(new SafariDriver());
		} 
		
		else {
			System.out.println("Please pass the right browser name: " + browserName);
			LOG.error("Please pass the right browser name : " + browserName);
			throw new FrameworkException(AppErrors.BROWSER_NOT_FOUND);
		}
		
		getDriver().manage().deleteAllCookies();
		getDriver().manage().window().maximize();
		getDriver().get(prop.getProperty("url"));
		
		return getDriver();   // The driver we return will give it to the driver in baseTest class in setup method

	}
	
	/*
	 * The below method is for remote execution
	 */

	private void init_remoteDriver(String browser) {
		
		System.out.println("Running testcases on remote grid machine with browser:" + browser);

		if (browser.equals("chrome")) {

			try {
				/**
				 * Below we have to use the 'new URL' object cz RemoteWebdriver is asking for
				 * the Url type of address not the string type of address
				 */

				tlDriver.set(new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getChromeOptions()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		else if (browser.equals("firefox")) {

			try {
				/**
				 * Below we have to use the 'new URL' object cz RemoteWebdriver is asking for
				 * the Url type of address not the string type of address
				 */

				tlDriver.set(new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getFirefoxOptions()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		else if (browser.equals("edge")) {

			try {
				/**
				 * Below we have to use the 'new URL' object cz RemoteWebdriver is asking for
				 * the Url type of address not the string type of address
				 */

				tlDriver.set(new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getEdgeOptions()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {

			System.out.println("Please pass the right broser for remote execution...." + browser);
		}

	}

	// With synchronized keyword we will never get any dead lock condition & all the
	// threads will not impact each other.

	public static synchronized WebDriver getDriver() {
		return tlDriver.get();
	}

	/**
	 * this method is used to init the config properties
	 * 
	 * @return
	 */
	public Properties initProp() {

		// Creating the object of Properties class.

		prop = new Properties();
		FileInputStream ip = null;

		// mvn clean install -Denv="dev"
		// mvn clean install

		// String envName = System.getenv("env");// stage/uat/qa/dev
		String envName = System.getProperty("env");
		System.out.println("-----> Running test cases on environment: ----->" + envName);
		LOG.info("-----> Running test cases on environment: ----->" + envName);

		if (envName == null) {
			System.out.println("No env is given..hence running it on QA env.....");
			try {
				ip = new FileInputStream("./src/test/resources/config/qa.config.properties"); // FileInputStream class
																								// will make the
																								// connection with
																								// config.properties
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		else {
			try {
				switch (envName) {
				case "qa":
					ip = new FileInputStream("./src/test/resources/config/qa.config.properties");
					break;
				case "dev":
					ip = new FileInputStream("./src/test/resources/config/dev.config.properties");
					break;
				case "stage":
					ip = new FileInputStream("./src/test/resources/config/stage.config.properties");
					break;
				case "uat":
					ip = new FileInputStream("./src/test/resources/config/uat.config.properties");
					break;
				case "prod":
					ip = new FileInputStream("./src/test/resources/config/config.properties");
					break;

				default:
					System.out.println("please pass the right env name...." + envName);
					throw new FrameworkException(AppErrors.ENV_NOT_FOUND);
				// break;
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

		try {
			prop.load(ip); // This is to load the properties from config.properties, while loading if any
							// execeptions occured then it will be come to below catch block
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prop;
	}

	/**
	 * take screenshot
	 */
	public static String getScreenshot() {

		File srcFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);

		String path = System.getProperty("user.dir") + "/screenshot/" + System.currentTimeMillis() + ".png";
		File destination = new File(path);

		try {
			FileUtils.copyFile(srcFile, destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;

	}

}
