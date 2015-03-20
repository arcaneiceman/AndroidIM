package at.vcity.androidim.types;


public class MessageInfo {

	public static final String MESSAGE_LIST = "messageList";
	public static final String USERID = "from";
	public static final String SENDT = "sendt";
	public static final String MESSAGETEXT = "text";
    public static final String PICName= "picturename";
    public static final String TGT= "tgt";
	public static final String ENCRYPTEDIMAGE= "image";
	
	public String userid;
	public String sendt;
	public String messagetext;
    public String picname;
    public String tgt;
    public byte[] encryptedimage;
}
