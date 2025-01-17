import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Commit {

    private String sha, author, desc, treeSha, prevSha, nextSha;

    // This is to commit a file that has the previous sha
    public Commit(String sha, String author, String desc) throws IOException {
        prevSha = sha;
        treeSha = treeify();
        this.author = author;
        this.desc = desc;
    }

    // this is if you do not have the previous sha
    public Commit(String author, String desc) throws IOException {
        treeSha = treeify();
        this.author = author;
        this.desc = desc;
    }

    // gets the sha of the tree, pretty damn inportant
    public String treeify() throws IOException {
        Tree tree = new Tree();
        return tree.getSha();
    }

    // Gets a date (not to homecoming)
    public String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    // This is actually what saves the file into the object folder, the new commit
    public void push() throws IOException {
        FileWriter write = new FileWriter(new File("objects/" + sha));
        write.write(treeSha + "\n" + prevSha + "\n" + nextSha + "\n" + author + "\n" + getDate() + "\n" + desc);
        write.close();
    }

    // Gets the sha, this is code from blob.java
    public String getSha() {
        String input = (treeSha + "\n" + prevSha + "\n" + author + "\n" + getDate() + "\n" + desc);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // This is the complicated part! Writes the new sha into the old file
    public void writeInNewCommit() throws IOException {
        File orginalFile = new File("objects/" + prevSha);
        File newFile = new File("balls");
        BufferedReader reader = new BufferedReader(new FileReader(orginalFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
        String curr;
        int i = 0;

        while ((curr = reader.readLine()) != null) {
            if (i == 2) {
                writer.write(getSha());
            } else
                writer.write(curr);

            if (i != 5)
                writer.write("\n");
            i++;
        }
        writer.close();
        reader.close();
        newFile.renameTo(orginalFile);
    }
}
