/**
 * 
 * !!disabled feature!!
 * 
 * controller for export function
 *
 */

//package apriori.workflow.export;
//
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * controller for export function 
// *
// */
//@RestController
//public class ExportController {
//
//	@Autowired
//	private ExportMapRepository export;
//
//	/**
//	 * @param adminId user id
//	 * @return export object
//	 */
//	@GetMapping(path = "/export", produces = { MediaType.APPLICATION_JSON_VALUE })
//	public ExportObject getExport(@RequestParam(value = "adminId") String adminId) {
//
//		UUID uuid = export.getConfigId(adminId);
//
//		return new ExportObject("admin", uuid);
//	}
//
//}
