import java.util.*;
import edu.duke.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
      StringBuilder sliced = new StringBuilder();
      for (int k = whichSlice; k < message.length(); k += totalSlices){
        char c = message.charAt(k);
        sliced.append(c);
      }
        return sliced.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker cracker = new CaesarCracker(mostCommon);
        for (int k = 0; k < klength; k++){
          String sliced = sliceString(encrypted, k, klength);
          int KeyForSliced = cracker.getKey(sliced);
          key[k] = KeyForSliced;
        }
        return key;
    }

    public void testTryKeyLength(){
      FileResource  fr = new FileResource("messages/secretmessage2.txt");
      String message = fr.asString();
      char mostCommon = 'e';
      int klength = 4;
      int[] key = tryKeyLength(message,klength,mostCommon);
      System.out.print("{" + key[0]);
      for (int i = 1; i < key.length; i++){
        System.out.print(" ," + key[i]);
      }
      System.out.print("}\n");
    }

    public HashSet<String> readDictionary(FileResource fr){
      HashSet<String> dictionary = new HashSet<String>();
      for (String line : fr.lines()){
        String word = line.toLowerCase();
        dictionary.add(word);
      }
      return dictionary;
    }

    public int countWords(String message, HashSet<String> dictionary){
      int count = 0;
      String[] wordsInMessage = message.split("\\W+");
      for (int i = 0; i < wordsInMessage.length; i++){
        if (dictionary.contains(wordsInMessage[i].toLowerCase())){
          count += 1;
        }
      }
      return count;
    }

    public String breakForLanguage(String encrypted, HashSet<String> dictionary){
      /*
      String decryptedMessage = "";
      int maxCount = 0;
      int[] rightKey = new int[100];
      for (int i = 1; i < 100; i++){
        char mostCommon = 'e';
        int klength = i;
        int[] key = tryKeyLength(encrypted,klength,mostCommon);
        VigenereCipher Vcipher = new VigenereCipher(key);
        String decrypted = Vcipher.decrypt(encrypted);
        int countRealWords = countWords(decrypted, dictionary);
        if (countRealWords > maxCount){
          decryptedMessage = decrypted;
          maxCount = countRealWords;
          rightKey = key;
        }
      }
      System.out.println("key length = " + rightKey.length);
      System.out.print("Key = {" + rightKey[0]);
      for (int i = 1; i < rightKey.length; i++){
        System.out.print(" ," + rightKey[i]);
      }
      System.out.print("}\n");
      System.out.println("There are " + maxCount + " valid words");
      return decryptedMessage;
      */
      String decryptedMessage = "";
      int maxCount = 0;
      int[] rightKey = new int[100];
      for (int i = 1; i < 100; i++){
        char mostCommon = mostCommonCharIn(dictionary);
        int klength = i;
        int[] key = tryKeyLength(encrypted,klength,mostCommon);
        VigenereCipher Vcipher = new VigenereCipher(key);
        String decrypted = Vcipher.decrypt(encrypted);
        int countRealWords = countWords(decrypted, dictionary);
        if (countRealWords > maxCount){
          decryptedMessage = decrypted;
          maxCount = countRealWords;
          rightKey = key;
        }
      }
      return decryptedMessage;
    }

    public char mostCommonCharIn(HashSet<String> dictionary){
      HashMap<Character, Integer> CharCount = new HashMap<Character, Integer>();
      String alphabet = "abcdefghijklmnopqrstuvwxyz";
      for (String word : dictionary){
        for (int i = 0; i < word.length(); i++){
          char c = word.charAt(i);
          int index = alphabet.indexOf(c);
          if (index != -1){
            if (!CharCount.containsKey(c)){
              CharCount.put(c,1);
            }
            else {
              CharCount.put(c,CharCount.get(c) + 1);
            }
          }
        }
      }

      int max = 0;
      char commonChar = '\0';
      for (char c : CharCount.keySet()){
        int count = CharCount.get(c);
        if (count > max){
          max = count;
          commonChar = c;
        }
      }
      return commonChar;
    }

  public void breakForAllLanguages(String encrypted, HashMap<String,HashSet<String>> languages){
      int maxValidWords = 0;
      String languageUsed = "";
      String decryptedMessage = "";
      for (String language : languages.keySet()){
        HashSet<String> dictionary = languages.get(language);
        String decrypted = breakForLanguage(encrypted,dictionary);
        int validWords = countWords(decrypted,dictionary);
        if (validWords > maxValidWords){
          languageUsed = language;
          decryptedMessage = decrypted;
          maxValidWords = validWords;
        }
      }
      System.out.println("Language of decryption : " + languageUsed);
      System.out.println("Number of valid words : " + maxValidWords);
      System.out.println("Decrypted message : \n" + decryptedMessage);
    }




    public void breakVigenere() {
      /*  FileResource fr = new FileResource();
        String encrypted = fr.asString();
        char mostCommon = 'e';
        int klength = 4;
        int[] key = tryKeyLength(encrypted,klength,mostCommon);
        VigenereCipher Vcipher = new VigenereCipher(key);
        String decrypted = Vcipher.decrypt(encrypted);
        System.out.println("Decrypted message : \n" + decrypted); */

        /*FileResource fr = new FileResource();
        String encrypted = fr.asString();
        FileResource f = new FileResource("dictionaries/English");
        HashSet<String> dictionary = readDictionary(f);
        String decrypted = breakForLanguage(encrypted, dictionary);
        System.out.println("Decrypted message : \n" + decrypted);
        System.out.println("##################");
        int[] K = tryKeyLength(encrypted, 38, 'e');
        VigenereCipher vc = new VigenereCipher(K);
        String decryption = vc.decrypt(encrypted);
        System.out.println(countWords(decryption, dictionary)); */

        String[] lang = {"English", "Danish", "French", "German", "Spanish",
                          "Dutch", "Italian", "Portuguese"};
        HashMap<String,HashSet<String>> dictionaries = new HashMap<String,HashSet<String>>();
        for (int i = 0; i < lang.length; i++){
          FileResource f = new FileResource("dictionaries/" + lang[i]);
          HashSet<String> dictionary = readDictionary(f);
          dictionaries.put(lang[i], dictionary);
        }

        FileResource fr = new FileResource();
        String encrypted = fr.asString();
        breakForAllLanguages(encrypted, dictionaries);
    }

    public static void main(String[] args){
      VigenereBreaker vb = new VigenereBreaker();
      // vb.testTryKeyLength();
      vb.breakVigenere();


    }

}
