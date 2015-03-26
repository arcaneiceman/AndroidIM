package at.vcity.androidim.extras;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * This is a basic sms message representation.
 */
public class Sms {
    private String otherNumber;
    private String otherName;
    private long time;
    private long messageId;
    private long threadId;
    private String content;
    // Type is sent, received, or draft.
    private int type;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getOtherNumber() {
        return otherNumber;
    }

    public void setOtherNumber(String otherNumber) {
        this.otherNumber = otherNumber;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Sms(String otherNumber, String otherName, long time, long messageId, long threadId,
               String content, int type) {
        this.otherNumber = otherNumber;
        this.otherName = otherName;
        this.time = time;
        this.messageId = messageId;
        this.threadId = threadId;
        this.content = content;
        this.type = type;
    }

    // Creates a JSONObject representation of the sms to send to the server.
    public JSONObject toJson(String myNumber) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("messageId", messageId);
            obj.put("threadId", threadId);
            obj.put("time", time);
            obj.put("myNumber", myNumber);
            obj.put("otherNumber", otherNumber);
            obj.put("otherName", otherName);
            obj.put("messageContent", content);
            obj.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

}