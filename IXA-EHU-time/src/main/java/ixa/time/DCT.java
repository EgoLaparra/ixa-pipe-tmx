package ixa.time;

public class DCT extends Features{

	public String file;
	public String id = "t0";
	public String value;
	
	public DCT(String fileName, String value) {
		
		this.value = value;
		this.file = fileName;
	}	
}
