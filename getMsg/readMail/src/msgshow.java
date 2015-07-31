
import java.util.*;
import java.io.*;
import javax.mail.*;
import java.awt.datatransfer.*;
import java.awt.*;

public class msgshow {

    public static void main(String argv[]) {

        String str = "";
        String str1 = "";
        String strFind;
        String text;
        String result;
        int indexStart;
        int lengthStr;

        try{
            Properties props=new Properties();
            props.put("mail.smtp.host", "whatever");
            // set as properties as needed
            

            javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
            System.out.println("Receiving mail...");
            Store store = session.getStore("pop3s");
            store.connect("pop.mail.ru", "", "");
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            Message message[] = folder.getMessages();
            strFind = ("Subscribe by July");
            for (int i = message.length-1; i >= 0; i--) {
                str = "" + (message[i].getFrom()[0]);
                str1 = "" + message[i].getSubject();
                if (str.substring(1,5).equals("STAR") && str1.contains("LAST")){
                    GetMulti gmulti = new GetMulti();
                    text = gmulti.getText(message[i]);
                    //text = "" + message[i].getContent();
                    lengthStr = strFind.length();
                    indexStart = text.indexOf(strFind);
                    result = text.substring(indexStart + lengthStr + 1, indexStart + lengthStr + 3);
                    System.out.println(result);
                    copyToSystemClipboard(result);
                    break;
                }

            }
            folder.close(false);
            store.close();
        }
        catch(Exception e){
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public static class GetMulti {

        public  boolean textIsHtml = false;

        /**
         * Return the primary text content of the message.
         */
        public String getText(Part p) throws MessagingException, IOException {
            if (p.isMimeType("text/*")) {
                String s = (String)p.getContent();
                textIsHtml = p.isMimeType("text/html");
                return s;
            }

            if (p.isMimeType("multipart/alternative")) {
                // prefer html text over plain text
                Multipart mp = (Multipart)p.getContent();
                String text = null;
                for (int i = 0; i < mp.getCount(); i++) {
                    Part bp = mp.getBodyPart(i);
                    if (bp.isMimeType("text/plain")) {
                        if (text == null)
                            text = getText(bp);
                        continue;
                    } else if (bp.isMimeType("text/html")) {
                        String s = getText(bp);
                        if (s != null)
                            return s;
                    } else {
                        return getText(bp);
                    }
                }
                return text;
            } else if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart)p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String s = getText(mp.getBodyPart(i));
                    if (s != null)
                        return s;
                }
            }

            return null;
        }

    }

    public static void copyToSystemClipboard(String str) {

        StringSelection ss = new StringSelection(str);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

    }
}
