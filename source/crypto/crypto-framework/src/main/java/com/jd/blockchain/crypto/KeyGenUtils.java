package com.jd.blockchain.crypto;

import java.util.Arrays;

import javax.crypto.SecretKey;

import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.security.AESUtils;
import com.jd.blockchain.utils.security.DecryptionException;
import com.jd.blockchain.utils.security.ShaUtils;

public class KeyGenUtils {

	private static final byte[] PUB_KEY_FILE_MAGICNUM = { (byte) 0xFF, 112, 117, 98 };

	private static final byte[] PRIV_KEY_FILE_MAGICNUM = { (byte) 0x00, 112, 114, 118 };

	/**
	 * 公钥编码输出为 Base58 字符；
	 * 
	 * @param pubKey
	 * @return
	 */
	public static String encodePubKey(PubKey pubKey) {
		byte[] pubKeyBytes = BytesUtils.concat(PUB_KEY_FILE_MAGICNUM, pubKey.toBytes());
		String base58PubKey = Base58Utils.encode(pubKeyBytes);
		return base58PubKey;
	}

	public static PubKey decodePubKey(String base58PubKey) {
		byte[] keyBytes = Base58Utils.decode(base58PubKey);
		return decodePubKey(keyBytes);
	}

	public static String encodePrivKey(PrivKey privKey, String base58Pwd) {
		byte[] pwdBytes = Base58Utils.decode(base58Pwd);
		return encodePrivKey(privKey, pwdBytes);
	}

	public static String encodePrivKey(PrivKey privKey, byte[] pwdBytes) {
		byte[] encodedPrivKeyBytes = encryptPrivKey(privKey, pwdBytes);
		String base58PrivKey = Base58Utils.encode(encodedPrivKeyBytes);
		return base58PrivKey;
	}

	public static byte[] encryptPrivKey(PrivKey privKey, byte[] pwdBytes) {
		SecretKey userKey = AESUtils.generateKey128(pwdBytes);
		byte[] encryptedPrivKeyBytes = AESUtils.encrypt(privKey.toBytes(), userKey);
		return BytesUtils.concat(PRIV_KEY_FILE_MAGICNUM, encryptedPrivKeyBytes);
	}

	/**
	 * @param encodedPubKeyBytes
	 * @return
	 */
	private static PubKey decodePubKeyBytes(byte[] encodedPubKeyBytes) {
		byte[] pubKeyBytes = Arrays.copyOfRange(encodedPubKeyBytes, PUB_KEY_FILE_MAGICNUM.length,
				encodedPubKeyBytes.length);
		return new PubKey(pubKeyBytes);
	}

	public static PrivKey decryptedPrivKeyBytes(byte[] encodedPrivKeyBytes, byte[] pwdBytes) {
		// Read privKye;
		SecretKey userKey = AESUtils.generateKey128(pwdBytes);
		byte[] encryptedKeyBytes = Arrays.copyOfRange(encodedPrivKeyBytes, PRIV_KEY_FILE_MAGICNUM.length,
				encodedPrivKeyBytes.length);
		try {
			byte[] plainKeyBytes = AESUtils.decrypt(encryptedKeyBytes, userKey);
			return new PrivKey(plainKeyBytes);
		} catch (DecryptionException e) {
			throw new DecryptionException("Invalid password!", e);
		}
	}

	public static PubKey readPubKey(String keyFile) {
		String base58KeyString = FileUtils.readText(keyFile);
		return decodePubKey(base58KeyString);
	}

	/**
	 * 解码公钥；
	 * 
	 * @param encodedPubKeyBytes 从公钥；
	 * @return
	 */
	public static PubKey decodePubKey(byte[] encodedPubKeyBytes) {
		if (BytesUtils.startsWith(encodedPubKeyBytes, PUB_KEY_FILE_MAGICNUM)) {
			// Read pubKey;
			return decodePubKeyBytes(encodedPubKeyBytes);
		}

		throw new IllegalArgumentException("The specified bytes is not valid PubKey generated by the KeyGen tool!");
	}

	/**
	 * 从控制台读取加密口令，以二进制数组形式返回原始口令的一次SHA256的结果；
	 * 
	 * @return
	 */
	public static byte[] readPassword() {
		byte[] pwdBytes = ConsoleUtils.readPassword();
		return ShaUtils.hash_256(pwdBytes);
	}

	/**
	 * 对指定的原始密码进行编码生成用于加解密的密码；
	 * 
	 * @param rawPassword
	 * @return
	 */
	public static byte[] encodePassword(String rawPassword) {
		byte[] pwdBytes = BytesUtils.toBytes(rawPassword, "UTF-8");
		return ShaUtils.hash_256(pwdBytes);
	}

	/**
	 * 对指定的原始密码进行编码生成用于加解密的密码；
	 * 
	 * @param rawPassword
	 * @return
	 */
	public static String encodePasswordAsBase58(String rawPassword) {
		return Base58Utils.encode(encodePassword(rawPassword));
	}

	/**
	 * 从控制台读取加密口令，以Base58字符串形式返回口令的一次SHA256的结果；
	 * 
	 * @return
	 */
	public static String readPasswordString() {
		return Base58Utils.encode(readPassword());
	}

	public static PrivKey readPrivKey(String keyFile, String base58Pwd) {
		return readPrivKey(keyFile, Base58Utils.decode(base58Pwd));
	}

	/**
	 * 从文件读取私钥；
	 * 
	 * @param keyFile
	 * @param pwdBytes
	 * @return
	 */
	public static PrivKey readPrivKey(String keyFile, byte[] pwdBytes) {
		String base58KeyString = FileUtils.readText(keyFile);
		byte[] keyBytes = Base58Utils.decode(base58KeyString);
		if (!BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM)) {
			throw new IllegalArgumentException("The specified file is not a private key file!");
		}
		return decryptedPrivKeyBytes(keyBytes, pwdBytes);
	}

	public static PrivKey decodePrivKey(String base58Key, String base58Pwd) {
		byte[] decryptedKey = Base58Utils.decode(base58Pwd);
		return decodePrivKey(base58Key, decryptedKey);
	}

	public static PrivKey decodePrivKey(String base58Key, byte[] pwdBytes) {
		byte[] keyBytes = Base58Utils.decode(base58Key);
		if (!BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM)) {
			throw new IllegalArgumentException("The specified file is not a private key file!");
		}
		return decryptedPrivKeyBytes(keyBytes, pwdBytes);
	}

	public static PrivKey decodePrivKeyWithRawPassword(String base58Key, String rawPassword) {
		byte[] pwdBytes = encodePassword(rawPassword);
		byte[] keyBytes = Base58Utils.decode(base58Key);
		if (!BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM)) {
			throw new IllegalArgumentException("The specified file is not a private key file!");
		}
		return decryptedPrivKeyBytes(keyBytes, pwdBytes);
	}

	public static boolean isPubKeyBytes(byte[] keyBytes) {
		return BytesUtils.startsWith(keyBytes, PUB_KEY_FILE_MAGICNUM);
	}

	public static boolean isPrivKeyBytes(byte[] keyBytes) {
		return BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM);
	}
}
