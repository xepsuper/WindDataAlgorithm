package algo.winddata;

/**
 * Simple application for retrieving and presenting temperature 
 * data from a weather station file.
 */
public class WindDataMain {

	/**
	 * Program entry point.
	 * 
	 * @param args optional argument for path to weather data file
	 */
	public static void main(String[] args) {
		WindDataHandler windDataHandler = new WindDataHandler();
		String fileName = "smhi-wind.csv";
		if(args.length > 0) {
			fileName = args[0];
		}		
		try {				
			windDataHandler.loadData(fileName);
			new WindDataUI(windDataHandler).startUI();

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
}