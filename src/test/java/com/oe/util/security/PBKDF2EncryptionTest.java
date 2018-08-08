package com.oe.util.security;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.oe.util.encrypt.PBKDF2Encryption;

public class PBKDF2EncryptionTest {

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
		String password = new String("111111");
		String token = "16$fAJjrR9AIxg54Ys5CopAjGTlq4c_jx6qnBLCOK2ixO8";
		PBKDF2Encryption pbkdf2 = new PBKDF2Encryption();
		System.out.println(token + "\t" + pbkdf2.authenticate(password, token));
		for (int i = 0; i < 10; i++) {
			token = pbkdf2.encode(password);
			System.out.print(token);
			System.out.println("\t" + pbkdf2.authenticate(password, token));
			System.out.println();
		}
	}

	@Test
	public void testAuthenticate() {
		PBKDF2Encryption pbkdf2 = new PBKDF2Encryption();
		String password = new String("111111");
		String token = "16$fAJjrR9AIxg54Ys5CopAjGTlq4c_jx6qnBLCOK2ixO8";
		pbkdf2.authenticate(password, token);
	}

}
