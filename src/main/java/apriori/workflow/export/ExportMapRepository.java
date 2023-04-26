/**
 * 
 * !!disabled feature!!
 * 
 * repository for export function 
 *
 */

//package apriori.workflow.export;
//
//import java.util.UUID;
//
//import org.springframework.stereotype.Repository;
//
///**
// * repository for export function 
// *
// */
//@Repository
//public class ExportMapRepository {
//
//	private static ExportMap export;
//
//	/**
//	 * constructors (static)
//	 */
//	static {
//		export = new ExportMap();
//	}
//
//	/**
//	 * for retrieving task configuration
//	 * 
//	 * @param adminId user id
//	 * @return task configuration (uuid)
//	 */
//	public UUID getConfigId(String adminId) {
//		UUID uuid = export.getConfig(adminId);
//		return uuid;
//	}
//
//	public void setConfigId(String adminId, UUID config) {
//		export.addConfig(adminId, config);
//	}
//
//	@Override
//	public String toString() {
//		return export.toString();
//	}
//
//}
