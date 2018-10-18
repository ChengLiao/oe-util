package com.oe.util.encrypt;

public class XorEncrypt {

	private static final String KEY = toHex("Teradata@2018");

	public static void main(String[] args) {
		System.out.println(toHex("t"));
		System.out.println(fromHex("74"));
		System.out.println(ec("74", KEY));
		System.out.println(toHex(ec("74", KEY)));
		System.out.println(ec("74657374", KEY));
		System.out.println(fromHex(toHex(ec("74657374", KEY))));
		
		/*System.out.println(encrypt("Teradata"));
		System.out.println(encrypt("�й�"));
		System.out.println(encrypt("uuye"));
		System.out.println(encrypt("asss"));
		
		System.out.println(decrypt("0000000000000000"));
		System.out.println(decrypt("151451245055"));
		System.out.println(decrypt("21100b04"));
		System.out.println(decrypt("35160112"));*/
	}
	/**
	 * ��ȡ������
	 * @param input
	 * @return
	 */
	private static String toHex(String input) {
		byte[] arr = input.getBytes();
		StringBuilder sb = new StringBuilder();
		for (byte c : arr) {
			sb.append(Integer.toHexString(c));
		}
		return sb.toString();
	}
	
	/**
	 * �Ӷ�����תΪ�ַ���
	 * @param input
	 * @return
	 */
	private static String fromHex(String input) {
		if(input.length() % 2 != 0) {
			throw new IllegalArgumentException("invalid input length:" + input.length());
		}
		byte[] arr = new byte[input.length()/2];
		for(int i=0; i< arr.length; i++) {
			arr[i] = Byte.valueOf(input.substring(i*2, (i*2)+2), 16);
		}
		return new String(arr);
	}

	/**
	 * ����
	 * 
	 * @param input
	 * @return
	 */
	public static String encrypt(String input) {
		if (input == null) {
			return null;
		}
		if (input.equals("")) {
			return "";
		}
		
		return toHex(ec(toHex(input),KEY));
	}

	/**
	 * ����
	 * 
	 * @param input
	 * @return
	 */
	public static String decrypt(String input) {
		if (input == null) {
			return null;
		}
		if (input.equals("")) {
			return "";
		}
		return ec(fromHex(input),KEY);
	}

	/**
     * xor����
     * @param h ���ܴ�
     * @param key   ��Կ
     * @return
     */
    public static String ec(String h,String key) {
        int l1 = h.length();
        int l2 = key.length();
        StringBuilder r = new StringBuilder("");
        for (int i = 0; i < l1; i = i + l2) {
            int j = i + l2;
            if (j >= h.length())
                j = h.length();
            r.append(xor(h.substring(i, j), key));
        }
        return r.toString();
    }
 
   
    public static String xor(String s1, String s2) {
        char c1[] = s1.toCharArray();
        char c2[] = s2.toCharArray();
        byte result[] = new byte[c1.length];
        int i = 0;
        for (; i < c1.length; i++) {
        	result[i] = (byte) (c1[i] ^ c2[i]);
        }
        return new String(result);
    }
}
