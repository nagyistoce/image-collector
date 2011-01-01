package team.nm.nnet.util;

/**
 * Thuc hien nhung xac dinh mang tinh chat logic
 * Hic coi bo hoi du nhung co dung thi lay thoi
 * @author MinhNhat
 *
 */
public class LogicUtils {

	/**
	 * Xac dinh mot chuoi co the parse thanh so duoc hay khong
	 * @param inputString
	 * @return Ket qua xac dinh
	 */
	public static boolean isNumber(String inputString) {
		if (inputString == null) {
			return false;
		}
		if (inputString.length() == 0) {
			return false;
		}
		char[] stringArray = inputString.toCharArray();
		for (int i = 0; i < stringArray.length; i ++) {
			if (stringArray[i] < '0' || stringArray[i] > '9') {
				return false;
			}
		}
		return true;
	}
	

}
