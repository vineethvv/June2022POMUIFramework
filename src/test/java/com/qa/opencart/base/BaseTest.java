package com.qa.opencart.base;

import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.qa.opencart.factory.DriverFactory;
import com.qa.opencart.pages.AccountsPage;
import com.qa.opencart.pages.LoginPage;
import com.qa.opencart.pages.ProductInfoPage;
import com.qa.opencart.pages.RegisterPage;
import com.qa.opencart.pages.SearchResultsPage;

public class BaseTest {
	
DriverFactory df;
	
	public Properties prop;
	public WebDriver driver;
	
	public LoginPage loginPage;
	public AccountsPage accPage;
	public SearchResultsPage searchResultsPage;
	public ProductInfoPage productInfoPage;
	public RegisterPage registerPage;
	
	
	@Parameters({"browser", "browserversion", "testname"})
	@BeforeTest
	public void setup(String browser, String browserVersion, String testName) { // this browser is coming from testng_regression.xml
		
		// creating object of DriverFactory
		
		df = new DriverFactory();
		prop = df.initProp();
		
		if (browser!=null) {
			
			prop.setProperty("browser", browser); // the 2nd browser is coming from testng_regression.xml, here update the browser value of config.prop file from the browser value coming from testng_regression.xml file
			prop.setProperty("browserversion", browserVersion);
			prop.setProperty("testname", testName);
		}
		
		/**
		 *  This driver is holding the driver which is returning from the initDriver in DriverFactory class
		 *  Here we are giving prop in initDriver, inside prop we have all the properties from config.properties file.
		 */
		driver = df.initDriver(prop);  
		
		/**
		 *  Creating the object of LoginPage class, inside the object we are passing the driver cz the loginpage constructor is waiting for the
		 *  driver, If we create the object of a class having constructor , by default that constructor will be called first & this same driver will be given 
            to the driver in LoginPage method in LoginPage class.
		 */
        
		loginPage = new LoginPage(driver); 
	}
	
	
	
	
	@AfterTest
	public void tearDown() {
		driver.quit();
	}
	

}
