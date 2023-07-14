import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main2 {

    public static void main(String[] args) {
        HashMap<String, ArrayList<String>> wordList = new HashMap<>();

        System.out.println("--挖矿与砍杀解密程序--");
        Path path = Path.of("word_list.txt");
        if (Files.exists(path)) {
            try {
                for (String word : Files.readAllLines(path)) {
                    String key = wordGetKey(word);
                    ArrayList<String> words = wordList.computeIfAbsent(key, k -> new ArrayList<>());
                    words.add(word);
                }
            } catch (IOException e) {
                System.err.println("word_list.txt读取失败！");
                throw new RuntimeException(e);
            }
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("输入谜题：");
                String puzzle = scanner.nextLine().toUpperCase();
                System.out.println("答案为：" + wordList.get(wordGetKey(puzzle)));
            }
        } else {
            System.err.println("word_list.txt文件不存在！");
        }
    }

    public static String wordGetKey(String word) {
        StringBuilder sb = new StringBuilder();
        for (char i = 'A'; i < 'Z' + 1; i++) {
            sb.append(word.length() - word.replaceAll(String.valueOf(i), "").length());
        }
        return sb.toString();
    }
}
