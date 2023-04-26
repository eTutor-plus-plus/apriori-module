/**
 * 
 * !!disabled feature!!
 * 
 * class for export object
 *
 */

//package apriori.workflow.export;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * class for export object 
// *
// */
//public class ExportMap {
//
//	private final Map<String, UUID> exports = new HashMap<>();
//
//	/**
//	 * constructor
//	 */
//	public ExportMap() {
//
//	}
//
//	/**
//	 * for adding configuration to export list
//	 * 
//	 * @param adminId user id
//	 * @param config  task configuration
//	 */
//	public void addConfig(String adminId, UUID config) {
//		if (exports.containsKey(adminId)) {
//			exports.remove(adminId);
//		}
//		exports.put(adminId, config);
//	}
//
//	/**
//	 * for retrieving task configuration
//	 * 
//	 * @param adminId user id
//	 * @return id task configuration (uuid)
//	 */
//	public UUID getConfig(String adminId) {
//		if (exports.containsKey(adminId)) {
//			return exports.get(adminId);
//		}
//		return null;
//	}
//
//	@Override
//	public String toString() {
//		return "ExportMap [exports=" + exports + "]";
//	}
//
//}
