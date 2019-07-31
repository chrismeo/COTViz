import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class parseFiles implements Runnable{
	int start,end;
    String[] futureslist;
    File[] list_of_files;
    String folder;
    HashMap<String, String> hash;
    
    public parseFiles(String folder, String[] futureslist, int start, int end, File[] list_of_files, HashMap<String, String> hash) {
    	this.folder = folder;
    	this.futureslist = futureslist;
    	this.start = start;
    	this.end = end;
    	this.list_of_files = list_of_files;
    	this.hash = hash;
    }
	
	@Override
	public void run() { System.out.println("start parsing");
		
		// TODO Auto-generated method stub
		for (int k = start; k <= end; k++) {
			String name = futureslist[k];
			String path = "";

			String OS = System.getProperty("os.name");
			if (OS.startsWith("Windows"))
				path = folder + "\\" + name;
			if (!OS.startsWith("Windows"))
				path = folder + "/" + name;
			

			File f = new File(path); System.out.println("file :"+f);
			try {
				FileWriter tablefw = new FileWriter(f, true);
				for (int l = 0; l < list_of_files.length; l++) {
					InputStream fs = new FileInputStream(list_of_files[l]);
					HSSFWorkbook wb = new HSSFWorkbook(fs);
					HSSFSheet sheet = wb.getSheetAt(0);

					int r = sheet.getLastRowNum();
					for (int j = r - 1; j >= 0; j--) {
						Row row = sheet.getRow(j);
						
						Cell cell0 = row.getCell(0);
						String celltext0 = cell0.getStringCellValue();
						String line = "";

						if (celltext0.contains(hash.get(name)))
						{
							// Date
							Cell cell2 = row.getCell(2);
							Date date = new Date();
							date = cell2.getDateCellValue();

							DateFormat df = new SimpleDateFormat("MM/yy");
						    DateFormat df2 = new SimpleDateFormat("dd/MM/yy");
							String datestring = df.format(date);
							/*							
							boolean check = false;
							if (headfile.exists()) {
								Date lastdate = df2.parse(lastdate_string);
								if (date.compareTo(lastdate) <= 0)
									check = false;
								if (date.compareTo(lastdate) > 0) {
									check = true;
								}
							}
				
							if ((check) || (!headfile.exists())) {*/
								line += datestring;
								line += " ";

								// Commercials
								Cell cell11 = row.getCell(11);
								Cell cell12 = row.getCell(12);
								double result = cell11.getNumericCellValue() - cell12.getNumericCellValue();
								int commercials = (int) result;
								line += String.valueOf(commercials);
								line += " ";

								// Large Traders
								Cell cell8 = row.getCell(8);
								Cell cell9 = row.getCell(9);
								double result2 = cell8.getNumericCellValue() - cell9.getNumericCellValue();
								int largetraders = (int) result2;
								line += String.valueOf(largetraders);
								line += " ";

								// Small Traders
								Cell cell15 = row.getCell(15);
								Cell cell16 = row.getCell(16);
								double result3 = cell15.getNumericCellValue() - cell16.getNumericCellValue();
								int smalltraders = (int) result3;
								line += String.valueOf(smalltraders);

								tablefw.write(line + "\n");
							
						}
					}

					wb.close();
					fs.close();
				}

				tablefw.close();
			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}

    }
}
