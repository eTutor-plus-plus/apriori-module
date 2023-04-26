package apriori.logic.algorithm;

/**
 * class for storing interim results in
 *
 */
public class InterimResults {

	private String id;
	private AprioriDataBase adb;

	/**
	 * constructor
	 * 
	 * @param id  id interim result table
	 * @param adb apriori database
	 */
	InterimResults(String id, AprioriDataBase adb) {
		this.id = id;
		this.adb = adb;
	}

	/**
	 * constructor
	 *
	 * @param ir interim result
	 */
	InterimResults(InterimResults ir) {
		this.adb = new AprioriDataBase(ir.getAdb());
		this.id = ir.getId();
	}

	/**
	 * for copying results for storage
	 * 
	 * @param InterimResults interim result
	 * @return InterimResults interim result
	 */
	static InterimResults deepCopy(InterimResults interimResult) {
		AprioriDataBaseRow[] adbr = interimResult.getAdb().getDb();
		AprioriDataBaseRow[] adbrnew = new AprioriDataBaseRow[adbr.length];
		int i = 0;
		for (AprioriDataBaseRow r : adbr) {
			ItemSet set = new ItemSet(r.getItemset());
			if (r.getIndicator() != null) {
				int min = r.getIndicator();
				AprioriDataBaseRow apnew = new AprioriDataBaseRow(set, min);
				adbrnew[i] = apnew;
			} else {
				AprioriDataBaseRow apnew = new AprioriDataBaseRow(set);
				adbrnew[i] = apnew;
			}
			i++;
		}
		int minsup = interimResult.getAdb().getMin_support();
		AprioriDataBase apdbnew = new AprioriDataBase(adbrnew, minsup);
		String idnew = interimResult.getId();
		InterimResults newresult = new InterimResults(idnew, apdbnew);
		return newresult;
	}

	/**
	 * for displaying interim results in tabular format (for development purposes
	 * only)
	 */
	public void displayTable() {
		System.out.println("Table: " + id);
		System.out.println("_____________________________________________________________________________");
		System.out.printf("%30s %30s", "ItemSet", "Support Count");
		System.out.println();
		System.out.println("-----------------------------------------------------------------------------");
		for (AprioriDataBaseRow r : adb.getDb()) {
			if (r.getItemset() != null || r.getIndicator() != null) {
				System.out.format("%30s %30s", r.getItemset().toString(), r.getIndicator());
				System.out.println();
			} else {
				if (r.getItemset() != null) {
					System.out.format("%30s %30s", r.getItemset().toString(), null);
					System.out.println();
				}
			}
		}
		System.out.println("_____________________________________________________________________________");
		System.out.println();
	}

	public String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	public AprioriDataBase getAdb() {
		return adb;
	}

	void setAdb(AprioriDataBase adb) {
		this.adb = adb;
	}
}
