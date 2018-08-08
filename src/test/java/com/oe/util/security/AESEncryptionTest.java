package com.oe.util.security;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.oe.util.encrypt.AESEncryption;

public class AESEncryptionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncode() {
		AESEncryption aes = new AESEncryption();
		String password = "meta123#";
		for (int i = 0; i < 10; i++) {
			System.out.println(aes.encode(password));
		}
		System.out.println(aes.decode("53DD978430150E154709EDC444D3DCF0"));
	}

//	@Test
	public void testDecode() {
		AESEncryption aes = new AESEncryption();
		String password = "YSDPIZ", token = aes.encode(password);
		System.out.println(token);
		Assert.assertEquals(password, aes.decode(token));
	}

}
