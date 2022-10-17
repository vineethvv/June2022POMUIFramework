package com.qa.opencart.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.qa.opencart.constants.AppConstants;
import com.qa.opencart.utils.ElementUtil;

import io.qameta.allure.Step;

public class LoginPage {
	
	/**
	 *  If we made this as public then any class can create the object of LoginPage & they try to access this driver over there
	 *  if they access, they might get NPE, in order to avoid this we made it as private.
        Note- We cannot access the private variables outside of the class.
        This driver is only used for LoginPage class.
        Private variables can be initialized with the help on constructors
	 */
    
	private WebDriver driver; // The java concept which is used to initialize the driver is constructor
	private ElementUtil eleUtil;
	
	private static final Logger LOG = Logger.getLogger(LoginPage.class);

	// 1. By locator:
	
	/**
	 *  All the By locators should be private in nature & should be accessed by the public page actions, this is the example of encapsulation.
	 *  We are making this as private cz , otherwise anyone can access this By locators outside of the class & can change the locator value.
	 */
	private By emailId = By.id("input-email");
	private By password = By.id("input-password");
	private By loginBtn = By.xpath("//input[@value='Login']");
	private By logoImage = By.cssSelector("img[title='naveenopencart']");
	private By forgotPwdLink = By.linkText("Forgotten Password");
	private By registerLink = By.linkText("Register");

	// 2. page constructor
	
	public LoginPage(WebDriver driver) {
		this.driver = driver;
		eleUtil = new ElementUtil(driver);
	}

	// 3. page actions:
	
	@Step("Waiting for login page title and fetching the title")
	public String getLoginPageTitle() {
		String title = eleUtil.waitForTitleIs(AppConstants.DEFAULT_TIME_OUT, AppConstants.LOGIN_PAGE_TITLE);
		System.out.println("login page title : " + title);
		LOG.info("login page title: " + title);
		return title;
	}

	@Step("Waiting for login page url and fetching the url")
	public boolean getLoginPageUrl() {
		String url = eleUtil.waitForUrlContains(AppConstants.DEFAULT_TIME_OUT, AppConstants.LOGIN_PAGE_URL_PARAM);
		System.out.println("login page url : " + url);
		if (url.contains(AppConstants.LOGIN_PAGE_URL_PARAM)) {
			return true;
		}
		return false;
	}

	@Step("checking forgot pwd link is displayed on login page")
	public boolean isForgotPwdLinkExist() {
		return eleUtil.doEleIsDisplayed(forgotPwdLink);
	}
	
	/**
	 * Page chaining model says that whenever the user is clicking on any link/button, after click if the user is landing on a new page then it is the method
	 * responsibility to return the object of that page.
	 * @param username
	 * @param pwd
	 * @return
	 */
	
	@Step("login with username : {0} and password: {1}")
	public AccountsPage doLogin(String username, String pwd) {
		System.out.println("user creds are : " + username + " : " + pwd);
		eleUtil.doSendKeysWithWait(emailId, AppConstants.DEFAULT_LARGE_TIME_OUT, username);
		eleUtil.doSendKeys(password, pwd);
		eleUtil.doClick(loginBtn);
		return new AccountsPage(driver);
	}
	
	@Step("navigating to register page")
	public RegisterPage navigateToRegisterPage() {
		System.out.println("navigating to register page.....");
		eleUtil.doClick(registerLink);
		return new RegisterPage(driver);
	}
	

}
