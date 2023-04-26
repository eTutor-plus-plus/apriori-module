package apriori.logic.utility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import apriori.db.HorizontalTransaction;
import apriori.logic.algorithm.ItemSet;
import apriori.logic.algorithm.Row;
import apriori.logic.datageneration.AssociationDataSet;
import apriori.logic.datageneration.AssociationRow;

/**
 * class for data base associated utility methods
 */
public class DbUtility {

	/**
	 * for verifying uuid specification
	 * 
	 * @param uuidCandidate uuid candidate to verify
	 * @return true (uuid), false (not an uuid)
	 */
	public static boolean verifyUUID(String uuidCandidate) {
		Pattern uuidPattern = Pattern
				.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
		boolean result = uuidPattern.matcher(uuidCandidate).matches();
		return result;
	}

	/**
	 * for decrypting a string
	 * 
	 * @param toDecrypt encrypted string
	 * @return decrypted string
	 */
	public static String decrypt(String toDecrypt) {
		String keyString = Property.getProperty("etutorplusplus.extension.key");
		try {
			IvParameterSpec iv = new IvParameterSpec(keyString.getBytes("UTF-8"));
			SecretKeySpec secretkeySpec = new SecretKeySpec(keyString.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretkeySpec, iv);
			byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(toDecrypt));
			return new String(cipherText);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * for decrypting initial variable (data set creation)
	 * 
	 * @param initialVariable encrypted parameter from eTutor++
	 * @return map with initial variables
	 */
	public static Map<String, String> decryptInitialVariableCreate(String initialVariable) {
		if (initialVariable == null) {
			return null;
		}
		String replaceChar = initialVariable.replace("-", "/");

		byte[] decoded = Base64.getDecoder().decode(replaceChar);
		String decodedString = new String(decoded);
		String decryption = null;

		try {
			decryption = DbUtility.decrypt(decodedString);

		} catch (Exception e) {
			return null;
		}

		if (decryption == null) {
			return null;
		}

		String decrypted = decryption.replace("[[{}]]", ";");

		String[] splitInitialVariable = decrypted.split(";");

		String idOne = splitInitialVariable[0];
		String difLevel = splitInitialVariable[1];
		String idTwo = splitInitialVariable[2];
		String user = splitInitialVariable[3];
		String idThree = splitInitialVariable[4];

		Map<String, String> map = new HashMap<>();
		map.put("idOne", idOne);
		map.put("difLevel", difLevel);
		map.put("idTwo", idTwo);
		map.put("user", user);
		map.put("idThree", idThree);

		return map;
	}

	/**
	 * for decrypting initial variable (training)
	 * 
	 * @param initialVariable encrypted parameters from eTutor++
	 * @return map with initial variables
	 */
	public static Map<String, String> decryptInitialVariableTraining(String initialVariable) {

		if (initialVariable == null) {
			return null;
		}

		String replaceChar = initialVariable.replace("-", "/");

		byte[] decoded = Base64.getDecoder().decode(replaceChar);
		String decodedString = new String(decoded);
		String decryption = null;
		try {
			decryption = DbUtility.decrypt(decodedString);

		} catch (Exception e) {
			return null;
		}

		if (decryption == null) {
			return null;
		}

		String decrypted = decryption.replace("[[{}]]", ";");

		String[] splitInitialVariable = decrypted.split(";");

		String courseInstanceUUID = splitInitialVariable[0];
		String exerciseSheetUUID = splitInitialVariable[1];
		String taskConfigId = splitInitialVariable[2];

		Map<String, String> map = new HashMap<>();
		map.put("courseInstanceUUID", courseInstanceUUID);
		map.put("exerciseSheetUUID", exerciseSheetUUID);
		map.put("taskConfigId", taskConfigId);

		return map;
	}

	/**
	 * for decrypting initial variable (exercise)
	 * 
	 * @param initialVariable encrypted parameters from eTutor++
	 * @return map with initial variables
	 */
	public static Map<String, String> decryptInitialVariable(String initialVariable) {

		if (initialVariable == null) {
			return null;
		}

		String replaceChar = initialVariable.replace("-", "/");

		byte[] decoded = Base64.getDecoder().decode(replaceChar);
		String decodedString = new String(decoded);
		String decryption = null;
		try {
			decryption = DbUtility.decrypt(decodedString);

		} catch (Exception e) {
			return null;
		}

		if (decryption == null) {
			return null;
		}

		String decrypted = decryption.replace("[[{}]]", ";");

		String[] splitInitialVariable = decrypted.split(";");

		String userId = splitInitialVariable[0];
		String _taskNo = splitInitialVariable[3];
		String courseInstanceUUID = splitInitialVariable[1];
		String exerciseSheetUUID = splitInitialVariable[2];
		String taskConfigId = splitInitialVariable[6];
		String maxPoints = splitInitialVariable[5];
		String difficultyLevel = splitInitialVariable[4];

		Map<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("courseInstanceUUID", courseInstanceUUID);
		map.put("exerciseSheetUUID", exerciseSheetUUID);
		map.put("_taskNo", _taskNo);
		map.put("difficultyLevel", difficultyLevel);
		map.put("maxPoints", maxPoints);
		map.put("taskConfigId", taskConfigId);

		return map;
	}

	/**
	 * for transforming association data set from apriori logic to data base format
	 * 
	 * @param ads  apriori data set (AssociationDataSet)
	 * @param uuid id data set (uuid)
	 * @param name data set name
	 * @return list data base rows 
	 */
	public static List<HorizontalTransaction> transformAssociationDataSet(AssociationDataSet ads, UUID uuid,
			String name) {
		List<HorizontalTransaction> horizontalList = new ArrayList<>();
		Row<String>[] adsRaw = ads.getRows();
		for (Row<String> row : adsRaw) {
			String tid = row.getIndicator();
			String[] items = row.getItemset().getItems();
			HorizontalTransaction newHT = new HorizontalTransaction();
			newHT.setUuid_ht(uuid);
			newHT.setTid(tid);
			newHT.setItems(items);
			newHT.setComment(name);
			horizontalList.add(newHT);
		}
		return horizontalList;
	}

	/**
	 * for transforming apriori logic data base format to association data set
	 * 
	 * @param dataset data set 
	 * @return association data set (AssociationDataSet)
	 */
	public static AssociationDataSet transformHorizontalTransaction(List<HorizontalTransaction> dataset) {

		AssociationRow[] adsRaw = new AssociationRow[dataset.size()];
		int index = 0;
		for (HorizontalTransaction ht : dataset) {
			adsRaw[index] = new AssociationRow(ht.getTid(), new ItemSet(ht.getItems()));
			index = index + 1;
		}
		AssociationDataSet ads = new AssociationDataSet(adsRaw);
		return ads;
	}
}
